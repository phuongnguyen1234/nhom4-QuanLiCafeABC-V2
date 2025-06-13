package com.backend.dto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.backend.model.NhanVien;

public class ThongKeDTO {
    private int thang, nam;
    private int doanhThuHomNay;
    private int tongDoanhThuThangTruoc;
    private int soDon;
    private int soMon;
    private Map<String, Integer> top5MonBanChay;
    private Map<String, Integer> top3NhanVien;
    private Map<String, Integer> khoangThoiGianDatDonNhieuNhat;
    private Map<String, Integer> doanhThuTrongThoiGian;

    public ThongKeDTO() {}

    public ThongKeDTO(int thang, int nam, int doanhThuHomNay, int tongDoanhThuThangTruoc, int soDon, int soMon, 
                      Map<String, Integer> top5MonBanChay, Map<String, Integer> top3NhanVien, Map<String, Integer> khoangThoiGianDatDonNhieuNhat, Map<String, Integer> doanhThuTrongThoiGian) {
        this.thang = thang;
        this.nam = nam;
        this.doanhThuHomNay = doanhThuHomNay;
        this.tongDoanhThuThangTruoc = tongDoanhThuThangTruoc;
        this.soDon = soDon;
        this.soMon = soMon;
        this.top5MonBanChay = top5MonBanChay;
        this.top3NhanVien = top3NhanVien;
        this.khoangThoiGianDatDonNhieuNhat = khoangThoiGianDatDonNhieuNhat;
        this.doanhThuTrongThoiGian = doanhThuTrongThoiGian;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }
    public int getNam() {
        return nam;
    }
    public void setNam(int nam) {
        this.nam = nam;
    }

    public int getDoanhThuHomNay() {
        return doanhThuHomNay;
    }

    public void setDoanhThuHomNay(int thang) {
        this.doanhThuHomNay = thang;
    }

    public int getTongDoanhThuThangTruoc() {
        return tongDoanhThuThangTruoc;
    }

    public void setTongDoanhThuThangTruoc(int tongDoanhThuThangTruoc) {
        this.tongDoanhThuThangTruoc = tongDoanhThuThangTruoc;
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

    public Map<String, Integer> getTop5MonBanChay() {
        return top5MonBanChay;
    }

    public void setTop5MonBanChay(Map<String, Integer> top5MonBanChay) {
        this.top5MonBanChay = top5MonBanChay;
    }

    public Map<String, Integer> getTop3NhanVien() {
        return top3NhanVien;
    }

    public void setTop3NhanVien(Map<String, Integer> top3NhanVien) {
        this.top3NhanVien = top3NhanVien;
    }

    public Map<String, Integer> getKhoangThoiGianDatDonNhieuNhat() {
        return khoangThoiGianDatDonNhieuNhat;
    }

    public void setKhoangThoiGianDatDonNhieuNhat(Map<String, Integer> khoangThoiGianDatDonNhieuNhat) {
        this.khoangThoiGianDatDonNhieuNhat = khoangThoiGianDatDonNhieuNhat;
    }

    public Map<String, Integer> getDoanhThuTrongThoiGian() {
        return doanhThuTrongThoiGian;
    }
    
    public void setDoanhThuTrongThoiGian(Map<String, Integer> doanhThuTrongThoiGian) {
        this.doanhThuTrongThoiGian = doanhThuTrongThoiGian;
    }
}
