package com.backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.DanhMucMonKhongAnhDTO;
import com.backend.dto.MonKhongAnhDTO;
import com.backend.model.DanhMuc;
import com.backend.repository.DanhMucRepository;
import com.backend.service.DanhMucService;

@Service
public class DanhMucServiceImpl implements DanhMucService {
    @Autowired
    private DanhMucRepository danhMucRepository;

    @Override
    public List<DanhMucMonKhongAnhDTO> getAllDanhMucKhongAnh() {
        List<DanhMuc> danhMucs = danhMucRepository.findAllWithMon();

        return danhMucs.stream().map(d -> {
            DanhMucMonKhongAnhDTO dto = new DanhMucMonKhongAnhDTO();
            dto.setMaDanhMuc(d.getMaDanhMuc());
            dto.setTenDanhMuc(d.getTenDanhMuc());
            dto.setTrangThai(d.getTrangThai());

            List<MonKhongAnhDTO> monDTOs = d.getMonList().stream().map(mon -> {
                MonKhongAnhDTO m = new MonKhongAnhDTO();
                m.setMaMon(mon.getMaMon());
                m.setTenMon(mon.getTenMon());
                m.setDonGia(mon.getDonGia());
                m.setTrangThai(mon.getTrangThai());
                return m;
            }).collect(Collectors.toList());

            dto.setMonList(monDTOs);
            return dto;
        }).collect(Collectors.toList());
    }
}
