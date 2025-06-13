package com.frontend;

import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
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

public class DatLaiMatKhauUI {

    @FXML private PasswordField mkMoiPWField; // Khớp FXML
    @FXML private TextField hienMKMoiTextField; // Khớp FXML
    @FXML private Hyperlink xemMKMoiHyperlink; // Khớp FXML

    @FXML private PasswordField xacNhanMKMoiPWField; // Khớp FXML
    @FXML private TextField xacNhanMKMoiTextField; // Khớp FXML
    @FXML private Hyperlink xemXacNhanMKMoiHyperlink; // Khớp FXML

    @FXML private Button btnDatLaiMatKhau; // Khớp FXML
    // @FXML private Button btnQuayLaiDatLai; // FXML không có nút này, có thể thêm
    @FXML private Pane rootPaneDatLai; // Giả sử AnchorPane là rootPaneDatLai

    private String emailNhanDuoc;
    private String otpNhanDuoc;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean isMkMoiVisible = false;
    private boolean isXacNhanMkMoiVisible = false;

    public void setDataFromOtpScreen(String email, String otp) {
        this.emailNhanDuoc = email;
        this.otpNhanDuoc = otp;
    }

    @FXML
    public void initialize() {
        // Ẩn TextField mật khẩu ban đầu
        hienMKMoiTextField.setVisible(false);
        hienMKMoiTextField.setManaged(false);
        xacNhanMKMoiTextField.setVisible(false);
        xacNhanMKMoiTextField.setManaged(false);

        // Đảm bảo FXML đã thiết lập icon ban đầu (view.png) cho các Hyperlink
        // Ví dụ: <Image url="@/icons/view.png" /> bên trong ImageView của Hyperlink
    }

    @FXML
    private void datLaiMatKhau() { // Khớp onAction FXML
        String newPassword = isMkMoiVisible ? hienMKMoiTextField.getText() : mkMoiPWField.getText();
        String confirmPassword = isXacNhanMkMoiVisible ? xacNhanMKMoiTextField.getText() : xacNhanMKMoiPWField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            MessageUtils.showErrorMessage("Mật khẩu mới và xác nhận mật khẩu không được để trống.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            MessageUtils.showErrorMessage("Mật khẩu mới và xác nhận mật khẩu không khớp.");
            return;
        }

        rootPaneDatLai.setDisable(true);
        if(btnDatLaiMatKhau != null) btnDatLaiMatKhau.setDisable(true);
        // if(btnQuayLaiDatLai != null) btnQuayLaiDatLai.setDisable(true);

        Task<Map<String, String>> task = new Task<>() {
            @Override
            protected Map<String, String> call() throws Exception {
                Map<String, String> payload = Map.of(
                        "email", emailNhanDuoc,
                        "otp", otpNhanDuoc,
                        "newPassword", newPassword,
                        "confirmPassword", confirmPassword // Backend cũng sẽ kiểm tra lại confirmPassword
                );
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/auth/reset-password"))
                        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                        .header("Content-Type", "application/json")
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                // Kiểm tra status code trước khi parse JSON
                if (response.statusCode() == 200) { // Thành công
                    return objectMapper.readValue(response.body(), new TypeReference<Map<String, String>>() {});
                } else { // Lỗi từ server (ví dụ: 400 Bad Request cho OTP sai/hết hạn)
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
                    throw new IOException(errorMessage); // Ném lỗi để onFailed xử lý
                }
            }
        };

        task.setOnSucceeded(e -> {
            rootPaneDatLai.setDisable(false);
            if(btnDatLaiMatKhau != null) btnDatLaiMatKhau.setDisable(false);
            // if(btnQuayLaiDatLai != null) btnQuayLaiDatLai.setDisable(false);

            Map<String, String> responseMap = task.getValue();
            MessageUtils.showInfoMessage(responseMap.getOrDefault("message", "Đặt lại mật khẩu thành công!"));
            closeDialog(); 
            // Có thể thêm logic để đóng tất cả các dialog quên mật khẩu và quay về màn hình đăng nhập chính
        });

        task.setOnFailed(e -> {
            rootPaneDatLai.setDisable(false);
            if(btnDatLaiMatKhau != null) btnDatLaiMatKhau.setDisable(false);
            // if(btnQuayLaiDatLai != null) btnQuayLaiDatLai.setDisable(false);
            MessageUtils.showErrorMessage("Lỗi: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    private void xemMatKhauMoi() {
        isMkMoiVisible = JavaFXUtils.togglePasswordVisibility(isMkMoiVisible, hienMKMoiTextField, mkMoiPWField, xemMKMoiHyperlink);
    }

    @FXML
    private void xemXacNhanMatKhauMoi() {
        isXacNhanMkMoiVisible = JavaFXUtils.togglePasswordVisibility(isXacNhanMkMoiVisible, xacNhanMKMoiTextField, xacNhanMKMoiPWField, xemXacNhanMKMoiHyperlink);
    }

    private void closeDialog() {
        Stage stage = (Stage) rootPaneDatLai.getScene().getWindow();
        stage.close();
    }
}
