package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.model.DoanhThu;

@Repository
public interface DoanhThuRepository extends JpaRepository<DoanhThu, Integer> {
    // Custom query methods can be added here if needed
    // For example, to find all DoanhThu records or by specific criteria

    List<DoanhThu> findByThangAndNam(int Thang, int Nam);
}
