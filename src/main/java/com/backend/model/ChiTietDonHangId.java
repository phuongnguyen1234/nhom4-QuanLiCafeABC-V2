package com.backend.model;

import java.util.Objects;

import jakarta.persistence.Column;

public class ChiTietDonHangId {
    @Column(name = "MaDonHang")
    private String maDonHang;

    @Column(name = "MaMon")
    private String maMon;

    public ChiTietDonHangId() {
    }

    public ChiTietDonHangId(String maDonHang, String maMon) {
        this.maDonHang = maDonHang;
        this.maMon = maMon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietDonHangId)) return false;
        ChiTietDonHangId that = (ChiTietDonHangId) o;
        return maDonHang.equals(that.maDonHang) && maMon.equals(that.maMon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDonHang, maMon);
    }
}
