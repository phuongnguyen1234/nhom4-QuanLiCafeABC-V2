package com.backend.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.dto.BangLuongDTO;
import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.DonHangDTO;
import com.backend.dto.DonHangSummaryDTO;
import com.backend.dto.MonDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.dto.NhanVienDTO;
import com.backend.model.BangLuong;
import com.backend.model.ChiTietDonHang;
import com.backend.model.DanhMuc;
import com.backend.model.DonHang;
import com.backend.model.Mon;
import com.backend.model.NhanVien;

import javafx.collections.FXCollections;

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
        Mon mon = new Mon();
        mon.setMaMon(monDTO.getMaMon());
        mon.setTenMon(monDTO.getTenMon());
        mon.setAnhMinhHoa(monDTO.getAnhMinhHoa());
        mon.setTrangThai(monDTO.getTrangThai());
        mon.setDonGia(monDTO.getDonGia());
        return mon;
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

        if (donHang.getChiTietDonHang() != null) {
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
        } else {
            dto.setDanhSachMonTrongDon(new ArrayList<>()); // Hoặc trả về danh sách rỗng
        }
        return dto;
    }

    // Phương thức chuyển đổi từ NhanVien Entity sang NhanVienDTO
    public static NhanVienDTO toNhanVienDTO(NhanVien nhanVien) {
        if (nhanVien == null) {
            return null;
        }
        NhanVienDTO dto = new NhanVienDTO();
        dto.setMaNhanVien(nhanVien.getMaNhanVien());
        dto.setTenNhanVien(nhanVien.getHoTen());
        dto.setAnhChanDung(nhanVien.getAnhChanDung());
        dto.setGioiTinh(nhanVien.getGioiTinh());
        dto.setNgaySinh(nhanVien.getNgaySinh());
        dto.setQueQuan(nhanVien.getQueQuan());
        dto.setDiaChi(nhanVien.getDiaChi());
        dto.setSoDienThoai(nhanVien.getSoDienThoai());
        dto.setLoaiNhanVien(nhanVien.getLoaiNhanVien());
        dto.setViTri(nhanVien.getViTri());
        dto.setThoiGianVaoLam(nhanVien.getThoiGianVaoLam());
        dto.setMucLuong(nhanVien.getMucLuong());
        dto.setTrangThai(nhanVien.getTrangThai()); // Trạng thái làm việc (Đi làm, Nghỉ việc)
        dto.setEmail(nhanVien.getEmail());
        dto.setMatKhau(null); // Không trả về mật khẩu ra frontend
        dto.setTrangThaiHoatDong(nhanVien.getTrangThaiHoatDong());
        return dto;
    }

    // Phương thức chuyển đổi từ NhanVienDTO sang NhanVien Entity
    public static NhanVien toNhanVien(NhanVienDTO dto) {
        // Lưu ý: Phương thức này không set MatKhau vì mật khẩu được xử lý riêng (mã hóa)
        // và không set TrangThaiHoatDong vì trạng thái hoạt động được quản lý bởi hệ thống
        return new NhanVien(dto.getMaNhanVien(), dto.getTenNhanVien(), dto.getAnhChanDung(), dto.getGioiTinh(), dto.getNgaySinh(), dto.getQueQuan(), dto.getDiaChi(), dto.getSoDienThoai(), dto.getLoaiNhanVien(), dto.getViTri(), dto.getThoiGianVaoLam(), dto.getMucLuong(), dto.getTrangThai(), dto.getEmail(), null, null);
    }
    public static DonHangSummaryDTO toDonHangSummaryDTO(DonHangDTO detailedDTO) {
        if (detailedDTO == null) return null;
        DonHangSummaryDTO summary = new DonHangSummaryDTO();
        summary.setMaDonHang(detailedDTO.getMaDonHang());
        summary.setHoTen(detailedDTO.getHoTen()); 
        summary.setThoiGianDatHang(detailedDTO.getThoiGianDatHang());
        summary.setTongTien(detailedDTO.getTongTien());
        return summary;
    }

    public static List<DonHangSummaryDTO> toDonHangSummaryDTOList(List<DonHangDTO> dtoList) {
        if (dtoList == null) {
            return FXCollections.emptyObservableList();
        }
        return dtoList.stream().map(DTOConversion::toDonHangSummaryDTO).collect(Collectors.toList());
    } 

    public static MonTrongDonDTO toMonTrongDonDTO(ChiTietDonHang ctdh){
        MonTrongDonDTO monDTO = new MonTrongDonDTO();
        monDTO.setMaMon(ctdh.getMon().getMaMon());
        monDTO.setTenMon(ctdh.getTenMon());
        monDTO.setSoLuong(ctdh.getSoLuong());
        monDTO.setDonGia(ctdh.getDonGia());
        monDTO.setYeuCauKhac(ctdh.getYeuCauKhac());
        monDTO.setTamTinh(ctdh.getTamTinh());
        return monDTO;
    }
}
