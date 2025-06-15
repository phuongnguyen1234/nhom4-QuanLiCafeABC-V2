package com.frontend;

import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane; // Hoặc AnchorPane, VBox, HBox tùy theo FXML
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import com.backend.utils.JavaFXUtils;

public class NhapEmailUI {

    @FXML private TextField emailField;
    @FXML private Button btnGuiOtp;
    @FXML private Button btnQuayLaiNhapEmail, btnQuayLai;
    @FXML private Pane rootPaneNhapEmail; 

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private void handleGuiOtp() {
        String email = emailField.getText().trim();
        if (email.isEmpty() || !email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
            MessageUtils.showErrorMessage("Vui lòng nhập địa chỉ email hợp lệ.");
            return;
        }

        rootPaneNhapEmail.setDisable(true);
        if(btnGuiOtp != null) btnGuiOtp.setDisable(true);
        // if(btnQuayLaiNhapEmail != null) btnQuayLaiNhapEmail.setDisable(true);

        Task<Map<String, String>> task = new Task<>() {
            @Override
            protected Map<String, String> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/auth/forgot-password"))
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(Map.of("email", email))))
                        .header("Content-Type", "application/json")
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                // Kiểm tra status code trước khi parse JSON
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), new TypeReference<Map<String, String>>() {});
                } else {
                     // Nếu không phải 200, cố gắng parse lỗi từ server nếu có, hoặc ném lỗi chung
                    String responseBody = response.body();
                    String errorMessage = "Lỗi máy chủ: " + response.statusCode();
                    if (responseBody != null && !responseBody.isEmpty()) {
                        try {
                            Map<String, String> errorMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, String>>() {});
                            errorMessage = errorMap.getOrDefault("message", errorMessage + ". " + responseBody);
                        } catch (Exception parseEx) {
                            // Không parse được thì dùng body gốc
                        }
                    }
                    throw new IOException(errorMessage);
                }
            }
        };

        task.setOnSucceeded(e -> {
            rootPaneNhapEmail.setDisable(false);
            if(btnGuiOtp != null) btnGuiOtp.setDisable(false);
            // if(btnQuayLaiNhapEmail != null) btnQuayLaiNhapEmail.setDisable(false);

            Map<String, String> responseMap = task.getValue();
            MessageUtils.showInfoMessage(responseMap.getOrDefault("message", "Yêu cầu đã được xử lý. Vui lòng kiểm tra email."));
            
            openNhapOTPDialog(email);
            // Đóng dialog Nhập Email sau khi dialog Nhập OTP đã được yêu cầu mở
            closeDialog(); 
        });

        task.setOnFailed(e -> {
            rootPaneNhapEmail.setDisable(false);
            if(btnGuiOtp != null) btnGuiOtp.setDisable(false);
            // if(btnQuayLaiNhapEmail != null) btnQuayLaiNhapEmail.setDisable(false);
            MessageUtils.showErrorMessage("Lỗi: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void openNhapOTPDialog(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/nhapOTP.fxml"));
            Parent root = loader.load();

            NhapOTPUI controller = loader.getController();
            controller.setEmailDaNhap(email); // Truyền email sang dialog nhập OTP

            Stage stage = JavaFXUtils.createDialog("Nhập mã OTP", root, null);
            stage.show(); // Thay đổi từ showAndWait() sang show()
            // Không cần closeDialog() ở đây vì nó sẽ được gọi trong handleGuiOtp
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageUtils.showErrorMessage("Không thể mở form nhập OTP.");
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) rootPaneNhapEmail.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void quayLai(){
        closeDialog();
    }
}