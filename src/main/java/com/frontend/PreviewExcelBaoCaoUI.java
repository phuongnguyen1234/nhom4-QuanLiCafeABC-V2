package com.frontend;

import com.backend.dto.DonHangDTO;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PreviewExcelBaoCaoUI {

    @FXML
    private TableView<DonHangDTO> tableBaoCao;

    @FXML
    private TableColumn<DonHangDTO, String> colSTT, colMaDonHang, colTenNhanVien, colThoiGianDat;

    @FXML
    private TableColumn<DonHangDTO, Integer> colTongTien;

    @FXML
    private Label lblTongDoanhThu, lblSoDonDaTao, lblSoMonBanRa, lblNguoiXuatBaoCao;

    @FXML
    private Button btnQuayLai;

    /*private DonHangController donHangController;
    private List<DonHangDTO> danhSach;
    private LocalDate ngayLoc;
    private NhanVien nhanVien;

    /**
     * Khởi tạo giao diện
     
    public void setDonHangController(DonHangController donHangController){
        this.donHangController = donHangController;
    }

    public DonHangController getDonHangController(){
        return donHangController;
    }

    public void setNhanVien(NhanVien nhanVien){
        this.nhanVien = nhanVien;
    }


    @FXML
    public void initialize() {
        // Cài đặt các cột cho bảng
        colSTT.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(tableBaoCao.getItems().indexOf(data.getValue()) + 1)));
        colMaDonHang.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getMaDonHang()));
        colTenNhanVien.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getTenNhanVien()));
        colThoiGianDat.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getThoiGianDatHang().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        colTongTien.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTongTien()));
        
        tableBaoCao.getColumns().forEach(column -> {
            column.setReorderable(false);
        });
    }

    @FXML
    void xuatBaoCao() {
        // Lấy danh sách đơn hàng hiện tại
        List<DonHangDTO> danhSachDonHang = danhSach;

        // Gọi phương thức xuất báo cáo
        lblNguoiXuatBaoCao.setText(nhanVien.getTenNhanVien());
        donHangController.xuatBaoCao(nhanVien,danhSachDonHang, ngayLoc);

        // Cập nhật nhãn người xuất báo cáo
        

        // Hiển thị thông báo (tuỳ chọn)
        System.out.println("Báo cáo đã được xuất bởi: " + nhanVien.getTenNhanVien());
    }

    @FXML
    void quayLai(javafx.event.ActionEvent event) {
        btnQuayLai.getScene().getWindow().hide();
        System.out.println("Đã quay lại màn hình trước đó.");
    }

    public void setData(List<DonHangDTO> danhSachDonHang,LocalDate thoiGian) {
        // Kiểm tra xem các thành phần giao diện đã được khởi tạo chưa
        if (tableBaoCao != null) {
            tableBaoCao.getItems().setAll(danhSachDonHang);
        } else {
            System.err.println("tableBaoCao is null. Cannot set data.");
        }
        ngayLoc = thoiGian ;
        danhSach = danhSachDonHang;
        capNhatThongKe(danhSachDonHang);
        lblNguoiXuatBaoCao.setText(nhanVien.getTenNhanVien());
    }

    /**
     * Cập nhật thông tin tổng doanh thu, số đơn đã tạo, và số cà phê bán ra
     
    private void capNhatThongKe(List<DonHangDTO> danhSachDonHang) {
        // Lấy danh sách đơn hàng hiện tại từ danh sách đã lọc
        //<DonHangDTO> danhSachDonHang = donHangController.getTrangHienTai();

        if (danhSachDonHang != null && !danhSachDonHang.isEmpty()) {
            // Tổng doanh thu từ tất cả các đơn hàng trong danh sách lọc
            int tongDoanhThu = danhSachDonHang.stream()
                    .mapToInt(DonHangDTO::getTongTien)
                    .sum();

            // Tổng số đơn hàng trong danh sách lọc
            int soDonDaTao = danhSachDonHang.size();

            // Tổng số lượng cà phê bán ra trong các đơn hàng lọc
            int soCaPheBanRa = danhSachDonHang.stream()
                    .flatMap(donHang -> donHang.getDanhSachCaPheTrongDon().stream()) // Lấy danh sách cà phê từ mỗi đơn hàng
                    .mapToInt(MonDTO::getSoLuong) // Lấy số lượng từ mỗi đối tượng CaPheDTO
                    .sum();

            // Cập nhật giao diện
            lblTongDoanhThu.setText(tongDoanhThu + " VND");
            lblSoDonDaTao.setText(String.valueOf(soDonDaTao));
            lblSoMonBanRa.setText(String.valueOf(soCaPheBanRa));
        } else {
            // Nếu danh sách đơn hàng lọc trống
            lblTongDoanhThu.setText("0 VND");
            lblSoDonDaTao.setText("0");
            lblSoMonBanRa.setText("0");
        }
    } */
}
