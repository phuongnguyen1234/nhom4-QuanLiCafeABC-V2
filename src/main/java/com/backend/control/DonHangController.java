package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.DonHangService;

@RestController
@RequestMapping("/api/nhanvien")
@CrossOrigin(origins = "*")
public class DonHangController {
    @Autowired
    private DonHangService donHangService;
    
}
