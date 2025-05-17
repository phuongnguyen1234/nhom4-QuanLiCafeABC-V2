package com.backend.dto;

public class ChiTietDonHangDTO {
    private String maCaPhe;
    private String maDonHang;
    private String  tenCaPhe;
    private int donGia ;
    private int soLuong;
    private String yeuCauDacBiet;
    private int tamTinh;

    public ChiTietDonHangDTO(String maCaPhe, String yeuCauDacBiet, int soLuong, int donGia, String maDonHang, String tenCaPhe, int tamTinh) {
        this.maCaPhe = maCaPhe;
        this.yeuCauDacBiet = yeuCauDacBiet;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.maDonHang = maDonHang;
        this.tenCaPhe = tenCaPhe;
        this.tamTinh = tamTinh;
    }

    public String getMaCaPhe() {
        return maCaPhe;
    }

    public void setMaCaPhe(String maCaPhe) {
        this.maCaPhe = maCaPhe;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public String getTenCaPhe() {
        return tenCaPhe;
    }

    public void setTenCaPhe(String tenCaPhe) {
        this.tenCaPhe = tenCaPhe;
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

    public String getYeuCauDacBiet() {
        return yeuCauDacBiet;
    }

    public void setYeuCauDacBiet(String yeuCauDacBiet) {
        this.yeuCauDacBiet = yeuCauDacBiet;
    }

    public int getTamTinh() {
        return tamTinh;
    }

    public void setTamTinh(int tamTinh) {
        this.tamTinh = tamTinh;
    }
}
