package com.backend.dto;

import java.time.LocalDate;

public class BangLuongDTO {
    private String maBangLuong;
    private String maNhanVien;
    private String hoTen; // Tên nhân viên
    private String loaiNhanVien;
    private String viTri;
    private int mucLuong;
    private LocalDate thang;
    private int ngayCong;
    private int nghiCoCong;
    private int nghiKhongCong;
    private int gioLamThem;
    private int donDaTao;
    private int thuongDoanhThu;
    private int luongThucNhan;
    private String ghiChu;
    private String duocPhepChinhSua;

    public BangLuongDTO(){}

    // Constructor
    public BangLuongDTO(String maBangLuong, String maNhanVien, String hoTen, String loaiNhanVien, String viTri, int mucLuong, LocalDate thang, int ngayCong,
                                    int nghiCoCong, int nghiKhongCong, int gioLamThem, int donDaTao,
                                    int thuongDoanhThu, int luongThucNhan, String ghiChu, String duocPhepChinhSua) {
        this.maBangLuong = maBangLuong;
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.loaiNhanVien = loaiNhanVien;
        this.viTri = viTri;
        this.mucLuong = mucLuong;
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

    // Getters and Setters
    public String getMaBangLuong() {
        return maBangLuong;
    }

    public void setMaBangLuong(String maBangLuong) {
        this.maBangLuong = maBangLuong;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getLoaiNhanVien() {
        return loaiNhanVien;
    }

    public void setLoaiNhanVien(String loaiNhanVien) {
        this.loaiNhanVien = loaiNhanVien;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public int getMucLuong() {
        return mucLuong;
    }

    public void setMucLuong(int mucLuong) {
        this.mucLuong = mucLuong;
    }

    public LocalDate getThang() {
        return thang;
    }
    
    public void setThang(LocalDate thang) {
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
}
