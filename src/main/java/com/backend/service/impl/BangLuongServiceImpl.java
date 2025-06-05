package com.backend.service.impl;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.dto.BangLuongDTO;
import com.backend.model.BangLuong;
import com.backend.repository.BangLuongRepository;
import com.backend.service.BangLuongService;

@Service
public class BangLuongServiceImpl implements BangLuongService {

    private final BangLuongRepository bangLuongRepository;

    public BangLuongServiceImpl(BangLuongRepository bangLuongRepository) {
        this.bangLuongRepository = bangLuongRepository;
    }

    @Override
    public BangLuongDTO themBangLuong(BangLuongDTO dto) {
        BangLuong entity = convertToEntity(dto);
        BangLuong saved = bangLuongRepository.save(entity);
        return convertToDTO(saved);
    }

    @Override
    public List<BangLuongDTO> layTatCaBangLuong() {
        return bangLuongRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BangLuongDTO timBangLuongTheoMa(String ma) {
        Optional<BangLuong> optional = bangLuongRepository.findById(ma);
        return optional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public BangLuongDTO suaBangLuong(String ma, BangLuongDTO dto) {
        Optional<BangLuong> optional = bangLuongRepository.findById(ma);
        if (optional.isPresent()) {
            BangLuong existing = optional.get();
            existing.setThang(dto.getThang());
            existing.setGhiChu(dto.getGhiChu());
            BangLuong updated = bangLuongRepository.save(existing);
            return convertToDTO(updated);
        }
        return null;
    }

    @Override
    public void xoaBangLuong(String ma) {
        bangLuongRepository.deleteById(ma);
    }

    @Override
    public List<BangLuongDTO> layBangLuongTheoThang(YearMonth thang) {
        // Dòng này bây giờ sẽ hoạt động vì phương thức đã được khai báo trong repository
        return bangLuongRepository.getBangLuongByThang(thang)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ------------------ HÀM CHUYỂN ĐỔI ------------------

    private BangLuongDTO convertToDTO(BangLuong entity) {
        BangLuongDTO dto = new BangLuongDTO();
        dto.setMaBangLuong(entity.getMaBangLuong());
        dto.setThang(entity.getThang());
        dto.setGhiChu(entity.getGhiChu());
        return dto;
    }

    private BangLuong convertToEntity(BangLuongDTO dto) {
        BangLuong entity = new BangLuong();
        entity.setMaBangLuong(dto.getMaBangLuong());
        entity.setThang(dto.getThang());
        entity.setGhiChu(dto.getGhiChu());
        return entity;
    }
}