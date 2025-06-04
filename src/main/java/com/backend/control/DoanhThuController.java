package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.ThongKeDTO;
import com.backend.service.DoanhThuService;

@RestController
@RequestMapping("/doanh-thu")
@CrossOrigin(origins = "*")
public class DoanhThuController {
    @Autowired
    private DoanhThuService doanhThuService;
    // @Autowired
    // private DonHangService donHangService;
    @GetMapping("/tong-quan")
    public ResponseEntity<ThongKeDTO> getThongKeTongQuan() {
        ThongKeDTO dto = new ThongKeDTO();
        //dto.setDoanhThuHomNay(donHangService.getTongDoanhThuHomNay());
        // dto.setTongDoanhThu(doanhThuService.getTongDoanhThuByThangAndNam());
        // dto.setSoDon(doanhThuService.getSoDonByThangAndNam());
        // dto.setSoMon(doanhThuService.getSoMonByThangAndNam());
        // dto.setTop5MonBanChay(donHangService.getTop5MonBanChayTheoThang());
        // dto.setTop3NhanVien(donHangService.getTop3NhanVienTheoThang());
        return ResponseEntity.ok(dto);
    }
}
