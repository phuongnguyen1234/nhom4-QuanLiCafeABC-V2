package com.backend.control;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.DonHangDTO;
import com.backend.service.DonHangService;

@RestController
@RequestMapping("/don-hang")
@CrossOrigin(origins = "*")
public class DonHangController {
    private final DonHangService donHangService;

    public DonHangController(DonHangService donHangService) {
        this.donHangService = donHangService;
    }

    @PostMapping("/create")
    public ResponseEntity<DonHangDTO> createDonHang(@RequestBody DonHangDTO donHangDTO) {
        DonHangDTO createdDonHang = donHangService.createDonHang(donHangDTO);
        return ResponseEntity.status(201).body(createdDonHang); // HTTP 201 Created
    }
      // 2. Lấy đơn hàng theo ID
    @GetMapping("/{maDonHang}")
    public ResponseEntity<DonHangDTO> getDonHangById(@PathVariable String maDonHang) {
        return donHangService.getDonHangById(maDonHang)
                .map(ResponseEntity::ok) // Nếu có giá trị -> trả về 200 OK
                .orElse(ResponseEntity.notFound().build()); // Nếu rỗng -> trả về 404
    }

    @GetMapping("/all")
    public ResponseEntity<List<DonHangDTO>> getAllDonHang() {
        List<DonHangDTO> donHangs = donHangService.getAllDonHang();
        return ResponseEntity.ok(donHangs); // Trả về 200 OK với danh sách đơn hàng
    }

    @GetMapping("/filter")
    public ResponseEntity<List<DonHangDTO>> filterDonHang(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {
        List<DonHangDTO> donHangs = donHangService.filterByNgay(ngay);
        if (donHangs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(donHangs);
    }
}
