package com.backend.control;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.ThongKeDTO;
import com.backend.model.DoanhThu;
import com.backend.service.DoanhThuService;

@RestController
@RequestMapping("/doanh-thu")
@CrossOrigin(origins = "*")
public class DoanhThuController {
    private final DoanhThuService doanhThuService;

    public DoanhThuController(DoanhThuService doanhThuService) {
        this.doanhThuService = doanhThuService;
    }

    @GetMapping("/tong-quan")
    public ResponseEntity<ThongKeDTO> getThongKeTongQuan() {
        ThongKeDTO dto = doanhThuService.getThongKeTongQuanData();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DoanhThu>> getAllDoanhThu() {
        return ResponseEntity.ok(doanhThuService.getAllDoanhThu());
    }

    @PostMapping("/kiem-tra-tong-hop-thang-truoc")
    public ResponseEntity<String> kiemTraVaTongHopThangTruoc() {
        try {
            String resultMessage = doanhThuService.kiemTraVaTongHopDoanhThuThangTruoc();
            return ResponseEntity.ok(resultMessage);
        } catch (Exception e) {
            e.printStackTrace(); // Log lỗi ở server
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi trong quá trình kiểm tra và tổng hợp doanh thu: " + e.getMessage());
        }
    }

}
