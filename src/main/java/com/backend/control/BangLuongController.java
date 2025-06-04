package com.backend.control;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.BangLuongService;

@RestController
@RequestMapping("/bang-luong")
@CrossOrigin(origins = "*")
public class BangLuongController {
    private final BangLuongService bangLuongService;

    public BangLuongController(BangLuongService bangLuongService) {
        this.bangLuongService = bangLuongService;
    }
}
