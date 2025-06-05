package com.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.BangLuong;

@Repository
public interface BangLuongRepository extends JpaRepository<BangLuong, String> {

    // Lấy danh sách bảng lương theo tháng (giả định tháng là kiểu LocalDate)
   List<BangLuong> getBangLuongByThang(LocalDate thang);

    // Tìm bảng lương theo mã
    BangLuong findByMaBangLuong(String maBangLuong);
 
    // Kiểm tra tồn tại
    boolean existsByMaBangLuong(String maBangLuong);

    
}
