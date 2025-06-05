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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ChinhSuaDanhMucUI {
    @FXML
    private TextField tenDanhMucTextField;

    @FXML
    private ComboBox<String> loaiComboBox;

    @FXML
    private CheckBox trangThaiCheckBox;

    @FXML
    private Button btnCapNhat, btnQuayLai;

    private DanhMucKhongMonDTO danhMuc;
    private QuanLiDanhMucUI quanLiDanhMucUI;

    @FXML
    private AnchorPane mainAnchorPane;

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

    public void setDanhMuc(DanhMucKhongMonDTO danhMuc) {
        this.danhMuc = danhMuc;
        tenDanhMucTextField.setText(danhMuc.getTenDanhMuc());
        trangThaiCheckBox.setSelected("Hoạt động".equals(danhMuc.getTrangThai()));
        loaiComboBox.setValue(danhMuc.getLoai());
    }

    @FXML
    public void capNhat() {
        if (tenDanhMucTextField.getText().isEmpty()) {
            MessageUtils.showErrorMessage("Tên danh mục không được để trống");
            return;
        }

        if (loaiComboBox.getValue() == null) {
            MessageUtils.showErrorMessage("Vui lòng chọn loại danh mục");
            return;
        }

        danhMuc.setTenDanhMuc(tenDanhMucTextField.getText());
        danhMuc.setLoai(loaiComboBox.getValue());
        danhMuc.setTrangThai(trangThaiCheckBox.isSelected() ? "Hoạt động" : "Ngừng hoạt động");

        mainAnchorPane.setDisable(true);
        btnCapNhat.setDisable(true);
        btnQuayLai.setDisable(true);

        Task<Void> task = updateRequest(danhMuc);

        task.setOnSucceeded(e -> {
            quanLiDanhMucUI.loadDanhSachDanhMuc();
            MessageUtils.showInfoMessage("Cập nhật danh mục thành công");
            tenDanhMucTextField.getScene().getWindow().hide();
            btnCapNhat.setDisable(false);
            btnQuayLai.setDisable(false);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace(); // giúp debug
            MessageUtils.showErrorMessage("Lỗi khi cập nhật: " + ex.getMessage());
            mainAnchorPane.setDisable(false); // Enable lại nếu lỗi
            btnCapNhat.setDisable(false);
            btnQuayLai.setDisable(false);
        });

        task.setOnCancelled(e -> {
            btnCapNhat.setDisable(false);
            btnQuayLai.setDisable(false);
        });

        new Thread(task).start();
    }

    @FXML
    public void quayLai() {
        tenDanhMucTextField.getScene().getWindow().hide();
    }

    private Task<Void> updateRequest(DanhMucKhongMonDTO danhMuc) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                String json = objectMapper.writeValueAsString(danhMuc);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/danh-muc/" + danhMuc.getMaDanhMuc()))
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Cập nhật thất bại: " + response.body());
                }

                return null;
            }
        };
    }
}
