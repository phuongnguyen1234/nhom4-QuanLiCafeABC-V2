package com.frontend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import com.backend.dto.BangLuongDTO;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text; // Thêm import này
import javafx.scene.paint.Color; // Thêm import này
import javafx.scene.layout.AnchorPane; // Thêm nếu có AnchorPane gốc
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class BangLuongUI {
    @FXML
    private TableView<BangLuongDTO> tableViewBangLuong;

    @FXML
    private TableColumn<BangLuongDTO, String> colMaBangLuong, colHoTen, colGhiChu;

    @FXML
    private TableColumn<BangLuongDTO, Integer> colNgayCong, colNghiCoCong, colNghiKhongCong, colThuongDoanhThu, colLuongThucNhan;

    @FXML
    private TableColumn<BangLuongDTO, Void> colHanhDong; // Đảm bảo kiểu dữ liệu là Void vì đây là cột hành động.

    @FXML
    private TableColumn<BangLuongDTO, LocalDate> colThang; // Thay đổi kiểu dữ liệu của cột tháng

    @FXML
    private ComboBox<String> thangCombobox, namCombobox;

    @FXML
    private Pagination phanTrang;

    @FXML
    private Button btnTaoBangLuong;

    @FXML
    private AnchorPane mainAnchorPane; // Thêm fx:id="mainAnchorPane" cho AnchorPane gốc trong FXML

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final int SO_DONG_MOI_TRANG = 50;

    private final ObservableList<BangLuongDTO> bangLuongList = FXCollections.observableArrayList();

    public void initialize() {
        // Đặt placeholder và vô hiệu hóa controls ngay từ đầu
        tableViewBangLuong.setPlaceholder(JavaFXUtils.createPlaceholder("Đang tải...", "/icons/loading.png"));

        // Link columns with data from getters
        colMaBangLuong.setCellValueFactory(new PropertyValueFactory<>("maBangLuong"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colThang.setCellValueFactory(new PropertyValueFactory<>("thang"));
        colThang.setCellFactory(column -> new TableCell<BangLuongDTO, LocalDate>() { // Cập nhật kiểu cho TableCell
            @Override
            protected void updateItem(LocalDate item, boolean empty) { // Item bây giờ là LocalDate
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("MM/yyyy")));
                }
            }
        });
        colNgayCong.setCellValueFactory(new PropertyValueFactory<>("ngayCong"));
        colNghiCoCong.setCellValueFactory(new PropertyValueFactory<>("nghiCoCong"));
        colNghiKhongCong.setCellValueFactory(new PropertyValueFactory<>("nghiKhongCong"));
        colThuongDoanhThu.setCellValueFactory(new PropertyValueFactory<>("thuongDoanhThu"));
        colLuongThucNhan.setCellValueFactory(new PropertyValueFactory<>("luongThucNhan"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));
        colGhiChu.setSortable(false); // Tắt chức năng sort cho cột Ghi Chú
        // Tùy chỉnh CellFactory cho cột Ghi Chú để wrap text
        colGhiChu.setCellFactory(column -> {
            return new TableCell<BangLuongDTO, String>() {
                private final Text text = new Text();

                {
                    text.wrappingWidthProperty().bind(column.widthProperty().subtract(10)); // Trừ đi một chút padding
                    setGraphic(text);
                    setPrefHeight(USE_COMPUTED_SIZE); // Cho phép cell tự điều chỉnh chiều cao
                    text.setFont(Font.font("Open Sans", 16)); // Bỏ dòng này để font chữ đồng bộ với các cột khác

                    // Listener để cập nhật màu chữ khi trạng thái selected thay đổi
                    selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                        // Luôn cập nhật màu chữ dựa trên trạng thái selected
                        text.setFill(isNowSelected ? Color.WHITE : Color.BLACK);
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        text.setText(null);
                        // Giữ dòng này để text.setFill ở dưới được áp dụng cho cả trường hợp cell rỗng
                    } else {
                        text.setText(item);
                    }
                    // Đặt màu chữ dựa trên trạng thái selected, sau khi text đã được set/clear
                    text.setFill(isSelected() ? Color.WHITE : Color.BLACK);
                }
            };
        });

        colLuongThucNhan.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d", item)); // Hiển thị 1000000 thành 1,000,000
                }
            }
        });

        // Cấu hình cột hành động với nút "Sửa"
        colHanhDong.setCellFactory(col -> new TableCell<>() {
            private final Button suaButton = new Button("Sửa");
            private final Button xemButton = new Button("Xem");
        
            {
                // Thiết lập nút "Xem"
                ImageView viewIcon = new ImageView(getClass().getResource("/icons/eye.png").toExternalForm());
                viewIcon.setFitWidth(30);
                viewIcon.setFitHeight(30);
                xemButton.setGraphic(viewIcon);
                xemButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                xemButton.getStyleClass().add("button-view"); // Giả sử bạn có style class này
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
        
                BangLuongDTO currentBangLuong = getTableRow() != null ? getTableRow().getItem() : null;
        
                if (empty || currentBangLuong == null) {
                    setGraphic(null);
                } else if ("1".equals(currentBangLuong.getDuocPhepChinhSua())) {
                    setGraphic(suaButton); // Hiển thị nút "Sửa" nếu bảng lương có thể chỉnh sửa
                } else {
                    setGraphic(xemButton); // Hiển thị nút "Xem" nếu không được phép sửa
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

        JavaFXUtils.disableHorizontalScrollBar(tableViewBangLuong);

        // Tải dữ liệu ban đầu khi khởi tạo giao diện
        setControlsDisabled(true); // Vô hiệu hóa trước khi tải
        loc();
    }

    private void sua(BangLuongDTO bangLuong) {
        if (bangLuong == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chinhSuaBangLuong.fxml"));
            Parent root = loader.load();
    
            ChinhSuaBangLuongUI controller = loader.getController();
            controller.setBangLuong(bangLuong);
    
            // Tạo và hiển thị dialog
            Stage stage = JavaFXUtils.createDialog("Sửa bảng lương " + bangLuong.getMaBangLuong(), root, null);
            stage.showAndWait();
            if (controller.isDataChanged()) { // Kiểm tra xem có thay đổi không
                loc(); // Tải lại dữ liệu nếu có thay đổi
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void xem(BangLuongDTO bangLuong) {
        if (bangLuong == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chiTietBangLuong.fxml"));
            Parent root = loader.load();
    
            ChiTietBangLuongUI controller = loader.getController();
            controller.setBangLuong(bangLuong);
    
            // Tạo và hiển thị dialog
            Stage stage = JavaFXUtils.createDialog("Chi tiết bảng lương: " + bangLuong.getMaBangLuong(), root, "/icons/view.png");
            stage.showAndWait();
            // Không cần tải lại dữ liệu ở đây vì chỉ xem
        } catch (Exception e) {
            MessageUtils.showErrorMessage("Lỗi hiển thị chi tiết bảng lương!");
            e.printStackTrace();
        }
    }

    public void hienThiDanhSachBangLuong(List<BangLuongDTO> danhSachBangLuong) {
        bangLuongList.clear();
        bangLuongList.addAll(danhSachBangLuong);
    
        // Cập nhật placeholder dựa trên kết quả
        if (bangLuongList.isEmpty()) {
            tableViewBangLuong.setPlaceholder(JavaFXUtils.createPlaceholder("Không có dữ liệu", "/icons/no-data.png"));
        } else {
            tableViewBangLuong.setPlaceholder(JavaFXUtils.createPlaceholder("Đang tải...", "/icons/loading.png")); // Xóa placeholder khi có dữ liệu
        }
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
        // Vô hiệu hóa UI tạm thời
        setControlsDisabled(true);


        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/bang-luong/create"))
                        .POST(HttpRequest.BodyPublishers.noBody()) // Không cần gửi body
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200 || response.statusCode() == 201) {
                    return response.body(); // Trả về thông báo từ server
                } else {
                    throw new RuntimeException("Lỗi khi tạo bảng lương: " + response.statusCode() + " - " + response.body());
                }
            }
        };

        task.setOnSucceeded(event -> {
            String responseMessage = task.getValue(); // lấy thông báo trả về từ server
            MessageUtils.showInfoMessage(responseMessage); // hiển thị thông báo đúng nội dung
            loc(); // Tải lại danh sách bảng lương sau khi tạo
            // setControlsDisabled(false); // Không cần thiết nếu loc() đã xử lý
        });


        task.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi tạo bảng lương");
            task.getException().printStackTrace();
            setControlsDisabled(false);
        });

        new Thread(task).start();
    }
    
    @FXML
    private void loc(){
        String thang = thangCombobox.getValue();
        String nam = namCombobox.getValue();

        if (thang == null || nam == null) {
            MessageUtils.showErrorMessage("Trường dữ liệu bị trống");
            return;
        }

        setControlsDisabled(true);

        Task<List<BangLuongDTO>> task = new Task<>() {
            @Override
            protected List<BangLuongDTO> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/bang-luong/thang?thang=" + nam + thang))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), new TypeReference<List<BangLuongDTO>>() {});
                } else {
                    throw new RuntimeException("Lỗi khi lọc bảng lương: " + response.statusCode() + " - " + response.body());
                }
            }
        };

        task.setOnSucceeded(event -> {
            hienThiDanhSachBangLuong(task.getValue());
            setControlsDisabled(false);
        });

        task.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi khi load bảng lương");
            task.getException().printStackTrace();
            setControlsDisabled(false);
            // Hiển thị placeholder lỗi nếu cần
            tableViewBangLuong.setPlaceholder(JavaFXUtils.createPlaceholder("Lỗi tải dữ liệu.", "/icons/error.png"));
        });

        new Thread(task).start();
    } 

    private void setControlsDisabled(boolean disabled) {
        Platform.runLater(() -> { // Đảm bảo chạy trên JavaFX Application Thread
            if (mainAnchorPane != null) { // Nếu bạn đã thêm AnchorPane gốc và đặt fx:id
                mainAnchorPane.setDisable(disabled);
            } else { // Nếu không, disable từng control
                tableViewBangLuong.setDisable(disabled);
                thangCombobox.setDisable(disabled);
                namCombobox.setDisable(disabled);
                phanTrang.setDisable(disabled);
                btnTaoBangLuong.setDisable(disabled);
            }
        });
    }
}
