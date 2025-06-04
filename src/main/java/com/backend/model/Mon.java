package com.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Mon")
public class Mon {
    @Id
    @Column(name = "MaMon")
    private String maMon;

    @Column(name = "TenMon")
    private String tenMon;

    @Column(name = "AnhMinhHoa")
    private byte[] anhMinhHoa;

    @Column(name = "DonGia")
    private int donGia;

    @Column(name = "TrangThai")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "MaDanhMuc", referencedColumnName = "MaDanhMuc")
    @JsonIgnore
    private DanhMuc danhMuc;

    @ManyToMany
    @JoinTable(
        name = "ChiTietDonHang",
        joinColumns = @JoinColumn(name = "MaMon"),
        inverseJoinColumns = @JoinColumn(name = "MaDonHang")
    )
    @JsonIgnore
    private List<DonHang> donHangList;

    public Mon(){}

    public Mon(String maMon, String tenMon, byte[] anhMinhHoa, int donGia, String trangThai, DanhMuc danhMuc){
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.anhMinhHoa = anhMinhHoa;
        this.donGia = donGia;
        this.trangThai = trangThai;
        this.danhMuc = danhMuc;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maCaPhe) {
        this.maMon = maCaPhe;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenCaPhe) {
        this.tenMon = tenCaPhe;
    }

    public byte[] getAnhMinhHoa() {
        return anhMinhHoa;
    }

    public void setAnhMinhHoa(byte[] anhMinhHoa) {
        this.anhMinhHoa = anhMinhHoa;
    }

    public int getDonGia() {
        return donGia;
    }

    public void setDonGia(int donGia) {
        this.donGia = donGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }
}
