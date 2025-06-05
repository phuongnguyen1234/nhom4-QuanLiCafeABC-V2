package com.backend.dto;

import java.time.LocalDateTime;

public class DonHangSummaryDTO {
    private String maDonHang;
    private String hoTen;
    private LocalDateTime thoiGianDatHang;
    private int tongTien;

    public DonHangSummaryDTO() {
    }

    public DonHangSummaryDTO(String maDonHang, String hoTen, LocalDateTime thoiGianDatHang, int tongTien) {
        this.maDonHang = maDonHang;
        this.hoTen = hoTen;
        this.thoiGianDatHang = thoiGianDatHang;
        this.tongTien = tongTien;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public String getHoTen() {
        return hoTen;
    }

    public LocalDateTime getThoiGianDatHang() {
        return thoiGianDatHang;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public void setThoiGianDatHang(LocalDateTime thoiGianDatHang) {
        this.thoiGianDatHang = thoiGianDatHang;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }
}
