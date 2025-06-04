package com.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.DoanhThuRepository;
import com.backend.service.DoanhThuService;

@Service
public class DoanhThuServiceImpl implements DoanhThuService {
    private final DoanhThuRepository doanhThuRepository;

    public DoanhThuServiceImpl(DoanhThuRepository doanhThuRepository) {
        this.doanhThuRepository = doanhThuRepository;
    }

}
