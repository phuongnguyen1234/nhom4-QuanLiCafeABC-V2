package com.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.model.DonHang;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {
    @Query("SELECT MAX(d.maDonHang) FROM DonHang d")
    String findMaxMaDonHang();
    // Phân trang đơn hàng
    Page<DonHang> findAll(Pageable pageable);
    
    // Lọc đơn hàng theo khoảng thời gian
    List<DonHang> findByThoiGianDatHangBetween(LocalDateTime start, LocalDateTime end);
    
    // Tìm kiếm đơn hàng theo mã hoặc tên nhân viên (sử dụng JOIN)
    @Query("SELECT dh FROM DonHang dh JOIN dh.nhanVien nv " +
           "WHERE dh.maDonHang LIKE %:keyword% OR nv.hoTen LIKE %:keyword%")
    List<DonHang> searchDonHang(@Param("keyword") String keyword);
}
