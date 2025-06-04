package com.frontend.control;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DonHangController {
    /*private Connection conn;
    private DonHangScreen donHangScreen;
    private List<DonHangDTO> danhSachDonHang = new ArrayList<>();
    private NhanVien nhanVien;

    public DonHangController(){
        try {
            conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDonHangScreen(DonHangScreen donHangScreen){
        this.donHangScreen = donHangScreen;
    }

    public void setNhanVien(NhanVien nhanVien){
        this.nhanVien = nhanVien;
    }

    public List<DonHangDTO> loadDonHangFromDatabase() {
        danhSachDonHang.clear();
        final String sqlDonHang = "SELECT dh.MaDonHang, dh.MaNhanVien, nv.TenNhanVien, dh.ThoiGianDatHang, dh.TongTien " +
                "FROM DONHANG dh " +
                "JOIN NHANVIEN nv ON dh.MaNhanVien = nv.MaNhanVien";

        final String sqlCaPhe = "SELECT ctdh.MaDonHang, cp.MaCaPhe, cp.TenCaPhe, cp.AnhMinhHoa, cp.DonGia, " +
                "cp.MaDanhMuc, dm.TenDanhMuc, ctdh.SoLuong, ctdh.YeuCauDacBiet, " +
                "(ctdh.SoLuong * cp.DonGia) AS TamTinh " +
                "FROM CHITIETDONHANG ctdh " +
                "JOIN CAPHE cp ON ctdh.MaCaPhe = cp.MaCaPhe " +
                "JOIN DANHMUC dm ON cp.MaDanhMuc = dm.MaDanhMuc " +
                "WHERE ctdh.MaDonHang = ?";

        try (PreparedStatement stmtDonHang = conn.prepareStatement(sqlDonHang);
             ResultSet rsDonHang = stmtDonHang.executeQuery()) {


            while (rsDonHang.next()) {
                String maDonHang = rsDonHang.getString("MaDonHang");
                String maNhanVien = rsDonHang.getString("MaNhanVien");
                String tenNhanVien = rsDonHang.getString("TenNhanVien");
                LocalDateTime thoiGianDat = rsDonHang.getTimestamp("ThoiGianDatHang").toLocalDateTime();
                int tongTien = rsDonHang.getInt("TongTien");

                List<CaPheDTO> danhSachCaPheTrongDon = loadCaPheByDonHang(sqlCaPhe, maDonHang);

                DonHangDTO donHang = new DonHangDTO(maDonHang, maNhanVien, tenNhanVien, danhSachCaPheTrongDon, thoiGianDat, tongTien);
                danhSachDonHang.add(donHang);
            }

            // Sắp xếp danh sách giảm dần theo thời gian đặt hàng
            danhSachDonHang.sort((dh1, dh2) -> dh2.getThoiGianDatHang().compareTo(dh1.getThoiGianDatHang()));

        } catch (SQLException e) {
            System.err.println("Database error while loading orders: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachDonHang;
    }


    private List<CaPheDTO> loadCaPheByDonHang(String sqlCaPhe, String maDonHang) throws SQLException {
        List<CaPheDTO> danhSachCaPhe = new ArrayList<>();

        try (PreparedStatement stmtCaPhe = conn.prepareStatement(sqlCaPhe)) {
            stmtCaPhe.setString(1, maDonHang);

            try (ResultSet rsCaPhe = stmtCaPhe.executeQuery()) {
                while (rsCaPhe.next()) {
                    String maCaPhe = rsCaPhe.getString("MaCaPhe");
                    String tenCaPhe = rsCaPhe.getString("TenCaPhe");
                    byte[] anhMinhHoa = rsCaPhe.getBytes("AnhMinhHoa");
                    int donGia = rsCaPhe.getInt("DonGia");
                    String maDanhMuc = rsCaPhe.getString("MaDanhMuc");
                    String tenDanhMuc = rsCaPhe.getString("TenDanhMuc");
                    int soLuong = rsCaPhe.getInt("SoLuong");
                    String yeuCauDacBiet = rsCaPhe.getString("YeuCauDacBiet");
                    int tamTinh = rsCaPhe.getInt("TamTinh");

                    // Tạo đối tượng CaPheDTO
                    CaPheDTO caPhe = new CaPheDTO(maCaPhe, tenCaPhe, anhMinhHoa, donGia, maDanhMuc,
                            tenDanhMuc, soLuong, yeuCauDacBiet, tamTinh);
                    danhSachCaPhe.add(caPhe);
                }
            }
        }
        return danhSachCaPhe;
    }

    public List<DonHangDTO> timKiemTheoMa(String maDonHang) {
        if (maDonHang == null || maDonHang.trim().isEmpty()) {
            return loadDonHangFromDatabase(); // Khôi phục danh sách ban đầu
        } else {
            String timKiem = maDonHang.trim(); // Loại bỏ khoảng trắng
            List<DonHangDTO> ketQua = danhSachDonHang.stream()
                    .filter(dh -> dh.getMaDonHang() != null && dh.getMaDonHang().contains(timKiem))
                    .collect(Collectors.toList());
            if (ketQua.isEmpty()) {
                hienThongBao("Không tìm thấy đơn hàng với mã \"" + timKiem + "\".");
            }
            return ketQua;
        }
    }

    public List<DonHangDTO> locTheoThoiGian(LocalDate ngayLoc) {
        if (ngayLoc == null) {
            danhSachDonHang = loadDonHangFromDatabase();// Nếu không có ngày lọc, khôi phục danh sách ban đầu
            return danhSachDonHang;
        } else {
            // Lọc theo ngày (so sánh phần LocalDate của thời gian đặt hàng)
            List<DonHangDTO> ketQua = danhSachDonHang.stream()
                    .filter(dh -> dh.getThoiGianDatHang() != null &&
                            dh.getThoiGianDatHang().toLocalDate().isEqual(ngayLoc))
                    .collect(Collectors.toList());

            // Kiểm tra nếu không có kết quả
            if (ketQua.isEmpty()) {
                hienThongBao("Không tìm thấy đơn hàng trong ngày \"" + ngayLoc + "\".");
            }
            return ketQua;
        }
    }

    public void xuatBaoCao(NhanVien nhanVien, List<DonHangDTO> danhSachLoc, LocalDate ngayLoc) {
        System.out.println("Danh sách lọc trước khi xuất báo cáo: " + danhSachLoc);
        System.out.println(ngayLoc);
        List<DonHangDTO> danhSachDonDeXuat = danhSachLoc.isEmpty() ? danhSachDonHang : danhSachLoc;

        if (danhSachDonDeXuat.isEmpty()) {
            hienThongBao("Không có đơn hàng nào để xuất báo cáo.");
            return;
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Lưu báo cáo doanh thu");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String ngayHienTai = LocalDate.now().format(dateFormatter);
        String ngayBaoCao = ngayLoc != null ? ngayLoc.format(dateFormatter) : ngayHienTai;

        fileChooser.setInitialFileName("BaoCaoDoanhThuNgay" + ngayBaoCao + ".xlsx");
        java.io.File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(ngayBaoCao);

            // Tạo CellStyle
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font titleFont = workbook.createFont();
            titleFont.setFontName("Arial");
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Tiêu đề báo cáo
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO DOANH THU NGÀY " + ngayBaoCao);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            // Thông tin báo cáo
            Row infoRow1 = sheet.createRow(2);
            infoRow1.createCell(0).setCellValue("Thời gian xuất báo cáo:");
            infoRow1.createCell(1).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
            Row infoRow2 = sheet.createRow(3);
            infoRow2.createCell(0).setCellValue("Người xuất báo cáo:");
            infoRow2.createCell(1).setCellValue(nhanVien.getTenNhanVien());

            // Tiêu đề các cột
            Row headerRow = sheet.createRow(5);
            String[] columnHeaders = {"Mã đơn hàng", "Tên nhân viên", "Thời gian đặt hàng", "Tổng tiền"};
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu
            int rowIndex = 6;
            for (DonHangDTO donHang : danhSachDonDeXuat) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(donHang.getMaDonHang());
                row.createCell(1).setCellValue(donHang.getTenNhanVien());
                row.createCell(2).setCellValue(donHang.getThoiGianDatHang().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                row.createCell(3).setCellValue(donHang.getTongTien());
            }

            // Dòng tổng kết
            int tongDoanhThu = danhSachDonDeXuat.stream().mapToInt(DonHangDTO::getTongTien).sum();
            int soDonDaTao = danhSachDonDeXuat.size();
            int tongSoLuongCaPhe = danhSachDonDeXuat.stream()
                    .flatMap(donHang -> donHang.getDanhSachCaPheTrongDon().stream())
                    .mapToInt(CaPheDTO::getSoLuong)
                    .sum();

            int summaryStartRow = rowIndex + 1;

            Row summaryRow1 = sheet.createRow(summaryStartRow);
            summaryRow1.createCell(3).setCellValue("Tổng doanh thu:");
            summaryRow1.createCell(4).setCellValue(tongDoanhThu);

            Row summaryRow2 = sheet.createRow(summaryStartRow + 1);
            summaryRow2.createCell(3).setCellValue("Số đơn đã tạo:");
            summaryRow2.createCell(4).setCellValue(soDonDaTao);

            Row summaryRow3 = sheet.createRow(summaryStartRow + 2);
            summaryRow3.createCell(3).setCellValue("Số cà phê bán ra:");
            summaryRow3.createCell(4).setCellValue(tongSoLuongCaPhe);

            Row summaryRow4 = sheet.createRow(summaryStartRow + 3);
            summaryRow4.createCell(3).setCellValue("Người xuất báo cáo kí xác nhận");

            // Auto-resize cột
            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi workbook ra file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                hienThongBao("Xuất báo cáo thành công! File đã được lưu: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                hienThongBao("Có lỗi xảy ra khi xuất báo cáo: " + e.getMessage());
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            hienThongBao("Xuất báo cáo đã bị hủy.");
        }
    }

    private void hienThongBao(String noiDung) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
*/
}
