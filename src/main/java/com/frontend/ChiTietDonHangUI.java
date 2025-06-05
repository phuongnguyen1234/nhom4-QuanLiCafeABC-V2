package com.frontend;

import java.io.File;
import java.io.FileOutputStream; // Keep this import for PDF generation

import com.backend.dto.DonHangDTO;
import com.backend.dto.MonTrongDonDTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.collections.FXCollections; // Import MonTrongDonDTO
import javafx.event.ActionEvent; // Import FXCollections
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class ChiTietDonHangUI {

    @FXML
    private Text lblMaDonHang, lblTenNhanVien, lblTongTien, lblThoiGianDat;

    @FXML
    private TableView<MonTrongDonDTO> tableChiTietDonHang; // Change TableView type to MonTrongDonDTO

    @FXML
    private TableColumn<MonTrongDonDTO, String> colSTT, colTenMon, colYeuCauKhac; // Change TableColumn types

    @FXML
    private TableColumn<MonTrongDonDTO, Integer> colSoLuong, colDonGia, colTamTinh; // Change TableColumn types

    @FXML
    private Button btnQuayLai;

    private DonHangDTO donHang;

    public void initData(DonHangDTO donHang) {
        this.donHang = donHang;
        lblMaDonHang.setText("Mã đơn hàng: "+donHang.getMaDonHang());
        lblTenNhanVien.setText("Nhân viên tạo đơn: "+donHang.getHoTen());
        lblTongTien.setText("Tổng tiền: " + donHang.getTongTien() + " VND");
        lblThoiGianDat.setText("Thời gian đặt hàng: " + donHang.getThoiGianDatHang());

        colSTT.setCellValueFactory(param ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(tableChiTietDonHang.getItems().indexOf(param.getValue()) + 1)));
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon")); 
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colYeuCauKhac.setCellValueFactory(new PropertyValueFactory<>("yeuCauKhac")); 
        colTamTinh.setCellValueFactory(new PropertyValueFactory<>("tamTinh")); 

        tableChiTietDonHang.setItems(FXCollections.observableArrayList(donHang.getDanhSachMonTrongDon())); // Set table items from DTO

        tableChiTietDonHang.getColumns().forEach(column -> {
            column.setReorderable(false);
        });
    }

    @FXML
    void inHoaDon() {
        try {
            // Lấy dữ liệu từ giao diện
            String maDonHang = donHang.getMaDonHang();
            String tenNhanVien = donHang.getHoTen();
            String tongTien = String.valueOf(donHang.getTongTien());
            String thoiGianDat = donHang.getThoiGianDatHang().toString();
    
            String folderPath = "hoadon/";  // Thư mục hoadon trong thư mục gốc dự án
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs(); // Tạo thư mục hoadon nếu chưa tồn tại
            }
    
            // Đường dẫn file PDF
            String filePath = folderPath + "hoadon_" + maDonHang + "_" + donHang.getThoiGianDatHang().toString().replace(':', '-') + ".pdf";
    
            // Tạo Document
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
    
            // Đường dẫn đến font hỗ trợ tiếng Việt (Arial Unicode hoặc Roboto)
            String fontPath = "/fonts/Roboto-Regular.ttf"; // Thay đường dẫn này bằng file font thực tế
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
    
            // Thêm tiêu đề quán cà phê (góc trái trên)
            Paragraph cafeTitle = new Paragraph("Cafe ABC", boldFont);
            cafeTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(cafeTitle);
    
            // Thêm tiêu đề hóa đơn
            Paragraph title = new Paragraph("HÓA ĐƠN", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
    
            // Thêm thông tin hóa đơn
            document.add(new Paragraph("\nMã đơn hàng: " + maDonHang, normalFont));
            document.add(new Paragraph("Nhân viên tạo đơn: " + tenNhanVien, normalFont));
            document.add(new Paragraph("Thời gian đặt: " + thoiGianDat, normalFont));
            document.add(new Paragraph("\n"));
    
            // Tạo bảng chi tiết hóa đơn
            PdfPTable table = new PdfPTable(6); // 6 cột
            table.setWidthPercentage(100); // Tỉ lệ phần trăm của bảng
            table.setSpacingBefore(10f);
    
            // Đặt độ rộng cho các cột (sử dụng tỷ lệ phần trăm)
            float[] columnWidths = {0.8f, 3f, 4f, 1.5f, 2f, 2f}; // Điều chỉnh độ rộng cột STT, Số lượng nhỏ, các cột còn lại rộng hơn
            table.setWidths(columnWidths);

            // Ẩn border của bảng
            table.getDefaultCell().setBorder(0);  // Loại bỏ border của các ô mặc định
    
            // Danh sách các tiêu đề bảng
            String[] headers = {"STT", "Tên món", "Yêu cầu khác", "Số lượng", "Đơn giá", "Tạm tính"};

            // Duyệt qua các tiêu đề và thêm vào bảng
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);  // Căn giữa cho tiêu đề
                table.addCell(cell);
            }

            // Thêm dữ liệu bảng
            int stt = 1;
            for (MonTrongDonDTO item : tableChiTietDonHang.getItems()) { // Iterate over MonTrongDonDTO
                // Cột STT căn giữa
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stt++), normalFont)) {
                    {
                        setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                });
                // Cột Tên món căn trái
                table.addCell(new PdfPCell(new Phrase(item.getTenMon(), normalFont)) { //Sửa thành getTenMon
                    {
                        setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                });
                // Cột Yêu cầu đặc biệt căn trái
                table.addCell(new PdfPCell(new Phrase(
                        item.getYeuCauKhac() != null ? item.getYeuCauKhac() : "", normalFont)) { 
                    {
                        setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                });
                // Cột Số lượng căn giữa
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getSoLuong()), normalFont)) {
                    {
                        setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                });
                // Cột Đơn giá căn giữa
                table.addCell(new PdfPCell(new Phrase(String.format("%d", item.getDonGia()), normalFont)) {
                    {
                        setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                });
                // Cột Tạm tính căn giữa
                table.addCell(new PdfPCell(new Phrase(String.format("%d", item.getTamTinh()), normalFont)) {
                    {
                        setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                });
            }
    
            // Thêm bảng vào tài liệu
            document.add(table);
    
            // Thêm tổng tiền
            Paragraph total = new Paragraph("\nTổng tiền: " + tongTien + " VND", boldFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
    
            // Thêm lời cảm ơn
            Paragraph thankYou = new Paragraph("\nCảm ơn quý khách!", normalFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            document.add(thankYou);
    
            // Đóng Document
            document.close();
            System.out.println("Hóa đơn PDF đã được tạo thành công tại: " + filePath);
    
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Hóa đơn đã được tạo thành công tại: " + filePath);
            alert.showAndWait();
        } catch (DocumentException | java.io.IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tạo file hóa đơn PDF.");
        }
    }
    


    @FXML
    void quayLai(ActionEvent event) {
        btnQuayLai.getScene().getWindow().hide();
    }

}
