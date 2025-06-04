package com.backend.service;

import java.util.List;

import com.backend.dto.DonHangDTO;
import com.backend.model.DonHang;
import com.backend.model.NhanVien;

public interface DonHangService {
    DonHang createDonHang(DonHangDTO donHangDTO);
    int getTongDoanhThuHomNay();
    List<NhanVien> getTop3NhanVienTheoThang();
    List<String> getTop5MonBanChayTheoThang();
}
