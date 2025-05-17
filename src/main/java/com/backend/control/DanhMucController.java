package com.backend.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.DanhMucMonKhongAnhDTO;
import com.backend.service.DanhMucService;

@RestController
@RequestMapping("/danh-muc")
@CrossOrigin(origins = "*")
public class DanhMucController {
    @Autowired
    private DanhMucService danhMucService;

    @GetMapping("/all")
    public ResponseEntity<List<DanhMucMonKhongAnhDTO>> getAllDanhMuc() {
        List<DanhMucMonKhongAnhDTO> danhMucList = danhMucService.getAllDanhMucKhongAnh();
        return ResponseEntity.ok(danhMucList);
    }
}
