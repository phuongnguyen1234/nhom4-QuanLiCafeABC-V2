package com.backend.service.impl;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

=======
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.backend.dto.ChiTietDonHangDTO;
>>>>>>> origin/manh-hoadon
import com.backend.repository.ChiTietDonHangRepository;
import com.backend.service.ChiTietDonHangService;

@Service
public class ChiTietDonHangServiceImpl implements ChiTietDonHangService {
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    public ChiTietDonHangServiceImpl(ChiTietDonHangRepository chiTietDonHangRepository) {
        this.chiTietDonHangRepository = chiTietDonHangRepository;
    }
<<<<<<< HEAD
=======
    private final List<ChiTietDonHangDTO> chiTietDonHangList = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<ChiTietDonHangDTO> findByMaDonHang(String maDonHang) {
        List<ChiTietDonHangDTO> result = new ArrayList<>();
        for (ChiTietDonHangDTO item : chiTietDonHangList) {
            if (item.getMaDonHang().equals(maDonHang)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public int calculateTongTien(String maDonHang) {
        return findByMaDonHang(maDonHang).stream()
                .mapToInt(ChiTietDonHangDTO::getTamTinh)
                .sum();
    }

    @Override
    public ChiTietDonHangDTO save(ChiTietDonHangDTO chiTietDonHangDTO) {
        //lưu vào database qua repository
        chiTietDonHangList.add(chiTietDonHangDTO);
        return chiTietDonHangDTO;
    }

    @Override
    public ChiTietDonHangDTO update(Long id, ChiTietDonHangDTO chiTietDonHangDTO) {
        //cập nhật vào database qua repository
        for (int i = 0; i < chiTietDonHangList.size(); i++) {
            if (chiTietDonHangList.get(i).getMaMon().equals(chiTietDonHangDTO.getMaMon()) 
                && chiTietDonHangList.get(i).getMaDonHang().equals(chiTietDonHangDTO.getMaDonHang())) {
                chiTietDonHangList.set(i, chiTietDonHangDTO);
                return chiTietDonHangDTO;
            }
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        //xóa từ database qua repository
        chiTietDonHangList.removeIf(item -> item.getMaMon().equals(id.toString()));
    }
>>>>>>> origin/manh-hoadon
}
