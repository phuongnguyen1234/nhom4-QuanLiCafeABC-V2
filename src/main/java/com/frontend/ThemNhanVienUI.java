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
import java.time.LocalDate;
import java.util.UUID;

import com.backend.dto.NhanVienDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication; // Import để lấy CookieManager
import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ThemNhanVienUI {
    @FXML
    private TextField tenNhanVienTextField, matKhauTextField, queQuanTextField, soDienThoaiTextField, mucLuongTextField, emailTextField;

    @FXML
    private HBox gioiTinhHBox;    
    @FXML
    private RadioButton namRadioButton, nuRadioButton;

    @FXML
    private DatePicker ngaySinhDatePicker, thoiGianVaoLamDatePicker;

    @FXML
    private TextArea diaChiTextArea;

    @FXML
    private ComboBox<String> loaiNhanVienComboBox, viTriComboBox;

    @FXML
    private PasswordField matKhauPasswordField;

    @FXML
    private ImageView anhChanDungImageView;
    
    @FXML
    private Button btnChonAnh, btnThem, btnQuayLai;

    @FXML
    private Label mucLuongLabel; // Thêm FXML cho mucLuongLabel

    @FXML
    private Hyperlink xemMKHyperlink;

    private boolean isPasswordVisible = false;

    private NhanVienUI nhanVienUI;
    private File selectedImageFile; // Lưu trữ file ảnh đã chọn
    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private ToggleGroup gioiTinhToggleGroup;

    private boolean dataChanged = false; // Biến để theo dõi thay đổi dữ liệu
    public boolean isDataChanged() {
        return dataChanged;
    }

    // Phương thức để nhận tham chiếu từ màn hình chính
    public void setNhanVienUI(NhanVienUI nhanVienUI) {
        this.nhanVienUI = nhanVienUI;
    }

    @FXML
    public void initialize() {
    // Ẩn TextField mật khẩu ban đầu
        matKhauTextField.setVisible(false);
        matKhauTextField.setManaged(false); // Không quản lý layout khi ẩn
            // Đảm bảo FXML đã thiết lập icon ban đầu (view.png) cho xemMKHyperlink
            // Ví dụ: <Image url="@/icons/view.png" /> bên trong ImageView của Hyperlink

        gioiTinhToggleGroup = new ToggleGroup();
        namRadioButton.setToggleGroup(gioiTinhToggleGroup);
        nuRadioButton.setToggleGroup(gioiTinhToggleGroup);
        // Khởi tạo các giá trị cho ComboBox
        loaiNhanVienComboBox.getItems().addAll("Chủ quán", "Full-time", "Part-time");
        viTriComboBox.getItems().addAll("Chủ quán", "Thu ngân", "Pha chế", "Phục vụ");

        // Đặt các giá trị mặc định nếu cần thiết
        ngaySinhDatePicker.setValue(LocalDate.now());
        thoiGianVaoLamDatePicker.setValue(LocalDate.now());

        addComboBoxEvents(); // Thêm sự kiện cho ComboBox
    }

    
  //Thêm sự kiện cho ComboBox.
    private void addComboBoxEvents() {
        loaiNhanVienComboBox.setOnAction(event -> updateValuesFromLoaiNhanVien());
        viTriComboBox.setOnAction(event -> updateValuesFromViTri());
    }

    private void updateValuesFromLoaiNhanVien() {
        String loaiNhanVien = loaiNhanVienComboBox.getValue();
        updateComboBoxesSafely(() -> {
            switch (loaiNhanVien) {
                case "Chủ quán" -> {
                    viTriComboBox.setValue("Chủ quán");
                    mucLuongLabel.setText("Mức lương (VND):");
                    matKhauPasswordField.setDisable(false);
                }
                case "Full-time" -> {
                    // Chuyển giá trị viTriComboBox về các giá trị khác nếu đang là "Chủ quán"
                    if ("Chủ quán".equals(viTriComboBox.getValue())) {
                        viTriComboBox.setValue("Thu ngân"); // Hoặc một giá trị mặc định khác
                    }
                    mucLuongLabel.setText("Mức lương (VND/ngày):");
                }
                case "Part-time" -> {
                    // Chuyển giá trị viTriComboBox và quyenTruyCapComboBox về các giá trị khác
                    if ("Chủ quán".equals(viTriComboBox.getValue())) {
                        viTriComboBox.setValue("Thu ngân");
                    }
                    mucLuongLabel.setText("Mức lương (VND/giờ):");
                }
            }
        });
    }


  //Cập nhật giá trị dựa trên Vị Trí.
    private void updateValuesFromViTri() {
        String viTri = viTriComboBox.getValue();
        updateComboBoxesSafely(() -> {
            switch (viTri) {
                case "Chủ quán" -> {
                    loaiNhanVienComboBox.setValue("Chủ quán");
                    matKhauPasswordField.setDisable(false);
                    mucLuongLabel.setText("Mức lương (VND):");
                }
                case "Thu ngân" -> {
                    loaiNhanVienComboBox.setValue("Full-time");
                    mucLuongLabel.setText("Mức lương (VND/ngày):");
                    matKhauPasswordField.setDisable(false); // Cho phép nhập mật khẩu
                }
                case "Pha chế", "Phục vụ" -> {
                    loaiNhanVienComboBox.setValue("Full-time");
                    mucLuongLabel.setText("Mức lương (VND/ngày):");
                    matKhauPasswordField.setDisable(true); // Không cho phép nhập mật khẩu
                    matKhauPasswordField.clear(); // Xóa mật khẩu nếu đã nhập
                    // Label lương sẽ được cập nhật bởi updateValuesFromLoaiNhanVien
                }
                default -> matKhauPasswordField.setDisable(false); // Mặc định cho phép nhập nếu không phải các trường hợp trên
            }
            updateValuesFromLoaiNhanVien();
        });
    }

  //Phương thức an toàn để cập nhật giá trị của ComboBox.
    private void updateComboBoxesSafely(Runnable updateAction) {
        // Vô hiệu hóa sự kiện để tránh vòng lặp
        loaiNhanVienComboBox.setOnAction(null);
        viTriComboBox.setOnAction(null);

        // Thực hiện hành động cập nhật
        updateAction.run();

        // Gán lại sự kiện
        addComboBoxEvents();
    }

    @FXML
    private void xemMatKhau(){
        isPasswordVisible = JavaFXUtils.togglePasswordVisibility(isPasswordVisible, matKhauTextField, matKhauPasswordField, xemMKHyperlink);
    }


    @FXML
    private void chonAnh() {
        // Mở cửa sổ chọn tệp
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));

        // Lấy đường dẫn đến tệp ảnh mà người dùng chọn
        File file = fileChooser.showOpenDialog(btnChonAnh.getScene().getWindow());
    
        if (file != null) {
            this.selectedImageFile = file;
            try {
                // Cập nhật ảnh trong giao diện (hiển thị ảnh)
                anhChanDungImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));
            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.showErrorMessage("Lỗi tải ảnh! Không thể hiển thị ảnh đã chọn.");
                this.selectedImageFile = null;
            }
        }
    }

    @FXML
    public void kichNutThemNhanVien() {
        String tenNhanVien = tenNhanVienTextField.getText().trim();
        RadioButton selectedGioiTinhRadio = (RadioButton) gioiTinhToggleGroup.getSelectedToggle();
        LocalDate ngaySinh = ngaySinhDatePicker.getValue();
        String queQuan = queQuanTextField.getText().trim();
        String diaChi = diaChiTextArea.getText().trim();
        String soDienThoai = soDienThoaiTextField.getText().trim();
        String loaiNhanVien = loaiNhanVienComboBox.getValue();
        String viTri = viTriComboBox.getValue();
        LocalDate thoiGianVaoLam = thoiGianVaoLamDatePicker.getValue();
        String mucLuongStr = mucLuongTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String matKhau = matKhauPasswordField.getText();

        // Kiểm tra các trường bắt buộc
        if (tenNhanVien.isEmpty() || selectedGioiTinhRadio == null || diaChi.isEmpty() || queQuan.isEmpty() || email.isEmpty() || loaiNhanVien == null || viTri == null
            || soDienThoai.isEmpty() || mucLuongStr.isEmpty() || ngaySinh == null || thoiGianVaoLam == null ) {
            MessageUtils.showErrorMessage("Thiếu thông tin! Vui lòng nhập đầy đủ thông tin nhân viên.");
            return;
        }

        if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
            MessageUtils.showErrorMessage("Email không hợp lệ! Vui lòng nhập địa chỉ email đúng định dạng.");
            return;
        }
        if (!soDienThoai.matches("\\d{10}")) {
            MessageUtils.showErrorMessage("Số điện thoại không hợp lệ! Số điện thoại phải gồm 10 chữ số.");
            return;
        }
        if (tenNhanVien.length() < 3) {
            MessageUtils.showErrorMessage("Tên quá ngắn! Tên nhân viên phải có ít nhất 3 ký tự.");
            return;
        }

        int mucLuong;
        try {
            mucLuong = Integer.parseInt(mucLuongStr);
            if (mucLuong <= 0) {
                MessageUtils.showErrorMessage("Mức lương không hợp lệ! Mức lương phải là số dương.");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtils.showErrorMessage("Mức lương không hợp lệ! Vui lòng nhập mức lương là một số.");
            return;
        }

        if (ngaySinh.isAfter(LocalDate.now().minusYears(16))) {
            MessageUtils.showErrorMessage("Tuổi không hợp lệ! Nhân viên phải đủ 16 tuổi.");
            return;
        }
        if (thoiGianVaoLam.isBefore(ngaySinh)) {
            MessageUtils.showErrorMessage("Ngày vào làm không hợp lệ! Ngày vào làm phải sau ngày sinh.");
            return;
        }

        NhanVienDTO newNhanVien = new NhanVienDTO();
        newNhanVien.setTenNhanVien(tenNhanVien);
        newNhanVien.setGioiTinh(selectedGioiTinhRadio.getText());
        newNhanVien.setNgaySinh(ngaySinh);
        newNhanVien.setQueQuan(queQuan);
        newNhanVien.setDiaChi(diaChi);
        newNhanVien.setSoDienThoai(soDienThoai);
        newNhanVien.setLoaiNhanVien(loaiNhanVien);
        newNhanVien.setViTri(viTri);
        newNhanVien.setThoiGianVaoLam(thoiGianVaoLam);
        newNhanVien.setMucLuong(mucLuong);
        newNhanVien.setEmail(email);
        newNhanVien.setMatKhau(matKhau); // Backend sẽ mã hóa mật khẩu này

        if (selectedImageFile != null) {
            try {
                String originalFileName = selectedImageFile.getName();
                String fileExtension = "";
                int lastDotIndex = originalFileName.lastIndexOf('.');
                if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
                    fileExtension = originalFileName.substring(lastDotIndex);
                }
                String newImageName = UUID.randomUUID().toString() + fileExtension;
                Path targetDirectory = Paths.get("src/main/resources/images/nhanvien");
                if (!Files.exists(targetDirectory)) {
                    Files.createDirectories(targetDirectory);
                }
                Path targetPath = targetDirectory.resolve(newImageName);
                Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                newNhanVien.setAnhChanDung("/images/nhanvien/" + newImageName);
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtils.showErrorMessage("Lỗi lưu ảnh! Không thể lưu ảnh chân dung: " + e.getMessage());
                return;
            }
        } else {
            newNhanVien.setAnhChanDung(null); // Hoặc một ảnh mặc định nếu có
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String json = objectMapper.writeValueAsString(newNhanVien);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/nhan-vien"))
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 201 && response.statusCode() != 200) { // 201 Created or 200 OK
                    throw new IOException("Thêm nhân viên thất bại: " + response.statusCode() + " - " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            MessageUtils.showInfoMessage("Thành công! Thêm nhân viên thành công!");
            this.dataChanged = true; // Đánh dấu dữ liệu đã thay đổi
            if (nhanVienUI != null) {
                nhanVienUI.loadNhanVienData(); // Tải lại danh sách nhân viên đang làm việc
            }
            closeDialog();
        });

        task.setOnFailed(e -> {
            MessageUtils.showErrorMessage("Lỗi! Không thể thêm nhân viên: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    public void quayLai() {
        closeDialog();
    }

    private void closeDialog() {
        Stage currentStage = (Stage) btnQuayLai.getScene().getWindow();
        currentStage.close();  // Đóng cửa sổ thêm nhân viên
    }

}
