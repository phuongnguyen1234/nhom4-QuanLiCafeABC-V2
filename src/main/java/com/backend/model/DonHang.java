package com.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "DonHang")
public class DonHang {
    @Id
    @Column(name = "MaDonHang")
    private String maDonHang;

    @ManyToOne
    @JoinColumn(name = "MaNhanVien")
    @JsonIgnore
    private NhanVien nhanVien;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "ThoiGianDatHang")
    private LocalDateTime thoiGianDatHang;

    @Column(name = "TongTien")
    private int tongTien;

    @OneToMany(mappedBy = "donHang")
    private List<ChiTietDonHang> chiTietDonHangs;

    public DonHang(){}

    public DonHang(String maDonHang, NhanVien nhanVien, String hoTen, LocalDateTime thoiGianDatHang, int tongTien){
        this.maDonHang = maDonHang;
        this.nhanVien = nhanVien;
        this.hoTen = hoTen;
        this.thoiGianDatHang = thoiGianDatHang;
        this.tongTien = tongTien;
        this.chiTietDonHangs = new ArrayList<>();
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDateTime getThoiGianDatHang() {
        return thoiGianDatHang;
    }

    public void setThoiGianDatHang(LocalDateTime thoiGianDatHang) {
        this.thoiGianDatHang = thoiGianDatHang;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }
}
