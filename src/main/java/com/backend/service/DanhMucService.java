package com.backend.service;

import java.util.List;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.DanhMucMonKhongAnhDTO;
import com.backend.model.DanhMuc;

public interface DanhMucService {
    List<DanhMucMonKhongAnhDTO> getAllDanhMucKhongAnh();

    DanhMuc createDanhMuc(DanhMucKhongMonDTO danhMuc);

    DanhMuc partialUpdate(int maDanhMuc, DanhMucKhongMonDTO danhMucUpdate);
}
