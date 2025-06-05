package com.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.backend.dto.DonHangDTO;
import com.backend.model.DonHang;
import com.backend.model.NhanVien;

public interface DonHangService {
    DonHang createDonHang(DonHangDTO donHangDTO);
     List<DonHangDTO> getAllDonHang();
    
    // Mới: Lấy đơn hàng theo ID
    Optional<DonHangDTO> getDonHangById(String maDonHang);
    
    // Mới: Lọc theo khoảng thời gian
    //List<DonHang> filterByDate(LocalDateTime start, LocalDateTime end);
    
    // Mới: Tìm kiếm theo mã đơn hàng hoặc tên nhân viên
    //List<DonHang> searchDonHang(String keyword);

    int getTongDoanhThuHomNay();
    List<NhanVien> getTop3NhanVienTheoThang();
    List<String> getTop5MonBanChayTheoThang();
    List<DonHangDTO> filterByNgay(LocalDate ngay);
}
