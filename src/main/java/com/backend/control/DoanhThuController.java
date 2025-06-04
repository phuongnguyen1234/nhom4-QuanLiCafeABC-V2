package com.backend.control;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.DoanhThuService;

@RestController
@RequestMapping("/doanh-thu")
@CrossOrigin(origins = "*")
public class DoanhThuController {
    private final DoanhThuService doanhThuService;

    public DoanhThuController(DoanhThuService doanhThuService) {
        this.doanhThuService = doanhThuService;
    }
}
