package com.frontend;

import com.backend.dto.NhanVienDTO;
import com.backend.utils.ImageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ThongTinNhanVienUI {
    @FXML private Text textMaNhanVien, textHoTen, textGioiTinh, textNgaySinh, textQueQuan, textDiaChi, 
                        textSoDienThoai, textLoaiNhanVien, textViTri, textThoiGianVaoLam, textMucLuong, 
                        textEmail, textTrangThaiHoatDong, textTrangThai;

    @FXML private Circle circleTrangThai;
    @FXML private ImageView imgChanDung;
    @FXML private Button btnQuayLai;

    // private NhanVienUI nhanVienUI; // Nếu cần callback
    // public void setNhanVienUI(NhanVienUI nhanVienUI) { this.nhanVienUI = nhanVienUI; }

    // Hiển thị thông tin nhân viên
public void hienThiThongTin(NhanVienDTO nhanVien) {
    textMaNhanVien.setText(nhanVien.getMaNhanVien());
    textHoTen.setText(nhanVien.getTenNhanVien());
    textGioiTinh.setText(nhanVien.getGioiTinh());
    textNgaySinh.setText(nhanVien.getNgaySinh() != null ? nhanVien.getNgaySinh().toString() : "N/A");
    textQueQuan.setText(nhanVien.getQueQuan());
    textDiaChi.setText(nhanVien.getDiaChi());
    textSoDienThoai.setText(nhanVien.getSoDienThoai());
    textLoaiNhanVien.setText(nhanVien.getLoaiNhanVien());
    textViTri.setText(nhanVien.getViTri());
    textThoiGianVaoLam.setText(nhanVien.getThoiGianVaoLam() != null ? nhanVien.getThoiGianVaoLam().toString() : "N/A");
    textMucLuong.setText(String.valueOf(nhanVien.getMucLuong()));
    textEmail.setText(nhanVien.getEmail());
    textTrangThaiHoatDong.setText(nhanVien.getTrangThaiHoatDong());
    textTrangThai.setText(nhanVien.getTrangThai() != null ? nhanVien.getTrangThai() : "N/A");

     if ("Online".equals(nhanVien.getTrangThaiHoatDong())) {
        circleTrangThai.setFill(Color.GREEN);  // Màu xanh lá nếu trạng thái là "Online"
        textTrangThaiHoatDong.setText("Đang hoạt động");
    } else if ("Offline".equals(nhanVien.getTrangThaiHoatDong())) {
        circleTrangThai.setFill(Color.GRAY);  // Màu xám nếu trạng thái là "Offline"
        textTrangThaiHoatDong.setText("Không hoạt động");
    }
    
    // Cập nhật hình ảnh nhân viên nếu có
    if (nhanVien.getAnhChanDung() != null && !nhanVien.getAnhChanDung().isEmpty()) {
        Image image = ImageUtils.loadFromResourcesOrDefault(nhanVien.getAnhChanDung(), "/icons/profile.png");
        imgChanDung.setImage(image);
    } else {
        imgChanDung.setImage(new Image(getClass().getResourceAsStream("/icons/profile.png"))); // Hình ảnh mặc định
    }
}


@FXML
public void quayLai() {
    // Đóng cửa sổ hiện tại
    Stage currentStage = (Stage) btnQuayLai.getScene().getWindow();
    currentStage.close();  // Đóng cửa sổ thêm nhân viên
}
}

