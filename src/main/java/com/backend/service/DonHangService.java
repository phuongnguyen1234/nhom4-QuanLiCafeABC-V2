package com.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.dto.DonHangDTO;
import com.backend.model.DonHang;
import com.backend.model.NhanVien;

public interface DonHangService {
    DonHang createDonHang(DonHangDTO donHangDTO);
     List<DonHang> getAllDonHang();
    
    // Mới: Lấy đơn hàng theo ID
    Optional<DonHang> getDonHangById(String maDonHang);
    
    // Mới: Xóa đơn hàng
    void deleteDonHang(String maDonHang);
    
    // Mới: Phân trang
    Page<DonHang> getAllDonHangPaginated(Pageable pageable);
    
    // Mới: Lọc theo khoảng thời gian
    //List<DonHang> filterByDate(LocalDateTime start, LocalDateTime end);
    
    // Mới: Tìm kiếm theo mã đơn hàng hoặc tên nhân viên
    //List<DonHang> searchDonHang(String keyword);

    int getTongDoanhThuHomNay();
    List<NhanVien> getTop3NhanVienTheoThang();
    List<String> getTop5MonBanChayTheoThang();
}
