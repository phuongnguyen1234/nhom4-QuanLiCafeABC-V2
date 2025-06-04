package com.backend.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.model.DoanhThu;
import com.backend.repository.DoanhThuRepository;
import com.backend.service.DoanhThuService;

@Service
public class DoanhThuServiceImpl implements DoanhThuService {
    @Autowired
    private DoanhThuRepository doanhThuRepository;

    // Lấy tổng doanh thu theo tháng và năm
    @Override
    public int getTongDoanhThuByThangAndNam() {
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();
        List<DoanhThu> list = doanhThuRepository.findByThangAndNam(thang, nam);
        if (list.isEmpty()) return 0;
        return list.get(0).getTongDoanhThu(); 
    }

    // Lấy số đơn theo tháng và năm
    @Override
    public int getSoDonByThangAndNam() {
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();
        List<DoanhThu> list = doanhThuRepository.findByThangAndNam(thang, nam);
        if (list.isEmpty()) return 0;
        return list.get(0).getSoDon();
    }
    @Override
    public int getSoMonByThangAndNam() {
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();
        List<DoanhThu> list = doanhThuRepository.findByThangAndNam(thang, nam);
        if (list.isEmpty()) return 0;
        return list.get(0).getSoMon();
    }

    @Override
    public List<Integer> doanhThuTungThang() {
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();
        List<Integer> doanhThuList = new ArrayList<>();

        for (int i = 1; i <= thang; i++) {
            List<DoanhThu> list = doanhThuRepository.findByThangAndNam(i, nam);
            if (!list.isEmpty()) {
                doanhThuList.add(list.get(0).getTongDoanhThu());
            } else {
                doanhThuList.add(0); // nếu không có dữ liệu thì ghi nhận doanh thu = 0
            }
        }
        return doanhThuList;
    }
}
