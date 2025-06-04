package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backend.model.DonHang;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {
    @Query("SELECT MAX(d.maDonHang) FROM DonHang d")
    String findMaxMaDonHang();

}
