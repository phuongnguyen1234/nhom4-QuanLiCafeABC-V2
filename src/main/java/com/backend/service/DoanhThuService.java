package com.backend.service;
import java.util.List;
import java.util.Map;

import com.backend.dto.ThongKeDTO;
import com.backend.model.DoanhThu;

public interface DoanhThuService {
    public int getDoanhThuHomNay();
    public int getTongDoanhThuByThangAndNam(int thang, int nam);
    public int getSoDonByThangAndNam(int thang, int nam);
    public int getSoMonByThangAndNam(int thang, int nam);
    public Map<String, Integer> getDoanhThuTrongThoiGian(int thangStart, int namStart, int thangEnd, int namEnd);
    public Map<String, Integer> getTop5MonBanChayByThangAndNam(int thang, int nam);
    public Map<String, Integer> getTop3NhanVienTaoDonByThangAndNam(int thang, int nam);
    public Map<String, Integer> getKhoangThoiGianDatDonNhieuNhatByThangAndNam(int thang, int nam);

    public List<DoanhThu> getAllDoanhThu();

    // Mới: Kiểm tra và tổng hợp doanh thu tháng trước nếu cần
    String kiemTraVaTongHopDoanhThuThangTruoc();

    // Mới: Lấy dữ liệu tổng quan cho ThongKeDTO
    ThongKeDTO getThongKeTongQuanData();
}
