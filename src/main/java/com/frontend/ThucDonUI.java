package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.DonHangDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
import com.backend.model.NhanVien;
import com.backend.utils.ImageUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import javafx.stage.Modality;
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
    private Label loadingLabel;

    @FXML
    private ScrollPane scrollPaneThucDon;

    private DonHangDTO donHang; //bien luu data de hien thi len man hinh

    private final ObservableList<MonTrongDonDTO> danhSachMonTrongDon = FXCollections.observableArrayList();

    private NhanVien nhanVien = new NhanVien();

    private final HttpClient client = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    private final ExecutorService imageLoaderExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public void setTrangChuUI(TrangChuUI trangChuUI) {
        this.trangChuUI = trangChuUI;
    }

    public void setNhanVien(NhanVien nhanVien){
        this.nhanVien = nhanVien;
    }

    @FXML
    public void initialize() {
        //hien thi loading
        loadingLabel.setVisible(true);
        btnQuanLiThucDon.setDisable(true);
        danhMucCombobox.setDisable(true);

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

        //khoi tao comboBox
        Task<List<DanhMuc>> loadDanhMucTask = new Task<>() {
            @Override
            protected List<DanhMuc> call() throws Exception {
                return layDanhSachDanhMuc();
            }
        };
    
        loadDanhMucTask.setOnSucceeded(e -> {
            List<DanhMuc> fetchedDanhMucWithItems = loadDanhMucTask.getValue();
            this.allDanhMucWithItems.clear();
            this.allDanhMucWithItems.addAll(fetchedDanhMucWithItems);

            // Populate tatCaDanhMucList (List<DanhMucKhongMonDTO>) for QuanLiThucDonUI and other potential uses
            this.tatCaDanhMucList.clear();
            for (DanhMuc dm : this.allDanhMucWithItems) {
                this.tatCaDanhMucList.add(new DanhMucKhongMonDTO(dm.getMaDanhMuc(), dm.getTenDanhMuc(), dm.getLoai(), dm.getTrangThai()));
            }

            // Prepare ComboBox items: active DanhMucKhongMonDTOs, sorted
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
            // Ẩn label loading
            loadingLabel.setVisible(false);

            // Initial menu display: active DanhMuc with items, sorted
            List<DanhMuc> danhMucCoMonHoatDongList = this.allDanhMucWithItems.stream()
                .filter(dm -> !"Ngừng hoạt động".equalsIgnoreCase(dm.getTrangThai()))
                .sorted(Comparator.comparing(dm -> loaiUuTienMap.getOrDefault(dm.getLoai(), Integer.MAX_VALUE)))
                .collect(Collectors.toList());
            hienThiThucDon(danhMucCoMonHoatDongList);

            btnQuanLiThucDon.setDisable(false);
            danhMucCombobox.setDisable(false);
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

        loadDanhMucTask.setOnFailed(e -> {
            loadingLabel.setText("Tải dữ liệu thất bại!");
            loadDanhMucTask.getException().printStackTrace();
        });

        Thread thread = new Thread(loadDanhMucTask);
        thread.setDaemon(true);
        thread.start();

        // Xử lý sự kiện khi chọn danh mục
        danhMucCombobox.setOnAction(event -> {
            DanhMucKhongMonDTO selectedDanhMucDTO = danhMucCombobox.getValue();
            if (selectedDanhMucDTO == null || selectedDanhMucDTO == danhMucTatCa) {
                // "Tất cả": hiển thị thực đơn cho các danh mục hoạt động (List<DanhMuc>), sorted
                List<DanhMuc> activeDanhMucToDisplay = this.allDanhMucWithItems.stream()
                    .filter(dm -> !"Ngừng hoạt động".equalsIgnoreCase(dm.getTrangThai()))
                    .sorted(Comparator.comparing(dm -> loaiUuTienMap.getOrDefault(dm.getLoai(), Integer.MAX_VALUE)))
                    .collect(Collectors.toList());
                hienThiThucDon(activeDanhMucToDisplay);
            } else {
                // Specific DanhMucKhongMonDTO selected
                // Find the corresponding DanhMuc object (with items) from allDanhMucWithItems
                this.allDanhMucWithItems.stream()
                    .filter(dm -> dm.getMaDanhMuc() == selectedDanhMucDTO.getMaDanhMuc())
                    .findFirst()
                    .ifPresentOrElse(
                        danhMucWithItems -> hienThiThucDon(List.of(danhMucWithItems)),
                        () -> hienThiThucDon(new ArrayList<>()) // Fallback: display nothing if not found
                    );
            }
        });

        // Gắn dữ liệu TableView với danh sách món trong đơn
        tableViewDatHang.setItems(danhSachMonTrongDon);
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
    
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chỉnh sửa " + mon.getTenMon() + " trong đơn hàng");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/edit-text.png")));
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
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Lỗi");
                errorAlert.setHeaderText("Không thể xóa món.");
                errorAlert.setContentText("Vui lòng thử lại sau.");
                errorAlert.showAndWait();
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
        vBoxThucDon.getChildren().clear();
        scrollPaneThucDon.setFitToHeight(false);

        for (DanhMuc danhMuc : danhMucList) {
            List<Mon> danhSachMonBan = danhMuc.getMonList().stream()
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
            vBoxThucDon.getChildren().add(danhMucBox);
        }
    }

    @FXML
    private void thanhToan() {
        //test
        nhanVien.setMaNhanVien("NV000");
        nhanVien.setHoTen("Admin");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chiTietDonHangDeThanhToan.fxml"));
            Parent root = loader.load();

            // Tải controller của form hóa đơn
            ChiTietDonHangDeThanhToanUI controller = loader.getController();

            //set thong tin don hang
            donHang = new DonHangDTO();
            donHang.setMaNhanVien(nhanVien.getMaNhanVien());
            donHang.setHoTen(nhanVien.getHoTen());
            donHang.setDanhSachMonTrongDon(danhSachMonTrongDon);
            int tongTien = 0;
            for (MonTrongDonDTO monDTO : danhSachMonTrongDon) {
                tongTien += monDTO.getTamTinh(); // Cộng giá trị tamTinh của từng món
            }
            donHang.setTongTien(tongTien);

            controller.setDonHang(donHang);
            controller.setThucDonUI(this);

            // Hiển thị dialog
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Hóa đơn");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/invoice.png")));
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
            MonTrongDonDTO monTrongDon = new MonTrongDonDTO();
            monTrongDon.setTenMon(mon.getTenMon());
            monTrongDon.setDonGia(mon.getDonGia());
            monTrongDon.setMaMon(mon.getMaMon());
            monTrongDon.setAnhMinhHoa(mon.getAnhMinhHoa()); // Thêm dòng này để truyền đường dẫn ảnh
            monTrongDon.setYeuCauKhac("");

            controller.setMon(monTrongDon);
            controller.setThucDonUI(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thêm " + mon.getTenMon() + " vào đơn");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/add.png")));
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
        tongTienText.setText("Tổng tiền: " + tongTien + " VND");
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

    private List<DanhMuc> layDanhSachDanhMuc() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/danh-muc/all"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } else {
            throw new IOException("HTTP Error: " + response.statusCode());
        }
    }

    private VBox taoMonBox(Mon mon) {
        VBox monBox = new VBox(6);
        monBox.setAlignment(Pos.TOP_CENTER);
        monBox.setPrefSize(160, 200);
        monBox.getStyleClass().addAll("white-bg", "shadow", "radius");

        ImageView imageView = new ImageView(new Image(getClass().getResource("/icons/loading.png").toExternalForm()));
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(false);

        // Tải ảnh trong một thread riêng
        imageLoaderExecutor.submit(() -> {
            String imagePath = mon.getAnhMinhHoa();
            String defaultImagePath = "/icons/loading.png"; // Đảm bảo bạn có file này trong resources/icons

            // Sử dụng ImageUtils để tải ảnh từ resource, với fallback
            Image imageToLoad = ImageUtils.loadFromResourcesOrDefault(imagePath, defaultImagePath);

            // Cập nhật ImageView trên UI thread
            Platform.runLater(() -> {
                if (imageToLoad != null) { // Chỉ set ảnh nếu tải thành công (chính hoặc mặc định)
                    imageView.setImage(imageToLoad);
                }
                // Nếu imageToLoad là null (cả ảnh chính và mặc định đều lỗi), ImageView sẽ giữ ảnh "loading.png"
            });
        });

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
