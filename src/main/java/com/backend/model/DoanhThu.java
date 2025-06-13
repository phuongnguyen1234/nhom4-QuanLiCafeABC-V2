package com.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DoanhThu")
public class DoanhThu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    @Column(name = "MaDoanhThu")
    private int maDoanhThu;

    @Column(name = "Thang")
    private int thang;

    @Column(name = "Nam")
    private int nam;

    @Column(name = "TongDoanhThu")
    private int tongDoanhThu;

    @Column(name = "TangTruongDoanhThu")
    private double tangTruongDoanhThu;

    @Column(name = "SoDon")
    private int soDon;

    @Column(name = "SoMon")
    private int soMon;

    @Column(name = "TrungBinhMoiDon")
    private double trungBinhMoiDon;

    @Column(name = "ThoiGianTongHopDoanhThu")
    private LocalDateTime thoiGianTongHopDoanhThu;

    public DoanhThu() {
    }

    public DoanhThu(int maDoanhThu, int thang, int nam, int tongDoanhThu, double tangTruongDoanhThu, int soDon,
            int soMon, double trungBinhMoiDon, LocalDateTime thoiGianTongHopDoanhThu) {
        this.maDoanhThu = maDoanhThu;
        this.thang = thang;
        this.nam = nam;
        this.tongDoanhThu = tongDoanhThu;
        this.tangTruongDoanhThu = tangTruongDoanhThu;
        this.soDon = soDon;
        this.soMon = soMon;
        this.trungBinhMoiDon = trungBinhMoiDon;
        this.thoiGianTongHopDoanhThu = thoiGianTongHopDoanhThu;
    }

    public int getMaDoanhThu() {
        return maDoanhThu;
    }

    public void setMaDoanhThu(int maDoanhThu) {
        this.maDoanhThu = maDoanhThu;
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

    public int getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(int tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public double getTangTruongDoanhThu() {
        return tangTruongDoanhThu;
    }

    public void setTangTruongDoanhThu(double tangTruongDoanhThu) {
        this.tangTruongDoanhThu = tangTruongDoanhThu;
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

    public double getTrungBinhMoiDon() {
        return trungBinhMoiDon;
    }

    public void setTrungBinhMoiDon(double trungBinhMoiDon) {
        this.trungBinhMoiDon = trungBinhMoiDon;
    }

    public LocalDateTime getThoiGianTongHopDoanhThu() {
        return thoiGianTongHopDoanhThu;
    }

    public void setThoiGianTongHopDoanhThu(LocalDateTime thoiGianTongHopDoanhThu) {
        this.thoiGianTongHopDoanhThu = thoiGianTongHopDoanhThu;
    }
}
