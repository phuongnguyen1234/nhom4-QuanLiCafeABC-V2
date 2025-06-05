package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.backend.dto.MonDTO;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class QuanLiThucDonUI {
    @FXML
    private Button btnDanhMuc, btnThemMon, btnQuayLai, btnTimKiem;

    @FXML
    private TableColumn<MonDTO, Integer> colSTT, colDonGia;

    @FXML
    private TableColumn<MonDTO, String> colMaMon, colTenMon, colTrangThai, colDanhMuc;

    @FXML
    private TableColumn<MonDTO, Void> colHanhDong;

    @FXML
    private TableView<MonDTO> tableViewMon;

    @FXML
    private Pagination phanTrang;

    @FXML
    private TextField timMonTheoTenTextField;

    private final ObservableList<MonDTO> list = FXCollections.observableArrayList();

    private List<DanhMuc> listDanhMuc = new ArrayList<>();

    private TrangChuUI trangChuUI;

    private QuanLiDanhMucUI quanLiDanhMucUI;

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // phan trang
    private static final int ROWS_PER_PAGE = 50;
    private List<MonDTO> allMonList = new ArrayList<>(); // Danh sách chứa tất cả món, không bị thay đổi bởi tìm kiếm
    private List<MonDTO> danhSachGoc = new ArrayList<>(); // Danh sách hiện tại để hiển thị và phân trang (sẽ trỏ tới allMonList hoặc kết quả lọc)

    public void setTrangChuUI(TrangChuUI trangChuUI) {
        this.trangChuUI = trangChuUI;
    }

    public List<DanhMuc> getListDanhMuc() {
        return listDanhMuc;
    }

    public QuanLiDanhMucUI getQuanLiDanhMucUI() {
        return this.quanLiDanhMucUI;
    }

    @FXML
    public void initialize() {
        // Liên kết các cột với thuộc tính của Mon
        colMaMon.setCellValueFactory(new PropertyValueFactory<>("maMon"));
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colDanhMuc.setCellValueFactory(new PropertyValueFactory<>("tenDanhMuc"));

        // Cấu hình cột STT để hiển thị số thứ tự dòng
        colSTT.setCellValueFactory(param -> {
            int rowIndex = danhSachGoc.indexOf(param.getValue());
            return new SimpleIntegerProperty(rowIndex + 1).asObject();
        });

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

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(suaButton);
                }
            }
        });

        tableViewMon.getColumns().forEach(column -> {
            column.setReorderable(false);
        });

        timMonTheoTenTextField.setOnAction(event -> timKiem());
    }

    public void setListMon(List<DanhMuc> listDanhMuc) {
        this.listDanhMuc = listDanhMuc;
        this.allMonList.clear(); // Xóa danh sách tất cả món cũ
        // this.danhSachGoc.clear(); // Không cần clear ở đây nữa, sẽ được gán lại

        for (DanhMuc danhMuc : listDanhMuc) {
            List<Mon> listMon = danhMuc.getMonList();
            for (Mon mon : listMon) {
                mon.setDanhMuc(danhMuc); // Gán danh mục cho món
                MonDTO dto = MonDTO.convertToMonDTO(mon);
                this.allMonList.add(dto);
            }
        }

        // Ban đầu, danh sách hiển thị là toàn bộ danh sách món
        this.danhSachGoc = new ArrayList<>(this.allMonList);
        setupPagination(); // gọi lại phân trang
    }

    public void sua(MonDTO mon) {
        // mo dialog danh muc
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chinhSuaMonTrongThucDon.fxml"));
            Parent node = loader.load();

            ChinhSuaMonTrongThucDonUI controller = loader.getController();
            controller.setMon(mon);
            // Tạo Stage dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
            dialogStage.setTitle("Danh mục");
            dialogStage.setScene(new Scene(node));
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image(getClass().getResource("/icons/edit-text.png").toExternalForm()));

            // Hiển thị và chờ người dùng đóng dialog
            dialogStage.showAndWait();
            reloadDanhSachMon(); // Tải lại danh sách món sau khi sửa
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void danhMuc() {
        // mo dialog danh muc
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/quanLiDanhMuc.fxml"));
            Parent node = loader.load();

            // Tạo Stage dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
            dialogStage.setTitle("Danh mục");
            dialogStage.setScene(new Scene(node));
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image(getClass().getResource("/icons/list.png").toExternalForm()));

            // Hiển thị và chờ người dùng đóng dialog
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void themMon() {
        // mo dialog danh muc
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/themVaoThucDon.fxml"));
            Parent node = loader.load();

            // Tạo Stage dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
            dialogStage.setTitle("Thêm vào thực đơn");
            dialogStage.setScene(new Scene(node));
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image(getClass().getResource("/icons/add.png").toExternalForm()));

            // Hiển thị và chờ người dùng đóng dialog
            dialogStage.showAndWait();
            reloadDanhSachMon(); // Tải lại danh sách món sau khi thêm
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void quayLai() {
        trangChuUI.thucDon(); // Gọi lại phương thức load giao diện Thực đơn
    }

    @FXML
    public void timKiem() {
        String tenMon = timMonTheoTenTextField.getText().trim().toLowerCase();
        if (tenMon.isEmpty()) {
            // Nếu ô tìm kiếm rỗng, hiển thị lại tất cả món từ allMonList
            this.danhSachGoc = new ArrayList<>(this.allMonList);
            setupPagination();
            return;
        }

        List<MonDTO> ketQua = new ArrayList<>();
        // Tìm kiếm trên danh sách allMonList (chứa tất cả MonDTO đã được chuyển đổi)
        for (MonDTO monDTO : this.allMonList) {
            if (monDTO.getTenMon().toLowerCase().contains(tenMon)) {
                ketQua.add(monDTO); // Thêm trực tiếp MonDTO đã có
            }
        }

        this.danhSachGoc = ketQua; // danhSachGoc bây giờ là kết quả đã lọc
        setupPagination();
    }

    private void capNhatTrang(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, danhSachGoc.size());

        List<MonDTO> pageData = danhSachGoc.subList(fromIndex, toIndex);
        tableViewMon.setItems(FXCollections.observableArrayList(pageData));
    }

    private void setupPagination() {
        int soTrang = (int) Math.ceil((double) danhSachGoc.size() / ROWS_PER_PAGE);
        phanTrang.setPageCount(soTrang == 0 ? 1 : soTrang);
        phanTrang.setCurrentPageIndex(0);
        capNhatTrang(0); // load trang đầu tiên

        phanTrang.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            capNhatTrang(newIndex.intValue());
        });
    }

    private void reloadDanhSachMon() {
        setDisableItems(true); // Vô hiệu hóa các nút trong khi tải dữ liệu
        Task<List<DanhMuc>> task = new Task<>() {
            @Override
            protected List<DanhMuc> call() throws Exception {
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
        };

        // Đặt sự kiện thành công
        task.setOnSucceeded(event -> {
            List<DanhMuc> danhMucList = task.getValue();
            setListMon(danhMucList); // Cập nhật danh sách món
            setDisableItems(false);
        });

        // Đặt sự kiện thất bại
        task.setOnFailed(event -> {
            Throwable exception = task.getException();
            System.err.println("Error loading data: " + exception.getMessage());
            exception.printStackTrace();
            setDisableItems(false);
        });

        new Thread(task).start();
    }

    private void setDisableItems(boolean disable) {
        tableViewMon.setDisable(disable);
        phanTrang.setDisable(disable);
        btnDanhMuc.setDisable(disable);
        btnThemMon.setDisable(disable);
        btnQuayLai.setDisable(disable);
        btnTimKiem.setDisable(disable);
        timMonTheoTenTextField.setDisable(disable);
    }

}
