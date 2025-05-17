package com.backend.model;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "BangLuong")
public class BangLuong {
    @Id
    @Column(name = "MaBangLuong")
    private String maBangLuong;

    @ManyToOne
    @JoinColumn(name = "MaNhanVien", referencedColumnName = "MaNhanVien")
    private NhanVien nhanVien;

    @Column(name = "Thang")
    private YearMonth thang;

    @Column(name = "NgayCong")
    private int ngayCong;

    @Column(name = "NghiCoCong")
    private int nghiCoCong;

    @Column(name = "NghiKhongCong")
    private int nghiKhongCong;

    @Column(name = "GioLamThem")
    private int gioLamThem;

    @Column(name = "DonDaTao")
    private int donDaTao;

    @Column(name = "ThuongDoanhThu")
    private int thuongDoanhThu;

    @Column(name = "LuongThucNhan")
    private int luongThucNhan;

    @Column(name = "GhiChu")
    private String ghiChu;

    @Column(name = "DuocPhepChinhSua")
    private String duocPhepChinhSua;

    public BangLuong(){}

    public BangLuong(String maBangLuong, NhanVien nhanVien, YearMonth thang, int ngayCong,
    int nghiCoCong, int nghiKhongCong, int gioLamThem, int donDaTao, int thuongDoanhThu,
    int luongThucNhan, String ghiChu, String duocPhepChinhSua) {
        // Constructor with parameters
        this.maBangLuong = maBangLuong;
        this.nhanVien = nhanVien;
        this.thang = thang;
        this.ngayCong = ngayCong;
        this.nghiCoCong = nghiCoCong;
        this.nghiKhongCong = nghiKhongCong;
        this.gioLamThem = gioLamThem;
        this.donDaTao = donDaTao;
        this.thuongDoanhThu = thuongDoanhThu;
        this.luongThucNhan = luongThucNhan;
        this.ghiChu = ghiChu;
        this.duocPhepChinhSua = duocPhepChinhSua;
    }

    public String getMaBangLuong() {
        return maBangLuong;
    }
    
    public void setMaBangLuong(String maBangLuong) {
        this.maBangLuong = maBangLuong;
    }
    
    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }
    
    public YearMonth getThang() {
        return thang;
    }
    
    public void setThang(YearMonth thang) {
        this.thang = thang;
    }
    
    public int getNgayCong() {
        return ngayCong;
    }

    public void setNgayCong(int ngayCong) {
        this.ngayCong = ngayCong;
    }

    public int getNghiCoCong() {
        return nghiCoCong;
    }

    
    public void setNghiCoCong(int nghiCoCong) {
        this.nghiCoCong = nghiCoCong;
    }

    public int getNghiKhongCong() {
        return nghiKhongCong;
    }

    
    public void setNghiKhongCong(int nghiKhongCong) {
        this.nghiKhongCong = nghiKhongCong;
    }


    public int getGioLamThem() {
        return gioLamThem;
    }

    
    public void setGioLamThem(int gioLamThem) {
        this.gioLamThem = gioLamThem;
    }

    public int getDonDaTao() {
        return donDaTao;
    }

    public void setDonDaTao(int donDaTao) {
        this.donDaTao = donDaTao;
    }

    public int getThuongDoanhThu() {
        return thuongDoanhThu;
    }
    
    public void setThuongDoanhThu(int thuongDoanhThu) {
        this.thuongDoanhThu = thuongDoanhThu;
    }
    
    public int getLuongThucNhan() {
        return luongThucNhan;
    }
    
    public void setLuongThucNhan(int luongThucNhan) {
        this.luongThucNhan = luongThucNhan;
    }
    
    public String getGhiChu() {
        return ghiChu;
    }
    
    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public String getDuocPhepChinhSua() {
        return duocPhepChinhSua;
    }
    
    public void setDuocPhepChinhSua(String duocPhepChinhSua) {
        this.duocPhepChinhSua = duocPhepChinhSua;
    }

    public YearMonth dateSQLToYearMonth(String dateSQL) {
        String[] parts = dateSQL.split("-");
        return YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public String yearMonthToDateSQL(YearMonth yearMonth) {
        String formattedDate = yearMonth.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return formattedDate;
    }
}
