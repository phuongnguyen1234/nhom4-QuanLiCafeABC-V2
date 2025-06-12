package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import com.backend.dto.DonHangDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils;
import com.backend.utils.PdfUtils; // Import lớp tiện ích mới
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.application.Platform;
import javafx.collections.FXCollections; // Import MonTrongDonDTO
import javafx.concurrent.Task;
import javafx.event.ActionEvent; // Import FXCollections
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class ChiTietDonHangUI {

    @FXML
    private Text lblMaDonHang, lblTenNhanVien, lblTongTien, lblThoiGianDat;

    @FXML
    private TableView<MonTrongDonDTO> tableChiTietDonHang; // Change TableView type to MonTrongDonDTO

    @FXML
    private TableColumn<MonTrongDonDTO, String> colSTT, colTenMon, colYeuCauKhac; // Change TableColumn types

    @FXML
    private TableColumn<MonTrongDonDTO, Integer> colSoLuong, colDonGia, colTamTinh; // Change TableColumn types

    @FXML
    private Button btnQuayLai;

    private DonHangDTO donHang;
    private String maDonHangToLoad;

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager())
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @FXML
    public void initialize() {
        // Thiết lập placeholder ban đầu
        tableChiTietDonHang.setPlaceholder(JavaFXUtils.createPlaceholder("Đang tải...", "/icons/loading.png"));

        // Cấu hình cột
        colSTT.setCellValueFactory(param ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(tableChiTietDonHang.getItems().indexOf(param.getValue()) + 1)));
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon")); 
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colYeuCauKhac.setCellValueFactory(new PropertyValueFactory<>("yeuCauKhac")); 
        colTamTinh.setCellValueFactory(new PropertyValueFactory<>("tamTinh"));

        tableChiTietDonHang.getColumns().forEach(column -> {
            column.setReorderable(false);
        });
        JavaFXUtils.disableHorizontalScrollBar(tableChiTietDonHang);
    }

    public void setMaDonHangAndLoad(String maDonHang) {
        this.maDonHangToLoad = maDonHang;
        loadChiTietDonHangData();
    }

    private void loadChiTietDonHangData() {
        if (maDonHangToLoad == null || maDonHangToLoad.isEmpty()) {
            MessageUtils.showErrorMessage("Không có mã đơn hàng để tải.");
            tableChiTietDonHang.setPlaceholder(JavaFXUtils.createPlaceholder("Lỗi: Không có mã đơn hàng.", "/icons/error.png"));
            return;
        }

        tableChiTietDonHang.setPlaceholder(JavaFXUtils.createPlaceholder("Đang tải chi tiết đơn hàng...", "/icons/loading.png"));

        Task<DonHangDTO> loadTask = new Task<>() {
            @Override
            protected DonHangDTO call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/don-hang/" + maDonHangToLoad))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), DonHangDTO.class);
                } else {
                    throw new IOException("Lỗi khi tải chi tiết đơn hàng: " + response.statusCode() + " - " + response.body());
                }
            }
        };

        loadTask.setOnSucceeded(event -> {
            this.donHang = loadTask.getValue();
            if (this.donHang != null) {
                lblMaDonHang.setText("Mã đơn hàng: " + this.donHang.getMaDonHang());
                lblTenNhanVien.setText("Nhân viên tạo đơn: " + this.donHang.getHoTen());
                lblTongTien.setText("Tổng tiền: " + String.format("%,d", this.donHang.getTongTien()) + " VND");
                lblThoiGianDat.setText("Thời gian đặt hàng: " + this.donHang.getThoiGianDatHang().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                tableChiTietDonHang.setItems(FXCollections.observableArrayList(this.donHang.getDanhSachMonTrongDon()));
                if (this.donHang.getDanhSachMonTrongDon() == null || this.donHang.getDanhSachMonTrongDon().isEmpty()) {
                    tableChiTietDonHang.setPlaceholder(JavaFXUtils.createPlaceholder("Đơn hàng này không có món nào.", "/icons/empty-box.png"));
                }
            } else {
                MessageUtils.showErrorMessage("Không tìm thấy chi tiết đơn hàng.");
                tableChiTietDonHang.setPlaceholder(JavaFXUtils.createPlaceholder("Không tìm thấy chi tiết đơn hàng.", "/icons/error.png"));
            }
        });

        loadTask.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi tải chi tiết đơn hàng: " + loadTask.getException().getMessage());
            loadTask.getException().printStackTrace();
            tableChiTietDonHang.setPlaceholder(JavaFXUtils.createPlaceholder("Lỗi tải dữ liệu.", "/icons/error.png"));
        });

        new Thread(loadTask).start();
    }

    @FXML
    void inHoaDon() {
        if (this.donHang != null) {
            PdfUtils.taoHoaDonPDF(this.donHang);
        } else {
            // Có thể thêm thông báo lỗi nếu donHang là null
            System.err.println("Không có thông tin đơn hàng để in.");
        }
    }
    
    @FXML
    void quayLai(ActionEvent event) {
        btnQuayLai.getScene().getWindow().hide();
    }
}
