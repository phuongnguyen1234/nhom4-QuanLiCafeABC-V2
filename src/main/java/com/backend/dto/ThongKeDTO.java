package com.backend.dto;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.model.NhanVien;

public class ThongKeDTO {
    private int doanhThuHomNay;
    private int tongDoanhThu;
    private int soDon;
    private int soMon;
    private List<String> top5MonBanChay;
    private List<String> top3NhanVien;


    public ThongKeDTO() {}

    public ThongKeDTO(int thang, int nam, int tongDoanhThu, int soDon, int soMon, 
                      List<String> top5MonBanChay, List<String> top3NhanVien) {
        this.doanhThuHomNay = thang;
        this.tongDoanhThu = tongDoanhThu;
        this.soDon = soDon;
        this.soMon = soMon;
        this.top5MonBanChay = top5MonBanChay;
        this.top3NhanVien = top3NhanVien;
    }

    public int getDoanhThuHomNay() {
        return doanhThuHomNay;
    }

    public void setDoanhThuHomNay(int thang) {
        this.doanhThuHomNay = thang;
    }

    public int getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(int tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public int getSoDon() {
        return soDon;
    }

    public void setSoDon(int soDon) {
        this.soDon = soDon;
    }

    public int getSoMon() {
        return soMon;
    }

    public void setSoMon(int soMon) {
        this.soMon = soMon;
    }
    public List<String> getTop5MonBanChay() {
        return top5MonBanChay;
    }
    public void setTop5MonBanChay(List<String> top5MonBanChay) {
        this.top5MonBanChay = top5MonBanChay;
    }
    public List<String> getTop3NhanVien() {
        return top3NhanVien;
    }
    public void setTop3NhanVien(List<NhanVien> top3NhanVien) {
        List<String> hoTenList = top3NhanVien.stream()
        .map(NhanVien::getHoTen)
        .collect(Collectors.toList());
        this.top3NhanVien = hoTenList;
    }
}
