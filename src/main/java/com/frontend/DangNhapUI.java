package com.frontend;

import java.io.IOException;

import com.frontend.control.DangNhapController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DangNhapUI {
    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField matKhauTextField;

    private DangNhapController dangNhapController;

    @FXML
    public void initialize() {
        try {
            // Khởi tạo DangNhapController với kết nối database
            dangNhapController = new DangNhapController();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi", "Lỗi kết nối đến cơ sở dữ liệu!", e.getMessage());
        }
    }
    
    @FXML
    private void kichNutDangNhap() {
        /*String email = emailTextField.getText().trim();
        String matKhau = matKhauTextField.getText().trim();

        if (email.isEmpty() || matKhau.isEmpty()) {
            showError("Lỗi", "Vui lòng nhập đầy đủ email và mật khẩu!", "Hãy chắc chắn rằng bạn đã điền cả hai trường.");
            return;
        }

        try {
            NhanVien nhanVien = dangNhapController.dangNhap(email, matKhau);
            System.out.println("Đăng nhập thành công: " + nhanVien.getTenNhanVien());
            System.out.println("Quyền truy cập: " + nhanVien.getQuyenTruyCap());

            // Tạo sessionId và lưu vào SessionManager
            String sessionId = java.util.UUID.randomUUID().toString();
            SessionManager.addSession(sessionId, nhanVien);

            // Chuyển sang màn hình chính
            if ("Admin".equalsIgnoreCase(nhanVien.getQuyenTruyCap())) {
                chuyenManHinh("/fxml/trangChuScreen.fxml", "Quản lí cà phê ABC - Admin");
            } else if ("User".equalsIgnoreCase(nhanVien.getQuyenTruyCap())) {
                chuyenManHinh("/fxml/trangChuScreen.fxml", "Quản lí cà phê ABC - User");
            } else {
                showError("Lỗi", "Quyền truy cập không hợp lệ!", "Hãy kiểm tra lại quyền truy cập của tài khoản.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi để debug
            showError("Lỗi", "Đăng nhập thất bại!", "Chi tiết lỗi: " + e.getMessage());
        } */
    }

    public void chuyenManHinh(String duongDanFXML, String tieuDe) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(duongDanFXML));
            Parent root = loader.load();
            Stage stage = (Stage) emailTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tieuDe);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Lỗi khi chuyển màn hình! Đường dẫn FXML không hợp lệ: " + duongDanFXML);
        }
    }

    private void showError(String tieuDe, String message, String chiTiet) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(tieuDe);
        alert.setHeaderText(message);
        alert.setContentText(chiTiet);
        alert.show();
    }
}

