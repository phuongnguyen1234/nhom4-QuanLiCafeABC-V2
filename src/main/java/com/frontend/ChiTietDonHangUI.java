package com.frontend;

import com.backend.dto.ChiTietDonHangDTO;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

public class ChiTietDonHangUI {

    @FXML
    private Text lblMaDonHang, lblTenNhanVien, lblTongTien, lblThoiGianDat;

    @FXML
    private TableView<ChiTietDonHangDTO> tableChiTietDonHang;

    @FXML
    private TableColumn<ChiTietDonHangDTO, String> colSTT, colTenCaPhe, colYeuCauDacBiet;

    @FXML
    private TableColumn<ChiTietDonHangDTO, Integer> colSoLuong, colDonGia, colTamTinh;

    @FXML
    private Button btnQuayLai;
/* 
    private DonHangController donHangController;

    private Connection conn;

    private DonHangScreen donHangScreen;

    private DonHangDTO donHang;

    public void setDonHangController(DonHangController donHangController){
        this.donHangController = donHangController;
    }

    public DonHangController getDonHangController(){
        return donHangController;
    }

    public ChiTietDonHangScreen(){
        try {
            conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDonHangScreen(DonHangScreen donHangScreen){
        this.donHangScreen = donHangScreen;
    }

    public void initData(DonHangDTO donHang) {
        this.donHang = donHang;
        lblMaDonHang.setText("Mã đơn hàng: "+donHang.getMaDonHang());
        lblTenNhanVien.setText("Nhân viên tạo đơn: "+donHang.getTenNhanVien());
        lblTongTien.setText("Tổng tiền: " + donHang.getTongTien());
        lblThoiGianDat.setText("Thời gian đặt hàng: " + donHang.getThoiGianDatHang());

        colSTT.setCellValueFactory(param ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(tableChiTietDonHang.getItems().indexOf(param.getValue()) + 1)));
        colTenCaPhe.setCellValueFactory(new PropertyValueFactory<>("tenCaPhe"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colYeuCauDacBiet.setCellValueFactory(new PropertyValueFactory<>("yeuCauDacBiet"));
        colTamTinh.setCellValueFactory(new PropertyValueFactory<>("tamTinh"));

        loadChiTietDonHang(donHang.getMaDonHang());

        tableChiTietDonHang.getColumns().forEach(column -> {
            column.setReorderable(false);
        });
    }

    private void loadChiTietDonHang(String maDonHang) {
        // Cập nhật câu lệnh SQL để lấy thêm tên cà phê và đơn giá cà phê từ bảng CAPHE
        String sql = "SELECT ct.MaCaPhe, cp.TenCaPhe, cp.DonGia, ct.SoLuong, ct.YeuCauDacBiet, ct.TamTinh " +
                "FROM CHITIETDONHANG ct " +
                "JOIN CAPHE cp ON ct.MaCaPhe = cp.MaCaPhe " +
                "WHERE ct.MaDonHang = ?";

        ObservableList<ChiTietDonHangDTO> chiTietList = FXCollections.observableArrayList();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDonHang);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String maCaPhe = rs.getString("MaCaPhe");
                String tenCaPhe = rs.getString("TenCaPhe"); // Lấy tên cà phê
                int donGia = rs.getInt("DonGia"); // Lấy đơn giá cà phê
                int soLuong = rs.getInt("SoLuong");
                String yeuCauDacBiet = rs.getString("YeuCauDacBiet");
                int tamTinh = rs.getInt("TamTinh");

                // Cập nhật constructor của ChiTietDonHang nếu cần chứa thêm tên cà phê và đơn giá
                ChiTietDonHangDTO chiTiet = new ChiTietDonHangDTO(maCaPhe, yeuCauDacBiet, soLuong, donGia, maDonHang, tenCaPhe, tamTinh);
                chiTietList.add(chiTiet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tableChiTietDonHang.setItems(chiTietList);
    }

    @FXML
    void inHoaDon() {
        try {
            // Lấy dữ liệu từ giao diện
            String maDonHang = donHang.getMaDonHang();
            String tenNhanVien = donHang.getTenNhanVien();
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
            String fontPath = "H:\\Documents\\TTCSN\\quanlicafe\\src\\main\\resources\\fonts\\Roboto-Regular.ttf"; // Thay đường dẫn này bằng file font thực tế
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
            String[] headers = {"STT", "Tên cà phê", "Yêu cầu đặc biệt", "Số lượng", "Đơn giá", "Tạm tính"};

            // Duyệt qua các tiêu đề và thêm vào bảng
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);  // Căn giữa cho tiêu đề
                table.addCell(cell);
            }

            // Thêm dữ liệu bảng
            int stt = 1;
            for (ChiTietDonHangDTO item : tableChiTietDonHang.getItems()) {
                // Cột STT căn giữa
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stt++), normalFont)) {
                    {
                        setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                });
                // Cột Tên cà phê căn trái
                table.addCell(new PdfPCell(new Phrase(item.getTenMon(), normalFont)) {
                    {
                        setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                });
                // Cột Yêu cầu đặc biệt căn trái
                table.addCell(new PdfPCell(new Phrase(
                        item.getYeuCauDacBiet() != null ? item.getYeuCauDacBiet() : "", normalFont)) {
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
            Paragraph total = new Paragraph("\nTổng tiền: " + tongTien, boldFont);
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
*/
}
