package com.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "ChiTietDonHang")
public class ChiTietDonHang {
    @EmbeddedId
    private ChiTietDonHangId id;

    @ManyToOne
    @MapsId("maDonHang")
    @JoinColumn(name = "MaDonHang")
    private DonHang donHang;

    @ManyToOne
    @MapsId("maMon")
    @JoinColumn(name = "MaMon")
    private Mon mon;

    @Column(name = "SoLuong")
    private int soLuong;

    @Column(name = "YeuCauKhac")
    private String yeuCauKhac;

    @Column(name = "TamTinh")
    private int tamTinh;

    public ChiTietDonHang(){}

    public ChiTietDonHang(DonHang donHang, Mon mon, int soLuong, String yeuCauKhac, int tamTinh) {
        this.donHang = donHang;
        this.mon = mon;
        this.soLuong = soLuong;
        this.yeuCauKhac = yeuCauKhac;
        this.tamTinh = tamTinh;
        this.id = new ChiTietDonHangId(donHang.getMaDonHang(), mon.getMaMon());
    }

    public ChiTietDonHangId getId() {
        return id;
    }

    public void setId(ChiTietDonHangId id) {
        this.id = id;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public Mon getMon() {
        return mon;
    }

    public void setMon(Mon mon) {
        this.mon = mon;
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
