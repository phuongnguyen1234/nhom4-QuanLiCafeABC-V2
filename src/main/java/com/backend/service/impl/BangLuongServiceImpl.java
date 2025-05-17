package com.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.repository.BangLuongRepository;
import com.backend.service.BangLuongService;

@Service
public class BangLuongServiceImpl implements BangLuongService {
    @Autowired
    private BangLuongRepository bangLuongRepository;


}
