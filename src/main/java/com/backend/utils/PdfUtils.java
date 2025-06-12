package com.backend.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

public class PdfUtils {

    private static final String FONT_PATH = "/fonts/Roboto-Regular.ttf"; // Đường dẫn font trong resources

    public static void taoHoaDonPDF(DonHangDTO donHang) {
        if (donHang == null) {
            MessageUtils.showErrorMessage("Lỗi! Không có thông tin đơn hàng để tạo PDF.");
            return;
        }
        try {
            String maDonHang = donHang.getMaDonHang();
            // Nếu mã đơn hàng chưa có (do server trả về sau), có thể tạo một mã tạm hoặc dùng thời gian
            if (maDonHang == null || maDonHang.isEmpty()) {
                maDonHang = "DH_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            }
            String tenNhanVien = donHang.getHoTen() != null ? donHang.getHoTen() : "N/A";
            String tongTien = String.format("%,d", donHang.getTongTien());
            String thoiGianDat = donHang.getThoiGianDatHang() != null ?
                                 donHang.getThoiGianDatHang().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) :
                                 LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            String folderPath = "hoadon/";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String sanitizedThoiGianDat = thoiGianDat.replace(":", "").replace("/", "");
            String filePath = folderPath + "hoadon_" + maDonHang + "_" + sanitizedThoiGianDat + ".pdf";

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            BaseFont baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);

            Paragraph cafeTitle = new Paragraph("Cafe ABC", boldFont);
            cafeTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(cafeTitle);

            Paragraph title = new Paragraph("HÓA ĐƠN", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\nMã đơn hàng: " + maDonHang, normalFont));
            document.add(new Paragraph("Nhân viên tạo đơn: " + tenNhanVien, normalFont));
            document.add(new Paragraph("Thời gian đặt: " + thoiGianDat, normalFont));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(6); // STT, Tên món, Yêu cầu khác, Số lượng, Đơn giá, Tạm tính
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            float[] columnWidths = {0.8f, 3f, 4f, 1.5f, 2f, 2f};
            table.setWidths(columnWidths);
            table.getDefaultCell().setBorder(0); // Optional: remove cell borders

            String[] headers = {"STT", "Tên món", "Yêu cầu khác", "Số lượng", "Đơn giá", "Tạm tính"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            if (donHang.getDanhSachMonTrongDon() != null) {
                int stt = 1;
                for (MonTrongDonDTO item : donHang.getDanhSachMonTrongDon()) {
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(stt++), normalFont)) {
                        { setHorizontalAlignment(Element.ALIGN_CENTER); }
                    });
                    table.addCell(new PdfPCell(new Phrase(item.getTenMon(), normalFont)) {
                        { setHorizontalAlignment(Element.ALIGN_LEFT); }
                    });
                    table.addCell(new PdfPCell(new Phrase(item.getYeuCauKhac() != null ? item.getYeuCauKhac() : "", normalFont)) {
                        { setHorizontalAlignment(Element.ALIGN_LEFT); }
                    });
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getSoLuong()), normalFont)) {
                        { setHorizontalAlignment(Element.ALIGN_CENTER); }
                    });
                    table.addCell(new PdfPCell(new Phrase(String.format("%,d", item.getDonGia()), normalFont)) {
                        { setHorizontalAlignment(Element.ALIGN_CENTER); }
                    });
                    table.addCell(new PdfPCell(new Phrase(String.format("%,d", item.getTamTinh()), normalFont)) {
                        { setHorizontalAlignment(Element.ALIGN_CENTER); }
                    });
                }
            }

            document.add(table);

            Paragraph total = new Paragraph("\nTổng tiền: " + tongTien + " VND", boldFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            Paragraph thankYou = new Paragraph("\nCảm ơn quý khách!", normalFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            document.add(thankYou);

            document.close();
            System.out.println("Hóa đơn PDF đã được tạo tại: " + filePath);
            // Sử dụng MessageUtils để hiển thị thông báo
            MessageUtils.showInfoMessage("Hóa đơn đã được tạo và lưu tại:\n" + new File(filePath).getAbsolutePath());

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            MessageUtils.showErrorMessage("Lỗi khi tạo file hóa đơn PDF: " + e.getMessage());
        }
    }
}
