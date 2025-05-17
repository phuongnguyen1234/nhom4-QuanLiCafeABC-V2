package com.backend.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.BangLuong;

@Repository
public interface BangLuongRepository extends JpaRepository<BangLuong, String> {
    //lay danh sach bang luong theo thang 
    public List<BangLuong> getBangLuongByThang(YearMonth thang);

    //tao danh sach bang luong thang hien tai
    @Override
    <S extends BangLuong> List<S> saveAll(Iterable<S> entities);

}   
