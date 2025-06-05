package com.backend.control;

import java.time.LocalDate;

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
    private final DoanhThuService doanhThuService;

    public DoanhThuController(DoanhThuService doanhThuService) {
        this.doanhThuService = doanhThuService;
    }

    @GetMapping("/tong-quan")
    public ResponseEntity<ThongKeDTO> getThongKeTongQuan() {
        ThongKeDTO dto = new ThongKeDTO();
        LocalDate today = LocalDate.now();

        // Tính toán tháng và năm của tháng trước
        LocalDate lastMonthDate = today.minusMonths(1);
        int lastMonthValue = lastMonthDate.getMonthValue();
        int yearOfLastMonth = lastMonthDate.getYear();

        // Tính toán khoảng thời gian cho 6 tháng gần nhất (bao gồm tháng hiện tại)
        LocalDate endDate6Months = today;
        LocalDate startDate6Months = today.minusMonths(5); // 5 tháng trước + tháng hiện tại = 6 tháng
        int thangStart6Months = startDate6Months.getMonthValue();
        int namStart6Months = startDate6Months.getYear();
        int thangEnd6Months = endDate6Months.getMonthValue();
        int namEnd6Months = endDate6Months.getYear();

        dto.setDoanhThuHomNay(doanhThuService.getDoanhThuHomNay());
        // Sử dụng lastMonthValue và yearOfLastMonth cho các thống kê của tháng trước
        dto.setTongDoanhThuThangTruoc(doanhThuService.getTongDoanhThuByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setSoDon(doanhThuService.getSoDonByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setSoMon(doanhThuService.getSoMonByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setTop5MonBanChay(doanhThuService.getTop5MonBanChayByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setTop3NhanVien(doanhThuService.getTop3NhanVienTaoDonByThangAndNam(lastMonthValue, yearOfLastMonth));
        // Lấy doanh thu 6 tháng gần nhất
        dto.setDoanhThuTrongThoiGian(doanhThuService.getDoanhThuTrongThoiGian(thangStart6Months, namStart6Months, thangEnd6Months, namEnd6Months));
        // Lấy khoảng thời gian đặt đơn nhiều nhất của tháng trước
        dto.setKhoangThoiGianDatDonNhieuNhat(doanhThuService.getKhoangThoiGianDatDonNhieuNhatByThangAndNam(lastMonthValue, yearOfLastMonth));
        return ResponseEntity.ok(dto);
    }
}
