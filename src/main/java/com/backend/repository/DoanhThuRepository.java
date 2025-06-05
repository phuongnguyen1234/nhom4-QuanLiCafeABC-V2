package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.model.DoanhThu;

@Repository
public interface DoanhThuRepository extends JpaRepository<DoanhThu, Integer> {
    // Custom query methods can be added here if needed
    // For example, to find all DoanhThu records or by specific criteria
    
    List<DoanhThu> findByThangAndNam(int Thang, int Nam);

    // Truy vấn doanh thu trong một khoảng thời gian (tháng/năm bắt đầu đến tháng/năm kết thúc)
    @Query("SELECT d FROM DoanhThu d WHERE (d.nam > :namStart OR (d.nam = :namStart AND d.thang >= :thangStart)) AND (d.nam < :namEnd OR (d.nam = :namEnd AND d.thang <= :thangEnd)) ORDER BY d.nam ASC, d.thang ASC")
    List<DoanhThu> findByThoiGianRange(@Param("thangStart") int thangStart, @Param("namStart") int namStart, @Param("thangEnd") int thangEnd, @Param("namEnd") int namEnd);
}
