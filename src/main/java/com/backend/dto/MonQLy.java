package com.backend.dto;

public class MonQLy {
    private String maMon;
    private String tenMon;
    private byte[] anhMinhHoa;
    private String trangThai;
    private int donGia;
    private int maDanhMuc;
    private String tenDanhMuc;

    public MonQLy(){}

    public MonQLy(String maMon, String tenMon, byte[] anhMinhHoa, String trangThai, int donGia, int maDanhMuc, String tenDanhMuc) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.anhMinhHoa = anhMinhHoa;
        this.trangThai = trangThai;
        this.donGia = donGia;
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public byte[] getAnhMinhHoa() {
        return anhMinhHoa;
    }

    public void setAnhMinhHoa(byte[] anhMinhHoa) {
        this.anhMinhHoa = anhMinhHoa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getDonGia() {
        return donGia;
    }

    public void setDonGia(int donGia) {
        this.donGia = donGia;
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

}
