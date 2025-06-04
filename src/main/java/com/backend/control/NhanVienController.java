package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.NhanVienService;

@RestController
@RequestMapping("/api/nhanvien")
@CrossOrigin(origins = "*") // Cho phép frontend truy cập từ domain khác (nếu cần)
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;
}
