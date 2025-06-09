package com.frontend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.IntStream;

import com.backend.dto.BangLuongDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    @FXML
    private Button btnTaoBangLuong;

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final int SO_DONG_MOI_TRANG = 50;

    private final ObservableList<BangLuongDTO> bangLuongList = FXCollections.observableArrayList();

    public void initialize() {
        Label label = new Label("Đang tải...");
        label.setFont(Font.font("Open Sans", 18));
        tableViewBangLuong.setPlaceholder(label);

        // Link columns with data from getters
        colMaBangLuong.setCellValueFactory(new PropertyValueFactory<>("maBangLuong"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colThang.setCellValueFactory(new PropertyValueFactory<>("thang"));
        colNgayCong.setCellValueFactory(new PropertyValueFactory<>("ngayCong"));
        colNghiCoCong.setCellValueFactory(new PropertyValueFactory<>("nghiCoCong"));
        colNghiKhongCong.setCellValueFactory(new PropertyValueFactory<>("nghiKhongCong"));
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

        // Tải dữ liệu ban đầu khi khởi tạo giao diện
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
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Modal dialog
            stage.setTitle("Sửa bảng lương " + bangLuong.getMaBangLuong());
            // Kích thước có thể cần điều chỉnh cho phù hợp với nội dung của chinhSuaBangLuong.fxml
            stage.setScene(new Scene(root)); 
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
        // Vô hiệu hóa UI tạm thời
        tableViewBangLuong.setDisable(true);
        phanTrang.setDisable(true);

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
            tableViewBangLuong.setDisable(false);
            phanTrang.setDisable(false);
        });


        task.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi tạo bảng lương");
            task.getException().printStackTrace();
            tableViewBangLuong.setDisable(false);
            phanTrang.setDisable(false);
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

        tableViewBangLuong.setDisable(true);
        phanTrang.setDisable(true);

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
            tableViewBangLuong.setDisable(false);
            phanTrang.setDisable(false);
        });

        task.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi khi load bảng lương");
            task.getException().printStackTrace();
            tableViewBangLuong.setDisable(false);
            phanTrang.setDisable(false);
        });

        new Thread(task).start();
    } 
}
