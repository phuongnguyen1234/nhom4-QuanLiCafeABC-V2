package com.frontend;

import java.util.Objects;

import com.backend.dto.MonTrongDonDTO;
import com.backend.utils.ImageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ThemVaoDonUI {
    @FXML
    private Text tenMonText, donGiaText;

    @FXML
    private ImageView anhMinhHoaImageView;

    @FXML
    private Spinner<Integer> soLuongSpinner;

    @FXML
    private TextArea yeuCauKhacTextArea;

    private MonTrongDonDTO mon;

    private ThucDonUI thucDonUI;

    public void setThucDonUI(ThucDonUI thucDonUI) {
        this.thucDonUI = thucDonUI;
    }

    public void initialize(){
        SpinnerValueFactory<Integer> valueFactory = 
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0, 1);

        soLuongSpinner.setValueFactory(valueFactory);

    }

    public void setMon(MonTrongDonDTO mon){
        this.mon = mon;

        tenMonText.setText("Tên: " + mon.getTenMon());
        // Sử dụng loadFromResourcesOrDefault với đường dẫn ảnh từ MonTrongDonDTO và một ảnh mặc định
        anhMinhHoaImageView.setImage(ImageUtils.loadFromResourcesOrDefault(mon.getAnhMinhHoa(), "/icons/loading.png"));
        donGiaText.setText("Đơn giá: " + mon.getDonGia() + " VND");
        soLuongSpinner.getValueFactory().setValue(1);
        yeuCauKhacTextArea.setText(mon.getYeuCauKhac());
    }

    @FXML
    private void themVaoDon() {
        if (this.mon == null) return;

        int soLuongMoiTuSpinner = soLuongSpinner.getValue();
        String yeuCauKhacMoi = yeuCauKhacTextArea.getText();

        if (soLuongMoiTuSpinner <= 0) {
            // Hiển thị thông báo lỗi hoặc không làm gì cả nếu số lượng không hợp lệ
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Số lượng không hợp lệ");
            alert.setHeaderText(null);
            alert.setContentText("Số lượng món phải lớn hơn 0.");
            alert.showAndWait();
            return; // Không đóng form, để người dùng sửa
        }

        boolean daCapNhatMonDaCo = false;
        for (MonTrongDonDTO monTrongDonHienCo : thucDonUI.layDanhSachMonTrongDon()) {
            // Kiểm tra cả mã món và yêu cầu khác
            if (monTrongDonHienCo.getMaMon().equals(this.mon.getMaMon()) &&
                Objects.equals(monTrongDonHienCo.getYeuCauKhac(), yeuCauKhacMoi)) {
                // Món đã tồn tại VÀ có cùng yêu cầu khác, cập nhật số lượng
                monTrongDonHienCo.setSoLuong(monTrongDonHienCo.getSoLuong() + soLuongMoiTuSpinner);
                monTrongDonHienCo.setTamTinh(monTrongDonHienCo.getSoLuong() * monTrongDonHienCo.getDonGia());
                daCapNhatMonDaCo = true;
                break;
            }
        }

        if (!daCapNhatMonDaCo) {
            // Món chưa tồn tại, thêm mới vào đơn
            // this.mon đã được set các thuộc tính cơ bản (maMon, tenMon, donGia, anhMinhHoa) từ ThucDonUI
            this.mon.setSoLuong(soLuongMoiTuSpinner);
            this.mon.setYeuCauKhac(yeuCauKhacMoi);
            this.mon.setTamTinh(soLuongMoiTuSpinner * this.mon.getDonGia());
            thucDonUI.layDanhSachMonTrongDon().add(this.mon);
        }

        thucDonUI.hienThiDanhSachMonTrongDon();
        thucDonUI.capNhatTongTien(thucDonUI.layDanhSachMonTrongDon());
        Stage stage = (Stage) soLuongSpinner.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void quayLai() {
        tenMonText.getScene().getWindow().hide();
    }
}
