package com.backend.service;
import java.util.List;

public interface DoanhThuService {
    public int getTongDoanhThuByThangAndNam();
    public int getSoDonByThangAndNam();
    public int getSoMonByThangAndNam();
    public List<Integer> doanhThuTungThang();
}
