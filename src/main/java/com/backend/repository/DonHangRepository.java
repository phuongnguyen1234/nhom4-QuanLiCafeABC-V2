package com.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.model.DonHang;


@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {
    @Query("SELECT MAX(d.maDonHang) FROM DonHang d")
    String findMaxMaDonHang();
    @Query(value = "SELECT SUM(TongTien) FROM DonHang WHERE DATE(ThoiGianDatHang) = CURRENT_DATE", nativeQuery = true)
    int tinhDoanhThuHomNay();
    @Query(value ="SELECT MaNhanVien FROM DonHang WHERE MONTH(dh.ThoiGianDatHang) = :thang AND YEAR(dh.ThoiGianDatHang) = :nam GROUP BY MaNhanVien ORDER BY COUNT(*) DESC LIMIT 3", nativeQuery = true)
    List<String> top3MaNhanVienTheoThang(@Param("thang") int thang, @Param("nam") int nam);
    
    @Query(value="""
            SELECT cd.MaMon
            FROM ChiTietDonHang cd
            JOIN DonHang dh ON cd.MaDonHang = dh.MaDonHang
            WHERE MONTH(dh.ThoiGianDatHang) = :thang AND YEAR(dh.ThoiGianDatHang) = :nam
            GROUP BY cd.MaMon
            ORDER BY SUM(cd.SoLuong) DESC
            LIMIT 5
            """, 
            nativeQuery = true)
    List<String> top5MonTheoThangNam(@Param("thang") int thang, @Param("nam") int nam);

    // Phân trang đơn hàng
    Page<DonHang> findAll(Pageable pageable);
    
    // Lọc đơn hàng theo khoảng thời gian
    List<DonHang> findByThoiGianDatHangBetween(LocalDateTime start, LocalDateTime end);
    
    // Tìm kiếm đơn hàng theo mã hoặc tên nhân viên (sử dụng JOIN)
    @Query("SELECT dh FROM DonHang dh JOIN dh.nhanVien nv " +
           "WHERE dh.maDonHang LIKE %:keyword% OR nv.hoTen LIKE %:keyword%")
    List<DonHang> searchDonHang(@Param("keyword") String keyword);

    @EntityGraph(attributePaths = "chiTietDonHangs")
    Optional<DonHang> findWithChiTietDonHangByMaDonHang(String maDonHang);

    @Query("SELECT COUNT(dh) FROM DonHang dh WHERE dh.nhanVien.maNhanVien = :maNhanVien AND FUNCTION('MONTH', dh.thoiGianDatHang) = :month AND FUNCTION('YEAR', dh.thoiGianDatHang) = :year")
    int countByNhanVienMaNhanVienAndThoiGianDatHangMonthAndThoiGianDatHangYear(@Param("maNhanVien") String maNhanVien, @Param("month") int month, @Param("year") int year);
}
