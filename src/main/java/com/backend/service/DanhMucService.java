package com.backend.service;

import java.util.List;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.model.DanhMuc;

public interface DanhMucService {
    List<DanhMuc> getAllDanhMuc();

    DanhMuc createDanhMuc(DanhMucKhongMonDTO danhMuc);

    DanhMuc partialUpdate(int maDanhMuc, DanhMucKhongMonDTO danhMucUpdate);

    List<DanhMucKhongMonDTO> getAllDanhMucKhongMon();
}
