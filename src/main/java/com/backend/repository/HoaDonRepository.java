package com.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.HoaDon;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, String> {
    //lay danh sach tat ca hoa don
    List<HoaDon> findAllByOrderByThoiGianDatHangDesc();

    //them hoa don
    HoaDon save(HoaDon hoaDon);

    //tim theo id
    Optional<HoaDon> findById(String maHoaDon);

    //tim kiem theo thoi gian dat hang
    List<HoaDon> findByThoiGianDatHang(LocalDateTime thoiGianDatHang);
}
