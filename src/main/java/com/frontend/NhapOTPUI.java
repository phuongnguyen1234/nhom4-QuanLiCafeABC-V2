package com.frontend;

import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class NhapOTPUI {

    @FXML private TextField maOTPTextField; // Khớp với FXML
    @FXML private Button btnXacNhanMaOTP; // Khớp với FXML
    // @FXML private Button btnGuiLaiOtp; // FXML không có nút này, có thể thêm nếu muốn
    // @FXML private Button btnQuayLaiNhapOtp; // FXML không có nút này, có thể thêm nếu muốn
    @FXML private Pane rootPaneNhapOtp; 

    private String emailNhanDuoc;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

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

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Đặt Lại Mật Khẩu");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageUtils.showErrorMessage("Không thể mở form đặt lại mật khẩu.");
        }
    }
    
    // @FXML
    // private void handleGuiLaiOtp() { // Nếu có nút gửi lại OTP
    //     if (emailNhanDuoc == null || emailNhanDuoc.isEmpty()) {
    //         MessageUtils.showErrorMessage("Không có thông tin email để gửi lại OTP.");
    //         return;
    //     }

    //     rootPaneNhapOtp.setDisable(true);
    //     // btnGuiLaiOtp.setDisable(true);

    //     Task<Map<String, String>> task = new Task<>() {
    //         @Override
    //         protected Map<String, String> call() throws Exception {
    //             HttpRequest request = HttpRequest.newBuilder()
    //                     .uri(URI.create("http://localhost:8080/auth/forgot-password"))
    //                     .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(Map.of("email", emailNhanDuoc))))
    //                     .header("Content-Type", "application/json")
    //                     .build();
    //             HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    //             if (response.statusCode() == 200) {
    //                 return objectMapper.readValue(response.body(), new TypeReference<Map<String, String>>() {});
    //             } else {
    //                 String responseBody = response.body();
    //                 String errorMessage = "Lỗi máy chủ: " + response.statusCode();
    //                  if (responseBody != null && !responseBody.isEmpty()) {
    //                     try {
    //                         Map<String, String> errorMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, String>>() {});
    //                         errorMessage = errorMap.getOrDefault("message", errorMessage + ". " + responseBody);
    //                     } catch (Exception parseEx) {
    //                         // Không parse được thì dùng body gốc
    //                     }
    //                 }
    //                 throw new IOException(errorMessage);
    //             }
    //         }
    //     };

    //     task.setOnSucceeded(e -> {
    //         rootPaneNhapOtp.setDisable(false);
    //         // btnGuiLaiOtp.setDisable(false);
    //         Map<String, String> responseMap = task.getValue();
    //         MessageUtils.showInfoMessage(responseMap.getOrDefault("message", "Yêu cầu gửi lại OTP đã được xử lý. Vui lòng kiểm tra email."));
    //     });

    //     task.setOnFailed(e -> {
    //         rootPaneNhapOtp.setDisable(false);
    //         // btnGuiLaiOtp.setDisable(false);
    //         MessageUtils.showErrorMessage("Lỗi khi gửi lại OTP: " + task.getException().getMessage());
    //         task.getException().printStackTrace();
    //     });

    //     new Thread(task).start();
    // }


    // @FXML
    // private void handleQuayLaiNhapOtp() { // Nếu có nút quay lại
    //     closeDialog();
    // }


    private void closeDialog() {
        Stage stage = (Stage) rootPaneNhapOtp.getScene().getWindow();
        stage.close();
    }
}
