package com.frontend;

import java.util.List;
import java.util.Objects;

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
            donGiaText.setText("Đơn giá: " + String.format("%,d", mon.getDonGia()) + " VND");
            soLuongSpinner.getValueFactory().setValue(mon.getSoLuong());
            yeuCauKhacTextArea.setText(mon.getYeuCauKhac());
        }
    }

    @FXML
    private void capNhat() {
        if (mon != null) {
            int soLuongMoi = soLuongSpinner.getValue();
            String yeuCauKhacMoi = yeuCauKhacTextArea.getText();

            // Cập nhật thông tin cho món hiện tại (this.mon)
            // this.mon là một tham chiếu đến đối tượng trong danhSachMonTrongDon của ThucDonUI
            this.mon.setSoLuong(soLuongMoi);
            this.mon.setYeuCauKhac(yeuCauKhacMoi);
            this.mon.setTamTinh(soLuongMoi * this.mon.getDonGia());

            // Kiểm tra xem có món nào khác trong đơn hàng giống hệt món vừa sửa không để gộp
            List<MonTrongDonDTO> danhSachHienTai = thucDonUI.layDanhSachMonTrongDon();
            MonTrongDonDTO monCanGopVao = null;
            int indexMonGoc = this.monIndex; // Lưu lại index của món đang sửa

            for (int i = 0; i < danhSachHienTai.size(); i++) {
                if (i == indexMonGoc) { // Bỏ qua chính nó
                    continue;
                }
                MonTrongDonDTO monKiemTra = danhSachHienTai.get(i);
                if (monKiemTra.getMaMon().equals(this.mon.getMaMon()) &&
                    Objects.equals(monKiemTra.getYeuCauKhac(), this.mon.getYeuCauKhac())) {
                    monCanGopVao = monKiemTra;
                    break;
                }
            }

            if (monCanGopVao != null) {
                // Gộp số lượng và xóa món gốc đã sửa
                monCanGopVao.setSoLuong(monCanGopVao.getSoLuong() + this.mon.getSoLuong());
                monCanGopVao.setTamTinh(monCanGopVao.getSoLuong() * monCanGopVao.getDonGia());
                danhSachHienTai.remove(this.mon); // Xóa món gốc (this.mon) vì đã gộp vào monCanGopVao
            } else {
                // Không có món nào để gộp, món đã được cập nhật tại vị trí monIndex (thông qua this.mon)
                // Không cần làm gì thêm ở đây vì this.mon đã được cập nhật ở trên.
            }

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
