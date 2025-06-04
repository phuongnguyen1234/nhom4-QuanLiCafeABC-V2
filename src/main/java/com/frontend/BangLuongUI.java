package com.frontend;

import com.backend.dto.BangLuongDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class BangLuongUI {
    @FXML
    private TableView<BangLuongDTO> tableViewBangLuong;

    @FXML
    private TableColumn<BangLuongDTO, String> colMaBangLuong, colHoTen, colThang, colGhiChu;

    @FXML
    private TableColumn<BangLuongDTO, Integer> colNgayCong, colNghiCoCong, colNghiKhongCong, colThuongDoanhThu, colLuongThucNhan;

    @FXML
    private TableColumn<BangLuongDTO, Void> colHanhDong; // Đảm bảo kiểu dữ liệu là Void vì đây là cột hành động.

    @FXML
    private ComboBox<String> thangCombobox, namCombobox;

    @FXML
    private Pagination phanTrang;

    private static final int SO_DONG_MOI_TRANG = 50;

    private final ObservableList<BangLuongDTO> bangLuongList = FXCollections.observableArrayList();

    /*private TaoVaChinhSuaBangLuongController taoLuongController;

    public void setTaoLuongController(TaoVaChinhSuaBangLuongController taoLuongController){
        this.taoLuongController = taoLuongController;
    }

    public TaoVaChinhSuaBangLuongController getTaoLuongController(){
        return taoLuongController;
    }

    public void initialize() {
        // Link columns with data from getters
        colMaBangLuong.setCellValueFactory(new PropertyValueFactory<>("maBangLuong"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        colThang.setCellValueFactory(new PropertyValueFactory<>("thang"));
        colNgayCong.setCellValueFactory(new PropertyValueFactory<>("soNgayCong"));
        colNghiCoCong.setCellValueFactory(new PropertyValueFactory<>("soNgayNghiCoCong"));
        colNghiKhongCong.setCellValueFactory(new PropertyValueFactory<>("soNgayNghiKhongCong"));
        colThuongDoanhThu.setCellValueFactory(new PropertyValueFactory<>("thuongDoanhThu"));
        colLuongThucNhan.setCellValueFactory(new PropertyValueFactory<>("luongThucNhan"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));

        // Cấu hình cột hành động với nút "Sửa"
        colHanhDong.setCellFactory(col -> new TableCell<>() {
            private final Button suaButton = new Button("Sửa");
        
            {
                // Tạo ImageView và thêm ảnh
                ImageView editIcon = new ImageView(getClass().getResource("/icons/write.png").toExternalForm());
                editIcon.setFitWidth(30); // Chiều rộng ảnh
                editIcon.setFitHeight(30); // Chiều cao ảnh
                suaButton.setGraphic(editIcon);
        
                suaButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        
                // Gắn class CSS vào nút
                suaButton.getStyleClass().add("button-edit");
        
                // Gắn sự kiện onAction cho nút "Sửa"
                suaButton.setOnAction(event -> sua(getTableRow().getItem()));
            }
        
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
        
                BangLuongDTO bangLuong = getTableRow() != null ? getTableRow().getItem() : null;
        
                if (empty || bangLuong == null || !"1".equals(bangLuong.getDuocPhepChinhSua())) {
                    setGraphic(null); // Ẩn nút nếu bảng lương không thể chỉnh sửa
                } else {
                    setGraphic(suaButton); // Hiển thị nút "Sửa" nếu bảng lương có thể chỉnh sửa
                }
            }
        });

        // Bind data to TableView
        tableViewBangLuong.setItems(bangLuongList);

        // Khởi tạo giá trị cho ComboBox tháng và năm
        thangCombobox.setItems(FXCollections.observableArrayList(
            IntStream.rangeClosed(1, 12).mapToObj(month -> String.format("%02d", month)).toList()
        ));

        namCombobox.setItems(FXCollections.observableArrayList(
            IntStream.rangeClosed(2024, Year.now().getValue()).mapToObj(String::valueOf).toList()
        ));

        // Đặt giá trị mặc định cho tháng và năm là hiện tại
        LocalDate currentDate = LocalDate.now();  // Lấy ngày hiện tại
        thangCombobox.getSelectionModel().select(String.format("%02d", currentDate.getMonthValue())); // Lấy tháng hiện tại
        namCombobox.getSelectionModel().select(String.valueOf(currentDate.getYear())); // Lấy năm hiện tại

        phanTrang.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            capNhatTrang(newIndex.intValue());
        });
    }

    private void sua(BangLuongDTO bangLuong) {
        if (bangLuong == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chiTietBangLuongForm.fxml"));
            Parent root = loader.load();
    
            ChinhSuaBangLuongScreen controller = loader.getController();
            controller.setBangLuong(bangLuong);
            controller.setBangLuongScreen(this); // Truyền tham chiếu đến BangLuongScreen
    
            // Tạo và hiển thị dialog
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Modal dialog
            stage.setTitle("Sửa bảng lương " + bangLuong.getMaBangLuong());
            stage.setScene(new Scene(root, 500, 690));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public void hienThiDanhSachBangLuong(List<BangLuongDTO> danhSachBangLuong) {
        bangLuongList.clear();
        bangLuongList.addAll(danhSachBangLuong);
    
        int soTrang = (int) Math.ceil((double) danhSachBangLuong.size() / SO_DONG_MOI_TRANG);
        phanTrang.setPageCount(soTrang);
        phanTrang.setCurrentPageIndex(0);
    
        // Hiển thị dữ liệu của trang đầu tiên
        capNhatTrang(0);
    }

    private void capNhatTrang(int soTrang) {
        int tuViTri = soTrang * SO_DONG_MOI_TRANG; // Vị trí bắt đầu
        int denViTri = Math.min(tuViTri + SO_DONG_MOI_TRANG, bangLuongList.size()); // Vị trí kết thúc
    
        ObservableList<BangLuongDTO> duLieuTrang = FXCollections.observableArrayList(
            bangLuongList.subList(tuViTri, denViTri)
        );
        tableViewBangLuong.setItems(duLieuTrang);
    }
    

    @FXML
    private void taoBangLuong() {
        try {
            // Gọi phương thức tạo bảng lương tháng này và nhận số lượng bảng lương được tạo mới
            int soLuongBangLuongDuocTao = taoLuongController.taoBangLuongThangNay();

            if (soLuongBangLuongDuocTao > 0) {
                // Hiển thị thông báo thành công nếu có bảng lương được tạo
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Bảng lương tháng này đã được tạo thành công! Số lượng bảng lương mới: " + soLuongBangLuongDuocTao);
                alert.showAndWait();
                hienThiDanhSachBangLuong(taoLuongController.layDanhSachBangLuongThangNay());
            } else {
                // Hiển thị thông báo nếu không có bảng lương nào được tạo
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Tất cả nhân viên đã có bảng lương. Không có bảng lương nào được tạo mới.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            // Hiển thị thông báo lỗi nếu có ngoại lệ xảy ra
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tạo bảng lương");
            alert.setContentText("Đã xảy ra lỗi trong quá trình tạo bảng lương. Vui lòng thử lại sau.");
            alert.showAndWait();

            // Ghi lại log lỗi (nếu cần)
            e.printStackTrace();
        }
    }

    @FXML
    private void loc(){
        try {
            hienThiDanhSachBangLuong(taoLuongController.layDanhSachBangLuongTheoThoiGian(thangCombobox.getValue(), namCombobox.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    } */
}
