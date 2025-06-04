package com.frontend;

import com.backend.dto.NhanVienDTO;

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

public class ChinhSuaNhanVienUI { 
    private NhanVienDTO nhanVien;

    @FXML private TextField tenNhanVienTextField, queQuanTextField, soDienThoaiTextField, mucLuongTextField, emailTextField;
    @FXML private TextArea diaChiTextArea;
    @FXML private PasswordField matKhauPasswordField;
    @FXML private ComboBox<String> loaiNhanVienComboBox, viTriComboBox, trangThaiComboBox;
    @FXML private DatePicker ngaySinhDatePicker, thoiGianVaoLamDatePicker;
    @FXML private RadioButton namRadioButton, nuRadioButton;
    @FXML private ImageView anhChanDungImageView;
    @FXML private Button btnChonAnh, btnQuayLai, btnCapNhat;
    @FXML private HBox gioiTinhHBox;

    private byte[] anhChanDung;  // Dùng để lưu ảnh chân dung đã chọn

    /*private QuanLiHoSoScreen quanLiHoSoScreen;  // Thêm trường này

   // Phương thức để nhận tham chiếu từ màn hình chính
    public void setMainScreen(QuanLiHoSoScreen quanLiHoSoScreen) {
        this.quanLiHoSoScreen = quanLiHoSoScreen;
    }
    @FXML
    public void initialize() {
        
       // Khởi tạo các giá trị cho ComboBox
        loaiNhanVienComboBox.getItems().addAll("Chủ quán", "Full-time", "Part-time");
        viTriComboBox.getItems().addAll("Chủ quán", "Thu ngân", "Pha chế", "Phục vụ");
        trangThaiComboBox.getItems().addAll("Admin", "User", "None");

       addComboBoxEvents();

    // Sự kiện nút quay lại
    btnQuayLai.setOnAction(event -> quayLai());
}


    
    
 Thêm sự kiện cho ComboBox.
 
private void addComboBoxEvents() {
    loaiNhanVienComboBox.setOnAction(event -> updateValuesFromLoaiNhanVien());
    viTriComboBox.setOnAction(event -> updateValuesFromViTri());
    trangThaiComboBox.setOnAction(event -> updateValuesFromQuyenTruyCap());
}

/**
 * Cập nhật giá trị dựa trên Loại Nhân Viên.

private void updateValuesFromLoaiNhanVien() {
    String loaiNhanVien = loaiNhanVienComboBox.getValue();
    updateComboBoxesSafely(() -> {
        switch (loaiNhanVien) {
            case "Chủ quán":
                viTriComboBox.setValue("Chủ quán");
                trangThaiComboBox.setValue("Admin");
                break;
            case "Full-time":
            case "Part-time":
                // Chuyển giá trị viTriComboBox và trangThaiComboBox về các giá trị khác
                if ("Chủ quán".equals(viTriComboBox.getValue())) {
                    viTriComboBox.setValue("Thu ngân");
                }
                if ("Admin".equals(trangThaiComboBox.getValue())) {
                    trangThaiComboBox.setValue("User");
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
                trangThaiComboBox.setValue("Admin");
                break;
            case "Thu ngân":
                loaiNhanVienComboBox.setValue("Full-time");
                trangThaiComboBox.setValue("User");
                break;
            case "Pha chế":
            case "Phục vụ":
                loaiNhanVienComboBox.setValue("Full-time");
                trangThaiComboBox.setValue("None");
                break;
        }
    });
}

/**
 * Cập nhật giá trị dựa trên Quyền Truy Cập.
 
private void updateValuesFromQuyenTruyCap() {
    String quyenTruyCap = trangThaiComboBox.getValue();
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
        try {
            // Đọc ảnh và hiển thị lên ImageView
            Image image = new Image(file.toURI().toString());
            anhChanDungImageView.setImage(image);

            // Chuyển ảnh thành byte[]
            FileInputStream fis = new FileInputStream(file);
            anhChanDung = new byte[(int) file.length()];
            fis.read(anhChanDung);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể chọn ảnh");
            alert.setContentText("Có lỗi khi chọn ảnh. Vui lòng thử lại.");
            alert.showAndWait();
        }
    }
}


    public void hienThiThongTin(NhanVienDTO nhanVien) {
    if (nhanVien != null) {
        // Gán thông tin vào các trường nhập liệu
        this.nhanVien = nhanVien; // Lưu đối tượng nhân viên vào biến nhanVien
        System.out.println("Mã nhân viên của nhanVien: " + nhanVien.getMaNhanVien());

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
        mucLuongTextField.setText(String.valueOf(nhanVien.getMucLuong()));
        emailTextField.setText(nhanVien.getEmail());
        matKhauPasswordField.setText(nhanVien.getMatKhau());
        trangThaiComboBox.setValue(nhanVien.getQuyenTruyCap());

        // Hiển thị ảnh chân dung nếu có
        if (nhanVien.getAnhChanDung() != null) {
            Image image = new Image(new ByteArrayInputStream(nhanVien.getAnhChanDung()));
            anhChanDungImageView.setImage(image);
        }
    }
}


    

@FXML
private void capNhat() {
    try {
        // Kiểm tra các trường thông tin không được để trống
        if (tenNhanVienTextField.getText().trim().isEmpty()
                || (!namRadioButton.isSelected() && !nuRadioButton.isSelected())
                || ngaySinhDatePicker.getValue() == null
                || queQuanTextField.getText().trim().isEmpty()
                || diaChiTextArea.getText().trim().isEmpty()
                || soDienThoaiTextField.getText().trim().isEmpty()
                || loaiNhanVienComboBox.getValue() == null
                || viTriComboBox.getValue() == null
                || thoiGianVaoLamDatePicker.getValue() == null
                || mucLuongTextField.getText().trim().isEmpty()
                || emailTextField.getText().trim().isEmpty()
                || trangThaiComboBox.getValue() == null) {

            showErrorAlert("Lỗi", "Hãy nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra số điện thoại
        String soDienThoai = soDienThoaiTextField.getText();
        if (!soDienThoai.matches("\\d{10}")) {
            showErrorAlert("Lỗi", "Số điện thoại phải gồm 10 chữ số.");
            return;
        }

        // Kiểm tra email
        String email = emailTextField.getText();
        if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$")) {
            showErrorAlert("Lỗi", "Email không hợp lệ.");
            return;
        }


        // Kiểm tra lương
        int mucLuong;
        try {
            mucLuong = Integer.parseInt(mucLuongTextField.getText());
            if (mucLuong <= 0) {
                showErrorAlert("Lỗi", "Mức lương phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Lỗi", "Mức lương không hợp lệ.");
            return;
        }

        // Kiểm tra độ tuổi (Nhân viên phải đủ 16 tuổi)
        LocalDate ngaySinh = ngaySinhDatePicker.getValue();
        if (ngaySinh != null && ngaySinh.isAfter(LocalDate.now().minusYears(16))) {
            showErrorAlert("Lỗi", "Nhân viên phải đủ 16 tuổi.");
            return;
        }

        // Kiểm tra ngày vào làm phải sau ngày sinh
        LocalDate thoiGianVaoLam = thoiGianVaoLamDatePicker.getValue();
        if (thoiGianVaoLam != null && thoiGianVaoLam.isBefore(ngaySinh)) {
            showErrorAlert("Lỗi", "Ngày vào làm phải sau ngày sinh.");
            return;
        }

        // Cập nhật thông tin nhân viên
        nhanVien.setSoDienThoai(soDienThoai);
        nhanVien.setTenNhanVien(tenNhanVienTextField.getText());
        nhanVien.setGioiTinh(namRadioButton.isSelected() ? "Nam" : "Nữ");
        nhanVien.setNgaySinh(ngaySinhDatePicker.getValue());
        nhanVien.setQueQuan(queQuanTextField.getText());
        nhanVien.setDiaChi(diaChiTextArea.getText());
        nhanVien.setLoaiNhanVien(loaiNhanVienComboBox.getValue());
        nhanVien.setViTri(viTriComboBox.getValue());
        nhanVien.setThoiGianVaoLam(thoiGianVaoLamDatePicker.getValue());
        nhanVien.setMucLuong(mucLuong);
        nhanVien.setEmail(email);
        nhanVien.setMatKhau(matKhauPasswordField.getText());
        nhanVien.setQuyenTruyCap(trangThaiComboBox.getValue());
        nhanVien.setAnhChanDung(anhChanDung);

        boolean thanhCong = quanLiHoSoScreen.getQuanLiHoSoController().capNhatNhanVien(nhanVien);

        if (thanhCong) {
             // Nếu cập nhật thành công, hiển thị thông báo thành công và làm mới danh sách nhân viên
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cập nhật thành công");
        alert.setHeaderText("Thông tin nhân viên đã được cập nhật.");
        alert.showAndWait();

        // Đóng cửa sổ chỉnh sửa sau khi cập nhật
        Stage stage = (Stage) btnCapNhat.getScene().getWindow();
        stage.close();

        quanLiHoSoScreen.hienThiDanhSachNhanVien(quanLiHoSoScreen.getQuanLiHoSoController().layDanhSachNhanVien());
    } else {
        // Nếu cập nhật thất bại, hiển thị thông báo lỗi
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Cập nhật thất bại");
        alert.setHeaderText("Không thể cập nhật thông tin nhân viên.");
        alert.showAndWait();
    }
    } catch (Exception e) {
        showErrorAlert("Lỗi", "Có lỗi xảy ra. Vui lòng kiểm tra lại.");
        e.printStackTrace();
    }
}

private void showErrorAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}



@FXML
private void quayLai() {
    Stage stage = (Stage) btnQuayLai.getScene().getWindow();
    stage.close(); // Đóng màn hình hiện tại
}
*/
}
