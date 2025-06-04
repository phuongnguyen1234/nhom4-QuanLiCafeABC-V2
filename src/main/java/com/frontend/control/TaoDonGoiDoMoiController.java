package com.frontend.control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TaoDonGoiDoMoiController {
    /*private Connection connection;

    private DonHangDTO donHang = new DonHangDTO();  //bien luu don hang hien tai

    private ObservableList<CaPheDTO> danhSachCaPheTrongDon = FXCollections.observableArrayList(); //bien luu danh sach cua don hien tai

    public TaoDonGoiDoMoiController(){
        try {
            this.connection = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public ObservableList<CaPheDTO> layDanhSachCaPheTrongDon(){ //getter lay danh sach ca phe cua don hien tai
        return danhSachCaPheTrongDon;
    }

    public void them(CaPheDTO caPhe) {
        danhSachCaPheTrongDon.add(caPhe);
    }
    
    public void sua(CaPheDTO caPhe) {
        // Tìm món cần sửa và cập nhật
        for (CaPheDTO item : danhSachCaPheTrongDon) {
            if (item.getMaMon().equals(caPhe.getMaMon())) {
                item.setSoLuong(caPhe.getSoLuong());
                item.setYeuCauDacBiet(caPhe.getYeuCauDacBiet());
                break;
            }
        }
    }  

    public void xoa(CaPheDTO caPhe){ //xoa ca phe khoi don
        int index = danhSachCaPheTrongDon.indexOf(caPhe);
        if(index != -1){
            danhSachCaPheTrongDon.remove(index);
        }
    }

    public void taoDonHang(DonHangDTO donHang){
        String maDonHang= generateMaDonHang();

        try {
            // Bắt đầu transaction
            connection.setAutoCommit(false);
    
            // Thêm thông tin đơn hàng vào bảng DONHANG
            String sqlDH = "INSERT INTO DONHANG (MaDonHang, MaNhanVien, ThoiGianDatHang, TongTien) VALUES (?, ?, NOW(), ?)";
            try (PreparedStatement ps = connection.prepareStatement(sqlDH)) {
                ps.setString(1, maDonHang);
                ps.setString(2, donHang.getMaNhanVien());
                ps.setInt(3, donHang.getTongTien());
    
                int rowAffected = ps.executeUpdate();
                if (rowAffected == 0) {
                    connection.rollback();
                    throw new SQLException("Thêm đơn hàng thất bại, không có hàng nào được chèn.");
                }
            }
    
            // Thêm chi tiết các món cà phê vào bảng CHITIETDONHANG
            String queryChiTietDonHang = "INSERT INTO CHITIETDONHANG (MaCaPhe, MaDonHang, MaNhanVien, SoLuong, YeuCauDacBiet,TamTinh) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psChiTiet = connection.prepareStatement(queryChiTietDonHang)) {
                for (CaPheDTO caPhe : danhSachCaPheTrongDon) {
                    psChiTiet.setString(1, caPhe.getMaMon());
                    psChiTiet.setString(2, maDonHang);
                    psChiTiet.setString(3, donHang.getMaNhanVien());
                    psChiTiet.setInt(4, caPhe.getSoLuong());
                    psChiTiet.setString(5, caPhe.getYeuCauDacBiet() != null ? caPhe.getYeuCauDacBiet() : "");
                    psChiTiet.setInt(6, caPhe.getTamTinh());

                    psChiTiet.addBatch(); // Thêm câu lệnh vào batch
                }
                psChiTiet.executeBatch(); // Thực thi batch
            }

            //cap nhat so don da tao nhan vien do
            String queryLaySoDonDaTao = "SELECT COUNT(*) AS SoLuongDonDaTao FROM DONHANG WHERE MaNhanVien = ? AND MONTH(ThoiGianDatHang) = MONTH(CURRENT_DATE()) AND YEAR(ThoiGianDatHang) = YEAR(CURRENT_DATE())";
            int soDonDaTao=0;

            try (PreparedStatement ps = connection.prepareStatement(queryLaySoDonDaTao)){
                ps.setString(1, donHang.getMaNhanVien());

                try (ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        soDonDaTao = rs.getInt("SoLuongDonDaTao");
                    }
                }
            }

            if(!"NV000".equals(donHang.getMaNhanVien())){
                String queryCapNhatSoDonDaTao = "UPDATE BANGLUONG SET SoLuongDonDaTao = ? WHERE MaNhanVien = ? AND DuocPhepChinhSua = '1'";
                try (PreparedStatement ps = connection.prepareStatement(queryCapNhatSoDonDaTao)){
                    ps.setInt(1, soDonDaTao);
                    ps.setString(2, donHang.getMaNhanVien());
                    ps.executeUpdate();
                } 
            } else {
                System.out.println("ADMIN khong cap nhat bang luong!");
            }

            // Commit transaction
            connection.commit();
            System.out.println("Thêm đơn hàng và chi tiết thành công!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateMaDonHang(){
        String query = "SELECT MAX(CAST(SUBSTRING(MaDonHang, 3) AS UNSIGNED)) FROM DONHANG";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                int lastMaDonHang = rs.getInt(1);
                // Tạo mã đơn mới
                int newMaDonHang = lastMaDonHang + 1;
                // Định dạng mã đơn hàng với 5 chữ số (ví dụ: DH00001)
                return String.format("DH%05d", newMaDonHang);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        // Nếu chưa có mã đơn hàng nào, tạo mã mới bắt đầu từ DH00001
        return "DH00001";
    }

    public List<DanhMucDTO> layThucDon(String maDanhMuc) {
        List<DanhMucDTO> danhMucList = new ArrayList<>();
        
        try {
            // Câu truy vấn danh mục
            String queryDanhMuc = "SELECT * FROM DANHMUC";
            if (!"all".equalsIgnoreCase(maDanhMuc)) {
                queryDanhMuc += " WHERE MaDanhMuc = ?";
            }
    
            try (PreparedStatement stmt = connection.prepareStatement(queryDanhMuc)) {
                // Nếu không phải lấy tất cả, gán giá trị cho tham số truy vấn
                if (!"all".equalsIgnoreCase(maDanhMuc)) {
                    stmt.setString(1, maDanhMuc);
                }
    
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String maDanhMucResult = rs.getString("MaDanhMuc");
                        String tenDanhMuc = rs.getString("TenDanhMuc");
    
                        // Lấy danh sách cà phê theo danh mục
                        List<Mon> danhSachCaPhe = new ArrayList<>();
                        String queryCaPhe = "SELECT * FROM CAPHE WHERE MaDanhMuc = ?";
                        try (PreparedStatement ps = connection.prepareStatement(queryCaPhe)) {
                            ps.setString(1, maDanhMucResult);
                            try (ResultSet rsCaPhe = ps.executeQuery()) {
                                while (rsCaPhe.next()) {
                                    Mon caPhe = new Mon();
                                    caPhe.setMaMon(rsCaPhe.getString("MaCaPhe"));
                                    caPhe.setTenMon(rsCaPhe.getString("TenCaPhe"));
    
                                    // Kiểm tra ảnh minh họa, nếu là null thì gán giá trị mặc định
                                    byte[] anhMinhHoa = rsCaPhe.getBytes("AnhMinhHoa");
                                    if (anhMinhHoa == null) {
                                        String defaultImagePath = "H:/Documents/TTCSN/quanlicafe/src/main/resources/icons/coffee-cup.png";
                                        try (InputStream inputStream = new FileInputStream(defaultImagePath)) {
                                            anhMinhHoa = inputStream.readAllBytes();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    caPhe.setAnhMinhHoa(anhMinhHoa);
                                    
                                    caPhe.setDonGia(rsCaPhe.getInt("DonGia"));
                                    caPhe.setMaDanhMuc(rsCaPhe.getString("MaDanhMuc"));
                                    danhSachCaPhe.add(caPhe);
                                }
                            }
                        }
    
                        // Tạo danh mục
                        DanhMucDTO danhMuc = new DanhMucDTO(maDanhMucResult, tenDanhMuc, danhSachCaPhe);
                        danhMucList.add(danhMuc);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> danhMucCoDinh = Arrays.asList("CaPheThuong", "CaPheDacBiet", "CaPheLanh", "CaPheTheoMua");
        danhMucList.sort(Comparator.comparingInt(danhMuc -> {
            int index = danhMucCoDinh.indexOf(danhMuc.getMaDanhMuc());
            return index == -1 ? Integer.MAX_VALUE : index; // Nếu không tìm thấy, đặt ở cuối
        }));
        return danhMucList;
    }

    public void resetDanhSachCaPheTrongDon(){
        danhSachCaPheTrongDon.clear();
    } */
}
