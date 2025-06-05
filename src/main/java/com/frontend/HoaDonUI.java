package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.dto.DonHangDTO;
import com.backend.dto.DonHangSummaryDTO;
import com.backend.dto.NhanVienDTO;
import com.backend.model.DonHang;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class HoaDonUI {
    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private TextField timKiemTheoMaTextField;

    @FXML
    private DatePicker thoiGian;

    @FXML
    private TableView<DonHangSummaryDTO> tableDonHang;

    @FXML
    private TableColumn<DonHangSummaryDTO, String> colSTT, colMaDonHang, colNhanVienTaoDon;

    @FXML
    private TableColumn<DonHangSummaryDTO, Integer> colTongTien;

    @FXML
    private TableColumn<DonHangSummaryDTO, LocalDateTime> colThoiGianDat;

    @FXML
    private TableColumn<DonHangSummaryDTO, Void> colHanhDong;

    @FXML
    private Pagination phanTrang;

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final int SO_DONG_MOI_TRANG = 50;

    // Danh sách gốc chứa DonHangDTO chi tiết
    private final ObservableList<DonHangDTO> donHangList = FXCollections.observableArrayList();
    private NhanVienDTO currentUser;

    // Phương thức để TrangChuUI truyền NhanVienDTO vào
    public void setCurrentUser(NhanVienDTO user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        disableItems(true);

        // Setup các cột
        colSTT.setCellValueFactory(cellDataFeatures -> {
            int pageIndex = phanTrang.getCurrentPageIndex();
            int rowIndexOnPage = tableDonHang.getItems().indexOf(cellDataFeatures.getValue());
            if (rowIndexOnPage != -1) { // Ensure item is found
                String stt = String.valueOf(pageIndex * SO_DONG_MOI_TRANG + rowIndexOnPage + 1);
                return new javafx.beans.property.SimpleStringProperty(stt);
            }
            return new javafx.beans.property.SimpleStringProperty(""); 
        });
        colMaDonHang.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMaDonHang())); // DonHangSummaryDTO có getMaDonHang
        colNhanVienTaoDon.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getHoTen())); // DonHangSummaryDTO có getHoTen
        colTongTien.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTongTien())); // DonHangSummaryDTO có getTongTien
        colThoiGianDat.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getThoiGianDatHang()));

        // Thêm cột Hành Động với nút "Xem Chi Tiết"
        colHanhDong.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();

            {
                // Tạo Image từ đường dẫn biểu tượng
                Image iconImage = new Image(getClass().getResource("/icons/eye.png").toExternalForm());
                ImageView icon = new ImageView(iconImage);
                icon.setFitHeight(25); // Kích thước của biểu tượng
                icon.setFitWidth(25);

                // Gán biểu tượng cho nút
                btn.setGraphic(icon);

                // Thêm style cho nút (nền xanh, viền bo góc)
                btn.setStyle("-fx-background-color: #007BFF; -fx-background-radius: 5; -fx-padding: 5; -fx-border-color: transparent; -fx-cursor: hand;");

                btn.setOnAction(event -> {
                    DonHangSummaryDTO donHang = getTableView().getItems().get(getIndex()); // Lấy DonHangSummaryDTO
                    if (donHang != null) {
                        hienThiChiTietDonHang(donHang);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        tableDonHang.getColumns().forEach(column -> {
            column.setReorderable(false);
        });

        phanTrang.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            capNhatTrang(newIndex.intValue());
        });

        loadDonHangData();
    }

    private void loadDonHangData() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<DonHangDTO> fetchedDonHangsDTO = layTatCaDonHangDTO(); 
                Platform.runLater(() -> setDuLieuDonHangTable(fetchedDonHangsDTO));
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                disableItems(false);
            }

            @Override
            protected void failed() {
                super.failed();
                disableItems(false);
                MessageUtils.showErrorMessage("Lỗi tải dữ liệu! Không thể tải danh sách đơn hàng: " + getException().getMessage());
                getException().printStackTrace();
            }
        };
        new Thread(task).start();
    }

    private void capNhatTrang(int soTrang) {
        int tuViTri = soTrang * SO_DONG_MOI_TRANG; // Vị trí bắt đầu
        int denViTri = Math.min(tuViTri + SO_DONG_MOI_TRANG, this.donHangList.size()); // Vị trí kết thúc

        List<DonHangDTO> subListOfDTOs = this.donHangList.subList(tuViTri, denViTri);
        List<DonHangSummaryDTO> summariesForPage = convertToSummaryDTOList(subListOfDTOs);
        tableDonHang.setItems(FXCollections.observableArrayList(summariesForPage));
    }

    @FXML
    void timKiem() {
        String maDonHang = timKiemTheoMaTextField.getText().trim();
        disableItems(true);

        // Task will return the HttpResponse to handle different status codes in onSucceeded
        Task<HttpResponse<String>> task = new Task<>() {
            @Override
            protected HttpResponse<String> call() throws Exception {
                if (maDonHang.isEmpty()) {
                    // Nếu ô tìm kiếm trống, tải lại toàn bộ danh sách DTO
                    List<DonHangDTO> allDTOs = layTatCaDonHangDTO();
                    Platform.runLater(() -> setDuLieuDonHangTable(allDTOs));
                    return null; // Trả về null vì đã xử lý cập nhật UI bên trong
                }
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/don-hang/" + maDonHang))
                        .GET()
                        .build();

                // Send the request and return the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response;
            }
        };

        task.setOnSucceeded(event -> {
            HttpResponse<String> response = task.getValue();
            if (response == null) {
                // Case: search field was empty, UI already updated in call()
            } else {
                if (response.statusCode() == 200) {
                    try {
                        // Giả định endpoint trả về DonHangDTO
                        DonHangDTO foundDonHangDTO = objectMapper.readValue(response.body(), DonHangDTO.class);
                        setDuLieuDonHangTable(List.of(foundDonHangDTO)); 
                    } catch (IOException e) {
                        MessageUtils.showErrorMessage("Lỗi xử lý dữ liệu đơn hàng: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (response.statusCode() == 404) {
                    Platform.runLater(() -> MessageUtils.showInfoMessage("Không tìm thấy đơn hàng với mã \"" + maDonHang + "\"."));
            }}
            // Nếu foundDonHang là null, UI đã được cập nhật trong call() (khi search trống) hoặc thông báo 404 đã hiển thị
            disableItems(false);
        });

        task.setOnFailed(event -> {
            disableItems(false);
            MessageUtils.showErrorMessage("Lỗi khi tìm kiếm đơn hàng: " + task.getException().getMessage()); // Error message from the exception
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    void loc() {
        LocalDate ngayLoc = thoiGian.getValue();
        disableItems(true);

        Task<List<DonHangDTO>> task = new Task<>() {
            @Override 
            protected List<DonHangDTO> call() throws Exception { // Sửa kiểu trả về thành List<DonHangDTO>
                if (ngayLoc == null) {
                    return layTatCaDonHangDTO(); // Tải lại tất cả DTO nếu không chọn ngày
                }
                // Định dạng ngày thành yyyy-MM-dd để gửi cho API
                String ngayLocFormatted = ngayLoc.format(DateTimeFormatter.ISO_DATE);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:8080/don-hang/filter?ngay=" + ngayLocFormatted))
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), new TypeReference<List<DonHangDTO>>() {}); // Sửa TypeReference
                } else if (response.statusCode() == 404) {
                     Platform.runLater(() -> MessageUtils.showInfoMessage("Không tìm thấy đơn hàng trong ngày \"" + ngayLocFormatted + "\"."));
                    return FXCollections.emptyObservableList(); // Trả về danh sách DTO rỗng
                } 
                else {
                    throw new IOException("Lỗi khi lọc đơn hàng: " + response.statusCode() + " - " + response.body());
                }
            }
        };

        task.setOnSucceeded(event -> {
            setDuLieuDonHangTable(task.getValue()); 
            disableItems(false);
        });

        task.setOnFailed(event -> {
            disableItems(false);
            MessageUtils.showErrorMessage("Lỗi khi lọc đơn hàng: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    void xuatBaoCao(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/previewExcelBaoCao.fxml"));
            Parent root = loader.load();
            // Lấy controller của màn hình xuất báo cáo
            PreviewExcelBaoCaoUI xuatBaoCaoController = loader.getController();

            List<DonHangDTO> dataToReport; 
            LocalDate filterDate = thoiGian.getValue();

            if (filterDate != null) {
                dataToReport = donHangList.stream()
                    .filter(dh -> dh.getThoiGianDatHang() != null &&
                                   dh.getThoiGianDatHang().toLocalDate().equals(filterDate))
                    .collect(Collectors.toList()); // Giữ nguyên kiểu DonHangDTO
            } else { // Nếu không có ngày nào được chọn, báo cáo tất cả đơn hàng đã tải
                dataToReport = FXCollections.observableArrayList(this.donHangList); 
            } 

            // Truyền dữ liệu (danh sách đơn hàng, ngày lọc, người dùng hiện tại)
            xuatBaoCaoController.setData(dataToReport, filterDate, this.currentUser);

            // Hiển thị màn hình mới
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Xem trước và Xuất Báo Cáo");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            MessageUtils.showErrorMessage("Có lỗi xảy ra khi mở giao diện xuất báo cáo.");
        }
    }

    // Phương thức này nhận List<DonHangDTO> và cập nhật bảng
    public void setDuLieuDonHangTable(List<DonHangDTO> danhSachDonHangDTO) {
        this.donHangList.setAll(danhSachDonHangDTO);

        int soTrang = (int) Math.ceil((double) this.donHangList.size() / SO_DONG_MOI_TRANG);
        phanTrang.setPageCount(soTrang == 0 ? 1 : soTrang); // Ensure page count is at least 1
        phanTrang.setCurrentPageIndex(0);
    
        // Hiển thị dữ liệu của trang đầu tiên
        capNhatTrang(0);
    }

    public void hienThiChiTietDonHang(DonHangSummaryDTO donHangSummary){ 
        try {
            // Task để tải chi tiết đơn hàng đầy đủ từ backend
            Task<DonHangDTO> loadDetailTask = new Task<>() {
                @Override
                protected DonHangDTO call() throws Exception {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI("http://localhost:8080/don-hang/" + donHangSummary.getMaDonHang()))
                            .GET()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() == 200) {
                        return objectMapper.readValue(response.body(), DonHangDTO.class);
                    } else {
                        throw new IOException("Lỗi khi tải chi tiết đơn hàng: " + response.statusCode() + " - " + response.body());
                    }
                }
            };

            loadDetailTask.setOnSucceeded(event -> {
                DonHangDTO donHangChiTiet = loadDetailTask.getValue();
                if (donHangChiTiet != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chiTietDonHang.fxml"));
                        Parent root = loader.load();
                        ChiTietDonHangUI controller = loader.getController();
                        controller.initData(donHangChiTiet); // Pass the converted DTO

                        // Tạo và hiển thị Stage dialog
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL); // Modal dialog
                        // Tạo một instance của controller với donHang
                        stage.setScene(new Scene(root));
                        stage.setTitle("Chi tiết đơn hàng " + donHangChiTiet.getMaDonHang());
                        stage.setResizable(false);
                        stage.showAndWait();

                    } catch (IOException e) {
                        e.printStackTrace();
                        MessageUtils.showErrorMessage("Lỗi hiển thị chi tiết đơn hàng.");
                    }
                } else {
                    MessageUtils.showErrorMessage("Không tìm thấy chi tiết đơn hàng.");
                }
            });

            loadDetailTask.setOnFailed(event -> {
                MessageUtils.showErrorMessage("Lỗi tải chi tiết đơn hàng: " + loadDetailTask.getException().getMessage());
                loadDetailTask.getException().printStackTrace();
            });

            new Thread(loadDetailTask).start();

        } catch (Exception e) { // Bắt các exception khác có thể xảy ra trước khi task chạy
            e.printStackTrace();
            MessageUtils.showErrorMessage("Lỗi chuẩn bị tải chi tiết đơn hàng.");
        }
    }

    private void disableItems(boolean value){
        mainAnchorPane.setDisable(value);
        timKiemTheoMaTextField.setDisable(value);
        thoiGian.setDisable(value);
        phanTrang.setDisable(value);
    }

    // Đổi tên và kiểu trả về để lấy List<DonHangDTO>
    private List<DonHangDTO> layTatCaDonHangDTO() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/don-hang/all"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<List<DonHangDTO>>() {});
        } else {
            throw new IOException("Lỗi khi tải danh sách đơn hàng: " + response.statusCode() + " - " + response.body());
        }
    }
    
    // Helper method to convert DonHangDTO (detailed) to DonHangSummaryDTO
    private DonHangSummaryDTO convertDonHangDTOToSummaryDTO(DonHangDTO detailedDTO) {
        if (detailedDTO == null) return null;
        DonHangSummaryDTO summary = new DonHangSummaryDTO();
        summary.setMaDonHang(detailedDTO.getMaDonHang());
        summary.setHoTen(detailedDTO.getHoTen()); 
        summary.setThoiGianDatHang(detailedDTO.getThoiGianDatHang());
        summary.setTongTien(detailedDTO.getTongTien());
        return summary;
    }

    private List<DonHangSummaryDTO> convertToSummaryDTOList(List<DonHangDTO> dtoList) {
        if (dtoList == null) {
            return FXCollections.emptyObservableList();
        }
        return dtoList.stream().map(this::convertDonHangDTOToSummaryDTO).collect(Collectors.toList());
    }
}
