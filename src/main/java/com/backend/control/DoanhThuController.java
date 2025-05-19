package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.DoanhThuService;

@RestController
@RequestMapping("/doanh-thu")
@CrossOrigin(origins = "*")
public class DoanhThuController {
    @Autowired
    private DoanhThuService doanhThuService;
}
