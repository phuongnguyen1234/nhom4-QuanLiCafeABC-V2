package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.backend.dto.NhanVienDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.ImageUtils;
import com.backend.utils.MessageUtils;

import javafx.application.Platform; // Import để lấy CookieManager
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TrangChuUI {
    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Button btnThucDon, btnHoaDon, btnNhanVien, btnBangLuong, btnThongKe;

    @FXML
    private ImageView profileImage;

    @FXML
    private Text tenNguoiDungText;

    // HttpClient để gửi request đăng xuất
    private final HttpClient httpClient = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng tường minh CookieManager đã chia sẻ
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private NhanVienDTO currentUser; // Lưu thông tin người dùng hiện tại
    private Object currentCenterController; // Để lưu controller của view đang hiển thị ở center

    public NhanVienDTO getNhanVien(){
        return this.currentUser;
    }

    public TrangChuUI getTrangChuUI() {
        return this;
    }

    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    // Phương thức helper để tải FXML và quản lý lifecycle của controller
    private void loadCenterContent(String fxmlPath) {
        // Gọi shutdownExecutor của controller cũ nếu đó là ThucDonUI
        if (currentCenterController instanceof ThucDonUI) {
            ((ThucDonUI) currentCenterController).shutdownExecutor();
        }
        // Thêm các kiểm tra tương tự nếu các controller khác cũng có executor cần đóng
        // else if (currentCenterController instanceof AnotherControllerWithExecutor) {
        //     ((AnotherControllerWithExecutor) currentCenterController).shutdownExecutor();
        // }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            currentCenterController = loader.getController(); // Lưu controller mới

            // Cấu hình cho controller mới dựa trên loại của nó
            if (currentCenterController instanceof ThucDonUI) {
                ThucDonUI thucDonControllerInstance = (ThucDonUI) currentCenterController;
                thucDonControllerInstance.setTrangChuUI(this);
                if (currentUser != null) {
                    thucDonControllerInstance.setNhanVienDTO(currentUser);
                    boolean coQuyenQuanLiThucDon = "CHỦ_QUÁN".equalsIgnoreCase(currentUser.getViTri().toUpperCase().replace(" ", "_"));
                    thucDonControllerInstance.setQuyenQuanLiThucDon(coQuyenQuanLiThucDon);
                }
            } else if (currentCenterController instanceof HoaDonUI) {
                HoaDonUI hoaDonControllerInstance = (HoaDonUI) currentCenterController;
                if (currentUser != null) {
                    hoaDonControllerInstance.setCurrentUser(currentUser);
                }
            }
            // Thêm các cấu hình cho các loại controller khác nếu cần

            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: " + fxmlPath);
        }
    }

    @FXML
    public void thucDon() {
        loadCenterContent("/fxml/main_screen/thucDon.fxml");
    }


    @FXML
    private void hoaDon() {
        loadCenterContent("/fxml/main_screen/hoaDon.fxml");
    }

    @FXML
    private void nhanVien() {
        loadCenterContent("/fxml/main_screen/nhanVien.fxml");
    }

    @FXML
    private void bangLuong() {
        loadCenterContent("/fxml/main_screen/bangLuong.fxml");
    }


    @FXML
    private void thongKe() {
        loadCenterContent("/fxml/main_screen/thongKe.fxml");
    }

    // Phương thức này sẽ được gọi từ DangNhapUI sau khi đăng nhập thành công
    public void setCurrentUser(NhanVienDTO user) {
        this.currentUser = user;
        if (currentUser != null) {
            tenNguoiDungText.setText(currentUser.getTenNhanVien());
            // Cập nhật ảnh đại diện sử dụng ImageUtils
            String anhChanDungPath = currentUser.getAnhChanDung(); // Đây là đường dẫn resource
            String defaultAvatarPath = "/icons/profile.png"; // Ảnh mặc định nếu không có ảnh hoặc lỗi
            Image avatar = ImageUtils.loadFromResourcesOrDefault(anhChanDungPath, defaultAvatarPath);
            if (avatar != null) {
                profileImage.setImage(avatar);
            } else {
                 // Nếu cả ảnh chính và mặc định đều không tải được, có thể để trống hoặc một placeholder khác
                // Hoặc ImageUtils đã log lỗi, profileImage sẽ giữ ảnh cũ (nếu có) hoặc trống
            }
            thietLapQuyenTruyCap(currentUser.getViTri());
            // Load giao diện ThucDonUI làm giao diện mặc định sau khi người dùng đã được thiết lập
            thucDon(); // Gọi thucDon() ở đây đảm bảo currentUser đã có giá trị
            // Thiết lập xử lý sự kiện đóng cửa sổ
            Platform.runLater(() -> { // Đảm bảo chạy trên JavaFX Application Thread và sau khi scene được gắn vào window
                Stage currentStage = (Stage) mainBorderPane.getScene().getWindow();
                if (currentStage != null) {
                    currentStage.setOnCloseRequest(event -> {
                        event.consume(); // Ngăn cửa sổ đóng ngay lập tức để xử lý đăng xuất

                        if (mainBorderPane != null) {
                            mainBorderPane.setDisable(true); // Vô hiệu hóa UI
                        }

                        Task<Boolean> closeLogoutTask = new Task<>() {
                            @Override
                            protected Boolean call() throws Exception {
                                if (currentUser == null) {
                                    System.out.println("Không có người dùng hiện tại, bỏ qua gọi API logout khi đóng cửa sổ.");
                                    return true; // Coi như thành công để đóng cửa sổ
                                }
                                try {
                                    HttpRequest request = HttpRequest.newBuilder()
                                            .uri(URI.create("http://localhost:8080/auth/logout"))
                                            .POST(HttpRequest.BodyPublishers.noBody())
                                            .timeout(Duration.ofSeconds(10))
                                            .build();
                                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                                    return response.statusCode() == 200;
                                } catch (Exception e) {
                                    System.err.println("Lỗi khi gửi request đăng xuất khi đóng cửa sổ: " + e.getMessage());
                                    // Không ném lỗi ở đây để onFailed xử lý
                                    throw e;
                                }
                            }

                            @Override
                            protected void done() {
                                super.done();
                            }
                        };

                        closeLogoutTask.setOnSucceeded(e -> {
                            System.out.println(closeLogoutTask.getValue() ? "Đăng xuất tự động khi đóng cửa sổ thành công." : "Đăng xuất tự động khi đóng cửa sổ không thành công từ server (nếu có user).");
                            // Đóng executor của controller hiện tại nếu là ThucDonUI
                            if (currentCenterController instanceof ThucDonUI) {
                                ((ThucDonUI) currentCenterController).shutdownExecutor();
                            }
                            Platform.exit(); // Đóng ứng dụng JavaFX
                            System.exit(0);  // Đảm bảo tiến trình Java thoát hoàn toàn
                        });

                        closeLogoutTask.setOnFailed(e -> {
                            MessageUtils.showErrorMessage("Lỗi! Đăng xuất tự động khi đóng cửa sổ thất bại! Chi tiết: " + closeLogoutTask.getException().getMessage());
                            Platform.exit(); // Vẫn đóng ứng dụng JavaFX
                            System.exit(0);  // Đảm bảo tiến trình Java thoát hoàn toàn
                        });

                        new Thread(closeLogoutTask).start();
                    });
                }
            });
        }
    }

    // Method for setting user access rights (disable buttons based on role)
    @FXML
    public void thietLapQuyenTruyCap(String vaiTro) {
        if (vaiTro == null) return;
        String roleNormalized = vaiTro.toUpperCase().replace(" ", "_");

        if ("THU_NGÂN".equals(roleNormalized)) {
            // Disable các nút
            btnNhanVien.setDisable(true);
            btnBangLuong.setDisable(true);
            btnThongKe.setDisable(true);
        } else if ("CHỦ_QUÁN".equals(roleNormalized)) {
            // admin được phép truy cập tất cả
            btnNhanVien.setDisable(false);
            btnBangLuong.setDisable(false);
            btnThongKe.setDisable(false);
        } else {
            // Default: disable 
            btnNhanVien.setDisable(true);
            btnBangLuong.setDisable(true);
            btnThongKe.setDisable(true);
        }
    }

    @FXML
    private void dangXuat(ActionEvent event) {
        // Vô hiệu hóa UI tạm thời
        if (mainBorderPane != null) {
            mainBorderPane.setDisable(true);
        }

        Task<Boolean> logoutTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/auth/logout"))
                            .POST(HttpRequest.BodyPublishers.noBody()) // Logout thường là POST không cần body
                            .timeout(Duration.ofSeconds(10))
                            .build();
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    return response.statusCode() == 200;
                } catch (Exception e) {
                    System.err.println("Lỗi khi gửi request đăng xuất: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
        };

        logoutTask.setOnSucceeded(e -> {
            if (logoutTask.getValue()) {
                // Đăng xuất thành công, chuyển về màn hình đăng nhập
                try {
                    //  Xóa thông tin người dùng ở client
                    this.currentUser = null; 

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/dangNhap.fxml"));
                    Parent root = loader.load();
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    currentStage.setScene(new Scene(root));
                    currentStage.setTitle("Đăng nhập");
                    currentStage.show();
                    System.out.println("Đăng xuất thành công!");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    MessageUtils.showErrorMessage("Lỗi khi chuyển về màn hình đăng nhập.");
                }
            } else {
                MessageUtils.showErrorMessage("Đăng xuất không thành công từ server.");
            }
            if (mainBorderPane != null) mainBorderPane.setDisable(false); // Bật lại UI
        });

        logoutTask.setOnFailed(e -> {
            MessageUtils.showErrorMessage("Lỗi! Đăng xuất thất bại! Chi tiết: " + logoutTask.getException().getMessage());
            if (mainBorderPane != null) mainBorderPane.setDisable(false); // Bật lại UI
        });

        new Thread(logoutTask).start();
    }
 }
