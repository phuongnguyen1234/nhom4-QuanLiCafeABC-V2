package com.frontend;

import com.backend.dto.NhanVienDTO;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class NhanVienUI {

    @FXML
    private TableView<NhanVienDTO> tableViewQuanLiHoSo;

    @FXML
    private TableColumn<NhanVienDTO, Integer> colSTT;

    @FXML
    private TableColumn<NhanVienDTO, String> colMaNhanVien, colHoTen, colLoaiNhanVien, colViTri, colTrangThaiHoatDong, colTrangThaiDiLam;

    @FXML
    private TableColumn<NhanVienDTO, Void> colHanhDong;

    @FXML
    private CheckBox hienNVNghiViecCheckBox;

    /*private QuanLiHoSoController quanLiHoSoController;

    private final ObservableList<NhanVienDTO> nhanVienList = FXCollections.observableArrayList();

    private List<NhanVienDTO> danhSachNhanVien;

    public void setQuanLiHoSoController(QuanLiHoSoController quanLiHoSoController){
        this.quanLiHoSoController = quanLiHoSoController;
    }

    public QuanLiHoSoController getQuanLiHoSoController(){
        return quanLiHoSoController;
    }

    public List<NhanVienDTO> getDanhSachNhanVien(){
        return danhSachNhanVien;
    }

    // Phương thức khởi tạo dữ liệu cho bảng
    @FXML
    public void initialize() {

        // Liên kết các cột với thuộc tính của NhanVienDTO
        colMaNhanVien.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        colLoaiNhanVien.setCellValueFactory(new PropertyValueFactory<>("loaiNhanVien"));
        colViTri.setCellValueFactory(new PropertyValueFactory<>("viTri"));
        colTrangThaiHoatDong.setCellValueFactory(new PropertyValueFactory<>("trangThaiHoatDong"));
        colTrangThaiDiLam.setCellValueFactory(new PropertyValueFactory<>("quyenTruyCap"));

        // Cấu hình cột STT để hiển thị số thứ tự dòng
        colSTT.setCellValueFactory(param -> new javafx.beans.property.SimpleIntegerProperty(tableViewQuanLiHoSo.getItems().indexOf(param.getValue()) + 1).asObject());

        // Cấu hình cột hành động với nút "Sửa"
        colHanhDong.setCellFactory(col -> new TableCell<>() {
            private final Button xemButton = new Button("Xem");
            private final Button suaButton = new Button("Sửa");

            {
                // Thiết lập nút "Xem"
                ImageView viewIcon = new ImageView(getClass().getResource("/icons/eye.png").toExternalForm());
                viewIcon.setFitWidth(30);
                viewIcon.setFitHeight(30);
                xemButton.setGraphic(viewIcon);
                xemButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                xemButton.getStyleClass().add("button-view");
                xemButton.setOnAction(event -> xem(getTableRow().getItem()));

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

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, xemButton, suaButton));
                }
            }
        });

        tableViewQuanLiHoSo.getColumns().forEach(column -> {
            column.setReorderable(false);
        });
    }
    

     // Phương thức để lấy danh sách nhân viên từ controller và hiển thị trên TableView
     public void hienThiDanhSachNhanVien(List<NhanVienDTO> danhSachNhanVien) {        
        if (danhSachNhanVien.isEmpty()) {
            System.out.println("Danh sách nhân viên trống.");
        } else {
            System.out.println("Số lượng nhân viên: " + danhSachNhanVien.size());
        }
        // Cập nhật danh sách nhân viên vào TableView
        nhanVienList.clear(); // Xóa dữ liệu cũ nếu có
        nhanVienList.addAll(danhSachNhanVien); // Thêm dữ liệu mới
        tableViewQuanLiHoSo.setItems(nhanVienList);
    }


    private void xem(NhanVienDTO nhanVien) {
        try {
            if (nhanVien == null) {
                // Nếu không có nhân viên nào được chọn, hiển thị thông báo lỗi
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Chưa chọn nhân viên");
                alert.setContentText("Vui lòng chọn một nhân viên để xem thông tin.");
                alert.showAndWait();
                return;
            }
    
            // Lấy mã nhân viên đã chọn
            String maNhanVien = nhanVien.getMaNhanVien();
    
            // Truy xuất thông tin chi tiết từ cơ sở dữ liệu
            NhanVienDTO nhanVienChiTiet = quanLiHoSoController.layThongTinNhanVien(maNhanVien);
    
            // Tải file FXML của form xem chi tiết
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/thongTinNhanVienForm.fxml"));
            Parent root = loader.load();
    
            // Lấy controller của form chi tiết
            ThongTinNhanVienScreen controller = loader.getController();
            controller.setMainScreen(this); // Truyền MainScreenController vào ThongTinNhanVienScreen
    
            // Hiển thị thông tin chi tiết nhân viên trên form
            controller.hienThiThongTin(nhanVienChiTiet);
    
            // Tạo một cửa sổ mới để hiển thị form xem chi tiết
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể mở form xem chi tiết.");
            alert.setContentText("Vui lòng kiểm tra lại file FXML hoặc logic xử lý.");
            alert.showAndWait();
        }
    }
    


    private void sua(NhanVienDTO nhanVien) {
        try {
            if (nhanVien == null) {
                // Nếu không có nhân viên nào được chọn, hiển thị thông báo lỗi
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Chưa chọn nhân viên");
                alert.setContentText("Vui lòng chọn một nhân viên để chỉnh sửa.");
                alert.showAndWait();
                return;
            }
    
            // Lấy mã nhân viên đã chọn
            String maNhanVien = nhanVien.getMaNhanVien();
    
            // Truy xuất thông tin chi tiết từ cơ sở dữ liệu
            NhanVienDTO nhanVienChiTiet = quanLiHoSoController.layThongTinNhanVien(maNhanVien);
        
            // Tải file FXML của form chỉnh sửa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chinhSuaNhanVienForm.fxml"));
            Parent root = loader.load();
            ChinhSuaNhanVienScreen controller = loader.getController();
            controller.setMainScreen(this); // Truyền MainScreenController vào ChinhSuaNhanVienScreen

            // Hiển thị thông tin chi tiết trên form chỉnh sửa
            controller.hienThiThongTin(nhanVienChiTiet);
        
            // Tạo một cửa sổ mới để hiển thị form chỉnh sửa
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();        

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể mở form chỉnh sửa.");
            alert.setContentText("Vui lòng kiểm tra lại file FXML hoặc logic xử lý.");
            alert.showAndWait();
        }
    }
    
    
    

    @FXML
    private void them(ActionEvent event) {
    try {
        // Tải FXML của màn hình thêm nhân viên
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/themNhanVienForm.fxml"));
        Parent root = loader.load();

        // Lấy controller của màn hình thêm nhân viên nếu cần thiết
        ThemNhanVienScreen controller = loader.getController();
        
        // Truyền tham chiếu tới màn hình chính (QuanLiHoSoScreen) nếu cần
        controller.setMainScreen(this); 

        // Tạo và hiển thị cửa sổ mới (dialog)
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Cửa sổ modal
        stage.setTitle("Thêm nhân viên");
        stage.setScene(new Scene(root, 680, 800)); // Kích thước cửa sổ
        stage.setResizable(false); // Không cho phép thay đổi kích thước
        stage.showAndWait();

    } catch (Exception e) {
        e.printStackTrace();
    }
}


*/
}
