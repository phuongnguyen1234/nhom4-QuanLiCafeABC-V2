package com.frontend;

import com.backend.dto.BangLuongDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChiTietBangLuongUI {

    @FXML
    private Text maBangLuongText, tenNhanVienText, loaiNhanVienText, viTriText, thangText,
            ngayCongText, nghiCoCongText, nghiKhongCongText, gioLamThemText,
            soDonDaTaoText, thuongDoanhThuText, luongThucNhanText, ghiChuText;

    @FXML
    private Button btnQuayLai;

    @FXML
    private AnchorPane mainAnchorPane;

    private BangLuongDTO bangLuong;

    public void setBangLuong(BangLuongDTO bangLuong) {
        this.bangLuong = bangLuong;
        hienThiThongTin();
    }

    @FXML
    public void initialize() {
        // Có thể thêm khởi tạo nếu cần
    }

    private void hienThiThongTin() {
        if (bangLuong != null) {
            maBangLuongText.setText(bangLuong.getMaBangLuong());
            tenNhanVienText.setText(bangLuong.getHoTen());
            loaiNhanVienText.setText(bangLuong.getLoaiNhanVien());
            viTriText.setText(bangLuong.getViTri());
            thangText.setText(bangLuong.getThang() != null ? bangLuong.getThang().toString() : "N/A");
            ngayCongText.setText(String.valueOf(bangLuong.getNgayCong()));
            nghiCoCongText.setText(String.valueOf(bangLuong.getNghiCoCong()));
            nghiKhongCongText.setText(String.valueOf(bangLuong.getNghiKhongCong()));
            gioLamThemText.setText(String.valueOf(bangLuong.getGioLamThem()));
            soDonDaTaoText.setText(String.valueOf(bangLuong.getDonDaTao()));
            thuongDoanhThuText.setText(String.format("%,d", bangLuong.getThuongDoanhThu()));
            luongThucNhanText.setText(String.format("%,d", bangLuong.getLuongThucNhan()));
            ghiChuText.setText(bangLuong.getGhiChu() != null && !bangLuong.getGhiChu().isEmpty() ? bangLuong.getGhiChu() : "Không có");
        }
    }

    @FXML
    private void quayLai() {
        Stage stage = (Stage) btnQuayLai.getScene().getWindow();
        stage.close();
    }
}

