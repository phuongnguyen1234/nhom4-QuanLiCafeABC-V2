package com.frontend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ThemDanhMucUI {
    @FXML
    private TextField tenDanhMucTextField;

    @FXML
    private ComboBox<String> loaiComboBox;

    @FXML
    private Button btnThem, btnQuayLai;

    @FXML
    private AnchorPane mainAnchorPane;

    private QuanLiDanhMucUI quanLiDanhMucUI;

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void setQuanLiDanhMucUI(QuanLiDanhMucUI quanLiDanhMucUI) {
        this.quanLiDanhMucUI = quanLiDanhMucUI;
    }

    @FXML
    public void initialize() {
        loaiComboBox.getItems().addAll("Đồ uống", "Đồ ăn", "Khác");
    }

    @FXML
    public void them() {
        DanhMucKhongMonDTO danhMuc = new DanhMucKhongMonDTO();

        if (tenDanhMucTextField.getText().isEmpty()) {
            MessageUtils.showErrorMessage("Tên danh mục không được để trống");
            return;
        }

        if (loaiComboBox.getValue() == null) {
            MessageUtils.showErrorMessage("Vui lòng chọn loại danh mục");
            return;
        }

        danhMuc.setTenDanhMuc(tenDanhMucTextField.getText());
        danhMuc.setTrangThai("Hoạt động");
        danhMuc.setLoai(loaiComboBox.getValue());

        // Đánh dấu đang xử lý
        setDisableItems(true);

        Task<Void> task = createRequest(danhMuc);

        task.setOnSucceeded(e -> {
            quanLiDanhMucUI.loadDanhSachDanhMuc();
            MessageUtils.showInfoMessage("Thêm danh mục thành công");
            setDisableItems(false);
            tenDanhMucTextField.getScene().getWindow().hide();
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            MessageUtils.showErrorMessage("Lỗi khi thêm: " + ex.getMessage());
            setDisableItems(false);
        });

        task.setOnCancelled(e -> {
            btnThem.setDisable(false);
            btnQuayLai.setDisable(false);
        });

        new Thread(task).start();
    }

    @FXML
    public void quayLai() {
        tenDanhMucTextField.getScene().getWindow().hide();
    }

    private Task<Void> createRequest(DanhMucKhongMonDTO danhMuc) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                String json = objectMapper.writeValueAsString(danhMuc);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/danh-muc/create"))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 201 && response.statusCode() != 200) {
                    throw new RuntimeException("Tạo thất bại: " + response.body());
                }

                return null;
            }
        };
    }

    private void setDisableItems(boolean value){
        mainAnchorPane.setDisable(value);
    }
}
