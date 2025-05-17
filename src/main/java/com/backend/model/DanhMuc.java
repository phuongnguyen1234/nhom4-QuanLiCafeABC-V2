package com.backend.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "DanhMuc")
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDanhMuc")
    private int maDanhMuc;

    @Column(name = "TenDanhMuc")
    private String tenDanhMuc;

    @Column(name = "TrangThai")
    private String trangThai;

    // Foreign key to Mon table
    @OneToMany(mappedBy = "danhMuc", cascade = CascadeType.ALL)
    private List<Mon> monList;

    public DanhMuc(){}

    public DanhMuc(int maDanhMuc, String tenDanhMuc, String trangThai, List<Mon> list) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.trangThai = trangThai;
        this.monList = list;
        for (Mon mon : list) {
            mon.setDanhMuc(this);
        }
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

    public List<Mon> getMonList() {
        return monList;
    }

    public void setMonList(List<Mon> monList) {
        this.monList = monList;
    }    
}
