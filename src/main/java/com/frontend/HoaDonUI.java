package com.frontend;

import java.time.LocalDateTime;

import com.backend.dto.DonHangDTO;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class HoaDonUI {

    @FXML
    private TextField timKiemTheoMaTextField;

    @FXML
    private DatePicker thoiGian;

    @FXML
    private TableView<DonHangDTO> tableDonHang;

    @FXML
    private TableColumn<DonHangDTO, String> colSTT, colMaDonHang, colNhanVienTaoDon;

    @FXML
    private TableColumn<DonHangDTO, Integer> colTongTien;

    @FXML
    private TableColumn<DonHangDTO, LocalDateTime> colThoiGianDat;

    @FXML
    private TableColumn<DonHangDTO, Void> colHanhDong;

    @FXML
    private Pagination phanTrang;

/* 
    private DonHangController donHangController; // Controller xử lý logic
    private TrangChuScreen trangChuScreen;
    private NhanVien nhanVien;
    private static final int SO_DONG_MOI_TRANG = 50;

    private final ObservableList<DonHangDTO> donHangList = FXCollections.observableArrayList();

    public void setDonHangController(DonHangController donHangController){
        this.donHangController = donHangController;
    }

    public DonHangController getDonHangController(){
        return donHangController;
    }

    public void setTrangChuScreen(TrangChuScreen trangChuScreen){
        this.trangChuScreen = trangChuScreen;
    }

    public void setNhanVien(NhanVien nhanVien){
        this.nhanVien = nhanVien;
    }

    @FXML
    public void initialize() {
        // Setup các cột
        colSTT.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            String.valueOf(tableDonHang.getItems().indexOf(data.getValue()) + 1)));
        colMaDonHang.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMaDonHang()));
        colNhanVienTaoDon.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTenNhanVien()));
        colTongTien.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTongTien()));
        colThoiGianDat.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getThoiGianDatHang()));

        // Thêm cột Hành Động với nút "Xem Chi Tiết"
        colHanhDong.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();

            {
                // Tạo Image từ đường dẫn biểu tượng
                Image iconImage = new Image(getClass().getResource("/icons/eye.png").toExternalForm());
                ImageView icon = new ImageView(iconImage);
                icon.setFitHeight(25); // Kích thước của biểu tượng
                icon.setFitWidth(25);

                // Gán biểu tượng cho nút
                btn.setGraphic(icon);

                // Thêm style cho nút (nền xanh, viền bo góc)
                btn.setStyle("-fx-background-color: #007BFF; -fx-background-radius: 5; -fx-padding: 5; -fx-border-color: transparent; -fx-cursor: hand;");

                btn.setOnAction(event -> {
                    DonHangDTO donHang = getTableView().getItems().get(getIndex());
                    if (donHang != null) {
                        hienThiChiTietDonHang(donHang);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        tableDonHang.getColumns().forEach(column -> {
            column.setReorderable(false);
        });

        phanTrang.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            capNhatTrang(newIndex.intValue());
        });
    }

    private void capNhatTrang(int soTrang) {
        int tuViTri = soTrang * SO_DONG_MOI_TRANG; // Vị trí bắt đầu
        int denViTri = Math.min(tuViTri + SO_DONG_MOI_TRANG, donHangList.size()); // Vị trí kết thúc
    
        ObservableList<DonHangDTO> duLieuTrang = FXCollections.observableArrayList(
            donHangList.subList(tuViTri, denViTri)
        );
        tableDonHang.setItems(duLieuTrang);
    }

    @FXML
    void timKiem() {
        hienThiDanhSachDonHang(donHangController.timKiemTheoMa(timKiemTheoMaTextField.getText().trim()));
    }

    @FXML
    void loc() {
        hienThiDanhSachDonHang(donHangController.locTheoThoiGian(thoiGian.getValue()));
    }

    @FXML
    void xuatBaoCao(ActionEvent event) {
        try {
            // Tải file FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/previewExcelBaoCaoScreen.fxml"));
            Parent root = loader.load();
            // Lấy controller của màn hình xuất báo cáo
            XuatBaoCaoScreen xuatBaoCaoController = loader.getController();
            // Truyền dữ liệu
            xuatBaoCaoController.setDonHangController(donHangController);
            xuatBaoCaoController.setNhanVien(nhanVien);
            xuatBaoCaoController.setData(donHangList, thoiGian.getValue());
            // Hiển thị màn hình mới
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Xuất báo cáo");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            hienThongBao("Có lỗi xảy ra khi mở giao diện xuất báo cáo.");
        }
    }

    public void hienThongBao(String noiDung) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Thông Báo");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    public void hienThiDanhSachDonHang(List<DonHangDTO> danhSachDonHang) {
        donHangList.clear();
        donHangList.addAll(danhSachDonHang);
    
        int soTrang = (int) Math.ceil((double) donHangList.size() / SO_DONG_MOI_TRANG);
        phanTrang.setPageCount(soTrang);
        phanTrang.setCurrentPageIndex(0);
    
        // Hiển thị dữ liệu của trang đầu tiên
        capNhatTrang(0);
    }

    public void hienThiChiTietDonHang(DonHangDTO donHang){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chiTietDonHangScreen.fxml"));
            Parent root = loader.load();
            
            ChiTietDonHangScreen controller = loader.getController();
            controller.setDonHangController(donHangController);
            controller.setDonHangScreen(this);
            controller.initData(donHang);
            // Tạo một instance của controller với donHang
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Modal dialog
            stage.setScene(new Scene(root));
            stage.setTitle("Chi tiết đơn hàng " + donHang.getMaDonHang());
            stage.setResizable(false);
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */
}
