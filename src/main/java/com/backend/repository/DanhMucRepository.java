package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.model.DanhMuc;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    // Lấy tất cả danh mục kèm món (ảnh minh họa loại bỏ ở DTO)
    @Query("SELECT DISTINCT d FROM DanhMuc d LEFT JOIN FETCH d.monList")
    List<DanhMuc> findAllWithMon();

    //them va cap nhat danh muc
    DanhMuc save(DanhMuc danhMuc);
}
