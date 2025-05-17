package com.backend.dto;

import java.util.List;

public class DanhMucMonKhongAnhDTO {
    private int maDanhMuc;
    private String tenDanhMuc;
    private String trangThai;
    private List<MonKhongAnhDTO> monList;

    public DanhMucMonKhongAnhDTO() {
    }

    public DanhMucMonKhongAnhDTO(int maDanhMuc, String tenDanhMuc, String trangThai, List<MonKhongAnhDTO> monList) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.trangThai = trangThai;
        this.monList = monList;
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public List<MonKhongAnhDTO> getMonList() {
        return monList;
    }

    public void setMonList(List<MonKhongAnhDTO> monList) {
        this.monList = monList;
    }
}
