package com.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ThemNhanVienUI {
    // Thông tin cá nhân
    @FXML
    private TextField tenNhanVienTextField;

    @FXML
    private HBox gioiTinhHBox;
    
    @FXML
    private RadioButton namRadioButton;

    @FXML
    private RadioButton nuRadioButton;

    @FXML
    private DatePicker ngaySinhDatePicker;

    @FXML
    private TextField queQuanTextField;

    @FXML
    private TextArea diaChiTextArea;

    @FXML
    private TextField soDienThoaiTextField;

    // Thông tin việc làm
    @FXML
    private ComboBox<String> loaiNhanVienComboBox;

    @FXML
    private ComboBox<String> viTriComboBox;

    @FXML
    private DatePicker thoiGianVaoLamDatePicker;

    @FXML
    private TextField mucLuongTextField;

    // Thông tin đăng nhập
    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField matKhauPasswordField;

    @FXML
    private ComboBox<String> quyenTruyCapComboBox;

    @FXML
    private ImageView anhChanDungImageView;

    @FXML
    private Button btnChonAnh; 

    @FXML
    private Button btnThem;

    @FXML
    private Button btnQuayLai;

/* 
    private QuanLiHoSoScreen quanLiHoSoScreen;  // Thêm trường này

    // Thêm các trường cho anhChanDung và maNhanVien
    private byte[] anhChanDung;  // Biến lưu trữ ảnh
    private String maNhanVien;  // Biến lưu trữ mã nhân viên

    // Phương thức để nhận tham chiếu từ màn hình chính
    public void setMainScreen(QuanLiHoSoScreen quanLiHoSoScreen) {
        this.quanLiHoSoScreen = quanLiHoSoScreen;
    }
    @FXML
    public void initialize() {
    // Khởi tạo các giá trị cho ComboBox
    loaiNhanVienComboBox.getItems().addAll("Chủ quán", "Full-time", "Part-time");
    viTriComboBox.getItems().addAll("Chủ quán", "Thu ngân", "Pha chế", "Phục vụ");
    quyenTruyCapComboBox.getItems().addAll("Admin", "User", "None");

    // Đặt các giá trị mặc định nếu cần thiết
    ngaySinhDatePicker.setValue(LocalDate.now());
    thoiGianVaoLamDatePicker.setValue(LocalDate.now());

    addComboBoxEvents(); // Thêm sự kiện cho ComboBox

}

    /**
 * Thêm sự kiện cho ComboBox.
 
private void addComboBoxEvents() {
    loaiNhanVienComboBox.setOnAction(event -> updateValuesFromLoaiNhanVien());
    viTriComboBox.setOnAction(event -> updateValuesFromViTri());
    quyenTruyCapComboBox.setOnAction(event -> updateValuesFromQuyenTruyCap());
}

private void updateValuesFromLoaiNhanVien() {
    String loaiNhanVien = loaiNhanVienComboBox.getValue();
    updateComboBoxesSafely(() -> {
        switch (loaiNhanVien) {
            case "Chủ quán":
                viTriComboBox.setValue("Chủ quán");
                quyenTruyCapComboBox.setValue("Admin");
                break;
            case "Full-time":
            case "Part-time":
                // Chuyển giá trị viTriComboBox và quyenTruyCapComboBox về các giá trị khác
                if ("Chủ quán".equals(viTriComboBox.getValue())) {
                    viTriComboBox.setValue("Thu ngân");
                }
                if ("Admin".equals(quyenTruyCapComboBox.getValue())) {
                    quyenTruyCapComboBox.setValue("User");
                }
                break;
        }
    });
}

/**
 * Cập nhật giá trị dựa trên Vị Trí.
 
private void updateValuesFromViTri() {
    String viTri = viTriComboBox.getValue();
    updateComboBoxesSafely(() -> {
        switch (viTri) {
            case "Chủ quán":
                loaiNhanVienComboBox.setValue("Chủ quán");
                quyenTruyCapComboBox.setValue("Admin");
                break;
            case "Thu ngân":
                loaiNhanVienComboBox.setValue("Full-time");
                quyenTruyCapComboBox.setValue("User");
                break;
            case "Pha chế":
            case "Phục vụ":
                loaiNhanVienComboBox.setValue("Full-time");
                quyenTruyCapComboBox.setValue("None");
                break;
        }
    });
}

/**
 * Cập nhật giá trị dựa trên Quyền Truy Cập.
 
private void updateValuesFromQuyenTruyCap() {
    String quyenTruyCap = quyenTruyCapComboBox.getValue();
    updateComboBoxesSafely(() -> {
        if ("Admin".equals(quyenTruyCap)) {
            loaiNhanVienComboBox.setValue("Chủ quán");
            viTriComboBox.setValue("Chủ quán");
        } else if ("User".equals(quyenTruyCap)) {
            loaiNhanVienComboBox.setValue("Full-time");
            viTriComboBox.setValue("Thu ngân");
        } else if ("None".equals(quyenTruyCap)) {
            loaiNhanVienComboBox.setValue("Full-time");
            viTriComboBox.setValue("Pha chế"); // Hoặc "Phục Vụ"
        }
    });
}

/**
 * Phương thức an toàn để cập nhật giá trị của ComboBox.
 
private void updateComboBoxesSafely(Runnable updateAction) {
    // Vô hiệu hóa sự kiện để tránh vòng lặp
    loaiNhanVienComboBox.setOnAction(null);
    viTriComboBox.setOnAction(null);
    quyenTruyCapComboBox.setOnAction(null);

    // Thực hiện hành động cập nhật
    updateAction.run();

    // Gán lại sự kiện
    addComboBoxEvents();
}


    @FXML
    private void chonAnh() {
        // Mở cửa sổ chọn tệp
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif"));

        // Lấy đường dẫn đến tệp ảnh mà người dùng chọn
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                // Đọc tệp ảnh và chuyển thành byte[]
                FileInputStream fis = new FileInputStream(file);
                byte[] anhByte = new byte[(int) file.length()];
                fis.read(anhByte);
                fis.close();

                // Cập nhật ảnh trong giao diện (hiển thị ảnh)
                anhChanDungImageView.setImage(new javafx.scene.image.Image(file.toURI().toString()));

                // Lưu ảnh vào thuộc tính anhChanDung
                this.anhChanDung = anhByte;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void kichNutThemNhanVien() {


    String tenNhanVien = tenNhanVienTextField.getText();
    String gioiTinh = namRadioButton.isSelected() ? "Nam" : "Nữ";
    LocalDate ngaySinh = ngaySinhDatePicker.getValue();
    String queQuan = queQuanTextField.getText();
    String diaChi = diaChiTextArea.getText();
    String soDienThoai = soDienThoaiTextField.getText();
    String loaiNhanVien = loaiNhanVienComboBox.getValue();
    String viTri = viTriComboBox.getValue();
    LocalDate thoiGianVaoLam = thoiGianVaoLamDatePicker.getValue();
    String mucLuong = mucLuongTextField.getText();
    String email = emailTextField.getText();
    String matKhau = matKhauPasswordField.getText();
    String quyenTruyCap = quyenTruyCapComboBox.getValue();

    // Lấy trạng thái hoạt động mặc định là 0
    String trangThaiHoatDong = "0";  

    // Kiểm tra tính hợp lệ của các trường dữ liệu
    if (tenNhanVien.isEmpty() || gioiTinh.isEmpty() || diaChi.isEmpty() || queQuan.isEmpty() ||email.isEmpty() || matKhau.isEmpty() || loaiNhanVien == null || viTri == null
        || soDienThoai.isEmpty() || mucLuong.isEmpty() || ngaySinh == null || thoiGianVaoLam == null) {
            showErrorAlert("Hãy nhập đầy đủ thông tin!");
        return;
    }

    // Kiểm tra email
    if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
        showErrorAlert("Email không hợp lệ.");
        return;
    }

    // Kiểm tra số điện thoại
    if (!soDienThoai.matches("\\d{10}")) {
        showErrorAlert("Số điện thoại phải gồm 10 chữ số.");
        return;
    }

    // Kiểm tra email và số điện thoại có bị trùng lặp không
    if (quanLiHoSoScreen.getQuanLiHoSoController().kiemTraEmailSoDienThoaiTrung(email, soDienThoai)) {
        showErrorAlert("Email hoặc số điện thoại đã tồn tại trong hệ thống.");
        return;
    }

    // Kiểm tra độ dài tên nhân viên
    if (tenNhanVien.length() < 3) {
        showErrorAlert("Tên nhân viên phải có ít nhất 3 ký tự.");
        return;
    }


    // Kiểm tra mức lương
    int mucLuongInt;
    try {
        mucLuongInt = Integer.parseInt(mucLuong);
        if (mucLuongInt <= 0) {
            showErrorAlert("Mức lương phải lớn hơn 0.");
            return;
        }
    } catch (NumberFormatException e) {
        showErrorAlert("Mức lương không hợp lệ.");
        return;
    }


    // Kiểm tra độ tuổi (Nhân viên phải đủ 16 tuổi)
    if (ngaySinh != null && ngaySinh.isAfter(LocalDate.now().minusYears(16))) {
        showErrorAlert("Nhân viên phải đủ 16 tuổi.");
        return;
    }

    // Kiểm tra ngày vào làm phải sau ngày sinh
    if (thoiGianVaoLam != null && thoiGianVaoLam.isBefore(ngaySinh)) {
        showErrorAlert("Ngày vào làm phải sau ngày sinh.");
        return;
    }


    // Tạo đối tượng NhanVien từ các dữ liệu đã nhập
    NhanVien nhanVien = new NhanVien(
        maNhanVien, tenNhanVien, anhChanDung, gioiTinh, ngaySinh, queQuan, diaChi, soDienThoai,
        loaiNhanVien, viTri, thoiGianVaoLam, Integer.parseInt(mucLuong), email, matKhau, quyenTruyCap, trangThaiHoatDong
    );

    // Gọi phương thức thêm nhân viên vào cơ sở dữ liệu
    boolean isSuccess = quanLiHoSoScreen.getQuanLiHoSoController().themNhanVien(nhanVien);

    if (isSuccess) {
        // Nếu thêm thành công, hiển thị thông báo và làm mới bảng
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText("Nhân viên đã được thêm");
        alert.showAndWait();

        // Đóng cửa sổ thêm nhân viên
        Stage stage = (Stage) btnThem.getScene().getWindow();
        stage.close();

        quanLiHoSoScreen.hienThiDanhSachNhanVien(quanLiHoSoScreen.getQuanLiHoSoController().layDanhSachNhanVien());

    } else {
        // Nếu thêm thất bại, hiển thị thông báo lỗi
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("Không thể thêm nhân viên");
        alert.showAndWait();
    }
}


// Phương thức hiển thị thông báo lỗi
private void showErrorAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Lỗi");
    alert.setHeaderText("Thông báo lỗi");
    alert.setContentText(message);
    alert.showAndWait();
}


@FXML
public void quayLai() {
    // Đóng cửa sổ hiện tại
    Stage currentStage = (Stage) btnQuayLai.getScene().getWindow();
    currentStage.close();  // Đóng cửa sổ thêm nhân viên
}

*/
}


