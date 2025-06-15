package com.frontend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.backend.dto.DonHangDTO;
import com.backend.dto.NhanVienDTO;
import com.backend.utils.JavaFXUtils;
import com.backend.utils.MessageUtils;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
public class PreviewExcelBaoCaoUI {

    @FXML
    private TableView<DonHangDTO> tableBaoCao;

    @FXML
    private TableColumn<DonHangDTO, String> colSTT, colMaDonHang, colTenNhanVien;
    
    @FXML
    private TableColumn<DonHangDTO, Integer> colTongTien;

    @FXML
    private Label lblTongDoanhThu, lblSoDonDaTao, lblSoMonBanRa, lblNguoiXuatBaoCao;

    @FXML
    private Button btnQuayLai;
    
    @FXML
    private Button btnXuatBaoCao; // Giả định nút này có trong FXML và onAction="#xuatBaoCao"

    @FXML
    private TableColumn<DonHangDTO, LocalDateTime> colThoiGianDat;

    private List<DonHangDTO> danhSachDonHangToExport; // Thay đổi kiểu dữ liệu
    private LocalDate ngayLoc;
    private NhanVienDTO nguoiXuatBaoCao;

    @FXML
    public void initialize() {
        // Cài đặt các cột cho bảng
        colSTT.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(tableBaoCao.getItems().indexOf(data.getValue()) + 1))); // STT dựa trên index
        colMaDonHang.setCellValueFactory(new PropertyValueFactory<>("maDonHang"));
        colTenNhanVien.setCellValueFactory(new PropertyValueFactory<>("hoTen")); // DonHangDTO có getHoTen()
        colThoiGianDat.setCellValueFactory(data -> {
            LocalDateTime time = data.getValue().getThoiGianDatHang();
            return new javafx.beans.property.SimpleObjectProperty<>(time);
        });

        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        
        tableBaoCao.getColumns().forEach(column -> {
            column.setReorderable(false);
        });

        colTongTien.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d", item)); // thêm dấu phẩy phân tách 3 chữ số
                }
            }
        });

        // Định dạng cột thời gian đặt hàng cho dễ đọc hơn
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        colThoiGianDat.setCellFactory(column -> new TableCell<DonHangDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        JavaFXUtils.disableHorizontalScrollBar(tableBaoCao);
    }

    @FXML
    void xuatBaoCao() {
        if (danhSachDonHangToExport == null || danhSachDonHangToExport.isEmpty()) {
            MessageUtils.showInfoMessage("Không có dữ liệu để xuất báo cáo.");
            return;
        }

        // Disable UI
        setControlsDisabled(true);

        // Tạo một Task để việc xuất file không block UI chính
        Task<File> exportTask = new Task<>() {
            @Override
            protected File call() throws Exception {
        // Tạo tên file động
        String initialFileName = "BaoCaoDoanhThuNgay_";
        if (ngayLoc != null) {
            initialFileName += ngayLoc.format(DateTimeFormatter.ofPattern("dd_MM_yyyy_"));
        }
        //Thêm timestamp để tránh trùng tên file nếu xuất nhiều lần trong cùng một ngày
        initialFileName += System.currentTimeMillis() + ".xlsx";

        // Xác định thư mục lưu trữ
        String projectDir = System.getProperty("user.dir"); // Lấy thư mục gốc của dự án
        String targetDirPath = Paths.get(projectDir, "baocaodoanhthu").toString(); // Thư mục baocaodoanhthu cùng cấp với src
        File targetDir = new File(targetDirPath);

        // Tạo thư mục nếu chưa tồn tại
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                        throw new IOException("Không thể tạo thư mục lưu báo cáo: " + targetDirPath);
            }
        }
        File file = new File(targetDir, initialFileName);

                try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                    XSSFSheet sheet = workbook.createSheet("BaoCao");
                    int rowNum = 0;

                    // Header của bảng chi tiết
                    Row headerRow = sheet.createRow(rowNum++);
                    String[] headers = {"STT", "Mã đơn hàng", "Nhân viên tạo đơn", "Thời gian Đặt hàng", "Tổng tiền (VND)"};
                    for (int i = 0; i < headers.length; i++) {
                        headerRow.createCell(i).setCellValue(headers[i]);
                    }
                    int stt = 1;
                    for (DonHangDTO donHang : danhSachDonHangToExport) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(stt++);
                        row.createCell(1).setCellValue(donHang.getMaDonHang());
                        row.createCell(2).setCellValue(donHang.getHoTen());
                        row.createCell(3).setCellValue(
                            donHang.getThoiGianDatHang() != null ? donHang.getThoiGianDatHang().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : ""
                        );
                        row.createCell(4).setCellValue(String.format("%,d", donHang.getTongTien()));
                    }

                    // Thêm một dòng trống để phân cách trước khi ghi thông tin tổng hợp
                    rowNum++;

                    // Thêm thông tin tổng hợp vào cuối file
                    Row rowTongDoanhThu = sheet.createRow(rowNum++);
                    rowTongDoanhThu.createCell(0).setCellValue("Tổng doanh thu:");
                    rowTongDoanhThu.createCell(1).setCellValue(lblTongDoanhThu.getText()); // Lấy text từ Label

                    Row rowSoDon = sheet.createRow(rowNum++);
                    rowSoDon.createCell(0).setCellValue("Số đơn đã tạo:");
                    rowSoDon.createCell(1).setCellValue(lblSoDonDaTao.getText());

                    Row rowSoMon = sheet.createRow(rowNum++);
                    rowSoMon.createCell(0).setCellValue("Số món bán ra:");
                    rowSoMon.createCell(1).setCellValue(lblSoMonBanRa.getText());
                    
                    Row rowNguoiXuat = sheet.createRow(rowNum++);
                    rowNguoiXuat.createCell(0).setCellValue("Người xuất báo cáo:");
                    rowNguoiXuat.createCell(1).setCellValue(lblNguoiXuatBaoCao.getText());

                    // Ghi ra file
                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        workbook.write(fileOut);
                    }
                    return file; // Trả về file đã tạo
                }
            }
        };

        exportTask.setOnSucceeded(event -> {
            File exportedFile = exportTask.getValue();
            MessageUtils.showInfoMessage("Báo cáo đã được xuất thành công: " + exportedFile.getAbsolutePath());
            setControlsDisabled(false);
            // Đóng dialog
            Stage stage = (Stage) btnQuayLai.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        });

        exportTask.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi khi xuất báo cáo: " + exportTask.getException().getMessage());
            exportTask.getException().printStackTrace();
            setControlsDisabled(false);
        });

        new Thread(exportTask).start();
    }

    @FXML
    void quayLai(javafx.event.ActionEvent event) {
        Stage stage = (Stage) btnQuayLai.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    public void setData(List<DonHangDTO> danhSachDonHang, LocalDate thoiGian, NhanVienDTO nguoiXuat) { // Chấp nhận List<DonHangSummaryDTO>
        this.danhSachDonHangToExport = danhSachDonHang;
        this.ngayLoc = thoiGian;
        this.nguoiXuatBaoCao = nguoiXuat;

        if (tableBaoCao != null) {
            tableBaoCao.getItems().setAll(danhSachDonHangToExport);
        } else {
            System.err.println("tableBaoCao is null. Cannot set data.");
        }
        
        capNhatThongKe(danhSachDonHang);
        lblNguoiXuatBaoCao.setText(nguoiXuatBaoCao != null && nguoiXuatBaoCao.getTenNhanVien() != null ? nguoiXuatBaoCao.getTenNhanVien() : "N/A"); // Hiển thị tên người xuất
    }

    private void capNhatThongKe(List<DonHangDTO> danhSachDonHang) { // Change parameter type
        if (danhSachDonHang != null && !danhSachDonHang.isEmpty()) {
            int tongDoanhThu = danhSachDonHang.stream()
                    .mapToInt(DonHangDTO::getTongTien).sum(); // Use DonHangSummaryDTO
            int soDonDaTao = danhSachDonHang.size();
            lblTongDoanhThu.setText(String.format("%,d VND", tongDoanhThu));
            lblSoDonDaTao.setText(String.valueOf(soDonDaTao));

            int soMonBanRa = danhSachDonHang.stream()
                    .filter(donHang -> donHang.getDanhSachMonTrongDon() != null) // Ensure list is not null
                    .flatMap(donHang -> donHang.getDanhSachMonTrongDon().stream()) // Flatten the list of MonTrongDonDTO
                    .mapToInt(mon -> mon.getSoLuong()) // Get the quantity of each item
                    .sum(); // Sum all quantities
            lblSoMonBanRa.setText(String.valueOf(soMonBanRa));
        } else {
            lblTongDoanhThu.setText("0 VND");
            lblSoDonDaTao.setText("0");
            lblSoMonBanRa.setText("0");
        }
    }

    private void setControlsDisabled(boolean disabled) {
        if (tableBaoCao != null) {
            tableBaoCao.setDisable(disabled);
        }
        if (btnXuatBaoCao != null) {
            btnXuatBaoCao.setDisable(disabled);
        }
        if (btnQuayLai != null) {
            btnQuayLai.setDisable(disabled);
        }
    }
}
