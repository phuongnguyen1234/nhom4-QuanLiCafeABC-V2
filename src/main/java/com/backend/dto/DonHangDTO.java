package com.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DonHangDTO {
    private String maDonHang;
    private String maNhanVien;
    private String tenNhanVien;
    private List<MonTrongDonDTO> danhSachCaPheTrongDon;
    private LocalDateTime thoiGianDatHang;
    private int tongTien;

    public DonHangDTO(){}

    public DonHangDTO(String maDonHang, String maNhanVien, String tenNhanVien, List<MonTrongDonDTO> danhSachCaPheTrongDon, LocalDateTime thoiGianDatHang, int tongTien){
        this.maDonHang = maDonHang;
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.danhSachCaPheTrongDon = danhSachCaPheTrongDon;
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

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public List<MonTrongDonDTO> getDanhSachCaPheTrongDon() {
        return danhSachCaPheTrongDon;
    }

    public void setDanhSachCaPheTrongDon(List<MonTrongDonDTO> danhSachCaPheTrongDon) {
        this.danhSachCaPheTrongDon = danhSachCaPheTrongDon;
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
