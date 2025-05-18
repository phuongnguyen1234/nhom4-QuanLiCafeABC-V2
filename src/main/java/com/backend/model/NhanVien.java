package com.backend.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "NhanVien")
public class NhanVien {
    @Id
    @Column(name = "MaNhanVien")
    private String maNhanVien;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "AnhChanDung")
    private byte[] anhChanDung;

    @Column(name = "GioiTinh")
    private String gioiTinh;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "QueQuan")
    private String queQuan;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "LoaiNhanVien")
    private String loaiNhanVien;

    @Column(name = "ViTri")
    private String viTri;

    @Column(name = "ThoiGianVaoLam")
    private LocalDate thoiGianVaoLam;

    @Column(name = "MucLuong")
    private int mucLuong;

    @Column(name = "TrangThai")
    private String trangThai;

    @Column(name = "Email")
    private String email;

    @Column(name = "MatKhau")
    private String matKhau;

    @Column(name = "TrangThaiHoatDong")
    private String trangThaiHoatDong;

    @OneToMany(mappedBy = "nhanVien")
    private List<DonHang> donHangList;

    @OneToMany(mappedBy = "nhanVien")
    private List<BangLuong> bangLuongList = new ArrayList<>();

    public NhanVien(){}

    public NhanVien(String maNhanVien, String hoTen, byte[] anhChanDung, String gioiTinh, 
    LocalDate ngaySinh, String queQuan, String diaChi, String soDienThoai, String loaiNhanVien, String viTri,
    LocalDate thoiGianVaoLam, int mucLuong, String trangThai, String email, String matKhau, String trangThaiHoatDong) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
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
        this.trangThai = trangThai;
        this.email = email;
        this.matKhau = matKhau;
        this.trangThaiHoatDong = trangThaiHoatDong;
    }

    // Getters and setters
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
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

    public String getTrangThaiHoatDong() {
        return trangThaiHoatDong;
    }

    public void setTrangThaiHoatDong(String trangThaiHoatDong) {
        this.trangThaiHoatDong = trangThaiHoatDong;
    }

    public List<DonHang> getDonHangList() {
        return donHangList;
    }

    public void setDonHangList(List<DonHang> donHangList) {
        this.donHangList = donHangList;
    }
}
