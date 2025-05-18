package com.backend.dto;

public class ChiTietDonHangDTO {
    private String maMon;
    private String maDonHang;
    private String  tenMon;
    private int donGia ;
    private int soLuong;
    private String yeuCauKhac;
    private int tamTinh;

    public ChiTietDonHangDTO(String maDonHang, String tenMon, String maMon, String yeuCauKhac, int soLuong, int donGia, int tamTinh) {
        this.maMon = maMon;
        this.yeuCauKhac = yeuCauKhac;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.maDonHang = maDonHang;
        this.tenMon = tenMon;
        this.tamTinh = tamTinh;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public int getDonGia() {
        return donGia;
    }

    public void setDonGia(int donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getYeuCauKhac() {
        return yeuCauKhac;
    }

    public void setYeuCauKhac(String yeuCauKhac) {
        this.yeuCauKhac = yeuCauKhac;
    }

    public int getTamTinh() {
        return tamTinh;
    }

    public void setTamTinh(int tamTinh) {
        this.tamTinh = tamTinh;
    }
}
