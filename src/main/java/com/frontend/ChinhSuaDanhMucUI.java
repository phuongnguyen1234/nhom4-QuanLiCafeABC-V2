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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ChinhSuaDanhMucUI {
    @FXML
    private TextField tenDanhMucTextField;

    @FXML
    private ComboBox<String> loaiComboBox;

    @FXML
    private CheckBox trangThaiCheckBox;

    private DanhMucKhongMonDTO danhMuc;

    private QuanLiDanhMucUI quanLiDanhMucUI;

    public void setQuanLiDanhMucUI(QuanLiDanhMucUI quanLiDanhMucUI) {
        this.quanLiDanhMucUI = quanLiDanhMucUI;
    }

    @FXML
    public void initialize(){
        loaiComboBox.getItems().addAll("Đồ uống", "Đồ ăn", "Khác");
    }

    public void setDanhMuc(DanhMucKhongMonDTO danhMuc) {
        this.danhMuc = danhMuc;
        tenDanhMucTextField.setText(danhMuc.getTenDanhMuc());
        trangThaiCheckBox.setSelected(danhMuc.getTrangThai().equals("Hoạt động"));
        loaiComboBox.setValue(danhMuc.getLoai());
    }

    @FXML
    public void capNhat(){
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
        danhMuc.setTrangThai(trangThaiCheckBox.isSelected() ? "Hoạt động" : "Ngừng hoạt động");

        // Gửi yêu cầu cập nhật danh mục đến server
        updateRequest(danhMuc);
        quanLiDanhMucUI.getListDanhMuc().set(quanLiDanhMucUI.getListDanhMuc().indexOf(danhMuc), danhMuc);
        MessageUtils.showInfoMessage("Cập nhật danh mục thành công");
        tenDanhMucTextField.getScene().getWindow().hide();
    }

    @FXML
    public void quayLai(){
        tenDanhMucTextField.getScene().getWindow().hide();
    }

    private void updateRequest(DanhMucKhongMonDTO danhMuc) {
        // Tạo một tác vụ bất đồng bộ để gửi yêu cầu cập nhật danh mục
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Tạo ObjectMapper để chuyển đổi đối tượng Mon thành JSON
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(danhMuc);

                    // Tạo HttpClient
                    HttpClient client = HttpClient.newHttpClient();

                // Tạo request PATCH
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/danh-muc/" + danhMuc.getMaDanhMuc()))
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build();

                // Gửi request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Xử lý phản hồi
                if (response.statusCode() == 200) {
                    System.out.println("Cập nhật thành công!");
                    System.out.println("Phản hồi: " + response.body());
                } else {
                    System.err.println("Lỗi khi cập nhật. Mã trạng thái: " + response.statusCode());
                    System.err.println("Thông điệp: " + response.body());
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi gửi PATCH request: " + e.getMessage());
            }

            return null;
        }
        };

        // Thực hiện tác vụ trong một luồng riêng
        new Thread(task).start();
    }
}
