package com.backend.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.DanhMucMonKhongAnhDTO;
import com.backend.model.DanhMuc;
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

    @PostMapping("/create")
    public ResponseEntity<DanhMuc> createDanhMuc(@RequestBody DanhMucKhongMonDTO danhMuc) {
        DanhMuc newDanhMuc = danhMucService.createDanhMuc(danhMuc);
        return ResponseEntity.ok(newDanhMuc);
    }

    @PatchMapping("/{maDanhMuc}")
    public ResponseEntity<DanhMuc> updateDanhMuc(@PathVariable int maDanhMuc, @RequestBody DanhMucKhongMonDTO danhMuc) {
        DanhMuc updatedDanhMuc = danhMucService.partialUpdate(maDanhMuc, danhMuc);
        return ResponseEntity.ok(updatedDanhMuc);
    }
}
