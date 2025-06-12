package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.time.Duration; // Thêm import cho Duration

import com.backend.dto.NhanVienDTO; // Thêm import NhanVienDTO
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.MessageUtils;
import com.backend.quanlicapheabc.QuanlicapheabcApplication; // Import để lấy CookieManager
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class DangNhapUI {
    @FXML
    private TextField emailTextField, matKhauTextField;

    @FXML
    private PasswordField matKhauPWField;

    @FXML
    private Hyperlink quenMatKhauHyperlink, xemMKHyperlink;

    private boolean isPasswordVisible = false;
    private Image viewIcon;
    private Image hideIcon;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Đăng ký JavaTimeModule
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // Cấu hình HttpClient với timeout
    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng tường minh CookieManager đã chia sẻ
            .connectTimeout(Duration.ofSeconds(5)) // Timeout kết nối 5 giây
            .build();

    @FXML
    public void initialize() {
       // Ẩn TextField mật khẩu ban đầu
        matKhauTextField.setVisible(false);
        matKhauTextField.setManaged(false); // Không quản lý layout khi ẩn

        // Load icons
        try {
            viewIcon = new Image(getClass().getResourceAsStream("/icons/view.png"));
            hideIcon = new Image(getClass().getResourceAsStream("/icons/hide.png"));
        } catch (Exception e) {
            System.err.println("Không thể tải icon xem/ẩn mật khẩu: " + e.getMessage());
            // Có thể đặt icon mặc định hoặc xử lý lỗi khác ở đây
        }
        // Set icon ban đầu cho hyperlink
        if (viewIcon != null) {
            ((ImageView) xemMKHyperlink.getGraphic()).setImage(viewIcon);
        }
    }
    
    @FXML
    private void quenMatKhau() {
        // MessageUtils.showInfoMessage("Chức năng quên mật khẩu hiện chưa được hỗ trợ.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/nhapEmail.fxml")); // Đường dẫn tới FXML của bạn
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Quên Mật Khẩu");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            // stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/forgot_password_icon.png"))); // Thêm icon nếu có
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtils.showErrorMessage("Không thể mở form quên mật khẩu.");
        }

    }

    @FXML
    private void xemMatKhau(ActionEvent event) {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Hiển thị mật khẩu
            matKhauTextField.setText(matKhauPWField.getText());
            matKhauTextField.setVisible(true);
            matKhauTextField.setManaged(true);
            matKhauPWField.setVisible(false);
            matKhauPWField.setManaged(false);
            if (hideIcon != null) {
                ((ImageView) xemMKHyperlink.getGraphic()).setImage(hideIcon);
            }
        } else {
            // Ẩn mật khẩu
            matKhauPWField.setText(matKhauTextField.getText());
            matKhauPWField.setVisible(true);
            matKhauPWField.setManaged(true);
            matKhauTextField.setVisible(false);
            matKhauTextField.setManaged(false);
            if (viewIcon != null) {
                ((ImageView) xemMKHyperlink.getGraphic()).setImage(viewIcon);
            }
        }
    }

    @FXML
    private void dangNhap() {

        String email = emailTextField.getText().trim();
        //String matKhau = matKhauPWField.getText().trim();

        // Lấy mật khẩu từ trường đang hiển thị
        String matKhau = isPasswordVisible ? matKhauTextField.getText() : matKhauPWField.getText();


        if (email.isEmpty() || matKhau.isEmpty()) {
            MessageUtils.showErrorMessage("Vui lòng nhập đầy đủ email và mật khẩu!");
            return;
        }

       // Vô hiệu hóa UI tạm thời
        if (emailTextField.getScene() != null && emailTextField.getScene().getRoot() instanceof Pane) {
            ((Pane) emailTextField.getScene().getRoot()).setDisable(true);
        }

       Task<NhanVienDTO> task = sendRequest(email, matKhau);

        task.setOnSucceeded(event -> {
            // Bật lại UI
            if (emailTextField.getScene() != null && emailTextField.getScene().getRoot() instanceof Pane) {
                ((Pane) emailTextField.getScene().getRoot()).setDisable(false);
            }
            NhanVienDTO nhanVien = task.getValue();
            try {
              if (nhanVien != null && nhanVien.getViTri() != null) {
                    System.out.println("Đăng nhập thành công: " + nhanVien.getTenNhanVien());
                    System.out.println("Vai trò: " + nhanVien.getViTri());


                    String role = nhanVien.getViTri().toUpperCase().replace(" ", "_");
                    if ("CHỦ_QUÁN".equals(role)) {
                        // Truyền NhanVienDTO sang TrangChuUI
                        FXMLLoader loader = chuyenManHinh("/fxml/main_screen/trangChu.fxml", "Quản lí cà phê ABC - Chủ quán");
                        TrangChuUI trangChuController = loader.getController();
                        trangChuController.setCurrentUser(nhanVien);
                    } else if ("THU_NGÂN".equals(role)) {
                        // Truyền NhanVienDTO sang TrangChuUI
                        FXMLLoader loader = chuyenManHinh("/fxml/main_screen/trangChu.fxml", "Quản lí cà phê ABC - Thu ngân");
                        TrangChuUI trangChuController = loader.getController();
                        trangChuController.setCurrentUser(nhanVien);
                    } else {
                        MessageUtils.showErrorMessage("Lỗi! Vai trò không được hỗ trợ: " + nhanVien.getViTri() + "Vui lòng liên hệ quản trị viên.");
                    }
                } else {
                     MessageUtils.showErrorMessage("Lỗi! Không nhận được thông tin người dùng hợp lệ.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.showErrorMessage("Lỗi! Không thể chuyển màn hình: " + e.getMessage());
            }
        });

        task.setOnFailed(event -> {
            if (emailTextField.getScene() != null && emailTextField.getScene().getRoot() instanceof Pane) {
                ((Pane) emailTextField.getScene().getRoot()).setDisable(false);
            }
            Throwable e = task.getException();
            String errorMessage = "Lỗi! Đăng nhập thất bại!";
            if (e != null && e.getMessage() != null) {
                 // Kiểm tra xem thông báo lỗi có chứa thông báo từ SessionAuthenticationException không
                 if (e.getMessage().contains("Tài khoản đã được đăng nhập ở nơi khác!")) {
                     errorMessage = "Tài khoản đã được đăng nhập ở nơi khác!";
                 } else {
                     errorMessage += " Chi tiết: " + e.getMessage();
                 }
            }
            MessageUtils.showErrorMessage(errorMessage);
        });

         // Quan trọng: Start Task trên một Thread mới để không block UI
        new Thread(task).start();
    }


    private Task<NhanVienDTO> sendRequest(String email, String matKhau) {
        return new Task<NhanVienDTO>() {
            @Override
            protected NhanVienDTO call() throws Exception {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/auth/login")) // Đảm bảo URL này đúng với cấu hình SecurityConfig
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\", \"matKhau\":\"" + matKhau + "\"}"))
                            .timeout(Duration.ofSeconds(15)) // Timeout cho request này là 15 giây
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String responseBody = response.body();

                    if (response.statusCode() != 200) {
                        String errorMessage = "Lỗi khi đăng nhập. Mã trạng thái: " + response.statusCode();
                        if (responseBody != null && !responseBody.isEmpty()) {
                            try {
                                // Sử dụng TypeReference để parse Map<String, String>
                                // Lấy thông báo lỗi từ trường 'message' trong JSON
                                Map<String, String> errorMap = objectMapper.readValue(responseBody, Map.class);
                                errorMessage = errorMap.getOrDefault("message", errorMessage + ". Phản hồi: " + responseBody);
                            } catch (Exception parseEx) {
                                errorMessage += ". Phản hồi: " + responseBody;
                            }
                        }
                        throw new RuntimeException(errorMessage);
                    }
                    return objectMapper.readValue(responseBody, NhanVienDTO.class);
                } catch (Exception e) {
                    // Log lỗi ở đây để dễ dàng debug hơn nếu có lỗi không mong muốn
                    System.err.println("Lỗi trong quá trình gửi request đăng nhập: " + e.getMessage());
                    e.printStackTrace(); // In stack trace để xem chi tiết lỗi
                    throw e; // Ném lại exception để Task chuyển sang trạng thái FAILED
                }
            }
        };
    }
    

    // Sửa phương thức này để trả về FXMLLoader
    public FXMLLoader chuyenManHinh(String duongDanFXML, String tieuDe) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(duongDanFXML));
            Parent root = loader.load();
            Stage stage = (Stage) emailTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(tieuDe);
            stage.show();
            return loader; // Trả về loader
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Lỗi khi chuyển màn hình! Đường dẫn FXML không hợp lệ: " + duongDanFXML);
        }
    }

   
}

