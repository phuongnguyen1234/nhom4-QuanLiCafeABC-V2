package com.backend.service;

import java.util.List;

import com.backend.dto.ChiTietDonHangDTO;

public interface ChiTietDonHangService {
     List<ChiTietDonHangDTO> findByMaDonHang(String maDonHang);
    int calculateTongTien(String maDonHang);
    ChiTietDonHangDTO save(ChiTietDonHangDTO chiTietDonHangDTO);
    ChiTietDonHangDTO update(Long id, ChiTietDonHangDTO chiTietDonHangDTO);
    void delete(Long id);

}
