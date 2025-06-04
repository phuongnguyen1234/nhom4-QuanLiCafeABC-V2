package com.backend.control;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.ChiTietDonHangService;

@RestController
@RequestMapping("/chi-tiet-don-hang")
@CrossOrigin(origins = "*")
public class ChiTietDonHangController {
    private final ChiTietDonHangService chiTietDonHangService;

    public ChiTietDonHangController(ChiTietDonHangService chiTietDonHangService) {
        this.chiTietDonHangService = chiTietDonHangService;
    }
}
