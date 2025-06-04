package com.backend.dto;

public class DoanhThuDTO {
    private int thang;
    private int nam;
    private int tongDoanhThu;
    private int soDon;
    private int soMon;

    public DoanhThuDTO() {}

    public DoanhThuDTO(int thang, int nam, int tongDoanhThu, int soDon, int soMon) {
        this.thang = thang;
        this.nam = nam;
        this.tongDoanhThu = tongDoanhThu;
        this.soDon = soDon;
        this.soMon = soMon;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public int getTongDoanhThu() {
        return tongDoanhThu;
    }

    public void setTongDoanhThu(int tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }

    public int getSoDon() {
        return soDon;
    }

    public void setSoDon(int soDon) {
        this.soDon = soDon;
    }

    public int getSoMon() {
        return soMon;
    }

    public void setSoMon(int soMon) {
        this.soMon = soMon;
    }
}
