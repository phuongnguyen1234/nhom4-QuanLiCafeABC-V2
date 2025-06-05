package com.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.model.ChiTietDonHang;
import com.backend.model.DoanhThu;
import com.backend.model.DonHang;
import com.backend.repository.DoanhThuRepository;
import com.backend.repository.DonHangRepository;
import com.backend.service.DoanhThuService;

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
            String tenNhanVien = dh.getHoTen(); // Giả sử DonHang.hoTen là tên nhân viên tạo đơn
            if (tenNhanVien != null && !tenNhanVien.isEmpty()) {
                nhanVienDonCounts.put(tenNhanVien, nhanVienDonCounts.getOrDefault(tenNhanVien, 0) + 1);
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
}
