package com.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @Column(name = "MaHoaDon")
    private String maHoaDon;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "ThoiGianDatHang")
    private LocalDateTime thoiGianDatHang;

    @Column(name = "TongTien")
    private int tongTien;

    @OneToMany(mappedBy = "hoaDon")
    private List<ChiTietHoaDon> chiTietHoaDons;

    public HoaDon(){}

    public HoaDon(String maHoaDon, String hoTen, LocalDateTime thoiGianDatHang, int tongTien) {
        this.maHoaDon = maHoaDon;
        this.hoTen = hoTen;
        this.thoiGianDatHang = thoiGianDatHang;
        this.tongTien = tongTien;
    }

    public String getMaHoaDon() {
        return maHoaDon;
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

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
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
