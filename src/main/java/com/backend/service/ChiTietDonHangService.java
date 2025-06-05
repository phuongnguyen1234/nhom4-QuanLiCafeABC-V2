package com.backend.service;

import java.util.List;

import com.backend.model.ChiTietDonHang;

public interface ChiTietDonHangService {
     List<ChiTietDonHang> findByMaDonHang(String maDonHang);
}
