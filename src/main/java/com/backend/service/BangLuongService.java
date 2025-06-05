package com.backend.service;


import java.time.YearMonth;
import java.util.List;

import com.backend.dto.BangLuongDTO;

public interface BangLuongService {

    // Thêm mới bảng lương
    BangLuongDTO themBangLuong(BangLuongDTO bangLuongDTO);

    // Lấy toàn bộ danh sách bảng lương
    List<BangLuongDTO> layTatCaBangLuong();

    // Tìm bảng lương theo mã
    BangLuongDTO timBangLuongTheoMa(String ma);

    // Sửa bảng lương theo mã
    BangLuongDTO suaBangLuong(String ma, BangLuongDTO bangLuongDTO);

    // Xoá bảng lương theo mã
    void xoaBangLuong(String ma);

    // Lấy danh sách bảng lương theo tháng
    List<BangLuongDTO> layBangLuongTheoThang(YearMonth thang);
}
