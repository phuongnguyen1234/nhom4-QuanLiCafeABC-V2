package com.backend.dto;

public class MonTrongDonDTO {
    private String maMon;
    private String tenMon;
    private byte[] anhMinhHoa;
    private int donGia;
    private String maDanhMuc;
    private String tenDanhMuc;
    private int soLuong;
    private String yeuCauKhac;
    private int tamTinh;

    public MonTrongDonDTO(){}

    public MonTrongDonDTO(String maMon, String tenMon, byte[] anhMinhHoa, int donGia, String maDanhMuc,
    String tenDanhMuc, int soLuong, String yeuCauKhac, int tamTinh) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.anhMinhHoa = anhMinhHoa;
        this.donGia = donGia;
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
        this.soLuong = soLuong;
        this.yeuCauKhac = yeuCauKhac;
        this.tamTinh = tamTinh;
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

    public int getDonGia() {
        return donGia;
    }

    public void setDonGia(int donGia) {
        this.donGia = donGia;
    }

    public String getMaDanhMuc() {
        return maDanhMuc;
    }

    public void setMaDanhMuc(String maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getYeuCauKhac() {
        return yeuCauKhac;
    }

    public void setYeuCauKhac(String yeuCauKhac) {
        this.yeuCauKhac = yeuCauKhac;
    }
    
    public int getTamTinh() {
        return tamTinh;
    }

    public void setTamTinh(int tamTinh) {
        this.tamTinh = tamTinh;
    }
}
