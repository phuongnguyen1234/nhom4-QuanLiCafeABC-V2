package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.BangLuongService;

@RestController
@RequestMapping("/api/nhanvien")
@CrossOrigin(origins = "*")
public class BangLuongController {
    @Autowired
    private BangLuongService bangLuongService;
}
