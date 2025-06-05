package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.dto.NhanVienDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication; // Import để lấy CookieManager
import com.backend.utils.MessageUtils;
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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
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

    @FXML
    private TextField timNVTheoTenTextField;

    private final ObservableList<NhanVienDTO> nhanVienList = FXCollections.observableArrayList();

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    // Phương thức khởi tạo dữ liệu cho bảng
    @FXML
    public void initialize() {

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
                    NhanVienDTO nhanVien = getTableRow().getItem();
                    suaButton.setDisable("Online".equals(nhanVien.getTrangThaiHoatDong())); // Disable nếu trạng thái là "Online"
                    setGraphic(new HBox(10, xemButton, suaButton)); // Hiển thị cả hai nút
                }
            }
        });

        tableViewQuanLiHoSo.getColumns().forEach(column -> {
            column.setReorderable(false);
        });

        tableViewQuanLiHoSo.setItems(nhanVienList);
        loadNhanVienData(); 

        // Xử lý sự kiện cho checkbox hiển thị nhân viên nghỉ việc
        hienNVNghiViecCheckBox.setOnAction(event -> {
            // Khi checkbox thay đổi, tải lại dữ liệu với trạng thái tương ứng
            // Bạn có thể tùy chọn xóa nội dung trường tìm kiếm ở đây nếu muốn
            // timNVTheoTenTextField.clear(); 
            loadNhanVienData();
        });
    }
    

     // Phương thức để lấy danh sách nhân viên từ controller và hiển thị trên TableView       
        public void loadNhanVienData() {
        Task<List<NhanVienDTO>> task = new Task<>() {
            @Override
            protected List<NhanVienDTO> call() throws Exception {
                String searchTerm = timNVTheoTenTextField.getText().trim();
                String endpoint;
                // Backend API sẽ trả về tất cả nhân viên nếu không có searchTerm,
                // hoặc tất cả nhân viên khớp searchTerm. Việc lọc "chỉ nghỉ việc" sẽ thực hiện ở frontend.
                if (!searchTerm.isEmpty()) {
                    // Endpoint này nên trả về tất cả nhân viên (active và inactive) khớp với tên
                    endpoint = "http://localhost:8080/nhanvien/search?ten=" + searchTerm; 
                } else {
                    // Endpoint này nên trả về tất cả nhân viên (active và inactive)
                    endpoint = "http://localhost:8080/nhanvien/all"; 
                }
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
            List<NhanVienDTO> fetchedEmployees = task.getValue();
            if (hienNVNghiViecCheckBox.isSelected()) {
                // Lọc chỉ những nhân viên có trạng thái "Nghỉ việc" (trangThai == "Nghỉ việc")
                List<NhanVienDTO> inactiveEmployees = fetchedEmployees.stream()
                        .filter(nv -> "Nghỉ việc".equals(nv.getTrangThai()))
                        .collect(Collectors.toList());
                nhanVienList.setAll(inactiveEmployees);
            } else {
                // Hiển thị tất cả nhân viên đã tải về
                nhanVienList.setAll(fetchedEmployees);
            }
            tableViewQuanLiHoSo.refresh();
        });

        task.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi tải dữ liệu! Không thể tải danh sách nhân viên: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }


    private void xem(NhanVienDTO nhanVien) {
        try {
            if (nhanVien == null) {
                MessageUtils.showErrorMessage("Chưa chọn nhân viên! Vui lòng chọn một nhân viên để xem thông tin.");
                return;
            }
    
            // Lấy mã nhân viên đã chọn
            String maNhanVien = nhanVien.getMaNhanVien();
    
            // Truy xuất thông tin chi tiết từ cơ sở dữ liệu
            //NhanVienDTO nhanVienChiTiet = quanLiHoSoController.layThongTinNhanVien(maNhanVien);
    
            // Tải file FXML của form xem chi tiết
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/thongTinNhanVien.fxml"));
            Parent root = loader.load();
    
            // Lấy controller của form chi tiết
            ThongTinNhanVienUI controller = loader.getController();
            // controller.setNhanVienUI(this); // Nếu cần callback
    
            // Hiển thị thông tin chi tiết nhân viên trên form
            controller.hienThiThongTin(nhanVien); // Truyền trực tiếp DTO đã có
    
            // Tạo một cửa sổ mới để hiển thị form xem chi tiết
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Thông tin nhân viên: " + nhanVien.getTenNhanVien());
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/profile.png")));
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể mở form xem chi tiết.");
            MessageUtils.showErrorMessage("Lỗi hiển thị! Không thể mở form xem chi tiết: " + e.getMessage());
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
            controller.hienThiThongTin(nhanVien);
        
            // Tạo một cửa sổ mới để hiển thị form chỉnh sửa
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Chỉnh sửa nhân viên: " + nhanVien.getTenNhanVien());
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/nhanVien/businessman.png")));
            stage.showAndWait();        

            // Sau khi dialog đóng, tải lại danh sách nếu có thay đổi
            loadNhanVienData();

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
        
        // Truyền tham chiếu tới màn hình chính nếu cần
        controller.setNhanVienUI(this); 


        // Tạo và hiển thị cửa sổ mới (dialog)
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Cửa sổ modal
        stage.setTitle("Thêm nhân viên");
        stage.setScene(new Scene(root, 680, 800)); // Kích thước cửa sổ
        stage.setResizable(false); // Không cho phép thay đổi kích thước
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/nhanVien/businessman.png")));
        stage.showAndWait();

        // Sau khi dialog đóng, tải lại danh sách
        loadNhanVienData();

    } catch (Exception e) {
        e.printStackTrace();
        MessageUtils.showErrorMessage("Lỗi hiển thị! Không thể mở form thêm nhân viên: " + e.getMessage());
    }
}

    @FXML
    private void quayLai(ActionEvent event) {
        // Logic để quay lại màn hình TrangChuUI hoặc đóng tab/pane hiện tại
        // Ví dụ: ((Stage) hienNVNghiViecCheckBox.getScene().getWindow()).close();
        // Hoặc nếu NhanVienUI được load vào BorderPane của TrangChuUI:
        // trangChuUI.loadDefaultView(); // (Cần có tham chiếu đến TrangChuUI)
        System.out.println("Nút quay lại được nhấn. Cần triển khai logic điều hướng.");
        // Hiện tại, nếu NhanVienUI là một phần của TrangChuUI, nút này có thể không cần thiết
        // hoặc sẽ được xử lý bởi TrangChuUI.
    }

    /* 
    private void setupPagination() {
        int soTrang = (int) Math.ceil((double) danhSachGoc.size() / ROWS_PER_PAGE);
        phanTrang.setPageCount(soTrang == 0 ? 1 : soTrang);
        phanTrang.setCurrentPageIndex(0);
        capNhatTrang(0); // load trang đầu tiên

        phanTrang.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            capNhatTrang(newIndex.intValue());
        });
    }
*/
    @FXML
    private void kichNutTimKiem(ActionEvent event) {
        // Khi nhấn nút tìm kiếm, tải lại dữ liệu dựa trên nội dung trường tìm kiếm
        // và trạng thái của checkbox "Hiển thị nhân viên nghỉ việc"
        loadNhanVienData();
    }
}
