package com.backend.service;
import java.util.Map;

public interface DoanhThuService {
    public int getDoanhThuHomNay();
    public int getTongDoanhThuByThangAndNam(int thang, int nam);
    public int getSoDonByThangAndNam(int thang, int nam);
    public int getSoMonByThangAndNam(int thang, int nam);
    public Map<String, Integer> getDoanhThuTrongThoiGian(int thangStart, int namStart, int thangEnd, int namEnd);
    public Map<String, Integer> getTop5MonBanChayByThangAndNam(int thang, int nam);
    public Map<String, Integer> getTop3NhanVienTaoDonByThangAndNam(int thang, int nam);
    public Map<String, Integer> getKhoangThoiGianDatDonNhieuNhatByThangAndNam(int thang, int nam);
}
