package com.frontend.control;

public class CapNhatThucDonController {
    public CapNhatThucDonController(){        
    }

    /*public boolean kiemTraNhanVienKhacDangOnline() {
        String sql = "SELECT COUNT(*) FROM NHANVIEN WHERE TrangThaiHoatDong = '1' AND ViTri = 'Thu ngân'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
    
            // Kiểm tra kết quả
            if (rs.next()) {
                int count = rs.getInt(1); // Lấy giá trị đếm từ cột đầu tiên
                return count > 0; // Nếu có nhân viên đang online, trả về true
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Không có nhân viên online
    }
    

    public void suaCaPhe(Mon caPheUpdate){
        String sql = "UPDATE CAPHE SET TenCaPhe=?, AnhMinhHoa=?, DonGia=?, MaDanhMuc=? WHERE MaCaPhe=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, caPheUpdate.getTenMon());
            ps.setBytes(2, caPheUpdate.getAnhMinhHoa());
            ps.setInt(3, caPheUpdate.getDonGia());
            ps.setString(4, caPheUpdate.getMaDanhMuc());
            ps.setString(5, caPheUpdate.getMaMon());

            // Sử dụng executeUpdate() để thực thi lệnh UPDATE
            int rowsAffected = ps.executeUpdate();
    
            // Kiểm tra kết quả
            if (rowsAffected > 0) {
                System.out.println("Sửa thành công!");
            } else {
                System.out.println("Sửa thất bại! Không tìm thấy ca phe có mã: " + caPheUpdate.getMaMon());
            }
        } catch (Exception e) {
        }
    }

    public void themCaPhe(Mon caPheThem) {
        String sql = "INSERT INTO CAPHE(MaCaPhe, TenCaPhe, AnhMinhHoa, DonGia, MaDanhMuc) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Gán giá trị cho các tham số
            ps.setString(1, generateMaCaPhe()); // Hàm generateMaCaPhe() cần đảm bảo không trùng mã
            ps.setString(2, caPheThem.getTenMon());
            ps.setBytes(3, caPheThem.getAnhMinhHoa());
            ps.setInt(4, caPheThem.getDonGia());
            ps.setString(5, caPheThem.getMaDanhMuc());
            
            // Thực thi câu lệnh
            ps.executeUpdate();
            System.out.println("Thêm cà phê thành công!");
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi nếu có vấn đề
        }
    }
    

    public String generateMaCaPhe() {
        String maCaPhe = "";
        try {
            // Lấy số đếm lớn nhất hiện tại trong bảng CAPHE
            String sql = "SELECT MAX(CAST(SUBSTRING(MaCaPhe, 3) AS UNSIGNED)) AS maxMaCaPhe FROM CAPHE";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    // Lấy số đếm lớn nhất (ví dụ: 001, 002, ...)
                    int maxMa = rs.getInt("maxMaCaPhe");
                    
                    // Tạo mã cà phê mới, thêm 1 vào số đếm hiện tại
                    maCaPhe = "CP" + String.format("%03d", maxMa + 1); // Ví dụ: CP001, CP002, ...
                } else {
                    // Nếu chưa có dữ liệu, bắt đầu từ CP001
                    maCaPhe = "CP001";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maCaPhe;
    }

    
*/
}
