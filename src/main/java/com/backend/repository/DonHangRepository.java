package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.DonHang;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, String> {
    // Custom query methods can be defined here if needed
    
}
