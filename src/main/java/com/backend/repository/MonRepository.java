package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.dto.MonDTO;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;

@Repository
public interface MonRepository extends JpaRepository<Mon, String> {
    // Tìm kiếm món theo tên
    List<Mon> findByTenMonContaining(String tenMon);

    // Tìm kiếm món theo danh mục
    List<Mon> findByDanhMuc(DanhMuc danhMuc);

    // Tìm kiếm món theo tên và danh mục
    List<Mon> findByTenMonContainingAndDanhMuc(String tenMon, DanhMuc danhMuc);

    // lay danh sach toan bo mon theo tung danh muc
    List<Mon> findByDanhMucIn(List<DanhMuc> danhMucList);

    //them va cap nhat mon
    Mon save(MonDTO mon);

    

}
