package com.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.ChiTietDonHangRepository;
import com.backend.service.ChiTietDonHangService;

@Service
public class ChiTietDonHangServiceImpl implements ChiTietDonHangService {
    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;
}
