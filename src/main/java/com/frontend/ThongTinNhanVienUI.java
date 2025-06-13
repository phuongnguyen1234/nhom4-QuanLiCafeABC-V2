package com.frontend;

import com.backend.dto.NhanVienDTO;
import com.backend.utils.ImageUtils;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ThongTinNhanVienUI {
    @FXML private Text textMaNhanVien, textHoTen, textGioiTinh, textNgaySinh, textQueQuan, textDiaChi, 
                        textSoDienThoai, textLoaiNhanVien, textViTri, textThoiGianVaoLam, textMucLuong, 
                        textEmail, textTrangThaiHoatDong, textTrangThai;

    @FXML 
    private Circle circleTrangThai;

    @FXML 
    private ImageView imgChanDung;

    @FXML
    private Label mucLuongLabel;

    @FXML 
    private Button btnQuayLai;

    @FXML
    private AnchorPane mainAnchorPane;

    private NhanVienDTO nhanVien;

    public void setNhanVien(NhanVienDTO nhanVien) {
        this.nhanVien = nhanVien;
    }

    @FXML
    public void initialize() {
        disableItems(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (nhanVien != null) {
                    hienThiThongTin(nhanVien);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            disableItems(false);
        });

        task.setOnFailed(event -> {
            disableItems(false);
        });

        new Thread(task).start();
    }
    

    // Hiển thị thông tin nhân viên
    private void hienThiThongTin(NhanVienDTO nhanVien) {
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
        // Cập nhật labelMucLuong và textMucLuong
                if ("Part-time".equals(nhanVien.getLoaiNhanVien())) {
                    mucLuongLabel.setText("Mức lương (VND/giờ):");
                } else if ("Full-time".equals(nhanVien.getLoaiNhanVien())) { 
                    mucLuongLabel.setText("Mức lương (VND/ngày):");
                } else {
                    mucLuongLabel.setText("Mức lương (VND):");
                }
        textMucLuong.setText(String.format("%,d", nhanVien.getMucLuong())); 
        textEmail.setText(nhanVien.getEmail());
        textTrangThaiHoatDong.setText(nhanVien.getTrangThaiHoatDong());
        textTrangThai.setText(nhanVien.getTrangThai() != null ? nhanVien.getTrangThai() : "N/A");

        if ("Online".equals(nhanVien.getTrangThaiHoatDong())) {
            circleTrangThai.setFill(Color.GREEN);  // Màu xanh lá nếu trạng thái là "Online"
            textTrangThaiHoatDong.setText("Online");
        } else if ("Offline".equals(nhanVien.getTrangThaiHoatDong())) {
            circleTrangThai.setFill(Color.GRAY);  // Màu xám nếu trạng thái là "Offline"
            textTrangThaiHoatDong.setText("Offline");
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

    private void disableItems(boolean value){
        mainAnchorPane.setDisable(value);
        btnQuayLai.setDisable(value);
    }
}

