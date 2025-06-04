package com.frontend.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhanTichHoatDongController {
    /*private Connection connection;

    public PhanTichHoatDongController(){
        try {
            connection = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int layDoanhThuHomNay(){
        String sql = "SELECT SUM(TongTien) FROM DONHANG WHERE DATE(ThoiGianDatHang)=?";
        int doanhThuHomNay = 0;

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            LocalDate ngayHomNay = LocalDate.now();
            String todayString = ngayHomNay.format(DateTimeFormatter.ISO_LOCAL_DATE);

            ps.setString(1, todayString);

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) doanhThuHomNay = rs.getInt(1);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return doanhThuHomNay;
    }

    public int layDoanhThuThangTruoc() {
        String sql = "SELECT SUM(TongTien) FROM DONHANG WHERE MONTH(ThoiGianDatHang) = ? AND YEAR(ThoiGianDatHang) = ?";
        int doanhThuThangTruoc = 0;
    
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Lấy tháng trước và năm tương ứng
            LocalDate ngayHomNay = LocalDate.now();
            LocalDate thangTruoc = ngayHomNay.minusMonths(1);
    
            int thang = thangTruoc.getMonthValue(); // Lấy số tháng (1-12)
            int nam = thangTruoc.getYear();        // Lấy năm
    
            // Gán giá trị cho câu lệnh SQL
            ps.setInt(1, thang);
            ps.setInt(2, nam);
    
            // Thực thi truy vấn
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    doanhThuThangTruoc = rs.getInt(1); // Lấy kết quả từ cột đầu tiên
                }
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return doanhThuThangTruoc;
    }

    public int laySoCaPheBanRaThangTruoc() {
        String sql = "SELECT SUM(SoLuong) FROM CHITIETDONHANG ct " +
                     "JOIN DONHANG dh ON ct.MaDonHang = dh.MaDonHang " +
                     "WHERE MONTH(dh.ThoiGianDatHang) = ? AND YEAR(dh.ThoiGianDatHang) = ?";
        int soCaPheBanRaThangTruoc = 0;
    
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Lấy tháng trước và năm tương ứng
            LocalDate ngayHomNay = LocalDate.now();
            LocalDate thangTruoc = ngayHomNay.minusMonths(1);
    
            int thang = thangTruoc.getMonthValue();
            int nam = thangTruoc.getYear();
    
            // Gán giá trị cho câu lệnh SQL
            ps.setInt(1, thang);
            ps.setInt(2, nam);
    
            // Thực thi truy vấn
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    soCaPheBanRaThangTruoc = rs.getInt(1); // Lấy tổng số lượng
                }
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return soCaPheBanRaThangTruoc;
    }

    public int laySoDonDaTaoThangTruoc() {
        String sql = "SELECT COUNT(*) FROM DONHANG " +
                     "WHERE MONTH(ThoiGianDatHang) = ? AND YEAR(ThoiGianDatHang) = ?";
        int soDonDaTaoThangTruoc = 0;
    
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Lấy tháng trước và năm tương ứng
            LocalDate ngayHomNay = LocalDate.now();
            LocalDate thangTruoc = ngayHomNay.minusMonths(1);
    
            int thang = thangTruoc.getMonthValue();
            int nam = thangTruoc.getYear();
    
            // Gán giá trị cho câu lệnh SQL
            ps.setInt(1, thang);
            ps.setInt(2, nam);
    
            // Thực thi truy vấn
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    soDonDaTaoThangTruoc = rs.getInt(1); // Lấy số đơn hàng
                }
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return soDonDaTaoThangTruoc;
    }

    public List<Map<String, Object>> layTop5CaPheBanChayThangTruoc() {
        String sql = "SELECT c.TenCaPhe, SUM(ct.SoLuong) AS TongSoLuong "
                + "FROM CAPHE c "
                + "JOIN CHITIETDONHANG ct ON c.MaCaPhe = ct.MaCaPhe "
                + "JOIN DONHANG d ON ct.MaDonHang = d.MaDonHang "
                + "WHERE MONTH(d.ThoiGianDatHang) = ? AND YEAR(d.ThoiGianDatHang) = ? "
                + "GROUP BY c.TenCaPhe "
                + "ORDER BY TongSoLuong DESC "
                + "LIMIT 5";
        
        List<Map<String, Object>> top5CaPhe = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Lấy tháng và năm tháng trước
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            int month = lastMonth.getMonthValue();
            int year = lastMonth.getYear();
            
            // Đặt giá trị tháng và năm vào PreparedStatement
            ps.setInt(1, month);
            ps.setInt(2, year);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> caPhe = new HashMap<>();
                    caPhe.put("TenCaPhe", rs.getString("TenCaPhe"));
                    caPhe.put("TongSoLuong", rs.getInt("TongSoLuong"));
                    top5CaPhe.add(caPhe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return top5CaPhe;
    }

    public List<Map<String, Object>> layTop3NhanVienTaoDonNhieuNhatThangTruoc() {
        List<Map<String, Object>> top3NhanVien = new ArrayList<>();
        
        // SQL query để lấy top 3 nhân viên tạo đơn nhiều nhất trong tháng trước, loại trừ NV000 và các vị trí "Pha chế", "Phục vụ"
        String sql = "SELECT nv.MaNhanVien, nv.TenNhanVien, COUNT(dh.MaDonHang) AS SoDon " +
                     "FROM NHANVIEN nv " +
                     "JOIN DONHANG dh ON nv.MaNhanVien = dh.MaNhanVien " +
                     "WHERE MONTH(dh.ThoiGianDatHang) = ? " +
                     "AND YEAR(dh.ThoiGianDatHang) = ? " +
                     "AND nv.MaNhanVien != 'NV000' " +
                     "AND (nv.ViTri != 'Pha chế' AND nv.ViTri != 'Phục vụ') " +
                     "GROUP BY nv.MaNhanVien " +
                     "ORDER BY SoDon DESC " +
                     "LIMIT 3";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Lấy tháng và năm tháng trước
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            int month = lastMonth.getMonthValue();
            int year = lastMonth.getYear();
            
            // Đặt giá trị tháng và năm vào PreparedStatement
            ps.setInt(1, month);
            ps.setInt(2, year);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> nhanVien = new HashMap<>();
                    nhanVien.put("MaNhanVien", rs.getString("MaNhanVien"));
                    nhanVien.put("TenNhanVien", rs.getString("TenNhanVien"));
                    nhanVien.put("SoDon", rs.getInt("SoDon"));
                    top3NhanVien.add(nhanVien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return top3NhanVien;
    }

    public List<Map<String, Object>> layBienDongDoanhThu6Thang() {
        String sql = "SELECT MONTH(dh.ThoiGianDatHang) AS Thang, SUM(ct.TamTinh) AS DoanhThu "
               + "FROM DONHANG dh "
               + "JOIN CHITIETDONHANG ct ON dh.MaDonHang = ct.MaDonHang "
               + "WHERE dh.ThoiGianDatHang BETWEEN DATE_FORMAT(CURRENT_DATE - INTERVAL 6 MONTH, '%Y-%m-01') "
               + "AND LAST_DAY(CURRENT_DATE - INTERVAL 1 MONTH) "
               + "GROUP BY MONTH(dh.ThoiGianDatHang) "
               + "ORDER BY Thang ASC";
    
        List<Map<String, Object>> doanhThuList = new ArrayList<>();
    
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("Thang", rs.getInt("Thang"));
                data.put("DoanhThu", rs.getDouble("DoanhThu"));
                doanhThuList.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doanhThuList;
    }
    
    public List<Map<String, Object>> layKhoangThoiGianDatDonNhieuNhatThangTruoc() {
        String sql = "SELECT HOUR(dh.ThoiGianDatHang) AS Gio, COUNT(dh.MaDonHang) AS SoDon "
                   + "FROM DONHANG dh "
                   + "WHERE MONTH(dh.ThoiGianDatHang) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) "
                   + "AND YEAR(dh.ThoiGianDatHang) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) "
                   + "GROUP BY HOUR(dh.ThoiGianDatHang) "
                   + "ORDER BY SoDon DESC "
                   + "LIMIT 5";
        
        List<Map<String, Object>> thoiGianDatDonList = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> data = new HashMap<>();
                data.put("Gio", rs.getInt("Gio"));
                data.put("SoDon", rs.getInt("SoDon"));
                thoiGianDatDonList.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thoiGianDatDonList;
    } */
}
