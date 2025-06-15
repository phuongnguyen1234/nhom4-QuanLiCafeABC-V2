package com.frontend;

import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import javafx.concurrent.Task;

public class NhapOTPUI {

    @FXML private TextField maOTPTextField; // Khớp với FXML
    @FXML private Button btnXacNhanMaOTP, btnHuyBo; // Khớp với FXML
    @FXML private Pane rootPaneNhapOtp; 

    private String emailNhanDuoc;

    public void setEmailDaNhap(String email) {
        this.emailNhanDuoc = email;
    }

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void xacNhanMaOTP() { // Khớp với onAction trong FXML
        String otp = maOTPTextField.getText().trim();
        if (otp.isEmpty() || !otp.matches("\\d{6}")) { // Giả sử OTP có 6 chữ số
            MessageUtils.showErrorMessage("Mã OTP không hợp lệ. Vui lòng nhập 6 chữ số.");
            return;
        }
         // Vô hiệu hóa UI
        if (rootPaneNhapOtp != null) rootPaneNhapOtp.setDisable(true);
        if (btnXacNhanMaOTP != null) btnXacNhanMaOTP.setDisable(true);
        if (btnHuyBo != null) btnHuyBo.setDisable(true);

        Task<Boolean> verifyOtpTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/auth/verify-otp")) // Endpoint mới
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(Map.of("email", emailNhanDuoc, "otp", otp))))
                        .header("Content-Type", "application/json")
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    // Backend trả về true/false dưới dạng text, cần parse
                    return Boolean.parseBoolean(response.body());
                } else {
                    // Xử lý lỗi từ server
                    String errorMessage = "Lỗi xác thực OTP: " + response.statusCode();
                    if (response.body() != null && !response.body().isEmpty()) {
                        errorMessage += " - " + response.body();
                    }
                    throw new IOException(errorMessage);
                }
            }
        };

        verifyOtpTask.setOnSucceeded(event -> {
            if (verifyOtpTask.getValue()) { // OTP hợp lệ
                openDatLaiMatKhauDialog(emailNhanDuoc, otp);
                closeDialog(); // Đóng dialog Nhập OTP sau khi dialog Đặt lại mật khẩu đã được yêu cầu mở
            } else { // OTP không hợp lệ
                MessageUtils.showErrorMessage("Mã OTP không chính xác hoặc đã hết hạn. Vui lòng thử lại.");
                enableUI();
            }
        });

        verifyOtpTask.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi khi xác thực OTP: " + verifyOtpTask.getException().getMessage());
            verifyOtpTask.getException().printStackTrace();
            enableUI();
        });

        new Thread(verifyOtpTask).start();
    }

    private void openDatLaiMatKhauDialog(String email, String otp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/datLaiMatKhau.fxml"));
            Parent root = loader.load();

            DatLaiMatKhauUI controller = loader.getController();
            controller.setDataFromOtpScreen(email, otp); // Truyền email và OTP

            Stage stage = JavaFXUtils.createDialog("Đặt lại mật khẩu", root, null);
            stage.show(); // Thay đổi từ showAndWait() sang show()
            // Không cần closeDialog() ở đây vì nó sẽ được gọi trong xacNhanMaOTP
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageUtils.showErrorMessage("Không thể mở form đặt lại mật khẩu.");
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) rootPaneNhapOtp.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void huyBo(){
        closeDialog();
    }

    private void enableUI() {
        if (rootPaneNhapOtp != null) rootPaneNhapOtp.setDisable(false);
        if (btnXacNhanMaOTP != null) btnXacNhanMaOTP.setDisable(false);
        if (btnHuyBo != null) btnHuyBo.setDisable(false);
    }
}
