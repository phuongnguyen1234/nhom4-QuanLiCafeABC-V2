package com.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.ChiTietHoaDonRepository;
import com.backend.service.ChiTietHoaDonService;

@Service
public class ChiTietHoaDonServiceImpl implements ChiTietHoaDonService {
    @Autowired
    private ChiTietHoaDonRepository chiTietHoaDonRepository;


}
