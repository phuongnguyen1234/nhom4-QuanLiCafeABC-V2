package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.backend.model.DoanhThu;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ChiTietDoanhThuUI {
    @FXML
    private ComboBox<String> thangCombobox, namCombobox;

    @FXML
    private TableView<DoanhThu> tableViewChiTietDoanhThu;

    @FXML
    private TableColumn<DoanhThu, String> colThoiGian;
    
    @FXML
    private TableColumn<DoanhThu, LocalDateTime> colThoiGianTongHopDoanhThu; // Sửa kiểu dữ liệu ở đây
    
    @FXML
    private TableColumn<DoanhThu, Integer> colSoDon, colSoMon, colTongDoanhThu;

    @FXML
    private TableColumn<DoanhThu, Double> colTrungBinhMoiDon, colTangTruongDoanhThu; // Giữ nguyên kiểu Double cho PropertyValueFactory

    @FXML
    private Pagination phanTrang;

    @FXML
    private Button btnQuayLai, btnLoc;

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final int ROWS_PER_PAGE = 20;
    private final ObservableList<DoanhThu> allDoanhThuList = FXCollections.observableArrayList();
    private final ObservableList<DoanhThu> displayedDoanhThuList = FXCollections.observableArrayList();

    private TrangChuUI trangChuUI;

    public void setTrangChuUI(TrangChuUI trangChuUI){
        this.trangChuUI = trangChuUI;
    }

    @FXML
    public void initialize() {
        setControlsDisabled(true);
        tableViewChiTietDoanhThu.setPlaceholder(JavaFXUtils.createPlaceholder("Đang tải...", "/icons/loading.png"));

        // Khởi tạo ComboBox tháng
        ObservableList<String> thangOptions = FXCollections.observableArrayList("Tất cả");
        IntStream.rangeClosed(1, 12).mapToObj(m -> String.format("%02d", m)).forEach(thangOptions::add);
        thangCombobox.setItems(thangOptions);
        thangCombobox.setValue("Tất cả");

        // Khởi tạo ComboBox năm
        ObservableList<String> namOptions = FXCollections.observableArrayList("Tất cả");
        int currentYear = Year.now().getValue();
        IntStream.rangeClosed(2020, currentYear).mapToObj(String::valueOf).forEach(namOptions::add);
        namCombobox.setItems(namOptions);
        namCombobox.setValue("Tất cả");

        // Thiết lập cột ThoiGian để hiển thị Tháng/Năm
        colThoiGian.setCellValueFactory(cellData -> {
            DoanhThu doanhThu = cellData.getValue();
            if (doanhThu != null) {
                return new javafx.beans.property.SimpleStringProperty(String.format("%02d/%d", doanhThu.getThang(), doanhThu.getNam()));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colSoDon.setCellValueFactory(new PropertyValueFactory<>("soDon"));
        colSoMon.setCellValueFactory(new PropertyValueFactory<>("soMon"));

        colTrungBinhMoiDon.setCellValueFactory(new PropertyValueFactory<>("trungBinhMoiDon"));
        colTrungBinhMoiDon.setCellFactory(column -> new TableCell<DoanhThu, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VND", item));
                }
            }
        });

        colTangTruongDoanhThu.setCellValueFactory(new PropertyValueFactory<>("tangTruongDoanhThu"));
        colTangTruongDoanhThu.setCellFactory(column -> new TableCell<DoanhThu, Double>() { // Sửa kiểu dữ liệu ở đây
             @Override
            protected void updateItem(Double item, boolean empty) { // Sửa kiểu dữ liệu của item
                super.updateItem(item, empty);
                if (empty || item == null) { // Kiểm tra item trực tiếp
                    setText(null);
                } else {
                    // Double tyLe = ((DoanhThu)getTableRow().getItem()).getTangTruongDoanhThu(); // Không cần lấy lại, dùng item
                    Double tyLe = item; // Sử dụng item đã được PropertyValueFactory cung cấp
                        setText(String.format("%+.2f%%", tyLe));
                        if (tyLe > 0) {
                            setTextFill(javafx.scene.paint.Color.GREEN);
                        } else if (tyLe < 0) {
                            setTextFill(javafx.scene.paint.Color.RED);
                        } else {
                            setTextFill(javafx.scene.paint.Color.BLACK);
                        }
                    // Không cần nhánh else setText("") vì đã kiểm tra item == null ở trên
                }
            }
        });


        colThoiGianTongHopDoanhThu.setCellValueFactory(new PropertyValueFactory<>("thoiGianTongHopDoanhThu")); // PropertyValueFactory trả về LocalDateTime
        colThoiGianTongHopDoanhThu.setCellFactory(column -> new TableCell<DoanhThu, LocalDateTime>() { // Sửa kiểu dữ liệu ở đây
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) { // Sửa kiểu dữ liệu của item
                super.updateItem(item, empty);
                if (empty || item == null) { // Kiểm tra item trực tiếp
                    setText(null);
                } else {
                    setText(item.format(formatter)); // Sử dụng item đã được PropertyValueFactory cung cấp
                }
            }
        });

        colTongDoanhThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu"));
        colTongDoanhThu.setCellFactory(column -> new TableCell<DoanhThu, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d VND", item));
                }
            }
        });

        phanTrang.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            capNhatTrang(newIndex.intValue(), displayedDoanhThuList);
        });

        JavaFXUtils.disableHorizontalScrollBar(tableViewChiTietDoanhThu);

        loadData();
    }

    private void loadData() {
        Task<List<DoanhThu>> task = getRequestTask();

        task.setOnSucceeded(event -> {
            allDoanhThuList.setAll(task.getValue());
            displayedDoanhThuList.setAll(allDoanhThuList);
            setupPagination(displayedDoanhThuList);
            capNhatTrang(0, displayedDoanhThuList);
            tableViewChiTietDoanhThu.setPlaceholder(new Label("Không có dữ liệu."));
            setControlsDisabled(false);
        });

        task.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi tải dữ liệu chi tiết doanh thu: " + task.getException().getMessage());
            task.getException().printStackTrace();
            tableViewChiTietDoanhThu.setPlaceholder(new Label("Lỗi tải dữ liệu."));
            setControlsDisabled(false);
        });

        new Thread(task).start();
    }

    @FXML
    public void loc() {
        String selectedThang = thangCombobox.getValue();
        String selectedNam = namCombobox.getValue();

        List<DoanhThu> filteredList = new ArrayList<>(allDoanhThuList);

        if (!"Tất cả".equals(selectedThang)) {
            int thang = Integer.parseInt(selectedThang);
            filteredList = filteredList.stream()
                                     .filter(dt -> dt.getThang() == thang)
                                     .collect(Collectors.toList());
        }

        if (!"Tất cả".equals(selectedNam)) {
            int nam = Integer.parseInt(selectedNam);
            filteredList = filteredList.stream()
                                     .filter(dt -> dt.getNam() == nam)
                                     .collect(Collectors.toList());
        }

        displayedDoanhThuList.setAll(filteredList);
        setupPagination(displayedDoanhThuList);
        capNhatTrang(0, displayedDoanhThuList);
    }

    @FXML
    public void quayLai(){
        if (trangChuUI != null) {
            trangChuUI.thongKe();
        }
    }

    private Task<List<DoanhThu>> getRequestTask(){
        return new Task<>() {
            @Override
            protected List<DoanhThu> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/doanh-thu/all"))
                    .GET()
                    .timeout(Duration.ofSeconds(15))
                    .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), new TypeReference<>() {});
                } else {
                    System.err.println("Lỗi khi lấy dữ liệu thống kê: " + response.statusCode() + " - " + response.body());
                    throw new IOException("Lỗi khi lấy dữ liệu thống kê: " + response.statusCode() + " - " + response.body());
                }
            }
        };
    }

    private void setupPagination(ObservableList<DoanhThu> dataSource) {
        int pageCount = (int) Math.ceil((double) dataSource.size() / ROWS_PER_PAGE);
        phanTrang.setPageCount(Math.max(1, pageCount)); // Page count should be at least 1
        phanTrang.setCurrentPageIndex(0);
    }

    private void capNhatTrang(int pageIndex, ObservableList<DoanhThu> dataSource) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, dataSource.size());

        if (fromIndex < toIndex) {
            tableViewChiTietDoanhThu.setItems(FXCollections.observableArrayList(dataSource.subList(fromIndex, toIndex)));
        } else {
            tableViewChiTietDoanhThu.setItems(FXCollections.emptyObservableList());
        }
    }

    private void setControlsDisabled(boolean disabled) {
        Platform.runLater(() -> {
            thangCombobox.setDisable(disabled);
            namCombobox.setDisable(disabled);
            tableViewChiTietDoanhThu.setDisable(disabled);
            phanTrang.setDisable(disabled);
            btnQuayLai.setDisable(disabled);
            btnLoc.setDisable(disabled);
        });
    }
}
