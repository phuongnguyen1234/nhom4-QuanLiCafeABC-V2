package com.backend.control;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.DonHangDTO;
import com.backend.model.DonHang;
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
    public ResponseEntity<DonHang> createDonHang(@RequestBody DonHangDTO donHangDTO) {
        DonHang createdDonHang = donHangService.createDonHang(donHangDTO);
        return ResponseEntity.status(201).body(createdDonHang); // HTTP 201 Created
    }
      // 2. Lấy đơn hàng theo ID
    @GetMapping("/{maDonHang}")
public ResponseEntity<DonHang> getDonHangById(@PathVariable String maDonHang) {
    return donHangService.getDonHangById(maDonHang)
            .map(ResponseEntity::ok) // Nếu có giá trị -> trả về 200 OK
            .orElse(ResponseEntity.notFound().build()); // Nếu rỗng -> trả về 404
}

    // 3. Lấy tất cả đơn hàng (phân trang)
    @GetMapping
    public ResponseEntity<Page<DonHang>> getAllDonHang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(donHangService.getAllDonHangPaginated(Pageable.ofSize(size).withPage(page)));
    }

    // 4. Xóa đơn hàng
    @DeleteMapping("/{maDonHang}")
    public ResponseEntity<Void> deleteDonHang(@PathVariable String maDonHang) {
        donHangService.deleteDonHang(maDonHang);
        return ResponseEntity.noContent().build();
    }
}
