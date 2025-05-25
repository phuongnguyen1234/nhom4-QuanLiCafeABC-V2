package com.frontend;

import com.backend.dto.MonTrongDonDTO;
import com.backend.utils.ImageUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ChinhSuaDonUI {
    @FXML
    private Text tenMonText, donGiaText;

    @FXML
    private ImageView anhMinhHoaImageView;

    @FXML
    private Spinner<Integer> soLuongSpinner;

    @FXML
    private TextArea yeuCauKhacTextArea;

    private MonTrongDonDTO mon;

    private int monIndex;
    public void setMonIndex(int monIndex) {
        this.monIndex = monIndex;
    }

    //private TaoDonGoiDoMoiController controller;
    private ThucDonUI thucDonUI;

    public void setThucDonUI(ThucDonUI thucDonUI) {
        this.thucDonUI = thucDonUI;
    }

    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1, 1);
        soLuongSpinner.setValueFactory(valueFactory);
    }

    public void setMon(MonTrongDonDTO mon) {
        this.mon = mon;
        loadChinhSuaDonData();
    }

    private void loadChinhSuaDonData() {
        if (mon != null) {
            tenMonText.setText("Tên: " + mon.getTenMon());
            // Sử dụng loadFromResourcesOrDefault với đường dẫn ảnh từ MonTrongDonDTO và một ảnh mặc định
            anhMinhHoaImageView.setImage(ImageUtils.loadFromResourcesOrDefault(mon.getAnhMinhHoa(), "/icons/loading.png"));
            donGiaText.setText("Đơn giá: " + mon.getDonGia() + " VND");
            soLuongSpinner.getValueFactory().setValue(mon.getSoLuong());
            yeuCauKhacTextArea.setText(mon.getYeuCauKhac());
        }
    }

    @FXML
    private void capNhat() {
        if (mon != null) {
            int soLuong = soLuongSpinner.getValue();
            String yeuCau = yeuCauKhacTextArea.getText();
            int tamTinh = soLuong*mon.getDonGia();

            mon.setSoLuong(soLuong);
            mon.setYeuCauKhac(yeuCau);
            mon.setTamTinh(tamTinh);

            thucDonUI.layDanhSachMonTrongDon().set(monIndex, mon);
            thucDonUI.hienThiDanhSachMonTrongDon();
            thucDonUI.capNhatTongTien(thucDonUI.layDanhSachMonTrongDon());

            tenMonText.getScene().getWindow().hide();
        }
    }

    @FXML
    private void quayLai() {
        tenMonText.getScene().getWindow().hide();
    }
} 
