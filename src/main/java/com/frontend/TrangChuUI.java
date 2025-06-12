package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.backend.dto.NhanVienDTO;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
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
import com.fasterxml.jackson.core.type.TypeReference; // Thêm import
import com.fasterxml.jackson.databind.ObjectMapper; // Thêm import

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

    // Cache dữ liệu thực đơn
    private List<DanhMuc> cachedAllDanhMucWithItems = null;
    private final Map<String, Image> cachedMonImageCache = new HashMap<>();
    private final ObjectMapper objectMapperTrangChu = new ObjectMapper(); // ObjectMapper riêng cho TrangChuUI nếu cần

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            currentCenterController = loader.getController(); // Lưu controller mới

            // Cấu hình cho controller mới dựa trên loại của nó
            // Việc truyền dữ liệu cho ThucDonUI sẽ được xử lý trong phương thức thucDon()
            // Các controller khác vẫn cấu hình như cũ
            if (currentCenterController instanceof HoaDonUI) {
                HoaDonUI hoaDonControllerInstance = (HoaDonUI) currentCenterController;
                if (currentUser != null) {
                    hoaDonControllerInstance.setCurrentUser(currentUser);
                }
            }
            // Thêm các cấu hình cho các loại controller khác nếu cần
            else if (currentCenterController instanceof ThongKeUI) {
                ThongKeUI thongKeControllerInstance = (ThongKeUI) currentCenterController;
                thongKeControllerInstance.setTrangChuUI(this);
            }

            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: " + fxmlPath);
        }
    }

    @FXML
    public void thucDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/thucDon.fxml"));
            Node content = loader.load();
            ThucDonUI thucDonController = loader.getController();
            
            setupThucDonController(thucDonController); // Cấu hình cơ bản (truyền user, set quyền)
            mainBorderPane.setCenter(content);
            currentCenterController = thucDonController; // Cập nhật controller hiện tại

            // Task để tải/làm mới dữ liệu thực đơn và ảnh
            Task<Void> dataLoadTask = new Task<>() {
                List<DanhMuc> fetchedDataTask; // Sử dụng biến cục bộ cho task
                Map<String, Image> imageCacheForThisLoadTask = new HashMap<>(); // Cache ảnh cho lần tải/refresh này

                @Override
                protected Void call() throws Exception {
                    if (cachedAllDanhMucWithItems == null) { // Lần đầu tải
                        fetchedDataTask = layDanhSachDanhMucFromServer();
                        // Tải ảnh mặc định
                        Image defaultImg = ImageUtils.loadFromResourcesOrDefault(null, ThucDonUI.DEFAULT_MON_IMAGE_PATH_STATIC);
                        if (defaultImg != null) {
                            imageCacheForThisLoadTask.put(ThucDonUI.DEFAULT_MON_IMAGE_PATH_STATIC, defaultImg);
                        }
                        // Thu thập và tải các ảnh khác
                        Set<String> imagePathsToLoad = new HashSet<>();
                        for (DanhMuc dm : fetchedDataTask) {
                            if (dm.getMonList() != null) {
                                for (Mon mon : dm.getMonList()) {
                                    String imagePath = mon.getAnhMinhHoa();
                                    if (imagePath != null && !imagePath.isEmpty() && !imageCacheForThisLoadTask.containsKey(imagePath)) {
                                        imagePathsToLoad.add(imagePath);
                                    }
                                }
                            }
                        }
                        Map<String, Image> newlyLoadedImages = loadImagesInParallel(imagePathsToLoad, ThucDonUI.DEFAULT_MON_IMAGE_PATH_STATIC);
                        imageCacheForThisLoadTask.putAll(newlyLoadedImages);
                    } else { // Làm mới dữ liệu
                        fetchedDataTask = layDanhSachDanhMucFromServer(); // Lấy dữ liệu mới nhất
                        
                        // Đảm bảo ảnh mặc định có trong imageCacheForThisLoadTask nếu nó chưa có trong cache chính
                        if (!cachedMonImageCache.containsKey(ThucDonUI.DEFAULT_MON_IMAGE_PATH_STATIC) &&
                            !imageCacheForThisLoadTask.containsKey(ThucDonUI.DEFAULT_MON_IMAGE_PATH_STATIC)) {
                            Image defaultImg = ImageUtils.loadFromResourcesOrDefault(null, ThucDonUI.DEFAULT_MON_IMAGE_PATH_STATIC);
                            if (defaultImg != null) {
                                imageCacheForThisLoadTask.put(ThucDonUI.DEFAULT_MON_IMAGE_PATH_STATIC, defaultImg);
                            }
                        }
                        
                        // Thu thập các ảnh mới hoặc thay đổi cần tải
                        Set<String> imagePathsToLoad = new HashSet<>();
                        for (DanhMuc dm : fetchedDataTask) {
                            if (dm.getMonList() != null) {
                                for (Mon mon : dm.getMonList()) {
                                    String imagePath = mon.getAnhMinhHoa();
                                    if (imagePath != null && !imagePath.isEmpty() && 
                                        !cachedMonImageCache.containsKey(imagePath) && 
                                        !imageCacheForThisLoadTask.containsKey(imagePath)) {
                                        imagePathsToLoad.add(imagePath);
                                    }
                                }
                            }
                        }
                        Map<String, Image> newlyLoadedImages = loadImagesInParallel(imagePathsToLoad, ThucDonUI.DEFAULT_MON_IMAGE_PATH_STATIC);
                        imageCacheForThisLoadTask.putAll(newlyLoadedImages);
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    if (cachedAllDanhMucWithItems == null) { // Lần đầu
                        cachedAllDanhMucWithItems = new ArrayList<>(fetchedDataTask);
                        cachedMonImageCache.clear(); 
                        cachedMonImageCache.putAll(imageCacheForThisLoadTask); 
                    } else { // Làm mới
                        cachedAllDanhMucWithItems = new ArrayList<>(fetchedDataTask); 
                        cachedMonImageCache.putAll(imageCacheForThisLoadTask); // Thêm các ảnh mới/thay đổi vào cache chính
                        
                        // Tùy chọn: Dọn dẹp cachedMonImageCache - loại bỏ ảnh không còn được tham chiếu
                        // (logic dọn dẹp có thể thêm ở đây nếu cần)
                    }
                    // Gọi populateThucDon trên controller của ThucDonUI đã được hiển thị
                    thucDonController.populateThucDon(new ArrayList<>(cachedAllDanhMucWithItems), new HashMap<>(cachedMonImageCache));
                }

                @Override
                protected void failed() {
                    getException().printStackTrace();
                    // Yêu cầu ThucDonUI hiển thị lỗi bên trong nó
                    //thucDonController.showError("Lỗi tải dữ liệu thực đơn! Vui lòng thử lại.");
                }
            };
            new Thread(dataLoadTask).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phương thức phụ trợ để tải ảnh song song
    private Map<String, Image> loadImagesInParallel(Set<String> imagePaths, String defaultImagePathForFallback) {
        Map<String, Image> loadedImages = new HashMap<>();
        if (imagePaths == null || imagePaths.isEmpty()) {
            return loadedImages;
        }

        int coreCount = Runtime.getRuntime().availableProcessors();
        int poolSize = Math.min(imagePaths.size(), Math.max(1, coreCount));
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<Future<Map.Entry<String, Image>>> futures = new ArrayList<>();

        for (String path : imagePaths) {
            Future<Map.Entry<String, Image>> future = executor.submit(() -> {
                Image loadedImage = ImageUtils.loadFromResourcesOrDefault(path, defaultImagePathForFallback);
                return loadedImage != null ? Map.entry(path, loadedImage) : null;
            });
            futures.add(future);
        }

        for (Future<Map.Entry<String, Image>> future : futures) {
            try {
                Map.Entry<String, Image> entry = future.get(); // Chờ và lấy kết quả
                if (entry != null) {
                    loadedImages.put(entry.getKey(), entry.getValue());
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Lỗi khi tải ảnh song song cho một đường dẫn (trong helper): " + e.getMessage());
            }
        }
        executor.shutdown();
        return loadedImages;
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
    public void thongKe() {
        loadCenterContent("/fxml/main_screen/thongKe.fxml");
    }

    // Helper để cấu hình ThucDonUI
    private void setupThucDonController(ThucDonUI controller) {
        controller.setTrangChuUI(this);
        if (currentUser != null) {
            controller.setNhanVienDTO(currentUser);
            boolean coQuyenQuanLiThucDon = "CHỦ_QUÁN".equalsIgnoreCase(currentUser.getViTri().toUpperCase().replace(" ", "_"));
            controller.setQuyenQuanLiThucDon(coQuyenQuanLiThucDon);
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

    // Phương thức tải danh mục từ server (tương tự như trong ThucDonUI cũ)
    private List<DanhMuc> layDanhSachDanhMucFromServer() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/danh-muc/all"))
                .GET()
                .timeout(Duration.ofSeconds(15)) // Có thể tăng timeout nếu cần
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            // Sử dụng objectMapperTrangChu đã khai báo ở class level
            return objectMapperTrangChu.readValue(response.body(), new TypeReference<List<DanhMuc>>() {});
        } else {
            throw new IOException("HTTP Error: " + response.statusCode() + " - " + response.body());
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
