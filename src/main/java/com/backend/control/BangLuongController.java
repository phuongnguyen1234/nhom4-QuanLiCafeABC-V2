package com.backend.control;

import java.time.YearMonth;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.BangLuongDTO;
import com.backend.service.BangLuongService;

@RestController
@RequestMapping("/bang-luong")
public class BangLuongController {

    private final BangLuongService bangLuongService;

    public BangLuongController(BangLuongService bangLuongService) {
        this.bangLuongService = bangLuongService;
    }

    // Thêm mới bảng lương
    @PostMapping("/create")
    public ResponseEntity<String> themBangLuong() {
        int soLuong = bangLuongService.taoBangLuongThangHienTai();
        if (soLuong == 0) {
            return ResponseEntity.ok("Đã tồn tại bảng lương cho tất cả nhân viên tháng này.");
        }
        return ResponseEntity.ok("Đã tạo " + soLuong + " bảng lương mới.");
    }

    // Lấy tất cả bảng lương
    @GetMapping("/thang-nay")
    public ResponseEntity<List<BangLuongDTO>> layTatCaBangLuongThangNay() {
        return ResponseEntity.ok(bangLuongService.layTatCaBangLuongThangNay());
    }

    // Tìm bảng lương theo mã
    @GetMapping("/{ma}")
    public ResponseEntity<BangLuongDTO> timBangLuongTheoMa(@PathVariable String ma) {
        BangLuongDTO dto = bangLuongService.timBangLuongTheoMa(ma);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Sửa bảng lương theo mã
    @PutMapping("/{ma}")
    public ResponseEntity<BangLuongDTO> suaBangLuong(@PathVariable String ma, @RequestBody BangLuongDTO dto) {
        BangLuongDTO updated = bangLuongService.suaBangLuong(ma, dto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Lấy bảng lương theo tháng
    @GetMapping("/thang")
    public ResponseEntity<List<BangLuongDTO>> layBangLuongTheoThang(
            @RequestParam("thang") @DateTimeFormat(pattern = "yyyyMM") YearMonth thang) {
        List<BangLuongDTO> list = bangLuongService.layBangLuongTheoThang(thang);
        return ResponseEntity.ok(list);
    }
}
