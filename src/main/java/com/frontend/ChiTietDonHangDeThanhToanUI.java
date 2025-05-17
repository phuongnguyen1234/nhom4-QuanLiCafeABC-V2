package com.frontend;

import com.backend.dto.MonTrongDonDTO;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class ChiTietDonHangDeThanhToanUI {
    @FXML
    private Text tenNhanVienText, tongTienText;

    @FXML
    private TableView<MonTrongDonDTO> tableViewDonHang;

    @FXML
    private TableColumn<MonTrongDonDTO, String> colSTT, colTenCaPhe, colYeuCauKhac;

    @FXML
    private TableColumn<MonTrongDonDTO, Integer> colSoLuong, colDonGia, colTamTinh;

    /*private DonHangDTO donHang;
    private TaoDonGoiDoMoiController taoDonController;
    private final ObservableList<MonDTO> danhSachCaPheTrongDon = FXCollections.observableArrayList();

    private ThucDonScreen thucDonScreen;

    public void setThucDonScreen(ThucDonScreen thucDonScreen){
        this.thucDonScreen = thucDonScreen;
    }

    public void setTaoDonController(TaoDonGoiDoMoiController taoDonController){
        this.taoDonController = taoDonController;
    }

    @FXML
    public void initialize() {
        // Cấu hình các cột trong TableView
        colTenCaPhe.setCellValueFactory(new PropertyValueFactory<>("tenCaPhe"));
        colYeuCauKhac.setCellValueFactory(new PropertyValueFactory<>("yeuCauDacBiet"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colTamTinh.setCellValueFactory(new PropertyValueFactory<>("tamTinh"));

        // Gắn dữ liệu TableView với danh sách cà phê trong đơn
        tableViewDonHang.setItems(danhSachCaPheTrongDon);
    }

    public void loadDonHangData() {
        if (donHang != null) {
            tenNhanVienText.setText("Nhân viên tạo đơn: " + donHang.getTenNhanVien());
            tongTienText.setText("Tổng tiền: " + donHang.getTongTien() + " VND");
    
            // Tải dữ liệu bảng từ danh sách các món trong đơn hàng
            ObservableList<MonDTO> chiTietDon = FXCollections.observableArrayList(donHang.getDanhSachCaPheTrongDon());
    
            // Cập nhật cột số thứ tự (STT) bằng cách tính số thứ tự dựa vào index của dòng
            colSTT.setCellValueFactory(param -> {
                int rowIndex = tableViewDonHang.getItems().indexOf(param.getValue()) + 1; // Tính STT bắt đầu từ 1
                return new SimpleStringProperty(String.valueOf(rowIndex));
            });
    
            colTenCaPhe.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenMon()));
            colYeuCauKhac.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getYeuCauDacBiet()));
            colSoLuong.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSoLuong()).asObject());
            colDonGia.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDonGia()).asObject());
            colTamTinh.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTamTinh()).asObject());
    
            tableViewDonHang.setItems(chiTietDon);
        }
    }
    
    @FXML
    private void xacNhanThanhToan(ActionEvent event) {
        try {
            taoDonController.taoDonHang(donHang);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Tạo đơn hàng thành công");
            alert.showAndWait();

            // Đóng dialog
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            thucDonScreen.datLai();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể lưu hóa đơn.");
            alert.setContentText("Vui lòng kiểm tra lại kết nối.");
            alert.showAndWait();
        }
    }

    @FXML
    private void quayLai(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setDonHang(DonHangDTO donHang){
        this.donHang = donHang;
        loadDonHangData();
    }
*/
}
