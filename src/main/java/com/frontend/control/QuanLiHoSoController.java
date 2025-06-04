package com.frontend.control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuanLiHoSoController {
    /*private Connection connection;

    public QuanLiHoSoController() {
        try {
            // Lấy kết nối từ DBConnection
            this.connection = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý thêm nếu cần, ví dụ: hiển thị thông báo lỗi
        }
    }

    // Phương thức lấy danh sách nhân viên từ cơ sở dữ liệu
    public List<NhanVienDTO> layDanhSachNhanVien() {
        List<NhanVienDTO> danhSachNhanVien = new ArrayList<>();
    
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                 "SELECT MaNhanVien, TenNhanVien, LoaiNhanVien, ViTri, TrangThaiHoatDong, QuyenTruyCap FROM NHANVIEN")) {
    
            while (resultSet.next()) {
                NhanVienDTO nhanVien = new NhanVienDTO();
                nhanVien.setMaNhanVien(resultSet.getString("MaNhanVien"));
                nhanVien.setTenNhanVien(resultSet.getString("TenNhanVien"));
                nhanVien.setLoaiNhanVien(resultSet.getString("LoaiNhanVien"));
                nhanVien.setViTri(resultSet.getString("ViTri"));
                nhanVien.setTrangThaiHoatDong(resultSet.getString("TrangThaiHoatDong"));
                nhanVien.setQuyenTruyCap(resultSet.getString("QuyenTruyCap"));
    
                danhSachNhanVien.add(nhanVien);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSachNhanVien;
    }

    public boolean themNhanVien(NhanVien nhanVien) {
        String maNhanVienMoi = layMaNhanVienMoi();
        nhanVien.setMaNhanVien(maNhanVienMoi);
        nhanVien.setTrangThaiHoatDong("0");  // Đặt mặc định là "0"
        
        String sql = "INSERT INTO NHANVIEN (MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, QueQuan, DiaChi, SoDienThoai, LoaiNhanVien, ViTri, ThoiGianVaoLam, MucLuong, Email, MatKhau, QuyenTruyCap, TrangThaiHoatDong, AnhChanDung) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nhanVien.getMaNhanVien());
            preparedStatement.setString(2, nhanVien.getTenNhanVien());
            preparedStatement.setString(3, nhanVien.getGioiTinh());
            preparedStatement.setDate(4, java.sql.Date.valueOf(nhanVien.getNgaySinh()));
            preparedStatement.setString(5, nhanVien.getQueQuan());
            preparedStatement.setString(6, nhanVien.getDiaChi());
            preparedStatement.setString(7, nhanVien.getSoDienThoai());
            preparedStatement.setString(8, nhanVien.getLoaiNhanVien());
            preparedStatement.setString(9, nhanVien.getViTri());
            preparedStatement.setDate(10, java.sql.Date.valueOf(nhanVien.getThoiGianVaoLam()));
            preparedStatement.setInt(11, nhanVien.getMucLuong());
            preparedStatement.setString(12, nhanVien.getEmail());
            preparedStatement.setString(13, nhanVien.getMatKhau());
            preparedStatement.setString(14, nhanVien.getQuyenTruyCap());
            preparedStatement.setString(15, nhanVien.getTrangThaiHoatDong());
            
            // Lưu ảnh vào cơ sở dữ liệu (ở dạng byte[])
            if (nhanVien.getAnhChanDung() != null) {
                preparedStatement.setBytes(16, nhanVien.getAnhChanDung());
                System.out.println("Có ảnh");
            } else {
                preparedStatement.setNull(16, java.sql.Types.BLOB); // Nếu không có ảnh, gán NULL
                System.out.println("Không có ảnh");
            }
            
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    // Phương thức lấy mã nhân viên mới (theo định dạng NV001, NV002,...)
    private String layMaNhanVienMoi() {
        String maNhanVienMoi = "NV000"; // Mặc định là "NV000"
        
        String sql = "SELECT MAX(MaNhanVien) AS MaxMaNhanVien FROM NHANVIEN";
        
        try (Statement statement = connection.createStatement(); 
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            if (resultSet.next()) {
                String maNhanVienCuoi = resultSet.getString("MaxMaNhanVien");
                if (maNhanVienCuoi != null && !maNhanVienCuoi.isEmpty()) {
                    // Lấy số cuối của mã nhân viên hiện tại
                    int soCuoi = Integer.parseInt(maNhanVienCuoi.replace("NV", ""));
                    soCuoi++;
                    maNhanVienMoi = "NV" + String.format("%03d", soCuoi); // Tạo mã nhân viên mới
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return maNhanVienMoi;
    }

    // Phương thức lấy thông tin nhân viên theo mã nhân viên
    public NhanVienDTO layThongTinNhanVien(String maNhanVien) {
        NhanVienDTO nhanVien = null;
        String sql = "SELECT MaNhanVien, TenNhanVien, GioiTinh, NgaySinh, QueQuan, DiaChi, SoDienThoai, LoaiNhanVien, ViTri, ThoiGianVaoLam, MucLuong, Email, MatKhau, QuyenTruyCap, TrangThaiHoatDong, AnhChanDung FROM NHANVIEN WHERE MaNhanVien = ?";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, maNhanVien);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    nhanVien = new NhanVienDTO();
                    nhanVien.setMaNhanVien(resultSet.getString("MaNhanVien"));
                    nhanVien.setTenNhanVien(resultSet.getString("TenNhanVien"));
                    nhanVien.setGioiTinh(resultSet.getString("GioiTinh"));
                    nhanVien.setNgaySinh(resultSet.getDate("NgaySinh").toLocalDate());
                    nhanVien.setQueQuan(resultSet.getString("QueQuan"));
                    nhanVien.setDiaChi(resultSet.getString("DiaChi"));
                    nhanVien.setSoDienThoai(resultSet.getString("SoDienThoai"));
                    nhanVien.setLoaiNhanVien(resultSet.getString("LoaiNhanVien"));
                    nhanVien.setViTri(resultSet.getString("ViTri"));
                    nhanVien.setThoiGianVaoLam(resultSet.getDate("ThoiGianVaoLam").toLocalDate());
                    nhanVien.setMucLuong(resultSet.getInt("MucLuong"));
                    nhanVien.setEmail(resultSet.getString("Email"));
                    nhanVien.setMatKhau(resultSet.getString("MatKhau"));
                    nhanVien.setQuyenTruyCap(resultSet.getString("QuyenTruyCap"));
                    nhanVien.setTrangThaiHoatDong(resultSet.getString("TrangThaiHoatDong"));

                    // Lấy ảnh chân dung
                    byte[] anhChanDung = resultSet.getBytes("AnhChanDung");
                    nhanVien.setAnhChanDung(anhChanDung);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nhanVien;
    }


    public boolean capNhatNhanVien(NhanVienDTO nhanVien) {
        String sql = "UPDATE NHANVIEN SET TenNhanVien = ?, GioiTinh = ?, NgaySinh = ?, QueQuan = ?, DiaChi = ?, SoDienThoai = ?, LoaiNhanVien = ?, ViTri = ?, ThoiGianVaoLam = ?, MucLuong = ?, Email = ?, MatKhau = ?, QuyenTruyCap = ?, AnhChanDung = ? WHERE MaNhanVien = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nhanVien.getTenNhanVien());
            System.out.println("Cập nhật nhân viên: " + nhanVien.getTenNhanVien());
            preparedStatement.setString(2, nhanVien.getGioiTinh());
            preparedStatement.setDate(3, java.sql.Date.valueOf(nhanVien.getNgaySinh()));
            preparedStatement.setString(4, nhanVien.getQueQuan());
            preparedStatement.setString(5, nhanVien.getDiaChi());
            preparedStatement.setString(6, nhanVien.getSoDienThoai());
            preparedStatement.setString(7, nhanVien.getLoaiNhanVien());
            preparedStatement.setString(8, nhanVien.getViTri());
            preparedStatement.setDate(9, java.sql.Date.valueOf(nhanVien.getThoiGianVaoLam()));
            preparedStatement.setInt(10, nhanVien.getMucLuong());
            preparedStatement.setString(11, nhanVien.getEmail());
            preparedStatement.setString(12, nhanVien.getMatKhau());
            preparedStatement.setString(13, nhanVien.getQuyenTruyCap());
    
            if (nhanVien.getAnhChanDung() != null) {
                preparedStatement.setBytes(14, nhanVien.getAnhChanDung());
            } else {
                preparedStatement.setNull(14, java.sql.Types.BLOB);
            }
    
            preparedStatement.setString(15, nhanVien.getMaNhanVien());
            System.out.println("Mã nhân viên: " + nhanVien.getMaNhanVien());
    
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                System.out.println("Không có bản ghi nào bị cập nhật.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean kiemTraEmailSoDienThoaiTrung(String email, String soDienThoai) {
        // Kiểm tra email và số điện thoại trùng lặp trong cơ sở dữ liệu
        String queryEmail = "SELECT COUNT(*) FROM NHANVIEN WHERE Email = ?";
        String querySoDienThoai = "SELECT COUNT(*) FROM NHANVIEN WHERE SoDienThoai = ?";
    
        try (PreparedStatement stmtEmail = connection.prepareStatement(queryEmail);
             PreparedStatement stmtSoDienThoai = connection.prepareStatement(querySoDienThoai)) {
    
            // Kiểm tra email
            stmtEmail.setString(1, email);
            ResultSet rsEmail = stmtEmail.executeQuery();
            if (rsEmail.next() && rsEmail.getInt(1) > 0) {
                return true;  // Email đã tồn tại
            }
    
            // Kiểm tra số điện thoại
            stmtSoDienThoai.setString(1, soDienThoai);
            ResultSet rsSoDienThoai = stmtSoDienThoai.executeQuery();
            if (rsSoDienThoai.next() && rsSoDienThoai.getInt(1) > 0) {
                return true;  // Số điện thoại đã tồn tại
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;  // Không có trùng lặp
    }
    
    */
}
