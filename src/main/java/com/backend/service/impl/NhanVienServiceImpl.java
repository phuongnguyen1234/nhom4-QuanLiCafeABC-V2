package com.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.NhanVienRepository;
import com.backend.service.NhanVienService;

@Service
public class NhanVienServiceImpl implements NhanVienService {
    @Autowired
    private NhanVienRepository nhanVienRepository;

}
