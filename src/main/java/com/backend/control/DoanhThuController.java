package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.DoanhThuDTO;
import com.backend.service.DoanhThuService;

@RestController
@RequestMapping("/doanh-thu")
@CrossOrigin(origins = "*")
public class DoanhThuController {
    @Autowired
    private DoanhThuService doanhThuService;
    @GetMapping("/tong-quan")
    public ResponseEntity<DoanhThuDTO> getThongKeTongQuan() {
        DoanhThuDTO dto = new DoanhThuDTO();
        dto.setThang(java.time.LocalDate.now().getMonthValue());
        dto.setNam(java.time.LocalDate.now().getYear());
        dto.setTongDoanhThu(doanhThuService.getTongDoanhThuByThangAndNam());
        dto.setSoDon(doanhThuService.getSoDonByThangAndNam());
        dto.setSoMon(doanhThuService.getSoMonByThangAndNam());
        return ResponseEntity.ok(dto);
    }
}
