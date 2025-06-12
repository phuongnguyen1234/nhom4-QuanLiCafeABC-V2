package com.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.model.ChiTietDonHang;
import com.backend.dto.ThongKeDTO;
import com.backend.model.DoanhThu;
import com.backend.model.DonHang;
import com.backend.repository.DoanhThuRepository;
import com.backend.repository.DonHangRepository;
import com.backend.service.DoanhThuService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoanhThuServiceImpl implements DoanhThuService {
    private final DonHangRepository donHangRepository;

    private final DoanhThuRepository doanhThuRepository;

    public DoanhThuServiceImpl(DonHangRepository donHangRepository, DoanhThuRepository doanhThuRepository) {
        this.donHangRepository = donHangRepository;
        this.doanhThuRepository = doanhThuRepository;
    }

    @Override
    public int getDoanhThuHomNay(){
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDateTime.now();
        List<DonHang> list = donHangRepository.findByThoiGianDatHangBetween(start, end);
        int doanhThu = 0;
        if (list.isEmpty()) return 0;
        else{
            for (DonHang dh : list) {
                doanhThu += dh.getTongTien();
            }
        }
        return doanhThu;
    }

    // Lấy tổng doanh thu theo tháng và năm
    @Override
    public int getTongDoanhThuByThangAndNam(int thang, int nam) {
        List<DoanhThu> list = doanhThuRepository.findByThangAndNam(thang, nam);
        if (list.isEmpty()) return 0;
        return list.get(0).getTongDoanhThu(); 
    }

    // Lấy số đơn theo tháng và năm
    @Override
    public int getSoDonByThangAndNam(int thang, int nam) {
        List<DoanhThu> list = doanhThuRepository.findByThangAndNam(thang, nam);
        if (list.isEmpty()) return 0;
        return list.get(0).getSoDon();
    }

    @Override
    public int getSoMonByThangAndNam(int thang, int nam) {
        List<DoanhThu> list = doanhThuRepository.findByThangAndNam(thang, nam);
        if (list.isEmpty()) return 0;
        return list.get(0).getSoMon();
    }

    @Override
    public Map<String, Integer> getDoanhThuTrongThoiGian(int thangStart, int namStart, int thangEnd, int namEnd) {
        List<DoanhThu> doanhThuList = doanhThuRepository.findByThoiGianRange(thangStart, namStart, thangEnd, namEnd);
        Map<String, Integer> result = new HashMap<>();
        if (doanhThuList.isEmpty()) {
            return result;
        }
        for (DoanhThu dt : doanhThuList) {
            result.put(String.format("%02d-%d", dt.getThang(), dt.getNam()), dt.getTongDoanhThu());
        }
        return result;
    }

    @Override
    public Map<String, Integer> getTop5MonBanChayByThangAndNam(int thang, int nam) {
        LocalDate firstDayOfMonth = LocalDate.of(nam, thang, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        LocalDateTime startDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime endDateTime = lastDayOfMonth.atTime(23, 59, 59, 999999999);

        List<DonHang> donHangsTrongThang = donHangRepository.findByThoiGianDatHangBetween(startDateTime, endDateTime);

        Map<String, Integer> monCounts = new HashMap<>();
        for (DonHang dh : donHangsTrongThang) {
            List<ChiTietDonHang> chiTietList = dh.getChiTietDonHang(); // chiTietDonHangs sẽ được lazy-loaded nếu cần do @Transactional
            if (chiTietList != null) {
                for (ChiTietDonHang ctdh : chiTietList) {
                    monCounts.put(ctdh.getTenMon(), monCounts.getOrDefault(ctdh.getTenMon(), 0) + ctdh.getSoLuong());
                }
            }
        }

        return monCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public Map<String, Integer> getTop3NhanVienTaoDonByThangAndNam(int thang, int nam) {
        LocalDate firstDayOfMonth = LocalDate.of(nam, thang, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        LocalDateTime startDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime endDateTime = lastDayOfMonth.atTime(23, 59, 59, 999999999);

        List<DonHang> donHangsTrongThang = donHangRepository.findByThoiGianDatHangBetween(startDateTime, endDateTime);

        Map<String, Integer> nhanVienDonCounts = new HashMap<>();
        for (DonHang dh : donHangsTrongThang) {
            // Kiểm tra xem nhân viên có phải là admin không
            if (dh.getNhanVien() != null && !"NV000".equals(dh.getNhanVien().getMaNhanVien())) {
                String tenNhanVien = dh.getHoTen(); 
                if (tenNhanVien != null && !tenNhanVien.isEmpty()) {
                    nhanVienDonCounts.put(tenNhanVien, nhanVienDonCounts.getOrDefault(tenNhanVien, 0) + 1);
                }
            }
        }

        return nhanVienDonCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public Map<String, Integer> getKhoangThoiGianDatDonNhieuNhatByThangAndNam(int thang, int nam) {
        LocalDate firstDayOfMonth = LocalDate.of(nam, thang, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        LocalDateTime startDateTime = firstDayOfMonth.atStartOfDay();
        LocalDateTime endDateTime = lastDayOfMonth.atTime(23, 59, 59, 999999999);

        List<DonHang> donHangsTrongThang = donHangRepository.findByThoiGianDatHangBetween(startDateTime, endDateTime);

        if (donHangsTrongThang.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> thoiGianCounts = new LinkedHashMap<>();
        String[] slots = {
            "00-02", "02-04", "04-06", "06-08", "08-10", "10-12",
            "12-14", "14-16", "16-18", "18-20", "20-22", "22-00"
        };
        for (String slot : slots) {
            thoiGianCounts.put(slot, 0);
        }

        for (DonHang dh : donHangsTrongThang) {
            LocalDateTime thoiGianDat = dh.getThoiGianDatHang();
            if (thoiGianDat != null) {
                int hour = thoiGianDat.getHour();
                // slotIndex: 0 for hours 0-1, 1 for 2-3, ..., 11 for 22-23
                int slotIndex = hour / 2;
                if (slotIndex >= 0 && slotIndex < slots.length) {
                    String slotKey = slots[slotIndex];
                    thoiGianCounts.put(slotKey, thoiGianCounts.get(slotKey) + 1);
                }
            }
        }

        if (thoiGianCounts.isEmpty()) { // Should not happen if slots are pre-populated
            return Collections.emptyMap();
        }

        int maxCount = Collections.max(thoiGianCounts.values());

        if (maxCount == 0) { // No orders were counted, or all slots have 0.
            return Collections.emptyMap();
        }

        return thoiGianCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public List<DoanhThu> getAllDoanhThu() {
        return doanhThuRepository.findAll();
    }

    // Chạy vào 00:01 ngày đầu tiên của mỗi tháng
    @Scheduled(cron = "0 1 0 1 * ?")
    @Transactional
    public void scheduledTongHopDoanhThuThangTruoc() {
        YearMonth thangCanTongHop = YearMonth.now().minusMonths(1);
        // Kiểm tra xem dữ liệu cho tháng này đã tồn tại chưa
        if (doanhThuRepository.findByThangAndNam(thangCanTongHop.getMonthValue(), thangCanTongHop.getYear()).isEmpty()) {
            System.out.println("Scheduled task: Bắt đầu tổng hợp doanh thu cho tháng: " + thangCanTongHop);
            String result = thucHienTongHopChoThang(thangCanTongHop);
            System.out.println("Scheduled task result: " + result);
        } else {
            System.out.println("Scheduled task: Dữ liệu cho tháng " + thangCanTongHop + " đã tồn tại. Bỏ qua tổng hợp.");
        }
    }

    @Override
    @Transactional
    public String kiemTraVaTongHopDoanhThuThangTruoc() {
        YearMonth thangCanTongHop = YearMonth.now().minusMonths(1);
        if (doanhThuRepository.findByThangAndNam(thangCanTongHop.getMonthValue(), thangCanTongHop.getYear()).isEmpty()) {
            System.out.println("On-demand: Bắt đầu tổng hợp doanh thu cho tháng: " + thangCanTongHop);
            return thucHienTongHopChoThang(thangCanTongHop);
        } else {
            String message = "Dữ liệu doanh thu cho tháng " + thangCanTongHop.format(DateTimeFormatter.ofPattern("MM/yyyy")) + " đã được tổng hợp trước đó.";
            System.out.println("On-demand: " + message);
            return message;
        }
    }

    private String thucHienTongHopChoThang(YearMonth thangCanTongHop) {
        LocalDate ngayBatDau = thangCanTongHop.atDay(1);
        LocalDate ngayKetThuc = thangCanTongHop.atEndOfMonth();

        List<DonHang> donHangTrongThang = donHangRepository.findByThoiGianDatHangBetween(
            ngayBatDau.atStartOfDay(),
            ngayKetThuc.atTime(23, 59, 59, 999999999)
        );

        int tongDoanhThuThang = 0;
        int tongSoDonThang = 0;
        int tongSoMonThang = 0;

        if (!donHangTrongThang.isEmpty()) {
            tongSoDonThang = donHangTrongThang.size();
            for (DonHang dh : donHangTrongThang) {
                tongDoanhThuThang += dh.getTongTien();
                if (dh.getChiTietDonHang() != null) {
                    for (ChiTietDonHang ctdh : dh.getChiTietDonHang()) {
                        tongSoMonThang += ctdh.getSoLuong();
                    }
                }
            }
        }

        double trungBinhMoiDonThang = (tongSoDonThang > 0) ? (double) tongDoanhThuThang / tongSoDonThang : 0;
        double tangTruongDoanhThu = 0.0;

        YearMonth thangTruocDoNua = thangCanTongHop.minusMonths(1);
        Optional<DoanhThu> doanhThuThangTruocDoNuaOpt = doanhThuRepository.findByThangAndNam(thangTruocDoNua.getMonthValue(), thangTruocDoNua.getYear())
                                                                    .stream().findFirst();

        if (doanhThuThangTruocDoNuaOpt.isPresent()) {
            DoanhThu doanhThuThangTruocDoNua = doanhThuThangTruocDoNuaOpt.get();
            int prevMonthRevenue = doanhThuThangTruocDoNua.getTongDoanhThu();
            if (prevMonthRevenue > 0) {
                tangTruongDoanhThu = ((double) (tongDoanhThuThang - prevMonthRevenue) / prevMonthRevenue) * 100.0;
            } else { // prevMonthRevenue is 0
                if (tongDoanhThuThang > 0) {
                    tangTruongDoanhThu = 100.0; // Grew from 0 to something positive
                } else {
                    tangTruongDoanhThu = 0.0; // Grew from 0 to 0
                }
            }
        } else {
            // No data for the month before that.
            if (tongDoanhThuThang > 0) {
                // Consider as 100% growth if it's the first month with data or current > 0 and no prior comparison
                tangTruongDoanhThu = 100.0;
            } else {
                tangTruongDoanhThu = 0.0; // First month, and it's 0
            }
        }

        DoanhThu doanhThuMoi = new DoanhThu();
        // MaDoanhThu is auto-generated by the database
        doanhThuMoi.setThang(thangCanTongHop.getMonthValue());
        doanhThuMoi.setNam(thangCanTongHop.getYear());
        doanhThuMoi.setTongDoanhThu(tongDoanhThuThang);
        doanhThuMoi.setTangTruongDoanhThu(tangTruongDoanhThu);
        doanhThuMoi.setSoDon(tongSoDonThang);
        doanhThuMoi.setSoMon(tongSoMonThang);
        doanhThuMoi.setTrungBinhMoiDon(trungBinhMoiDonThang);
        doanhThuMoi.setThoiGianTongHopDoanhThu(LocalDateTime.now());

        doanhThuRepository.save(doanhThuMoi);
        
        String message;
        if (donHangTrongThang.isEmpty()) {
            message = "Không có đơn hàng nào trong tháng " + thangCanTongHop.format(DateTimeFormatter.ofPattern("MM/yyyy")) + ". Đã lưu bản ghi doanh thu với giá trị 0.";
        } else {
            message = "Đã tổng hợp và lưu doanh thu cho tháng: " + thangCanTongHop.format(DateTimeFormatter.ofPattern("MM/yyyy"));
        }
        System.out.println(message);
        return message;
    }

    @Override
    public ThongKeDTO getThongKeTongQuanData() {
        ThongKeDTO dto = new ThongKeDTO();
        LocalDate today = LocalDate.now();

        // Tính toán tháng và năm của tháng trước
        LocalDate lastMonthDate = today.minusMonths(1);
        int lastMonthValue = lastMonthDate.getMonthValue();
        int yearOfLastMonth = lastMonthDate.getYear();

        // Tính toán khoảng thời gian cho 6 tháng trước (không bao gồm tháng hiện tại)
        LocalDate endDate6Months = lastMonthDate; // Kết thúc vào tháng trước
        LocalDate startDate6Months = lastMonthDate.minusMonths(5); // Bắt đầu từ 5 tháng trước của tháng trước (tổng cộng 6 tháng)
        int thangStart6Months = startDate6Months.getMonthValue();
        int namStart6Months = startDate6Months.getYear();
        int thangEnd6Months = endDate6Months.getMonthValue();
        int namEnd6Months = endDate6Months.getYear();

        dto.setDoanhThuHomNay(this.getDoanhThuHomNay());
        // Sử dụng lastMonthValue và yearOfLastMonth cho các thống kê của tháng trước
        dto.setTongDoanhThuThangTruoc(this.getTongDoanhThuByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setSoDon(this.getSoDonByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setSoMon(this.getSoMonByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setTop5MonBanChay(this.getTop5MonBanChayByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setTop3NhanVien(this.getTop3NhanVienTaoDonByThangAndNam(lastMonthValue, yearOfLastMonth));
        dto.setDoanhThuTrongThoiGian(this.getDoanhThuTrongThoiGian(thangStart6Months, namStart6Months, thangEnd6Months, namEnd6Months));
        dto.setKhoangThoiGianDatDonNhieuNhat(this.getKhoangThoiGianDatDonNhieuNhatByThangAndNam(lastMonthValue, yearOfLastMonth));
        return dto;
    }
}
