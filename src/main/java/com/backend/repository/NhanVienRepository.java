package com.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.model.NhanVien;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, String> {
    Optional<NhanVien> findByEmail(String email);

    @Query("SELECT MAX(nv.maNhanVien) FROM NhanVien nv WHERE nv.maNhanVien LIKE 'NV%'")
    String findMaxMaNhanVien();

    // Thêm phương thức để tìm tất cả nhân viên theo trạng thái
    List<NhanVien> findByTrangThai(String trangThai);

    // Thêm phương thức để tìm nhân viên theo tên (không phân biệt hoa thường)
    List<NhanVien> findByHoTenContainingIgnoreCase(String hoTen);

    List<NhanVien> findByViTriAndTrangThaiHoatDong(String viTri, String trangThaiHoatDong);
}
