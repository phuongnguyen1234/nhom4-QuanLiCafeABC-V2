package com.frontend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ThemDanhMucUI {
    @FXML
    private TextField tenDanhMucTextField;

    @FXML
    private ComboBox<String> loaiComboBox;
    
    private QuanLiDanhMucUI quanLiDanhMucUI;

    public void setQuanLiDanhMucUI(QuanLiDanhMucUI quanLiDanhMucUI) {
        this.quanLiDanhMucUI = quanLiDanhMucUI;
    }

    @FXML
    public void initialize(){
        loaiComboBox.getItems().addAll("Đồ uống", "Đồ ăn", "Khác");
    }

    @FXML
    public void them(){
        DanhMucKhongMonDTO danhMuc = new DanhMucKhongMonDTO();

        // Kiểm tra các trường nhập liệu
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

        createRequest(danhMuc);
        quanLiDanhMucUI.getListDanhMuc().add(danhMuc);
        MessageUtils.showInfoMessage("Thêm danh mục thành công");
        tenDanhMucTextField.getScene().getWindow().hide();
    }

    @FXML
    public void quayLai(){
        tenDanhMucTextField.getScene().getWindow().hide();
    }

    private void createRequest(DanhMucKhongMonDTO danhMuc) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            try {
                // Tạo ObjectMapper để chuyển đối tượng Mon thành JSON
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(danhMuc);
                System.out.println("JSON gửi đi: " + json);
                // Tạo HttpClient
                HttpClient client = HttpClient.newHttpClient();

                // Tạo request POST
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/danh-muc/create")) // endpoint tạo mới
                        .POST(HttpRequest.BodyPublishers.ofString(json)) // dùng POST thay vì PATCH
                        .header("Content-Type", "application/json")
                        .build();

                // Gửi request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Xử lý phản hồi
                if (response.statusCode() == 201 || response.statusCode() == 200) {
                    System.out.println("Tạo danh mục thành công!");
                    System.out.println("Phản hồi: " + response.body());
                } else {
                    System.err.println("Lỗi khi tạo danh mục. Mã trạng thái: " + response.statusCode());
                    System.err.println("Thông điệp: " + response.body());
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi gửi POST request: " + e.getMessage());
            }

            return null;
        }
        };
        new Thread(task).start();
    }
}
