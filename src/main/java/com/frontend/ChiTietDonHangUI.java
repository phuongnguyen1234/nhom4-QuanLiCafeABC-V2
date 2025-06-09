package com.frontend;

import com.backend.dto.DonHangDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.utils.PdfUtils; // Import lớp tiện ích mới

import javafx.collections.FXCollections; // Import MonTrongDonDTO
import javafx.event.ActionEvent; // Import FXCollections
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class ChiTietDonHangUI {

    @FXML
    private Text lblMaDonHang, lblTenNhanVien, lblTongTien, lblThoiGianDat;

    @FXML
    private TableView<MonTrongDonDTO> tableChiTietDonHang; // Change TableView type to MonTrongDonDTO

    @FXML
    private TableColumn<MonTrongDonDTO, String> colSTT, colTenMon, colYeuCauKhac; // Change TableColumn types

    @FXML
    private TableColumn<MonTrongDonDTO, Integer> colSoLuong, colDonGia, colTamTinh; // Change TableColumn types

    @FXML
    private Button btnQuayLai;

    private DonHangDTO donHang;

    public void initData(DonHangDTO donHang) {
        Label label = new Label("Đang tải...");
        label.setFont(javafx.scene.text.Font.font("Open Sans", 16));
        tableChiTietDonHang.setPlaceholder(label);

        this.donHang = donHang;
        lblMaDonHang.setText("Mã đơn hàng: "+donHang.getMaDonHang());
        lblTenNhanVien.setText("Nhân viên tạo đơn: "+donHang.getHoTen());
        lblTongTien.setText("Tổng tiền: " + donHang.getTongTien() + " VND");
        lblThoiGianDat.setText("Thời gian đặt hàng: " + donHang.getThoiGianDatHang());

        colSTT.setCellValueFactory(param ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(tableChiTietDonHang.getItems().indexOf(param.getValue()) + 1)));
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon")); 
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colYeuCauKhac.setCellValueFactory(new PropertyValueFactory<>("yeuCauKhac")); 
        colTamTinh.setCellValueFactory(new PropertyValueFactory<>("tamTinh")); 

        tableChiTietDonHang.setItems(FXCollections.observableArrayList(donHang.getDanhSachMonTrongDon())); // Set table items from DTO

        tableChiTietDonHang.getColumns().forEach(column -> {
            column.setReorderable(false);
        });
    }

    @FXML
    void inHoaDon() {
        if (this.donHang != null) {
            PdfUtils.taoHoaDonPDF(this.donHang);
        } else {
            // Có thể thêm thông báo lỗi nếu donHang là null
            System.err.println("Không có thông tin đơn hàng để in.");
        }
    }
    
    @FXML
    void quayLai(ActionEvent event) {
        btnQuayLai.getScene().getWindow().hide();
    }
}
