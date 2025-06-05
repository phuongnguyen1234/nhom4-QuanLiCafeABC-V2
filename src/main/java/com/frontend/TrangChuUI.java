package com.frontend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.backend.dto.NhanVienDTO;
import com.backend.model.NhanVien;
import com.backend.utils.ImageUtils;
import com.backend.quanlicapheabc.QuanlicapheabcApplication; // Import để lấy CookieManager
import com.backend.utils.MessageUtils;

import javafx.application.Platform;
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

    //khoi tao 1 controller cho cac chuc nang
    private ThucDonUI thucDonController = new ThucDonUI();
    private QuanLiThucDonUI quanLiThucDonController = new QuanLiThucDonUI();
    private QuanLiDanhMucUI quanLiDanhMucController = new QuanLiDanhMucUI();
    private HoaDonUI hoaDonController = new HoaDonUI();
    private NhanVienUI nhanVienController = new NhanVienUI();
    private BangLuongUI bangLuongController = new BangLuongUI();
    private ThongKeUI thongKeUI = new ThongKeUI();

    // HttpClient để gửi request đăng xuất
    private final HttpClient httpClient = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng tường minh CookieManager đã chia sẻ
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private NhanVienDTO currentUser; // Lưu thông tin người dùng hiện tại

    private NhanVien nhanVien;

    public TrangChuUI getTrangChuUI() {
        return this;
    }

    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    @FXML
    public void initialize() {
       /*  try {
            // Khai báo và lấy thông tin nhân viên (chỉ gọi 1 lần)
            
            nhanVien = SessionManager.getNhanVienByCurrentSession();
            if (nhanVien != null) {
                // Cập nhật tên người dùng
                tenNguoiDungText.setText(nhanVien.getTenNhanVien());

                // Cập nhật ảnh đại diện (nếu có)
                byte[] anhChanDung = nhanVien.getAnhChanDung();
                if (anhChanDung != null && anhChanDung.length > 0) {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(anhChanDung);
                    profileImage.setImage(new Image(byteArrayInputStream));
                } else {
                    profileImage.setImage(new Image("/icons/profile.png"));
                }

                // Thiết lập quyền truy cập
                if (nhanVien.getViTri().equalsIgnoreCase("Chủ quán")) {
                    btnNhanVien.setDisable(true);
                    btnBangLuong.setDisable(true);
                    btnThongKe.setDisable(true);
                } else if (nhanVien.getViTri().equalsIgnoreCase("Thu ngân")) {
                    btnNhanVien.setDisable(false);
                    btnBangLuong.setDisable(false);
                    btnThongKe.setDisable(false);
                }

                // Đảm bảo mainBorderPane đã được khởi tạo
                Platform.runLater(() -> {
                    if (mainBorderPane != null) {
                        Stage currentStage = (Stage) mainBorderPane.getScene().getWindow();
                        currentStage.setOnCloseRequest(event -> {
                            try {
                                String currentSessionId = SessionManager.getCurrentSessionId();
                                if(currentSessionId != null){
                                    NhanVien nhanVienHienTai = SessionManager.getNhanVienByCurrentSession();
                                    if(nhanVien!=null){
                                        //NhanVienController.capNhatTrangThaiHoatDong(nhanVien.getEmail(), "0");
                                    SessionManager.removeSession(SessionManager.getCurrentSessionId());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Platform.exit();
                        });
                    }
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi khởi tạo giao diện: " + e.getMessage());
        } */
        thucDon();

    }

    @FXML
    public void thucDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/thucDon.fxml"));
            Node content = loader.load();

            // Lấy controller từ FXML đã tải
            ThucDonUI screenController = loader.getController();
            // Truyền đối tượng TaoDonGoiDoMoiController đã khởi tạo từ trước
            //screenController.setThucDonUI(taoDonController);
            //screenController.setCapNhatThucDonController(capNhatThucDonController);
            
            screenController.setTrangChuUI(this);
            //screenController.setNhanVien(nhanVien);

            // Hiển thị danh sách đồ uống trong đơn hàng
            screenController.hienThiDanhSachMonTrongDon();

            // Hiển thị thực đơn
            //screenController.hienThiThucDon(taoDonController.layThucDon("all"));
            //screenController.datLai();

            // Đặt nội dung vào phần center của BorderPane
            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/thucDon.fxml");
        }
    }


    @FXML
    private void hoaDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/hoaDon.fxml"));
            Node content = loader.load();
            
            // Lấy controller từ FXML đã tải
            HoaDonUI screenController = loader.getController();

            //screenController.setDonHangController(donHangController);
            //screenController.setNhanVien(nhanVien);
            //screenController.hienThiDanhSachDonHang(donHangController.loadDonHangFromDatabase());
            // Đặt nội dung vào phần center của BorderPane
            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/hoaDon.fxml");
        }
    }

    @FXML
    private void nhanVien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/nhanVien.fxml"));
            Node content = loader.load();

            NhanVienUI screenController = loader.getController();

            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/nhanVien.fxml");
        }
    }

    @FXML
    private void bangLuong() {
        // Tải nội dung FXML và lấy controller của nó
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/bangLuong.fxml"));
            Node content = loader.load();
            
            // Lấy controller từ FXML đã tải
            BangLuongUI screenController = loader.getController();

            //screenController.setTaoLuongController(taoLuongController);
            //screenController.hienThiDanhSachBangLuong(taoLuongController.layDanhSachBangLuongThangNay());
            // Đặt nội dung vào phần center của BorderPane
            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/bangLuong.fxml");
        }
    }


    @FXML
    private void thongKe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/thongKe.fxml"));
            Node content = loader.load();
            
            // Lấy controller từ FXML đã tải
            ThongKeUI screenController = loader.getController();

            //screenController.setPhanTichHoatDongController(phanTichHoatDongController);
            //screenController.hienThiSoLieuThongKe();
            // Đặt nội dung vào phần center của BorderPane
            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/dashboard.fxml");
        }
    }

    // Method to load center content based on action
    private void loadCenterContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            mainBorderPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        };

                        closeLogoutTask.setOnSucceeded(e -> {
                            System.out.println(closeLogoutTask.getValue() ? "Đăng xuất tự động khi đóng cửa sổ thành công." : "Đăng xuất tự động khi đóng cửa sổ không thành công từ server (nếu có user).");
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
