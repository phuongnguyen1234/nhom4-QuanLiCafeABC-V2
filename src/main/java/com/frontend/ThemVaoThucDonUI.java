package com.frontend;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.MonDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.HttpUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.concurrent.Task;
import java.util.UUID;

public class ThemVaoThucDonUI {
    @FXML
    private TextField tenMonTextField, donGiaTextField;

    @FXML
    private ImageView anhMinhHoaImageView;

    @FXML
    private ComboBox<DanhMucKhongMonDTO> danhMucCombobox;

    @FXML
    private Button btnThemVaoThucDon, btnQuayLai;

    private MonDTO mon;

    private List<DanhMucKhongMonDTO> danhMucList;

    private File selectedImageFile; // Lưu trữ file ảnh đã chọn

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    private AnchorPane mainAnchorPane;

    private boolean dataChanged = false; // Biến để theo dõi thay đổi dữ liệu

    @FXML
    public void initialize() {
        try{
            // Cập nhật cách hiển thị tên danh mục trong ComboBox
            danhMucCombobox.setCellFactory(param -> new ListCell<DanhMucKhongMonDTO>() {
                @Override
                protected void updateItem(DanhMucKhongMonDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getTenDanhMuc());  // Hiển thị TenDanhMuc thay vì đối tượng
                }
            });

            danhMucCombobox.setButtonCell(new ListCell<DanhMucKhongMonDTO>() {
                @Override
                protected void updateItem(DanhMucKhongMonDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getTenDanhMuc());  // Cập nhật khi hiển thị trên button
                }
            });

            Task<List<DanhMucKhongMonDTO>> loadDanhMucTask = new Task<>() {
                @Override
                protected List<DanhMucKhongMonDTO> call() throws Exception {
                    danhMucList = HttpUtils.getListDanhMucKhongMon();
                    return danhMucList;
                }
            };

            // Disable toàn bộ dialog trong khi loading
            mainAnchorPane.setDisable(true);

            // Khi load thành công:
            loadDanhMucTask.setOnSucceeded(event -> {
                List<DanhMucKhongMonDTO> list = loadDanhMucTask.getValue();
                // loai bo cac danh muc co trang thai "Ngừng hoạt động"
                list.removeIf(danhMuc -> danhMuc.getTrangThai().equals("Ngừng hoạt động"));
                danhMucCombobox.getItems().setAll(list);
                mainAnchorPane.setDisable(false); // enable lại
            });

            // Nếu có lỗi:
            loadDanhMucTask.setOnFailed(event -> {
                Throwable ex = loadDanhMucTask.getException();
                ex.printStackTrace(); // hoặc hiện alert
                mainAnchorPane.setDisable(false); // vẫn enable lại để không bị kẹt
            });

            // Chạy task:
            new Thread(loadDanhMucTask).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void themVaoThucDon() {
        if (mon == null) {
            mon = new MonDTO();
        }

        int donGia = 0;

        // Kiểm tra đầu vào
        if (tenMonTextField.getText().isEmpty()) {
            MessageUtils.showErrorMessage("Tên món không được để trống!");
            return;
        }

        try {
            donGia = Integer.parseInt(donGiaTextField.getText());
            if (donGia <= 0) {
                MessageUtils.showErrorMessage("Đơn giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtils.showErrorMessage("Đơn giá phải là số hợp lệ!");
            return;
        }

        if (danhMucCombobox.getValue() == null) {
            MessageUtils.showErrorMessage("Vui lòng chọn danh mục!");
            return;
        }

        // Xử lý ảnh minh họa: copy file và lưu đường dẫn
        if (this.selectedImageFile == null) {
            MessageUtils.showErrorMessage("Vui lòng chọn ảnh minh họa!");
            return;
        }

        String originalFileName = selectedImageFile.getName();
        String fileExtension = "";
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
            fileExtension = originalFileName.substring(lastDotIndex); // e.g., .jpg, .png
        }
        // Tạo tên file duy nhất để tránh ghi đè
        String newImageName = UUID.randomUUID().toString() + fileExtension;

        Path targetDirectory = Paths.get("src/main/resources/images/mon");
        try {
            if (!Files.exists(targetDirectory)) {
                Files.createDirectories(targetDirectory);
            }
            Path targetPath = targetDirectory.resolve(newImageName);
            Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            mon.setAnhMinhHoa("/images/mon/" + newImageName); // Lưu đường dẫn tương đối cho resource loading
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtils.showErrorMessage("Lỗi khi lưu ảnh minh họa! " + e.getMessage());
                return;
        }

        mon.setMaMon(null);
        mon.setTenMon(tenMonTextField.getText());
        mon.setDonGia(donGia);
        mon.setTrangThai("Bán");
        mon.setMaDanhMuc(danhMucCombobox.getValue().getMaDanhMuc());

        // Disable form khi bắt đầu xử lý
        mainAnchorPane.setDisable(true);
        btnThemVaoThucDon.setDisable(true);
        btnQuayLai.setDisable(true);

        Task<Void> requestTask = createRequest(mon);

        requestTask.setOnSucceeded(event -> {
            MessageUtils.showInfoMessage("Thêm món thành công!");
            this.dataChanged = true; // Đánh dấu dữ liệu đã thay đổi
            btnThemVaoThucDon.setDisable(false);
            btnQuayLai.setDisable(false);
            tenMonTextField.getScene().getWindow().hide();
        });

        requestTask.setOnFailed(event -> {
            Throwable ex = requestTask.getException();
            ex.printStackTrace();
            MessageUtils.showErrorMessage("Lỗi khi thêm món: " + ex.getMessage());
            mainAnchorPane.setDisable(false); // Enable lại nếu lỗi
            btnThemVaoThucDon.setDisable(false);
            btnQuayLai.setDisable(false);
        });

        requestTask.setOnCancelled(e -> {
                btnThemVaoThucDon.setDisable(false);
                btnQuayLai.setDisable(false);
                mainAnchorPane.setDisable(false); // Enable lại nếu bị hủy
            });

        new Thread(requestTask).start();
    }

    @FXML
    public void quayLai(){
        tenMonTextField.getScene().getWindow().hide();
    }


    @FXML
    private void chonHinhAnh() {
        // Tạo đối tượng FileChooser
        FileChooser fileChooser = new FileChooser();

        // Cấu hình bộ lọc chỉ cho phép chọn các file ảnh
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        // Hiển thị hộp thoại chọn file
        File selectedFile = fileChooser.showOpenDialog(tenMonTextField.getScene().getWindow());

        if (selectedFile != null) {
            try {
                this.selectedImageFile = selectedFile; // Lưu file đã chọn
                // Đọc file ảnh đã chọn và chuyển thành đối tượng Image
                Image selectedImage = new Image(selectedFile.toURI().toString());

                // Hiển thị ảnh trong ImageView
                anhMinhHoaImageView.setImage(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
                this.selectedImageFile = null; // Reset nếu có lỗi
                // Thông báo lỗi nếu không thể tải ảnh
                MessageUtils.showErrorMessage("Không thể tải ảnh: " + e.getMessage());
            }
        }
    }

    private Task<Void> createRequest(MonDTO mon) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                String jsonPayload = objectMapper.writeValueAsString(mon);
                // System.out.println("JSON gửi đi để tạo món: " + jsonPayload); // Debug
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/mon"))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 300) { // Check for any error status code (3xx, 4xx, 5xx)
                    String errorMessage = response.body(); 
                    // Nếu body rỗng hoặc null, cung cấp một thông báo mặc định
                    if (errorMessage == null || errorMessage.trim().isEmpty()) {
                        errorMessage = "Lỗi không xác định từ máy chủ. Mã trạng thái: " + response.statusCode();
                    }
                    throw new IOException(errorMessage);
                }
                return null;
            }
        };
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

}
