package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.ChiTietHoaDon;

@Repository
public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, String> {
    

}
