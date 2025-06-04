package com.backend.control;

import java.util.List;

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
import com.backend.model.DanhMuc;
import com.backend.service.DanhMucService;

@RestController
@RequestMapping("/danh-muc")
@CrossOrigin(origins = "*")
public class DanhMucController {
    private final DanhMucService danhMucService;

    public DanhMucController(DanhMucService danhMucService) {
        this.danhMucService = danhMucService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DanhMuc>> getAllDanhMuc() {
        List<DanhMuc> danhMucList = danhMucService.getAllDanhMuc();
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

    @GetMapping("/all/no-dish")
    public ResponseEntity<List<DanhMucKhongMonDTO>> getAllDanhMucKhongMon() {
        List<DanhMucKhongMonDTO> danhMucList = danhMucService.getAllDanhMucKhongMon();
        return ResponseEntity.ok(danhMucList);
    }
}
