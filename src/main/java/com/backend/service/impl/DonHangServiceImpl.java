package com.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.DonHangRepository;
import com.backend.service.DonHangService;

@Service
public class DonHangServiceImpl implements DonHangService {
    @Autowired
    private DonHangRepository donHangRepository;
    
    // Implement methods for DonHangService here

}
