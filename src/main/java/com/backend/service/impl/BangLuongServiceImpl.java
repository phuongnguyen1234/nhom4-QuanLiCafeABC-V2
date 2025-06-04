package com.backend.service.impl;

import org.springframework.stereotype.Service;

import com.backend.repository.BangLuongRepository;
import com.backend.service.BangLuongService;

@Service
public class BangLuongServiceImpl implements BangLuongService {
    private final BangLuongRepository bangLuongRepository;

    public BangLuongServiceImpl(BangLuongRepository bangLuongRepository) {
        this.bangLuongRepository = bangLuongRepository;
    }

}
