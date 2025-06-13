package com.frontend;

import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class NhapOTPUI {

    @FXML private TextField maOTPTextField; // Khớp với FXML
    @FXML private Button btnXacNhanMaOTP; // Khớp với FXML
    @FXML private Pane rootPaneNhapOtp; 

    private String emailNhanDuoc;

    public void setEmailDaNhap(String email) {
        this.emailNhanDuoc = email;
    }

    @FXML
    private void xacNhanMaOTP() { // Khớp với onAction trong FXML
        String otp = maOTPTextField.getText().trim();
        if (otp.isEmpty() || !otp.matches("\\d{6}")) { // Giả sử OTP có 6 chữ số
            MessageUtils.showErrorMessage("Mã OTP không hợp lệ. Vui lòng nhập 6 chữ số.");
            return;
        }
        openDatLaiMatKhauDialog(emailNhanDuoc, otp);
        closeDialog();
    }

    private void openDatLaiMatKhauDialog(String email, String otp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/datLaiMatKhau.fxml"));
            Parent root = loader.load();

            DatLaiMatKhauUI controller = loader.getController();
            controller.setDataFromOtpScreen(email, otp); // Truyền email và OTP

            Stage stage = JavaFXUtils.createDialog("Đặt lại mật khẩu", root, null);
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageUtils.showErrorMessage("Không thể mở form đặt lại mật khẩu.");
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) rootPaneNhapOtp.getScene().getWindow();
        stage.close();
    }
}
