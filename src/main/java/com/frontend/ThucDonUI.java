package com.frontend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.DonHangDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.dto.NhanVienDTO;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
import com.backend.utils.DTOConversion;
import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class ThucDonUI {
    @FXML
    private TableView<MonTrongDonDTO> tableViewDatHang;

    @FXML
    private TableColumn<MonTrongDonDTO, String> colTenMon, colYeuCauKhac;

    @FXML
    private TableColumn<MonTrongDonDTO, Integer> colSoLuong, colDonGia, colTamTinh;

    @FXML
    private TableColumn<MonTrongDonDTO, Void> colHanhDong;

    @FXML
    private VBox vBoxThucDon;

    @FXML
    private Text tongTienText;

    @FXML
    private ComboBox<DanhMucKhongMonDTO> danhMucCombobox;

    @FXML
    private Button btnThanhToan, btnQuanLiThucDon;

    @FXML
    private HBox loadingHbox;

    @FXML
    private ScrollPane scrollPaneThucDon;

    private DonHangDTO donHang; //bien luu data de hien thi len man hinh

    private final ObservableList<MonTrongDonDTO> danhSachMonTrongDon = FXCollections.observableArrayList();

    private NhanVienDTO nhanVien;

    List<DanhMucKhongMonDTO> tatCaDanhMucList = new ArrayList<>();

    // Biến mới để lưu trữ tất cả DanhMuc với danh sách Món đầy đủ từ server
    private final List<DanhMuc> allDanhMucWithItems = new ArrayList<>();

    private final DanhMucKhongMonDTO danhMucTatCa = new DanhMucKhongMonDTO(0, "Tất cả", "", "");

    private TrangChuUI trangChuUI;
    private final Map<String, Integer> loaiUuTienMap = new HashMap<>() {{
        put("Đồ uống", 1);
        put("Đồ ăn", 2);
        put("Khác", 3);
    }};

    // Cache để lưu trữ ảnh đã tải
    private final Map<String, Image> monImageCache = new HashMap<>();
    // Đổi thành public static final để TrangChuUI có thể truy cập
    public static final String DEFAULT_MON_IMAGE_PATH_STATIC = "/icons/loading.png";

    public void setTrangChuUI(TrangChuUI trangChuUI) {
        this.trangChuUI = trangChuUI;
    }

    public void setNhanVienDTO(NhanVienDTO nhanVien) {
        this.nhanVien = nhanVien;
    }

    private boolean coQuyenQuanLiThucDon = false; // Mặc định không có quyền

    public void setQuyenQuanLiThucDon(boolean coQuyen) {
        this.coQuyenQuanLiThucDon = coQuyen;
        capNhatTrangThaiNutQuanLy(); // Cập nhật trạng thái nút khi quyền thay đổi
    }

    private void capNhatTrangThaiNutQuanLy() {
        if (btnQuanLiThucDon != null) { // Đảm bảo nút đã được khởi tạo từ FXML
            // Nút quản lý thực đơn chỉ được enable nếu có quyền VÀ danh mục đã tải xong
            boolean danhMucDaTaiXong = !loadingHbox.isVisible();
            btnQuanLiThucDon.setDisable(!(this.coQuyenQuanLiThucDon && danhMucDaTaiXong));
        }
    }
    @FXML
    public void initialize() {
        //hien thi loading ban đầu
        loadingHbox.setVisible(true);
        vBoxThucDon.getChildren().clear(); // Xóa nội dung cũ nếu có
        danhMucCombobox.setDisable(true);
        btnQuanLiThucDon.setDisable(true); // Nút quản lý cũng disable ban đầu
        btnThanhToan.setDisable(true); // Nút thanh toán disable ban đầu

        tableViewDatHang.setPlaceholder(JavaFXUtils.createPlaceholder("Đơn trống!", "/icons/sad.png"));

        // Cấu hình các cột trong TableView
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colYeuCauKhac.setCellValueFactory(new PropertyValueFactory<>("yeuCauKhac"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colTamTinh.setCellValueFactory(new PropertyValueFactory<>("tamTinh"));
    
        colHanhDong.setCellFactory(col -> new TableCell<>() {
            private final Button suaButton = new Button("Sửa");
            private final Button xoaButton = new Button("Xóa");
    
            {
                suaButton.getStyleClass().add("btn-sua");
                suaButton.setOnAction(event -> sua(getTableRow().getItem()));
    
                xoaButton.getStyleClass().add("btn-xoa");
                xoaButton.setOnAction(event -> xoa(getTableRow().getItem()));
            }
    
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
    
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(10, suaButton, xoaButton);
                    hBox.setAlignment(Pos.CENTER);
                    setGraphic(hBox);
                }
            }
        });

        tableViewDatHang.getColumns().forEach(column -> {
            column.setReorderable(false);
        });

        danhMucCombobox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DanhMucKhongMonDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTenDanhMuc());
            }
        });

        danhMucCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DanhMucKhongMonDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTenDanhMuc());
            }
        });

        danhMucCombobox.setOnAction(event -> {
            DanhMucKhongMonDTO selectedDanhMucDTO = danhMucCombobox.getValue();
            if (selectedDanhMucDTO == null || selectedDanhMucDTO == danhMucTatCa) {
                List<DanhMuc> activeDanhMucToDisplay = this.allDanhMucWithItems.stream()
                    .filter(dm -> !"Ngừng hoạt động".equalsIgnoreCase(dm.getTrangThai()))
                    .sorted(Comparator.comparing(dm -> loaiUuTienMap.getOrDefault(dm.getLoai(), Integer.MAX_VALUE)))
                    .collect(Collectors.toList());
                hienThiThucDon(activeDanhMucToDisplay);
            } else {
                this.allDanhMucWithItems.stream()
                    .filter(dm -> dm.getMaDanhMuc() == selectedDanhMucDTO.getMaDanhMuc())
                    .findFirst()
                    .ifPresentOrElse(
                        danhMucWithItems -> hienThiThucDon(List.of(danhMucWithItems)),
                        () -> hienThiThucDon(new ArrayList<>()) 
                    );
            }
        });

        tableViewDatHang.setItems(danhSachMonTrongDon);
        hienThiDanhSachMonTrongDon(); // Cập nhật trạng thái nút thanh toán
        capNhatTrangThaiNutQuanLy(); // Cập nhật nút quản lý dựa trên quyền và trạng thái tải
    }
        
    // Phương thức mới để nhận dữ liệu đã cache từ TrangChuUI
    public void populateThucDon(List<DanhMuc> danhMucList, Map<String, Image> imageCache) {
        this.allDanhMucWithItems.clear();
        this.allDanhMucWithItems.addAll(danhMucList);

        this.monImageCache.clear();
        this.monImageCache.putAll(imageCache);

        this.tatCaDanhMucList.clear();
        for (DanhMuc dm : this.allDanhMucWithItems) {
            this.tatCaDanhMucList.add(new DanhMucKhongMonDTO(dm.getMaDanhMuc(), dm.getTenDanhMuc(), dm.getLoai(), dm.getTrangThai()));
        }

        List<DanhMucKhongMonDTO> danhMucKhongMonHoatDongList = this.allDanhMucWithItems.stream()
            .filter(dm -> !"Ngừng hoạt động".equalsIgnoreCase(dm.getTrangThai()))
            .map(dm -> new DanhMucKhongMonDTO(dm.getMaDanhMuc(), dm.getTenDanhMuc(), dm.getLoai(), dm.getTrangThai()))
            .sorted(Comparator.comparing(dto -> loaiUuTienMap.getOrDefault(dto.getLoai(), Integer.MAX_VALUE)))
            .collect(Collectors.toList());

        List<DanhMucKhongMonDTO> danhMucHienThiCombobox = new ArrayList<>();
        danhMucHienThiCombobox.add(danhMucTatCa);
        danhMucHienThiCombobox.addAll(danhMucKhongMonHoatDongList);

        danhMucCombobox.setItems(FXCollections.observableArrayList(danhMucHienThiCombobox));
        danhMucCombobox.setValue(danhMucTatCa);

        List<DanhMuc> danhMucCoMonHoatDongList = this.allDanhMucWithItems.stream()
            .filter(dm -> !"Ngừng hoạt động".equalsIgnoreCase(dm.getTrangThai()))
            .sorted(Comparator.comparing(dm -> loaiUuTienMap.getOrDefault(dm.getLoai(), Integer.MAX_VALUE)))
            .collect(Collectors.toList());
        
        hienThiThucDon(danhMucCoMonHoatDongList);
        loadingHbox.setVisible(false);
        capNhatTrangThaiNutQuanLy(); // Kích hoạt nút quản lý nếu có quyền
        danhMucCombobox.setDisable(false);

    }

    public void sua(MonTrongDonDTO mon) {
        if (mon == null) return;
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chinhSuaDon.fxml"));
            Parent root = loader.load();
    
            ChinhSuaDonUI controller = loader.getController();
            controller.setMon(mon);
            controller.setMonIndex(danhSachMonTrongDon.indexOf(mon));
            controller.setThucDonUI(this);
    
            Stage stage = JavaFXUtils.createDialog("Chỉnh sửa " + mon.getTenMon() + " trong đơn hàng", root, "/icons/edit-text.png");
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void xoa(MonTrongDonDTO mon) {
        if (mon == null) {
            return;
        }
    
        // Xử lý phản hồi của người dùng
        if (MessageUtils.showConfirmationDialog("Xác nhận xóa", "Bạn có chắc chắn muốn xóa " + mon.getTenMon() + " ra khỏi đơn?", "Hành động này không thể hoàn tác.", "/icons/bin.png", this.getClass())) {
            try {
                // Gọi hàm xóa trong TaoDonGoiDoMoiController
                danhSachMonTrongDon.remove(mon);
    
                // Cập nhật lại giao diện TableView
                hienThiDanhSachMonTrongDon();
                capNhatTongTien(danhSachMonTrongDon);
    
                System.out.println("Món đã được xóa: " + mon.getTenMon());
            } catch (Exception e) {
                e.printStackTrace();
                MessageUtils.showErrorMessage("Loi! Khong the xoa mon ra khoi don");
            }
        } else {
            System.out.println("Người dùng đã hủy.");
        }
    }
    
    public void hienThiDanhSachMonTrongDon() {
        tableViewDatHang.setItems(danhSachMonTrongDon);
        tableViewDatHang.refresh();
        capNhatTongTien(danhSachMonTrongDon);
        if(danhSachMonTrongDon.isEmpty()) btnThanhToan.setDisable(true);
        else btnThanhToan.setDisable(false);
    }

    public void hienThiThucDon(List<DanhMuc> danhMucList) {
        vBoxThucDon.setMaxHeight(Region.USE_COMPUTED_SIZE);
        scrollPaneThucDon.setFitToHeight(false);

        List<Node> newCategoryNodes = new ArrayList<>();

        for (DanhMuc danhMuc : danhMucList) {
            List<Mon> danhSachMonBan = danhMuc.getMonList().stream()
                    // Đảm bảo getMonList() không null trước khi stream
                    .filter(Objects::nonNull) 
                    .filter(mon -> "Bán".equalsIgnoreCase(mon.getTrangThai()))
                    .collect(Collectors.toList());

            if (danhSachMonBan.isEmpty()) continue;

            VBox danhMucBox = new VBox(10);
            danhMucBox.setPadding(new Insets(0, 0, 10, 0));

            Label lblDanhMuc = new Label(danhMuc.getTenDanhMuc());
            lblDanhMuc.getStyleClass().add("category-label");

            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(8, 8, 25, 8));

            int columns = 4;

            for (int i = 0; i < danhSachMonBan.size(); i++) {
                Mon mon = danhSachMonBan.get(i);
                VBox monBox = taoMonBox(mon);
                int row = i / columns;
                int col = i % columns;
                gridPane.add(monBox, col, row);
            }

            danhMucBox.getChildren().addAll(lblDanhMuc, gridPane);
            newCategoryNodes.add(danhMucBox);
        }
        vBoxThucDon.getChildren().setAll(newCategoryNodes); // Cập nhật một lần
    }

    @FXML
    private void thanhToan() {        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chiTietDonHangDeThanhToan.fxml"));
            Parent root = loader.load();

            // Tải controller của form hóa đơn
            ChiTietDonHangDeThanhToanUI controller = loader.getController();

            //set thong tin don hang
            donHang = new DonHangDTO();
            donHang.setMaNhanVien(nhanVien.getMaNhanVien());
            donHang.setHoTen(nhanVien.getTenNhanVien());
            donHang.setDanhSachMonTrongDon(danhSachMonTrongDon);
            int tongTien = 0;
            for (MonTrongDonDTO monDTO : danhSachMonTrongDon) {
                tongTien += monDTO.getTamTinh(); // Cộng giá trị tamTinh của từng món
            }
            donHang.setTongTien(tongTien);

            controller.setDonHang(donHang);
            controller.setThucDonUI(this);

            // Hiển thị dialog
            Stage stage = JavaFXUtils.createDialog("Đơn hàng", root, "/icons/invoice.png");
            stage.showAndWait();
        } catch (IOException e) {
            MessageUtils.showErrorMessage("Không thể mở hóa đơn.");
            e.printStackTrace();
        } 
    }

    public void hienThiFormThemMon(Mon mon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/themVaoDon.fxml"));
            Parent root = loader.load();

            ThemVaoDonUI controller = loader.getController();
            MonTrongDonDTO monTrongDon = DTOConversion.toMonTrongDonDTO(mon);

            controller.setMon(monTrongDon);
            controller.setThucDonUI(this);

            Stage stage = JavaFXUtils.createDialog("Thêm " + mon.getTenMon() + " vào đơn", root, "/icons/add.png");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void quanLiThucDon(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/quanLiThucDon.fxml"));
            Parent root = loader.load();

            QuanLiThucDonUI controller = loader.getController();
            controller.setTrangChuUI(trangChuUI);
            controller.setListMon(this.allDanhMucWithItems);

            // CHỈ cần gán vào center, KHÔNG cần mở Stage mới
            trangChuUI.getMainBorderPane().setCenter(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void capNhatTongTien(List<MonTrongDonDTO> danhSachMonTrongDon) {
        int tongTien = 0;
        // Lặp qua từng món trong danh sách và tính tổng tiền
        for (MonTrongDonDTO monDTO : danhSachMonTrongDon) {
            tongTien += monDTO.getTamTinh(); // Cộng giá trị tamTinh của từng món
        }
    
        // Cập nhật tổng tiền lên giao diện
        tongTienText.setText("Tổng tiền: " + String.format("%,d", tongTien) + " VND");
    }

    @FXML
    public void datLai() {
        if (MessageUtils.showConfirmationDialog("Xác nhận đặt lại", "Bạn có chắc chắn muốn đặt lại đơn hàng?", "Hành động này sẽ xóa tất cả món trong đơn hàng hiện tại.", "/icons/bin.png", this.getClass())) {
            resetDonHang();
            System.out.println("Đơn hàng đã được đặt lại.");
        } else {
            System.out.println("Người dùng đã hủy.");
        }
    } 

    public void resetDonHang() {
        // Xóa tất cả các món trong đơn
        danhSachMonTrongDon.clear();
            
        // Cập nhật lại TableView
        tableViewDatHang.setItems(danhSachMonTrongDon);
        tableViewDatHang.refresh();
        btnThanhToan.setDisable(true);
    
        // Cập nhật lại tổng tiền
        capNhatTongTien(danhSachMonTrongDon);
    }

    public List<MonTrongDonDTO> layDanhSachMonTrongDon() {
        return danhSachMonTrongDon;
    }

    private VBox taoMonBox(Mon mon) {
        VBox monBox = new VBox(6);
        monBox.setPadding(new Insets(5, 0, 0, 0));
        monBox.setAlignment(Pos.TOP_CENTER);
        monBox.setPrefSize(160, 200);
        monBox.getStyleClass().addAll("white-bg", "shadow", "radius");
        
        ImageView imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(false);

        String imagePath = mon.getAnhMinhHoa();
        Image imageToSet;

        if (imagePath != null && !imagePath.isEmpty()) {
            imageToSet = monImageCache.get(imagePath); // Lấy từ cache
            // Nếu không có trong cache (trường hợp hiếm sau khi pre-load), hoặc imagePath không hợp lệ, lấy ảnh mặc định từ cache
            if (imageToSet == null) {
                imageToSet = monImageCache.get(DEFAULT_MON_IMAGE_PATH_STATIC);
            }
        } else { // imagePath là null hoặc rỗng
            imageToSet = monImageCache.get(DEFAULT_MON_IMAGE_PATH_STATIC); // Lấy ảnh mặc định từ cache
        }
        if (imageToSet != null) {
            imageView.setImage(imageToSet);
        } else {
            imageView.setImage(new Image(getClass().getResource("/icons/loading.png").toExternalForm())); // Fallback cuối cùng
        }
        imageView.setCursor(Cursor.HAND);
        imageView.setOnMouseClicked(event -> hienThiFormThemMon(mon));

        Text tenMonText = new Text(mon.getTenMon());
        tenMonText.setWrappingWidth(156);
        tenMonText.setTextAlignment(TextAlignment.CENTER);
        tenMonText.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-font-family: Open Sans;");

        Text donGiaText = new Text(String.format("%,d VND", mon.getDonGia())); // Định dạng số tiền
        donGiaText.setTextAlignment(TextAlignment.CENTER);
        donGiaText.setStyle("-fx-font-size: 16px; -fx-font-family: Open Sans;");

        VBox vBoxText = new VBox(tenMonText);
        vBoxText.setPrefHeight(45);
        vBoxText.setAlignment(Pos.TOP_CENTER);

        monBox.getChildren().addAll(imageView, vBoxText, donGiaText);
        return monBox;
    }
}
