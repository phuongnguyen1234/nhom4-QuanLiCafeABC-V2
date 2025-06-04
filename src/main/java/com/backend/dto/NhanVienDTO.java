package com.backend.dto;

import java.time.LocalDate;
import java.util.Arrays;

public class NhanVienDTO {
    private String maNhanVien;
    private String tenNhanVien;
    private byte[] anhChanDung;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private String queQuan;
    private String diaChi;
    private String soDienThoai;
    private String loaiNhanVien;
    private String viTri;
    private LocalDate thoiGianVaoLam;
    private int mucLuong;
    private String email;
    private String matKhau;
    private String quyenTruyCap;
    private String trangThaiHoatDong;

    public NhanVienDTO(){}

    public NhanVienDTO(String maNhanVien, String tenNhanVien, byte[] anhChanDung, String gioiTinh, 
    LocalDate ngaySinh, String queQuan, String diaChi, String soDienThoai, String loaiNhanVien, String viTri,
    LocalDate thoiGianVaoLam, int mucLuong, String email, String matKhau, String quyenTruyCap, String trangThaiHoatDong) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.anhChanDung = anhChanDung;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.queQuan = queQuan;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.loaiNhanVien = loaiNhanVien;
        this.viTri = viTri;
        this.thoiGianVaoLam = thoiGianVaoLam;
        this.mucLuong = mucLuong;
        this.email = email;
        this.matKhau = matKhau;
        this.quyenTruyCap = quyenTruyCap;
        this.trangThaiHoatDong = trangThaiHoatDong;
    }

    // Getters and setters
    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public byte[] getAnhChanDung() {
        return anhChanDung;
    }

    public void setAnhChanDung(byte[] anhChanDung) {
        this.anhChanDung = anhChanDung;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getQueQuan() {
        return queQuan;
    }

    public void setQueQuan(String queQuan) {
        this.queQuan = queQuan;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
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

    public LocalDate getThoiGianVaoLam() {
        return thoiGianVaoLam;
    }

    public void setThoiGianVaoLam(LocalDate thoiGianVaoLam) {
        this.thoiGianVaoLam = thoiGianVaoLam;
    }

    public int getMucLuong() {
        return mucLuong;
    }

    public void setMucLuong(int mucLuong) {
        this.mucLuong = mucLuong;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getQuyenTruyCap() {
        return quyenTruyCap;
    }

    public void setQuyenTruyCap(String quyenTruyCap) {
        this.quyenTruyCap = quyenTruyCap;
    }

    public String getTrangThaiHoatDong() {
        return trangThaiHoatDong;
    }

    public void setTrangThaiHoatDong(String trangThaiHoatDong) {
        this.trangThaiHoatDong = trangThaiHoatDong;
    }

    @Override
    public String toString() {
        return "NhanVienDTO [maNhanVien=" + maNhanVien + ", tenNhanVien=" + tenNhanVien + ", anhChanDung="
                + Arrays.toString(anhChanDung) + ", gioiTinh=" + gioiTinh + ", ngaySinh=" + ngaySinh + ", queQuan="
                + queQuan + ", diaChi=" + diaChi + ", soDienThoai=" + soDienThoai + ", loaiNhanVien=" + loaiNhanVien + ", viTri=" + viTri
                + ", thoiGianVaoLam=" + thoiGianVaoLam + ", mucLuong=" + mucLuong + ", email=" + email + ", matKhau="
                + matKhau + ", quyenTruyCap=" + quyenTruyCap + ", trangThaiHoatDong=" + trangThaiHoatDong + "]";
    }
    
}
