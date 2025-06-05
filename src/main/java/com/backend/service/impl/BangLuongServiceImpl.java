package com.backend.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.dto.BangLuongDTO;
import com.backend.model.BangLuong;
import com.backend.model.DoanhThu;
import com.backend.model.NhanVien;
import com.backend.repository.BangLuongRepository;
import com.backend.repository.DoanhThuRepository;
import com.backend.repository.DonHangRepository;
import com.backend.repository.NhanVienRepository;
import com.backend.service.BangLuongService;

import jakarta.transaction.Transactional;

@Service
public class BangLuongServiceImpl implements BangLuongService {
    private final BangLuongRepository bangLuongRepository;

    private final NhanVienRepository nhanVienRepository;

    private final DonHangRepository donHangRepository;

    private final DoanhThuRepository doanhThuRepository;

    public BangLuongServiceImpl(BangLuongRepository bangLuongRepository, NhanVienRepository nhanVienRepository, DonHangRepository donHangRepository, DoanhThuRepository doanhThuRepository) {
        this.bangLuongRepository = bangLuongRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.donHangRepository = donHangRepository;
        this.doanhThuRepository = doanhThuRepository;
    }

@Override
@Transactional
public int taoBangLuongThangHienTai() {
    LocalDate thangHienTai = YearMonth.now().atDay(1);
    int tongDoanhThuThangTruoc = getTongDoanhThuThangTruoc();

    // Cập nhật lương tháng trước và khóa chỉnh sửa
    capNhatBangLuongThangTruoc(thangHienTai.minusMonths(1), tongDoanhThuThangTruoc);

    List<NhanVien> danhSachNhanVien = nhanVienRepository.findAll()
        .stream()
        .filter(nv -> !"NV000".equals(nv.getMaNhanVien()))
        .filter(nv -> !"Nghỉ việc".equalsIgnoreCase(nv.getTrangThai()))
        .toList();

    int soLuongTaoMoi = 0;

    for (NhanVien nv : danhSachNhanVien) {
        String maBangLuong = String.format("BL%04d%02d-%s",
                thangHienTai.getYear(),
                thangHienTai.getMonthValue(),
                nv.getMaNhanVien());

        if (bangLuongRepository.existsById(maBangLuong)) {
            continue; // đã có bảng lương tháng này → bỏ qua
        }

        BangLuong bangLuong = new BangLuong();
        bangLuong.setMaBangLuong(maBangLuong);
        bangLuong.setNhanVien(nv);
        bangLuong.setThang(thangHienTai);

        // Các giá trị mặc định
        bangLuong.setNgayCong(28);
        bangLuong.setNghiCoCong(0);
        bangLuong.setNghiKhongCong(0);
        bangLuong.setGioLamThem(0);
        bangLuong.setDonDaTao(0);
        bangLuong.setThuongDoanhThu(0);
        bangLuong.setLuongThucNhan(
            tinhLuongThucNhan(nv.getLoaiNhanVien(), nv.getMucLuong(), 28, 0, 0, 0)
        );
        bangLuong.setGhiChu("");
        bangLuong.setDuocPhepChinhSua("1");

        bangLuongRepository.save(bangLuong);
        soLuongTaoMoi++; // tăng biến đếm
    }

    return soLuongTaoMoi; // 0 nếu không tạo được gì, >0 nếu có tạo
}


    @Override
    public List<BangLuongDTO> layTatCaBangLuongThangNay() {
        LocalDate currentMonth = YearMonth.now().atDay(1);
        List<BangLuong> bangLuongs = bangLuongRepository.getBangLuongByThang(currentMonth);
        return bangLuongs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BangLuongDTO timBangLuongTheoMa(String ma) {
        Optional<BangLuong> optional = bangLuongRepository.findById(ma);
        return optional.map(this::convertToDTO).orElse(null);
    }

    @Override
    public BangLuongDTO suaBangLuong(String ma, BangLuongDTO dto) {
        Optional<BangLuong> optional = bangLuongRepository.findById(ma);
        if (optional.isPresent()) {
            BangLuong existing = optional.get();
            existing.setNgayCong(dto.getNgayCong());
            existing.setNghiCoCong(dto.getNghiCoCong());
            existing.setNghiKhongCong(dto.getNghiKhongCong());
            existing.setGioLamThem(dto.getGioLamThem());
            existing.setDonDaTao(dto.getDonDaTao());
            existing.setThang(dto.getThang());
            existing.setThuongDoanhThu(dto.getThuongDoanhThu());
            existing.setGhiChu(dto.getGhiChu());

            //tinh lai luong thuc nhan
            existing.setLuongThucNhan(tinhLuongThucNhan(existing.getNhanVien().getLoaiNhanVien(), existing.getNhanVien().getMucLuong(),
                    existing.getNgayCong(), existing.getNghiKhongCong(), existing.getGioLamThem(), 0));
            BangLuong updated = bangLuongRepository.save(existing);
            return convertToDTO(updated);
        }
        return null;
    }

    @Override
    public List<BangLuongDTO> layBangLuongTheoThang(YearMonth thang) {
        // Dòng này bây giờ sẽ hoạt động vì phương thức đã được khai báo trong repository
        LocalDate month = thang.atDay(1);
        return bangLuongRepository.getBangLuongByThang(month)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ------------------ HÀM CHUYỂN ĐỔI ------------------

    private BangLuongDTO convertToDTO(BangLuong entity) {
        BangLuongDTO dto = new BangLuongDTO();
        dto.setMaBangLuong(entity.getMaBangLuong());
        dto.setHoTen(entity.getNhanVien().getHoTen());
        dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
        dto.setLoaiNhanVien(entity.getNhanVien().getLoaiNhanVien());
        dto.setViTri(entity.getNhanVien().getViTri());
        dto.setMucLuong(entity.getNhanVien().getMucLuong());
        dto.setThang(entity.getThang());
        dto.setNgayCong(entity.getNgayCong());
        dto.setNghiCoCong(entity.getNghiCoCong());
        dto.setNghiKhongCong(entity.getNghiKhongCong());
        dto.setGioLamThem(entity.getGioLamThem());
        dto.setDonDaTao(entity.getDonDaTao());
        dto.setThuongDoanhThu(entity.getThuongDoanhThu());
        dto.setLuongThucNhan(entity.getLuongThucNhan());
        dto.setGhiChu(entity.getGhiChu());
        dto.setDuocPhepChinhSua(entity.getDuocPhepChinhSua());
        return dto;
    }

    private int getSoDonDaTao(String maNhanVien, LocalDate thang) {
        return donHangRepository.countByNhanVienMaNhanVienAndThoiGianDatHangMonthAndThoiGianDatHangYear(maNhanVien, thang.getMonthValue(), thang.getYear());
    }

    private int getTongDoanhThuThangTruoc(){
        YearMonth thangHienTai = YearMonth.now();
        YearMonth thangTruoc = thangHienTai.minusMonths(1);
        List<DoanhThu> doanhThus = doanhThuRepository.findByThangAndNam(thangTruoc.getMonthValue(), thangTruoc.getYear());
        return doanhThus.stream().mapToInt(DoanhThu::getTongDoanhThu).sum();
    }

    private int tinhThuongDoanhThu(int totalRevenue, double targetRevenue) {
        if (totalRevenue >= targetRevenue * 1.5) {
            return 1000000;
        } else if (totalRevenue >= targetRevenue * 1.25) {
            return 500000;
        } else if (totalRevenue >= targetRevenue * 1.1) {
            return 200000;
        }
        return 0;
    }

    private int tinhLuongThucNhan(String loaiNhanVien, int mucLuong, int soNgayCong, int soNgayNghiKhongCong, int soGioLamThem, int tongDoanhThu) {
        int soGioLamMotNgay = "Full-time".equals(loaiNhanVien) ? 8 : 5;
        int soNgayLamThem = soGioLamThem / soGioLamMotNgay;
        int soGioLamThemDu = soGioLamThem % soGioLamMotNgay;
        int soTienLamThem = (int) (soGioLamThemDu * mucLuong);
        soNgayCong += soNgayLamThem;
        soNgayCong -= soNgayNghiKhongCong;
        int thuongDoanhThu = tinhThuongDoanhThu(tongDoanhThu, 50000000);
        return mucLuong * soNgayCong * soGioLamMotNgay + thuongDoanhThu + soTienLamThem;
    }

    private void capNhatBangLuongThangTruoc(LocalDate thangTruoc, int tongDoanhThu){
        List<BangLuong> bangLuongs = bangLuongRepository.getBangLuongByThang(thangTruoc);
        for (BangLuong bangLuong : bangLuongs) {
            int thuongDoanhThu = tinhThuongDoanhThu(tongDoanhThu, 50000000);
            bangLuong.setThuongDoanhThu(thuongDoanhThu);
            bangLuong.setDonDaTao(getSoDonDaTao(bangLuong.getNhanVien().getMaNhanVien(), thangTruoc));

            bangLuong.setLuongThucNhan(tinhLuongThucNhan(bangLuong.getNhanVien().getLoaiNhanVien(), bangLuong.getNhanVien().getMucLuong(),
                    bangLuong.getNgayCong(), bangLuong.getNghiKhongCong(), bangLuong.getGioLamThem(), tongDoanhThu));
            bangLuong.setDuocPhepChinhSua("0");
            bangLuongRepository.save(bangLuong);
        }
    }
}
