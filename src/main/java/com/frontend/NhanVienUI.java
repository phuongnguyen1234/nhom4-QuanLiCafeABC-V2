package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.text.Normalizer; // Thêm import này
import java.util.regex.Pattern; // Thêm import này
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.backend.dto.NhanVienDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils; // Import để lấy CookieManager
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

    @FXML private Button btnThemNhanVien; 
    @FXML private Button btnTimKiem;

    @FXML
    private TextField timNVTheoTenTextField;

    @FXML
    private Pagination phanTrang;

    private final ObservableList<NhanVienDTO> nhanVienList = FXCollections.observableArrayList();
    // Danh sách gốc chứa tất cả nhân viên tải từ backend
    private List<NhanVienDTO> allNhanVienMasterList = new ArrayList<>(); // Danh sách lưu trữ tất cả nhân viên
    // Danh sách sau khi lọc, dùng cho phân trang
    private List<NhanVienDTO> filteredNhanVienList = new ArrayList<>();
    private static final int ROWS_PER_PAGE = 20; // Số dòng mỗi trang

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    // Phương thức khởi tạo dữ liệu cho bảng
    @FXML
    public void initialize() {

        // Đặt placeholder ban đầu khi đang tải
        tableViewQuanLiHoSo.setPlaceholder(JavaFXUtils.createPlaceholder("Đang tải...", "/icons/loading.png"));

        // Liên kết các cột với thuộc tính của NhanVienDTO
        colMaNhanVien.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        colLoaiNhanVien.setCellValueFactory(new PropertyValueFactory<>("loaiNhanVien"));
        colViTri.setCellValueFactory(new PropertyValueFactory<>("viTri"));
        colTrangThaiDiLam.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThaiHoatDong.setCellValueFactory(new PropertyValueFactory<>("trangThaiHoatDong"));

                
        // Custom cell factory cho cột Trạng thái hoạt động
        colTrangThaiHoatDong.setCellFactory(column -> new TableCell<NhanVienDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });
       
        // Cấu hình cột STT để hiển thị số thứ tự dòng
        colSTT.setCellValueFactory(param -> {
            int pageIndex = phanTrang.getCurrentPageIndex();
            int rowIndexOnPage = tableViewQuanLiHoSo.getItems().indexOf(param.getValue());
            return new javafx.beans.property.SimpleIntegerProperty(pageIndex * ROWS_PER_PAGE + rowIndexOnPage + 1).asObject();
        });

        // Cấu hình cột hành động với nút "Sửa"
        colHanhDong.setCellFactory(col -> new TableCell<>() {
            private final Button xemButton = new Button("Xem");
            private final Button suaButton = new Button("Sửa");
        
        // Căn giữa colHanhDong
            {
                setAlignment(javafx.geometry.Pos.CENTER);
            }

            {
                // Thiết lập nút "Xem"
                ImageView viewIcon = new ImageView(getClass().getResource("/icons/eye.png").toExternalForm());
                viewIcon.setFitWidth(30);
                viewIcon.setFitHeight(30);
                xemButton.setGraphic(viewIcon); // Gán icon cho nút
                xemButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                xemButton.getStyleClass().add("button-view");
                xemButton.setOnAction(event -> xem(getTableRow().getItem()));

                // Tạo ImageView và thêm ảnh
                ImageView editIcon = new ImageView(getClass().getResource("/icons/write.png").toExternalForm());
                editIcon.setFitWidth(30); // Chiều rộng ảnh
                editIcon.setFitHeight(30); // Chiều cao ảnh
                suaButton.setGraphic(editIcon); // Gán icon cho nút
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
                    setGraphic(null); // Không hiển thị gì nếu dòng trống
                } else {
                    HBox hBox = new HBox(10, xemButton, suaButton); // Tạo HBox chứa các nút
                    hBox.setAlignment(javafx.geometry.Pos.CENTER); // Căn giữa các nút trong HBox
                    NhanVienDTO nhanVien = getTableRow().getItem();
                    suaButton.setDisable("Online".equals(nhanVien.getTrangThaiHoatDong())); // Disable nếu trạng thái là "Online"
                    setGraphic(hBox); // Hiển thị HBox
                }
            }
        });

        tableViewQuanLiHoSo.getColumns().forEach(column -> {
            column.setReorderable(false);
        });

        JavaFXUtils.disableHorizontalScrollBar(tableViewQuanLiHoSo);

        tableViewQuanLiHoSo.setItems(nhanVienList);
        loadNhanVienData(); 

        // Xử lý sự kiện cho checkbox hiển thị nhân viên nghỉ việc
        hienNVNghiViecCheckBox.setOnAction(event -> {
            // Khi checkbox thay đổi, tải lại dữ liệu với trạng thái tương ứng
            applyFiltersAndRefreshTable();
        });

        // Xử lý sự kiện nhấn Enter trong TextField tìm kiếm
        if (timNVTheoTenTextField != null) {
            timNVTheoTenTextField.setOnAction(event -> kichNutTimKiem(event));
        }
    }
    

     // Phương thức để lấy danh sách nhân viên từ controller và hiển thị trên TableView       
        public void loadNhanVienData() {
        // Đặt placeholder là "Đang tải..." trước khi bắt đầu task
        setDisableControls(true); // Vô hiệu hóa controls
        tableViewQuanLiHoSo.setPlaceholder(JavaFXUtils.createPlaceholder("Đang tải...", "/icons/loading.png"));
        Task<List<NhanVienDTO>> task = new Task<>() {
            @Override
            protected List<NhanVienDTO> call() throws Exception {
                // Luôn tải tất cả nhân viên
                String endpoint = "http://localhost:8080/nhan-vien/all";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(endpoint))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), new TypeReference<List<NhanVienDTO>>() {});
                } else {
                    // In ra body để debug nếu có lỗi từ server
                    System.err.println("Lỗi response body: " + response.body());
                    throw new IOException("Lỗi khi tải danh sách nhân viên: " + response.statusCode());
                }
            }
        };

        task.setOnSucceeded(event -> {
            allNhanVienMasterList = task.getValue(); // Lưu vào danh sách master
            applyFiltersAndRefreshTable(); // Áp dụng bộ lọc và hiển thị
            applyFiltersAndSetupPagination(); // Áp dụng bộ lọc và thiết lập phân trang
            setDisableControls(false); // Kích hoạt controls
        });

        task.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi tải dữ liệu! Không thể tải danh sách nhân viên: " + task.getException().getMessage());
            task.getException().printStackTrace();
            setDisableControls(false); // Kích hoạt controls
        });

        new Thread(task).start();
    }

    // Phương thức để bật/tắt các controls
    private void setDisableControls(boolean disable) {
        // Kiểm tra null trước khi setDisable để tránh NullPointerException nếu fx:id chưa được gán
        if (tableViewQuanLiHoSo != null) tableViewQuanLiHoSo.setDisable(disable);
        if (hienNVNghiViecCheckBox != null) hienNVNghiViecCheckBox.setDisable(disable);
        if (timNVTheoTenTextField != null) timNVTheoTenTextField.setDisable(disable);
        if (btnThemNhanVien != null) btnThemNhanVien.setDisable(disable); 
        if (btnTimKiem != null) btnTimKiem.setDisable(disable);         
    }

    // Phương thức mới để áp dụng bộ lọc và làm mới TableView
    private void applyFiltersAndRefreshTable() {
        String rawSearchTerm = timNVTheoTenTextField.getText().trim();
        String searchTermNormalized = removeAccents(rawSearchTerm).toLowerCase();
        boolean showInactive = hienNVNghiViecCheckBox.isSelected();

        List<NhanVienDTO> filteredList = allNhanVienMasterList.stream()
                .filter(nv -> {
                    // Lọc theo tên
                    boolean nameMatches = searchTermNormalized.isEmpty() ||
                                          (nv.getTenNhanVien() != null && removeAccents(nv.getTenNhanVien().toLowerCase()).contains(searchTermNormalized));
                    if (!nameMatches) {
                        return false;
                    }

                    // Nếu checkbox được chọn, hiển thị TẤT CẢ nhân viên
                    if (showInactive) {
                        return true;
                    } else {
                        // Nếu checkbox KHÔNG được chọn, chỉ hiển thị nhân viên "Đi làm" (không lọc theo trạng thái)
                        return "Đi làm".equals(nv.getTrangThai());
                    }
                })
                .collect(Collectors.toList());

        this.filteredNhanVienList = filteredList; // Lưu kết quả lọc
        setupPagination(); // Thiết lập lại phân trang với danh sách đã lọc
    }

    // Phương thức áp dụng bộ lọc và thiết lập phân trang
    private void applyFiltersAndSetupPagination() {
        String rawSearchTerm = timNVTheoTenTextField.getText().trim();
        String searchTermNormalized = removeAccents(rawSearchTerm).toLowerCase();
        boolean showInactive = hienNVNghiViecCheckBox.isSelected();

        this.filteredNhanVienList = allNhanVienMasterList.stream()
                .filter(nv -> {
                    // Lọc theo tên
                    boolean nameMatches = searchTermNormalized.isEmpty() ||
                                          (nv.getTenNhanVien() != null && removeAccents(nv.getTenNhanVien()).toLowerCase().contains(searchTermNormalized));
                    if (!nameMatches) {
                        return false;
                    }

                   // Nếu checkbox được chọn, hiển thị TẤT CẢ nhân viên
                    if (showInactive) {
                        return true;
                    } else {
                         // Nếu checkbox KHÔNG được chọn, chỉ hiển thị nhân viên "Đi làm" (không lọc theo trạng thái)
                        return "Đi làm".equals(nv.getTrangThai());
                    }
                })
                .collect(Collectors.toList());

        setupPagination(); // Thiết lập lại phân trang với danh sách đã lọc
    }

    // Phương thức tiện ích để loại bỏ dấu tiếng Việt
    private String removeAccents(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    private void xem(NhanVienDTO nhanVien) {
        try {
            if (nhanVien == null) {
                MessageUtils.showErrorMessage("Chưa chọn nhân viên! Vui lòng chọn một nhân viên để xem thông tin.");
                return;
            }
    
            // Truy xuất thông tin chi tiết từ cơ sở dữ liệu
            //NhanVienDTO nhanVienChiTiet = quanLiHoSoController.layThongTinNhanVien(maNhanVien);
    
            // Tải file FXML của form xem chi tiết
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/thongTinNhanVien.fxml"));
            Parent root = loader.load();
    
            // Lấy controller của form chi tiết
            ThongTinNhanVienUI controller = loader.getController();
            // controller.setNhanVienUI(this); // Nếu cần callback
    
            // Hiển thị thông tin chi tiết nhân viên trên form
            controller.setNhanVien(nhanVien); // Truyền trực tiếp DTO đã có
    
            Stage dialogStage = JavaFXUtils.createDialog("Thông tin nhân viên: " + nhanVien.getTenNhanVien(), root, "/icons/view.png");
            dialogStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtils.showErrorMessage("Lỗi hiển thị! Không thể mở form xem chi tiết!");
        }
    }
    
    private void sua(NhanVienDTO nhanVien) {
        try {
            if (nhanVien == null) {
                MessageUtils.showErrorMessage("Chưa chọn nhân viên! Vui lòng chọn một nhân viên để chỉnh sửa.");
                return;
            }
    
            // Kiểm tra nếu nhân viên đã nghỉ việc
            if ("Nghỉ việc".equals(nhanVien.getTrangThai())) {
                boolean wantsToEdit = MessageUtils.showConfirmationDialog(
                        "Xác nhận chỉnh sửa",
                        "Nhân viên này đã nghỉ việc.",
                        "Bạn vẫn muốn chỉnh sửa thông tin?",
                         "/icons/caution.png", // iconPath 
                        getClass() // clazz
                );
                if (!wantsToEdit) {
                    return; // Người dùng chọn "Quay lại" 
                }
            }
        
            // Tải file FXML của form chỉnh sửa
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chinhSuaNhanVien.fxml"));
            Parent root = loader.load();
            ChinhSuaNhanVienUI controller = loader.getController();
            controller.setNhanVienUI(this); 

            // Hiển thị thông tin chi tiết trên form chỉnh sửa
            controller.setNhanVien(nhanVien);
        
            Stage dialogStage = JavaFXUtils.createDialog("Chỉnh sửa nhân viên: " + nhanVien.getTenNhanVien(), root, "/icons/edit-text.png");
            dialogStage.showAndWait(); 

            // Sau khi dialog đóng, tải lại danh sách nếu có thay đổi
            if (controller.isDataChanged()) {
            loadNhanVienData();
        }

        } catch (IOException e) {
            e.printStackTrace();
            MessageUtils.showErrorMessage("Lỗi hiển thị! Không thể mở form chỉnh sửa: " + e.getMessage());
        }
    }
    
    @FXML
    private void them(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/themNhanVien.fxml"));
        Parent root = loader.load();

        ThemNhanVienUI controller = loader.getController();
        
        controller.setNhanVienUI(this); 

       Stage dialogStage = JavaFXUtils.createDialog("Thêm nhân viên", root, "/icons/add.png");
        dialogStage.showAndWait();

        // Sau khi dialog đóng, tải lại danh sách
        if (controller.isDataChanged()) {
            loadNhanVienData();
        }

    } catch (Exception e) {
        e.printStackTrace();
        MessageUtils.showErrorMessage("Lỗi hiển thị! Không thể mở form thêm nhân viên: " + e.getMessage());
    }
}

    @FXML
    private void kichNutTimKiem(ActionEvent event) {
        // Khi nút tìm kiếm được nhấn, áp dụng bộ lọc và làm mới TableView
        applyFiltersAndRefreshTable();
        applyFiltersAndSetupPagination(); // Cập nhật phân trang sau khi lọc
    }


    // Phương thức thiết lập phân trang
    private void setupPagination() {
        if (phanTrang == null) return; // Đảm bảo Pagination đã được load từ FXML

        int totalItems = filteredNhanVienList.size();
        int pageCount = (int) Math.ceil((double) totalItems / ROWS_PER_PAGE);
        phanTrang.setPageCount(pageCount == 0 ? 1 : pageCount); // Đảm bảo ít nhất 1 trang
        phanTrang.setCurrentPageIndex(0); // Reset về trang đầu tiên

        // Cập nhật TableView cho trang đầu tiên
        updateTableView(0);

        // Lắng nghe sự kiện thay đổi trang
        phanTrang.currentPageIndexProperty().removeListener(this::handlePaginationChange); // Xóa listener cũ nếu có
        phanTrang.currentPageIndexProperty().addListener(this::handlePaginationChange); // Thêm listener mới

        // Cập nhật placeholder sau khi thiết lập phân trang
        if (totalItems == 0) {
            tableViewQuanLiHoSo.setPlaceholder(JavaFXUtils.createPlaceholder("Không tìm thấy nhân viên.", "/icons/empty-box.png"));
        } else {
             tableViewQuanLiHoSo.setPlaceholder(null); // Xóa placeholder nếu có dữ liệu
        }
    }

    // Listener cho sự kiện thay đổi trang của Pagination
    private void handlePaginationChange(javafx.beans.value.ObservableValue<? extends Number> obs, Number oldIndex, Number newIndex) {
        updateTableView(newIndex.intValue());
    }

    // Phương thức cập nhật TableView dựa trên chỉ số trang
    private void updateTableView(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredNhanVienList.size());

        List<NhanVienDTO> pageData = filteredNhanVienList.subList(fromIndex, toIndex);
        nhanVienList.setAll(pageData); // Cập nhật ObservableList được bind với TableView
    }
}
