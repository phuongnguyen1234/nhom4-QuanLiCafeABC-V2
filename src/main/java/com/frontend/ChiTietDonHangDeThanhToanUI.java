package com.frontend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.backend.dto.DonHangDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChiTietDonHangDeThanhToanUI {
    @FXML
    private Text tenNhanVienText, tongTienText;

    @FXML
    private TableView<MonTrongDonDTO> tableViewDonHang;

    @FXML
    private TableColumn<MonTrongDonDTO, String> colSTT, colTenMon, colYeuCauKhac;

    @FXML
    private TableColumn<MonTrongDonDTO, Integer> colSoLuong, colDonGia, colTamTinh;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Button btnXacNhanThanhToan, btnQuayLai;

    private DonHangDTO donHang;

    private final ObservableList<MonTrongDonDTO> danhSachMonTrongDon = FXCollections.observableArrayList();

    private ThucDonUI thucDonUI;

    public void setThucDonUI(ThucDonUI thucDonUI) {
        this.thucDonUI = thucDonUI;
    }

    @FXML
    public void initialize() {
        // Cấu hình các cột trong TableView
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colYeuCauKhac.setCellValueFactory(new PropertyValueFactory<>("yeuCauKhac"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colTamTinh.setCellValueFactory(new PropertyValueFactory<>("tamTinh"));

        // Gắn dữ liệu TableView với danh sách món trong đơn
        tableViewDonHang.setItems(danhSachMonTrongDon);
    }
    
    public void setDonHang(DonHangDTO donHang){
        this.donHang = donHang;
        tenNhanVienText.setText("Nhân viên tạo đơn: " + donHang.getHoTen());
        tongTienText.setText("Tổng tiền: " + donHang.getTongTien() + " VND");
    
        // Tải dữ liệu bảng từ danh sách các món trong đơn hàng
        ObservableList<MonTrongDonDTO> chiTietDon = FXCollections.observableArrayList(donHang.getDanhSachMonTrongDon());

        // Cập nhật cột số thứ tự (STT) bằng cách tính số thứ tự dựa vào index của dòng
        colSTT.setCellValueFactory(param -> {
            int rowIndex = tableViewDonHang.getItems().indexOf(param.getValue()) + 1; // Tính STT bắt đầu từ 1
            return new SimpleStringProperty(String.valueOf(rowIndex));
        });
    
        colTenMon.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenMon()));
        colYeuCauKhac.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getYeuCauKhac()));
        colSoLuong.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSoLuong()).asObject());
        colDonGia.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDonGia()).asObject());
        colTamTinh.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTamTinh()).asObject());
    
        tableViewDonHang.setItems(chiTietDon);
    }

    @FXML
    private void xacNhanThanhToan(ActionEvent event) {
        try {
            mainAnchorPane.setDisable(true);
            btnXacNhanThanhToan.setDisable(true);
            btnQuayLai.setDisable(true);

            Task<Void> task = createDonHangTask(donHang);

            task.setOnSucceeded(e -> {
                mainAnchorPane.setDisable(false);
                btnXacNhanThanhToan.setDisable(false);
                btnQuayLai.setDisable(false);
                MessageUtils.showInfoMessage("Tạo đơn thành công");
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                thucDonUI.resetDonHang();
            });

            task.setOnFailed(e -> {
                Throwable ex = task.getException();
                ex.printStackTrace();
                mainAnchorPane.setDisable(false);
                btnXacNhanThanhToan.setDisable(false);
                btnQuayLai.setDisable(false);
                MessageUtils.showErrorMessage("Tạo đơn thất bại");
            });

            task.setOnCancelled(e -> {
                mainAnchorPane.setDisable(false);
                btnXacNhanThanhToan.setDisable(false);
                btnQuayLai.setDisable(false);
            });

            new Thread(task).start();
            // Đóng dialog
            

        } catch (Exception e) {
            MessageUtils.showErrorMessage("Không thể lưu hóa đơn.");
        }
    }

    @FXML
    private void quayLai(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private Task<Void> createDonHangTask(DonHangDTO donHang) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(donHang);
                System.out.println("JSON: " + json);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/don-hang/create"))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 201 && response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi tạo đơn hàng. Mã trạng thái: " + response.statusCode());
                }
                return null;
            }
        };
    }

}
