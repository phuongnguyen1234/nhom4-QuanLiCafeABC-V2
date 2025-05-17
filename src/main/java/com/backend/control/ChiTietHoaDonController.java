package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.ChiTietHoaDonService;

@RestController
@RequestMapping("/api/cthd")
@CrossOrigin(origins = "*")
public class ChiTietHoaDonController {
    @Autowired
    private ChiTietHoaDonService chiTietHoaDonService;

}
