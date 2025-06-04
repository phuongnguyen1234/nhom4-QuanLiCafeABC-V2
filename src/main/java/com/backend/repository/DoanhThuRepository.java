package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.DoanhThu;

@Repository
public interface DoanhThuRepository extends JpaRepository<DoanhThu, Integer> {
    // Custom query methods can be added here if needed
    // For example, to find all DoanhThu records or by specific criteria

}
