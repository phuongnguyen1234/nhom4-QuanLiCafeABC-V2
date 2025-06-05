package com.backend.repository;

import java.util.List;

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
}
