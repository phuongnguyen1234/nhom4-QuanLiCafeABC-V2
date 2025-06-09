package com.backend.utils;

import java.util.List;

import com.backend.dto.BangLuongDTO;
import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.DonHangDTO;
import com.backend.dto.MonDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.model.BangLuong;
import com.backend.model.DanhMuc;
import com.backend.model.DonHang;
import com.backend.model.Mon;

public class DTOConversion {
    public static DanhMucKhongMonDTO toDanhMucKhongMonDTO(DanhMuc danhMuc) {
        return new DanhMucKhongMonDTO(
            danhMuc.getMaDanhMuc(),
            danhMuc.getTenDanhMuc(),
            danhMuc.getLoai(),
            danhMuc.getTrangThai()
        );
    }

    public static MonDTO toMonDTO(Mon mon) {
        return new MonDTO(
            mon.getMaMon(),
            mon.getTenMon(),
            mon.getAnhMinhHoa(),
            mon.getTrangThai(),
            mon.getDonGia(),
            mon.getDanhMuc().getMaDanhMuc(),
            mon.getDanhMuc().getTenDanhMuc()
        );
    }

    public static MonTrongDonDTO toMonTrongDonDTO(Mon mon) {
        MonTrongDonDTO monTrongDon = new MonTrongDonDTO();
        monTrongDon.setTenMon(mon.getTenMon());
        monTrongDon.setDonGia(mon.getDonGia());
        monTrongDon.setMaMon(mon.getMaMon());
        monTrongDon.setAnhMinhHoa(mon.getAnhMinhHoa()); // Thêm dòng này để truyền đường dẫn ảnh
        monTrongDon.setYeuCauKhac("");
        return monTrongDon;
    }

    public static DanhMuc toDanhMuc(DanhMucKhongMonDTO danhMucDTO) {
        return new DanhMuc(
            danhMucDTO.getMaDanhMuc(),
            danhMucDTO.getTenDanhMuc(),
            danhMucDTO.getLoai(),
            danhMucDTO.getTrangThai(),
            null // monList is not set here
        );
    }

    public static Mon toMon(MonDTO monDTO){
        DanhMuc danhMuc = new DanhMuc(monDTO.getMaDanhMuc(), monDTO.getTenDanhMuc(), null, null, null);
        return new Mon(
            monDTO.getMaMon(),
            monDTO.getTenMon(),
            monDTO.getAnhMinhHoa(),
            monDTO.getDonGia(),
            monDTO.getTrangThai(),
            danhMuc
        );
    }

    public static BangLuongDTO toBangLuongDTO(BangLuong bangLuong) {
        BangLuongDTO dto = new BangLuongDTO();
        dto.setMaBangLuong(bangLuong.getMaBangLuong());
        dto.setHoTen(bangLuong.getNhanVien().getHoTen());
        dto.setMaNhanVien(bangLuong.getNhanVien().getMaNhanVien());
        dto.setLoaiNhanVien(bangLuong.getNhanVien().getLoaiNhanVien());
        dto.setViTri(bangLuong.getNhanVien().getViTri());
        dto.setMucLuong(bangLuong.getNhanVien().getMucLuong());
        dto.setThang(bangLuong.getThang());
        dto.setNgayCong(bangLuong.getNgayCong());
        dto.setNghiCoCong(bangLuong.getNghiCoCong());
        dto.setNghiKhongCong(bangLuong.getNghiKhongCong());
        dto.setGioLamThem(bangLuong.getGioLamThem());
        dto.setDonDaTao(bangLuong.getDonDaTao());
        dto.setThuongDoanhThu(bangLuong.getThuongDoanhThu());
        dto.setLuongThucNhan(bangLuong.getLuongThucNhan());
        dto.setGhiChu(bangLuong.getGhiChu());
        dto.setDuocPhepChinhSua(bangLuong.getDuocPhepChinhSua());
        return dto;
    }

    public static DonHangDTO toDonHangDTO(DonHang donHang) {
        if (donHang == null) {
            return null;
        }
        DonHangDTO dto = new DonHangDTO();
        dto.setMaDonHang(donHang.getMaDonHang());
        if (donHang.getNhanVien() != null) {
            dto.setMaNhanVien(donHang.getNhanVien().getMaNhanVien());
        }
        dto.setHoTen(donHang.getHoTen()); // Giả sử DonHang lưu trực tiếp họ tên nhân viên tạo đơn
        dto.setThoiGianDatHang(donHang.getThoiGianDatHang());
        dto.setTongTien(donHang.getTongTien());

        List<MonTrongDonDTO> monTrongDonList = donHang.getChiTietDonHang().stream().map(ctdh -> {
            MonTrongDonDTO monDTO = new MonTrongDonDTO();
            monDTO.setMaMon(ctdh.getMon().getMaMon());
            monDTO.setTenMon(ctdh.getTenMon());
            monDTO.setSoLuong(ctdh.getSoLuong());
            monDTO.setDonGia(ctdh.getDonGia());
            monDTO.setYeuCauKhac(ctdh.getYeuCauKhac());
            monDTO.setTamTinh(ctdh.getTamTinh());
            // Các trường khác của MonTrongDonDTO có thể được set ở đây nếu cần
            return monDTO;
        }).toList();
        dto.setDanhSachMonTrongDon(monTrongDonList);
        return dto;
    }

    
}
