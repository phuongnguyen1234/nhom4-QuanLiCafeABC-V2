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
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.JavaFXUtils;
import com.backend.utils.ImageUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ChinhSuaNhanVienUI { 
    private NhanVienDTO nhanVien;

    @FXML 
    private TextField tenNhanVienTextField, queQuanTextField, soDienThoaiTextField, mucLuongTextField, emailTextField, matKhauTextField;

    @FXML 
    private TextArea diaChiTextArea;

    @FXML 
    private PasswordField matKhauPasswordField;

    @FXML 
    private ComboBox<String> loaiNhanVienComboBox, viTriComboBox, trangThaiComboBox;

    @FXML 
    private DatePicker ngaySinhDatePicker, thoiGianVaoLamDatePicker;

    @FXML 
    private RadioButton namRadioButton, nuRadioButton;

    @FXML 
    private ImageView anhChanDungImageView;

    @FXML 
    private Button btnChonAnh, btnQuayLai, btnCapNhat;

    @FXML 
    private HBox gioiTinhHBox;

    @FXML
    private Label mucLuongLabel; // Thêm FXML cho mucLuongLabel

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Hyperlink xemMKHyperlink;

    private ToggleGroup gioiTinhToggleGroup;

    private File newSelectedImageFile; // Dùng để lưu ảnh chân dung mới nếu người dùng chọn
    private NhanVienUI nhanVienUI;
    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private boolean dataChanged = false; // Biến để theo dõi thay đổi dữ liệu
    public boolean isDataChanged() {
        return dataChanged;
    }

    private boolean isPasswordVisible = false;

    public void setNhanVien(NhanVienDTO nhanVien) {
        this.nhanVien = nhanVien;
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
        gioiTinhToggleGroup = new ToggleGroup();
        namRadioButton.setToggleGroup(gioiTinhToggleGroup);
        nuRadioButton.setToggleGroup(gioiTinhToggleGroup);
       // Khởi tạo các giá trị cho ComboBox
        loaiNhanVienComboBox.getItems().addAll("Chủ quán", "Full-time", "Part-time");
        viTriComboBox.getItems().addAll("Chủ quán", "Thu ngân", "Pha chế", "Phục vụ");
        trangThaiComboBox.getItems().addAll("Đi làm", "Nghỉ việc");
        // Đảm bảo FXML đã thiết lập icon ban đầu (view.png) cho xemMKHyperlink
        // Ví dụ: <Image url="@/icons/view.png" /> bên trong ImageView của Hyperlink

       addComboBoxEvents();

       disableItems(true);

       Task<NhanVienDTO> task = new Task<NhanVienDTO>() {
            @Override
            protected NhanVienDTO call() throws Exception {
                // Không cập nhật UI ở đây
                return ChinhSuaNhanVienUI.this.nhanVien; // Trả về nhân viên để xử lý trong onSucceeded
            }
        };

        task.setOnSucceeded(event -> {
            NhanVienDTO nv = task.getValue();
            if (nv != null) {
                hienThiThongTin(nv); // Gọi hiển thị thông tin ở đây (trên luồng JavaFX)
            }
            disableItems(false);
        });

        task.setOnFailed(event -> {
            task.getException().printStackTrace();
            MessageUtils.showErrorMessage("Lỗi tải thông tin nhân viên: " + task.getException().getMessage());
            disableItems(false);
        });
        new Thread(task).start();

        // Sự kiện nút quay lại
        btnQuayLai.setOnAction(event -> quayLai());
    }
    
    //Thêm sự kiện cho ComboBox.
    private void addComboBoxEvents() {
        loaiNhanVienComboBox.setOnAction(event -> updateValuesFromLoaiNhanVien());
        viTriComboBox.setOnAction(event -> updateValuesFromViTri());
    }

    // Cập nhật giá trị dựa trên Loại Nhân Viên.
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


    // Cập nhật giá trị dựa trên Vị Trí.
    private void updateValuesFromViTri() {
        String viTri = viTriComboBox.getValue();
        updateComboBoxesSafely(() -> {
            switch (viTri) {
                case "Chủ quán" -> {
                    loaiNhanVienComboBox.setValue("Chủ quán");
                    mucLuongLabel.setText("Mức lương (VND):");
                    matKhauPasswordField.setDisable(false); // Cho phép nhập mật khẩu
                }
                case "Thu ngân" -> {
                    if ("Chủ quán".equals(loaiNhanVienComboBox.getValue())) { // Chỉ thay đổi nếu không phải Chủ quán
                        loaiNhanVienComboBox.setValue("Full-time");
                        mucLuongLabel.setText("Mức lương (VND/ngày):");
                    }
                    matKhauPasswordField.setDisable(false); // Cho phép nhập mật khẩu
                }
                case "Pha chế", "Phục vụ" -> {
                    if ("Chủ quán".equals(loaiNhanVienComboBox.getValue())) { // Chỉ thay đổi nếu không phải Chủ quán
                        loaiNhanVienComboBox.setValue("Full-time");
                        mucLuongLabel.setText("Mức lương (VND/ngày):");
                    }
                    matKhauPasswordField.setDisable(true); // Không cho phép nhập mật khẩu
                    matKhauPasswordField.clear(); // Xóa mật khẩu nếu đã nhập
                }
            }
        });
    }

    // Phương thức an toàn để cập nhật giá trị của ComboBox.
    private void updateComboBoxesSafely(Runnable updateAction) {
        // Vô hiệu hóa sự kiện để tránh vòng lặp
        loaiNhanVienComboBox.setOnAction(null);
        viTriComboBox.setOnAction(null);
        trangThaiComboBox.setOnAction(null);

        // Thực hiện hành động cập nhật
        updateAction.run();

        // Gán lại sự kiện
        addComboBoxEvents();
    }


    @FXML
    private void chonAnh() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(btnChonAnh.getScene().getWindow());
        
        if (file != null) {
            this.newSelectedImageFile = file;
            try {
                // Đọc ảnh và hiển thị lên ImageView
                Image image = new Image(file.toURI().toString());
                anhChanDungImageView.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.showErrorMessage("Lỗi tải ảnh! Không thể hiển thị ảnh đã chọn.");
                this.newSelectedImageFile = null;
            }
        }
    }

    private void hienThiThongTin(NhanVienDTO nhanVien) {
        if (nhanVien != null) {
            // Gán thông tin vào các trường nhập liệu
            System.out.println("Mã nhân viên của nhanVien: " + nhanVien.getMaNhanVien());

            if(nhanVien.getViTri().equals("Pha chế") || nhanVien.getViTri().equals("Phục vụ")) {
                matKhauPasswordField.setDisable(true);
            }

            tenNhanVienTextField.setText(nhanVien.getTenNhanVien());
            if ("Nam".equals(nhanVien.getGioiTinh())) {
                namRadioButton.setSelected(true);
            } else {
                nuRadioButton.setSelected(true);
            }
            ngaySinhDatePicker.setValue(nhanVien.getNgaySinh());
            queQuanTextField.setText(nhanVien.getQueQuan());
            diaChiTextArea.setText(nhanVien.getDiaChi());
            soDienThoaiTextField.setText(nhanVien.getSoDienThoai());
            loaiNhanVienComboBox.setValue(nhanVien.getLoaiNhanVien());
            viTriComboBox.setValue(nhanVien.getViTri());
            thoiGianVaoLamDatePicker.setValue(nhanVien.getThoiGianVaoLam());
            // Cập nhật labelMucLuong và textMucLuong
                if ("Part-time".equals(nhanVien.getLoaiNhanVien())) {
                    mucLuongLabel.setText("Mức lương (VND/giờ):");
                } else if ("Full-time".equals(nhanVien.getLoaiNhanVien())) { 
                    mucLuongLabel.setText("Mức lương (VND/ngày):");
                } else {
                    mucLuongLabel.setText("Mức lương (VND):");
                }
            mucLuongTextField.setText(String.valueOf(nhanVien.getMucLuong()));
            trangThaiComboBox.setValue(nhanVien.getTrangThai());
            emailTextField.setText(nhanVien.getEmail());
            matKhauPasswordField.setText(nhanVien.getMatKhau());

            // Hiển thị ảnh chân dung nếu có
            if (nhanVien.getAnhChanDung() != null && !nhanVien.getAnhChanDung().isEmpty()) {
                Image image = ImageUtils.loadFromResourcesOrDefault(nhanVien.getAnhChanDung(), "/icons/profile.png");
                anhChanDungImageView.setImage(image);
                } else {
                anhChanDungImageView.setImage(new Image(getClass().getResourceAsStream("/icons/profile.png")));
            }
        }
    }


    @FXML
    private void capNhat() {
        try {
            if (nhanVien == null) {
                MessageUtils.showErrorMessage("Lỗi! Không có thông tin nhân viên để cập nhật.");
                return;
            }

            // Lấy thông tin từ các trường nhập liệu
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
            String trangThai = trangThaiComboBox.getValue();
            String email = emailTextField.getText().trim();

            // Kiểm tra các trường thông tin không được để trống
            if (tenNhanVien.isEmpty()
                    || selectedGioiTinhRadio == null
                    || ngaySinhDatePicker.getValue() == null
                    || queQuan.isEmpty()
                    || diaChi.isEmpty()
                    || soDienThoai.isEmpty()
                    || loaiNhanVienComboBox.getValue() == null
                    || viTriComboBox.getValue() == null
                    || thoiGianVaoLamDatePicker.getValue() == null
                    || mucLuongStr.isEmpty()
                    || email.isEmpty()
                    || trangThaiComboBox.getValue() == null) {

                MessageUtils.showErrorMessage("Thiếu thông tin! Vui lòng nhập đầy đủ thông tin nhân viên.");
                return;
            }

            
            if (!soDienThoai.matches("\\d{10}")) {
                MessageUtils.showErrorMessage("Số điện thoại không hợp lệ! Số điện thoại phải gồm 10 chữ số.");
                return;
            }

            if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
                MessageUtils.showErrorMessage("Email không hợp lệ! Vui lòng nhập địa chỉ email đúng định dạng.");
                return;
            }


            int mucLuong;
            try {
                mucLuong = Integer.parseInt(mucLuongStr);
                if (mucLuong < 0) {
                    MessageUtils.showErrorMessage("Mức lương không hợp lệ! Mức lương phải lớn hơn 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                MessageUtils.showErrorMessage("Mức lương không hợp lệ! Vui lòng nhập mức lương đúng định dạng.");
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

            // Cập nhật thông tin nhân viên
            // Chỉ cập nhật các trường có thể thay đổi, mã nhân viên và trạng thái hoạt động giữ nguyên
            NhanVienDTO updatedNhanVien = new NhanVienDTO();
            updatedNhanVien.setMaNhanVien(nhanVien.getMaNhanVien()); // Giữ nguyên mã
            updatedNhanVien.setTenNhanVien(tenNhanVien);
            updatedNhanVien.setGioiTinh(selectedGioiTinhRadio.getText());
            updatedNhanVien.setNgaySinh(ngaySinh);
            updatedNhanVien.setQueQuan(queQuan);
            updatedNhanVien.setDiaChi(diaChi);
            updatedNhanVien.setSoDienThoai(soDienThoai);
            updatedNhanVien.setLoaiNhanVien(loaiNhanVien);
            updatedNhanVien.setViTri(viTri);
            updatedNhanVien.setThoiGianVaoLam(thoiGianVaoLam);
            updatedNhanVien.setMucLuong(mucLuong);
            updatedNhanVien.setTrangThai(trangThai);
            updatedNhanVien.setEmail(email);

            // Xử lý mật khẩu dựa trên vị trí 
            if ("Pha chế".equals(viTri) || "Phục vụ".equals(viTri)) {
                updatedNhanVien.setMatKhau(null); // Set mật khẩu về null nếu vị trí là Pha chế hoặc Phục vụ
            } else {
                // Nếu vị trí là Chủ quán hoặc Thu ngân
                String newPassword = matKhauPasswordField.getText();
                
                // Kiểm tra nếu chuyển từ vị trí không cần mật khẩu sang vị trí cần mật khẩu và mật khẩu trống
                if ((newPassword == null || newPassword.trim().isEmpty()) && 
                    ("Pha chế".equals(this.nhanVien.getViTri()) || "Phục vụ".equals(this.nhanVien.getViTri()))) {
                    MessageUtils.showErrorMessage("Vui lòng nhập mật khẩu cho vị trí " + viTri + ".");
                    return; // Dừng việc cập nhật
                }
                updatedNhanVien.setMatKhau(newPassword == null || newPassword.trim().isEmpty() ? this.nhanVien.getMatKhau() : newPassword);
            }

            updatedNhanVien.setTrangThaiHoatDong(this.nhanVien.getTrangThaiHoatDong()); // Giữ nguyên trạng thái hoạt động

            if (newSelectedImageFile != null) {
                try { //NOSONAR
                    String originalFileName = newSelectedImageFile.getName();
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
                    Files.copy(newSelectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    updatedNhanVien.setAnhChanDung("/images/nhanvien/" + newImageName);
                } catch (IOException e) {
                    e.printStackTrace();
                    MessageUtils.showErrorMessage("Lỗi lưu ảnh! Không thể lưu ảnh chân dung mới: " + e.getMessage());
                    return;
                }
            } else {
                updatedNhanVien.setAnhChanDung(this.nhanVien.getAnhChanDung()); // Giữ ảnh cũ nếu không chọn ảnh mới
            }

            // Disable form trước khi gửi request
            disableItems(true);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    String json = objectMapper.writeValueAsString(updatedNhanVien);
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/nhan-vien/" + updatedNhanVien.getMaNhanVien()))
                            .method("PUT", HttpRequest.BodyPublishers.ofString(json)) 
                            .header("Content-Type", "application/json")
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() != 200) {
                        throw new IOException("Cập nhật nhân viên thất bại: " + response.statusCode() + " - " + response.body());
                    }
                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                MessageUtils.showInfoMessage("Thành công! Cập nhật thông tin nhân viên thành công!");
                this.dataChanged = true; // Đánh dấu dữ liệu đã thay đổi
                if (nhanVienUI != null) {
                    nhanVienUI.loadNhanVienData(); // Tải lại danh sách nhân viên đang làm việc
                    // Enable lại form và đóng dialog
                disableItems(false);
                }
                closeDialog();
            });

            task.setOnFailed(e -> {
                MessageUtils.showErrorMessage("Lỗi! Không thể cập nhật nhân viên: " + task.getException().getMessage());
                task.getException().printStackTrace();
                // Enable lại form nếu có lỗi
                disableItems(false);
            });

            new Thread(task).start();

        } catch (Exception e) {
            MessageUtils.showErrorMessage("Lỗi hệ thống! Có lỗi không mong muốn xảy ra: " + e.getMessage());
            e.printStackTrace(); //NOSONAR
            disableItems(false); // Đảm bảo form được enable nếu có lỗi ngoài Task
        }
    }

    @FXML
    private void quayLai() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnQuayLai.getScene().getWindow();
        stage.close(); // Đóng màn hình hiện tại
    }

    @FXML
    private void xemMatKhau(){
        isPasswordVisible = JavaFXUtils.togglePasswordVisibility(isPasswordVisible, matKhauTextField, matKhauPasswordField, xemMKHyperlink);
    }

    private void disableItems(boolean value){
        mainAnchorPane.setDisable(value);
        //btnQuayLai.setDisable(value);
    }

}
