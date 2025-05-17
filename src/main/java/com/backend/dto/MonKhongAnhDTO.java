package com.backend.dto;

public class MonKhongAnhDTO {
    private String maMon;
    private String tenMon;
    private int donGia;
    private String trangThai;


    public MonKhongAnhDTO() {
    }

    public MonKhongAnhDTO(String maMon, String tenMon, int donGia, String trangThai) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
