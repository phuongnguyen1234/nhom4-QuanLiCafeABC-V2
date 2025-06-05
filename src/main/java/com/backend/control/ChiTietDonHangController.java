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
/* 
    // Lấy tất cả chi tiết đơn hàng theo mã đơn hàng
    @GetMapping("/don-hang/{maDonHang}")
    public ResponseEntity<List<ChiTietDonHangDTO>> getByMaDonHang(@PathVariable String maDonHang) {
        List<ChiTietDonHangDTO> chiTietDonHangList = chiTietDonHangService.findByMaDonHang(maDonHang);
        return ResponseEntity.ok(chiTietDonHangList);
    }

    // Lấy tổng tiền của đơn hàng
    @GetMapping("/tong-tien/{maDonHang}")
    public ResponseEntity<Integer> getTongTienByMaDonHang(@PathVariable String maDonHang) {
        int tongTien = chiTietDonHangService.calculateTongTien(maDonHang);
        return ResponseEntity.ok(tongTien);
    }

    // Thêm mới chi tiết đơn hàng
    @PostMapping
    public ResponseEntity<ChiTietDonHangDTO> createChiTietDonHang(@RequestBody ChiTietDonHangDTO chiTietDonHangDTO) {
        ChiTietDonHangDTO savedItem = chiTietDonHangService.save(chiTietDonHangDTO);
        return ResponseEntity.ok(savedItem);
    }

    // Cập nhật chi tiết đơn hàng
    @PutMapping("/{id}")
    public ResponseEntity<ChiTietDonHangDTO> updateChiTietDonHang(
            @PathVariable Long id,
            @RequestBody ChiTietDonHangDTO chiTietDonHangDTO) {
        ChiTietDonHangDTO updatedItem = chiTietDonHangService.update(id, chiTietDonHangDTO);
        return ResponseEntity.ok(updatedItem);
    }

    // Xóa chi tiết đơn hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChiTietDonHang(@PathVariable Long id) {
        chiTietDonHangService.delete(id);
        return ResponseEntity.noContent().build();
    } */
}
