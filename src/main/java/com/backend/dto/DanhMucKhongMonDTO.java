package com.backend.dto;

public class DanhMucKhongMonDTO {
    private int maDanhMuc;
    private String tenDanhMuc;
    private String loai;
    private String trangThai;

    public DanhMucKhongMonDTO(){}

    public DanhMucKhongMonDTO(int maDanhMuc, String tenDanhMuc, String loai, String trangThai) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.loai = loai;
        this.trangThai = trangThai;
    }

    public int getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(int maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
