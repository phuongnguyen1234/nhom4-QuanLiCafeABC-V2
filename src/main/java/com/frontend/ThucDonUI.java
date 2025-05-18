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
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.backend.dto.DanhMucMonKhongAnhDTO;
import com.backend.dto.DonHangDTO;
import com.backend.dto.MonKhongAnhDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.model.NhanVien;
import com.backend.utils.ImageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frontend.control.CapNhatThucDonController;
import com.frontend.control.TaoDonGoiDoMoiController;

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
import javafx.scene.control.ButtonType;
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
    private ComboBox<DanhMucMonKhongAnhDTO> danhMucCombobox;

    @FXML
    private Button btnThanhToan;

    @FXML
    private Label loadingLabel;

    @FXML
    private ScrollPane scrollPaneThucDon;

    private DonHangDTO donHang; //bien luu data de hien thi len man hinh

    private final ObservableList<MonTrongDonDTO> danhSachMonTrongDon = FXCollections.observableArrayList();

    private NhanVien nhanVien;

    private final HttpClient client = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper = new ObjectMapper();

    List<DanhMucMonKhongAnhDTO> tatCaDanhMucList = new ArrayList<>();

    private final DanhMucMonKhongAnhDTO danhMucTatCa = new DanhMucMonKhongAnhDTO(0, "Tất cả", "", "", new ArrayList<>());

    private TrangChuUI trangChuUI;

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
        Task<List<DanhMucMonKhongAnhDTO>> loadDanhMucTask = new Task<>() {
            @Override
            protected List<DanhMucMonKhongAnhDTO> call() throws Exception {
                return layDanhSachDanhMuc();
            }
        };
    
        loadDanhMucTask.setOnSucceeded(e -> {
            // Lưu lại tất cả danh mục (bao gồm cả ngừng hoạt động)
            tatCaDanhMucList = loadDanhMucTask.getValue();

            // Bản đồ thứ tự ưu tiên cho từng loại
            Map<String, Integer> loaiUuTien = new HashMap<>();
            loaiUuTien.put("Đồ uống", 1);
            loaiUuTien.put("Đồ ăn", 2);
            loaiUuTien.put("Khác", 3);

            // Lọc và sắp xếp theo thứ tự định nghĩa
            List<DanhMucMonKhongAnhDTO> danhMucHoatDongList = tatCaDanhMucList.stream()
                .filter(dm -> !"Ngừng hoạt động".equalsIgnoreCase(dm.getTrangThai()))
                .sorted(Comparator.comparing(dm -> loaiUuTien.getOrDefault(dm.getLoai(), Integer.MAX_VALUE)))
                .collect(Collectors.toList());

            // Thêm "Tất cả" vào đầu danh sách hiển thị
            List<DanhMucMonKhongAnhDTO> hienThiTrenCombobox = new ArrayList<>();
            hienThiTrenCombobox.add(danhMucTatCa);
            hienThiTrenCombobox.addAll(danhMucHoatDongList);

            // Gán danh sách vào ComboBox
            danhMucCombobox.setItems(FXCollections.observableArrayList(hienThiTrenCombobox));
            danhMucCombobox.setValue(danhMucTatCa); // Chọn mặc định

            // Ẩn label loading
            loadingLabel.setVisible(false);

            // Hiển thị thực đơn cho các danh mục hoạt động
            hienThiThucDon(danhMucHoatDongList);
        });

        danhMucCombobox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DanhMucMonKhongAnhDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTenDanhMuc());
            }
        });

        danhMucCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DanhMucMonKhongAnhDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTenDanhMuc());
            }
        });

        danhMucCombobox.setOnAction(event -> {
            DanhMucMonKhongAnhDTO selected = danhMucCombobox.getValue();
            if (selected == null || selected.getMaDanhMuc() == -1) {
                hienThiThucDon(tatCaDanhMucList);
            } else {
                hienThiThucDon(List.of(selected));
            }
        });

        loadDanhMucTask.setOnFailed(e -> {
            loadingLabel.setText("Tải dữ liệu thất bại!");
            loadDanhMucTask.getException().printStackTrace();
        });

        Thread thread = new Thread(loadDanhMucTask);
        thread.setDaemon(true);
        thread.start();

        ObservableList<DanhMucMonKhongAnhDTO> observableDanhMucList = FXCollections.observableArrayList(tatCaDanhMucList);
        danhMucCombobox.setItems(observableDanhMucList);
         
        // Xử lý sự kiện khi chọn danh mục
        danhMucCombobox.setOnAction(event -> {
            DanhMucMonKhongAnhDTO selectedDanhMuc = danhMucCombobox.getValue();
            if (selectedDanhMuc == null || selectedDanhMuc == danhMucTatCa) {
                // Chọn "Tất cả": hiển thị toàn bộ danh mục
                hienThiThucDon(tatCaDanhMucList);
            } else {
                // Chỉ hiển thị món trong danh mục được chọn
                hienThiThucDon(List.of(selectedDanhMuc));
            }
        });

        // Gắn dữ liệu TableView với danh sách món trong đơn
        tableViewDatHang.setItems(danhSachMonTrongDon);
        //nhanVien = session.getNhanVienByCurrentSession();
        //btnThemVaoThucDon.setVisible(false);
        //hien thi thuc don voi tat ca danh muc
        hienThiThucDon(tatCaDanhMucList);
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
            //controller.setController(taoDonController);
    
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chỉnh sửa " + mon.getTenMon() + " trong đơn hàng");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void xoa(MonTrongDonDTO mon) {
        if (mon == null) {
            return;
        }
    
        // Tạo Alert kiểu CONFIRMATION
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa " + mon.getTenMon() + " ra khỏi đơn?");
        alert.setContentText("Hành động này không thể hoàn tác.");
    
        // Hiển thị và chờ người dùng phản hồi
        Optional<ButtonType> result = alert.showAndWait();
    
        // Xử lý phản hồi của người dùng
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
    

    public void hienThiThucDon(List<DanhMucMonKhongAnhDTO> danhMucList) {
        vBoxThucDon.setMaxHeight(Region.USE_COMPUTED_SIZE);
        vBoxThucDon.getChildren().clear();
        scrollPaneThucDon.setFitToHeight(false);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (DanhMucMonKhongAnhDTO danhMuc : danhMucList) {
            List<MonKhongAnhDTO> danhSachMonBan = danhMuc.getMonList().stream()
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
                MonKhongAnhDTO mon = danhSachMonBan.get(i);

                VBox monBox = new VBox(6);
                monBox.setAlignment(Pos.TOP_CENTER);
                monBox.setPrefSize(160, 200);
                monBox.getStyleClass().addAll("white-bg", "shadow", "radius");

                ImageView imageView = new ImageView();
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
                imageView.setPreserveRatio(false);
                imageView.setImage(new Image(getClass().getResource("/icons/loading.png").toExternalForm()));

                // Tải ảnh bất đồng bộ
                executor.submit(() -> {
                    Image image = ImageUtils.getMonImage(mon.getMaMon());
                    Platform.runLater(() -> imageView.setImage(image));
                });

                imageView.setCursor(Cursor.HAND);
                imageView.setOnMouseClicked(event -> hienThiFormThemMon(mon));

                Text tenMonText = new Text(mon.getTenMon());
                tenMonText.setWrappingWidth(156);
                tenMonText.setTextAlignment(TextAlignment.CENTER);
                tenMonText.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-font-family: Open Sans;");

                VBox vBoxText = new VBox(tenMonText);
                vBoxText.setPrefHeight(45);
                vBoxText.setAlignment(Pos.TOP_CENTER);

                Text donGiaText = new Text(String.format("%d VND", mon.getDonGia()));
                donGiaText.setTextAlignment(TextAlignment.CENTER);
                donGiaText.setStyle("-fx-font-size: 16px; -fx-font-family: Open Sans;");

                monBox.getChildren().addAll(imageView, vBoxText, donGiaText);

                int row = i / columns;
                int col = i % columns;
                gridPane.add(monBox, col, row);
            }

            danhMucBox.getChildren().addAll(lblDanhMuc, gridPane);
            vBoxThucDon.getChildren().add(danhMucBox);
        }

        executor.shutdown();
    }

    @FXML
    private void thanhToan() {
        /*try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/chiTietDonHangDeThanhToan.fxml"));
            Parent root = loader.load();

            // Tải controller của form hóa đơn
            XacNhanDonHangScreen controller = loader.getController();

            //set thong tin don hang
            donHang = new DonHangDTO();
            donHang.setMaNhanVien(nhanVien.getMaNhanVien());
            donHang.setTenNhanVien(nhanVien.getTenNhanVien());
            donHang.setdanhSachMonTrongDon(taoDonController.laydanhSachMonTrongDon());
            int tongTien = 0;
            for (MonDTO monDTO : taoDonController.laydanhSachMonTrongDon()) {
                tongTien += monDTO.getTamTinh(); // Cộng giá trị tamTinh của từng món
            }
            donHang.setTongTien(tongTien);

            controller.setDonHang(donHang);
            controller.setThucDonUI(taoDonController);
            controller.setThucDonScreen(this);

            // Hiển thị dialog
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Hóa Đơn");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể mở form hóa đơn.");
            alert.setContentText("Vui lòng kiểm tra lại file FXML.");
            alert.showAndWait();
        } */
    }

    public void hienThiFormThemMon(MonKhongAnhDTO mon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/themVaoDon.fxml"));
            Parent root = loader.load();

            ThemVaoDonUI controller = loader.getController();
            MonTrongDonDTO monTrongDon = new MonTrongDonDTO();
            monTrongDon.setTenMon(mon.getTenMon());
            monTrongDon.setDonGia(mon.getDonGia());
            monTrongDon.setMaMon(mon.getMaMon());
            monTrongDon.setYeuCauKhac("");

            controller.setMon(monTrongDon);
            controller.setThucDonUI(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Thêm " + mon.getTenMon() + " vào đơn");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
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
            controller.setListMon(tatCaDanhMucList);

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

    private List<DanhMucMonKhongAnhDTO> layDanhSachDanhMuc() throws Exception {
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

}
