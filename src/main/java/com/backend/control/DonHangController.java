package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.DonHangDTO;
import com.backend.model.DonHang;
import com.backend.service.DonHangService;

@RestController
@RequestMapping("/don-hang")
@CrossOrigin(origins = "*")
public class DonHangController {
    @Autowired
    private DonHangService donHangService;
    
    @PostMapping("/create")
    public ResponseEntity<DonHang> createDonHang(@RequestBody DonHangDTO donHangDTO) {
        DonHang createdDonHang = donHangService.createDonHang(donHangDTO);
        return ResponseEntity.status(201).body(createdDonHang); // HTTP 201 Created
    }
}
