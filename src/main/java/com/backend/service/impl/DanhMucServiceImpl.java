package com.backend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.dto.DanhMucKhongMonDTO;
<<<<<<< HEAD
=======
import com.backend.dto.DanhMucMonKhongAnhDTO;
import com.backend.dto.MonKhongAnhDTO;
>>>>>>> origin/manh-hoadon
import com.backend.model.DanhMuc;
import com.backend.repository.DanhMucRepository;
import com.backend.service.DanhMucService;

@Service
public class DanhMucServiceImpl implements DanhMucService {
    private final DanhMucRepository danhMucRepository;

    public DanhMucServiceImpl(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    @Override
<<<<<<< HEAD
    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepository.findAll();
=======
    public List<DanhMucMonKhongAnhDTO> getAllDanhMucKhongAnh() {
        List<DanhMuc> danhMucs = danhMucRepository.findAllWithMon();

        return danhMucs.stream().map(d -> {
            DanhMucMonKhongAnhDTO dto = new DanhMucMonKhongAnhDTO();
            dto.setMaDanhMuc(d.getMaDanhMuc());
            dto.setTenDanhMuc(d.getTenDanhMuc());
            dto.setLoai(d.getLoai());
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
>>>>>>> origin/manh-hoadon
    }

    @Override
    public DanhMuc createDanhMuc(DanhMucKhongMonDTO danhMuc) {
        DanhMuc newDanhMuc = new DanhMuc();
        newDanhMuc.setTenDanhMuc(danhMuc.getTenDanhMuc());
        newDanhMuc.setTrangThai(danhMuc.getTrangThai());
        newDanhMuc.setLoai(danhMuc.getLoai());
        return danhMucRepository.save(newDanhMuc);
    }

    @Override
    public DanhMuc partialUpdate(int maDanhMuc, DanhMucKhongMonDTO danhMucUpdate) {
        Optional<DanhMuc> optionalDanhMuc = danhMucRepository.findById(maDanhMuc);
        if (!optionalDanhMuc.isPresent()) {
            throw new RuntimeException("Không tìm thấy danh mục với mã: " + maDanhMuc);
        }

        DanhMuc danhMuc = optionalDanhMuc.get();

        // Cập nhật các trường không null / hợp lệ
        if (danhMucUpdate.getTenDanhMuc() != null) danhMuc.setTenDanhMuc(danhMucUpdate.getTenDanhMuc());
        if (danhMucUpdate.getTrangThai() != null) danhMuc.setTrangThai(danhMucUpdate.getTrangThai());
        if (danhMucUpdate.getLoai() != null) danhMuc.setLoai(danhMucUpdate.getLoai());

        return danhMucRepository.save(danhMuc);
    }

    @Override
    public List<DanhMucKhongMonDTO> getAllDanhMucKhongMon() {
        List<DanhMuc> danhMucs = danhMucRepository.findAll();
        return danhMucs.stream().map(d -> {
            DanhMucKhongMonDTO dto = new DanhMucKhongMonDTO();
            dto.setMaDanhMuc(d.getMaDanhMuc());
            dto.setTenDanhMuc(d.getTenDanhMuc());
            dto.setTrangThai(d.getTrangThai());
            dto.setLoai(d.getLoai());
            return dto;
        }).collect(Collectors.toList());
    }
}
