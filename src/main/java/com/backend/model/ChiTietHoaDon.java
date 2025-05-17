package com.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ChiTietHoaDon")
public class ChiTietHoaDon {
    @Id
    @Column(name = "MaChiTietHoaDon")
    private String maChiTietHoaDon;

    @ManyToOne
    @JoinColumn(name = "MaHoaDon")
    private HoaDon hoaDon;

    @Column(name = "TenMon")
    private String tenMon;

    @Column(name = "DonGia")
    private int donGia;

    @Column(name = "SoLuong")
    private int soLuong;

    @Column(name = "YeuCauKhac")
    private String yeuCauKhac;

    @Column(name = "TamTinh")
    private int tamTinh;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maChiTietHoaDon, HoaDon hoaDon, String tenMon, int donGia, int soLuong, String yeuCauKhac, int tamTinh) {
        this.maChiTietHoaDon = maChiTietHoaDon;
        this.hoaDon = hoaDon;
        this.tenMon = tenMon;
        this.donGia = donGia;
        this.soLuong = soLuong;
        this.yeuCauKhac = yeuCauKhac;
        this.tamTinh = tamTinh;
    }

    public String getMaChiTietHoaDon() {
        return maChiTietHoaDon;
    }

    public void setMaChiTietHoaDon(String maChiTietHoaDon) {
        this.maChiTietHoaDon = maChiTietHoaDon;
    }
    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
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
