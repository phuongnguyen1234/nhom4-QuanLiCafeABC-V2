package com.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DonHangDTO {
    private String maDonHang;
    private String maNhanVien;
    private String hoTen;
    private List<MonTrongDonDTO> danhSachMonTrongDon;
    private LocalDateTime thoiGianDatHang;
    private int tongTien;

    public DonHangDTO(){}

    public DonHangDTO(String maDonHang, String hoTen, String maNhanVien, List<MonTrongDonDTO> danhSachMonTrongDon, LocalDateTime thoiGianDatHang, int tongTien){
        this.maDonHang = maDonHang;
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.danhSachMonTrongDon = danhSachMonTrongDon;
        this.thoiGianDatHang = thoiGianDatHang;
        this.tongTien = tongTien;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
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

    public List<MonTrongDonDTO> getDanhSachMonTrongDon() {
        return danhSachMonTrongDon;
    }

    public void setDanhSachMonTrongDon(List<MonTrongDonDTO> danhSachMonTrongDon) {
        this.danhSachMonTrongDon = danhSachMonTrongDon;
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
