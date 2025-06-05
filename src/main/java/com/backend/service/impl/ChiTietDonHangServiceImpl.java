package com.backend.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.backend.model.ChiTietDonHang;
import com.backend.repository.ChiTietDonHangRepository;
import com.backend.service.ChiTietDonHangService;

@Service
public class ChiTietDonHangServiceImpl implements ChiTietDonHangService {
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    public ChiTietDonHangServiceImpl(ChiTietDonHangRepository chiTietDonHangRepository) {
        this.chiTietDonHangRepository = chiTietDonHangRepository;
    }
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<ChiTietDonHang> findByMaDonHang(String maDonHang) {
        return chiTietDonHangRepository.findByDonHang_MaDonHang(maDonHang);
    }
}
