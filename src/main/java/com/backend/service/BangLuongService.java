package com.backend.service;


import java.time.YearMonth;
import java.util.List;

import com.backend.dto.BangLuongDTO;

public interface BangLuongService {
    // Thêm mới bảng lương
    int taoBangLuongThangHienTai();

    // Lấy toàn bộ danh sách bảng lương
    List<BangLuongDTO> layTatCaBangLuongThangNay();

    // Tìm bảng lương theo mã
    BangLuongDTO timBangLuongTheoMa(String ma);

    // Sửa bảng lương theo mã
    BangLuongDTO suaBangLuong(String ma, BangLuongDTO bangLuongDTO);

    // Lấy danh sách bảng lương theo tháng
    List<BangLuongDTO> layBangLuongTheoThang(YearMonth thang);
}
