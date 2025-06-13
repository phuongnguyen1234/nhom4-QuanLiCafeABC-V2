package com.backend.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
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
import com.backend.utils.DTOConversion;

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

        List <NhanVien> danhSachNhanVien = nhanVienRepository.findAll()
            .stream()
            .filter(nv -> !"NV000".equals(nv.getMaNhanVien()))
            .filter(nv -> !"Nghỉ việc".equalsIgnoreCase(nv.getTrangThai()))
            .toList();

        int soLuongTaoMoi = 0;

        for (NhanVien nv: danhSachNhanVien) {
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

            // Các giá trị mặc định và ngày công chuẩn
            int ngayCongChuan;
            if ("Full-time".equalsIgnoreCase(nv.getLoaiNhanVien())) {
                ngayCongChuan = 28;
            } else { // Part-time
                ngayCongChuan = 20;
            }
            bangLuong.setNgayCong(ngayCongChuan);
            bangLuong.setNghiCoCong(0);
            bangLuong.setNghiKhongCong(0);
            bangLuong.setGioLamThem(0);
            bangLuong.setDonDaTao(0);
            bangLuong.setThuongDoanhThu(0);
            bangLuong.setLuongThucNhan(
                tinhLuongThucNhan(nv.getLoaiNhanVien(), nv.getMucLuong(), ngayCongChuan, 0, 0, 0)
            );
            bangLuong.setGhiChu("");
            bangLuong.setDuocPhepChinhSua("1");

            bangLuongRepository.save(bangLuong);
            soLuongTaoMoi++; // tăng biến đếm
        }

        return soLuongTaoMoi; // 0 nếu không tạo được gì, >0 nếu có tạo
    }

    @Override
    public List <BangLuongDTO> layTatCaBangLuongThangNay() {
        LocalDate currentMonth = YearMonth.now().atDay(1);
        List < BangLuong > bangLuongs = bangLuongRepository.getBangLuongByThang(currentMonth);
        return bangLuongs.stream()
            .map(DTOConversion::toBangLuongDTO)
            .collect(Collectors.toList());
    }

    @Override
    public BangLuongDTO timBangLuongTheoMa(String ma) {
        Optional <BangLuong> optional = bangLuongRepository.findById(ma);
        return optional.map(DTOConversion::toBangLuongDTO).orElse(null);
    }

    @Override
    public BangLuongDTO suaBangLuong(String ma, BangLuongDTO dto) {
        return bangLuongRepository.findById(ma)
            .map(existing -> {
                existing.setNgayCong(dto.getNgayCong());
                existing.setNghiCoCong(dto.getNghiCoCong());
                existing.setNghiKhongCong(dto.getNghiKhongCong());
                existing.setGioLamThem(dto.getGioLamThem());
                existing.setDonDaTao(dto.getDonDaTao());
                existing.setThang(dto.getThang());
                existing.setThuongDoanhThu(dto.getThuongDoanhThu());
                existing.setGhiChu(dto.getGhiChu());

                // Tính lương cơ bản + làm thêm (thưởng doanh thu trong công thức này là 0 cho tháng hiện tại)
                int luongCoBanVaLamThem = tinhLuongThucNhan(
                    existing.getNhanVien().getLoaiNhanVien(),
                    existing.getNhanVien().getMucLuong(),
                    existing.getNgayCong(),
                    existing.getNghiKhongCong(),
                    existing.getGioLamThem(),
                    0 // Thưởng doanh thu = 0 khi sửa bảng lương tháng hiện tại
                );
                // Cộng khoản thưởng người dùng đã nhập (lưu trong existing.getThuongDoanhThu())
                existing.setLuongThucNhan(luongCoBanVaLamThem + existing.getThuongDoanhThu());

                return bangLuongRepository.save(existing);
            })
            .map(DTOConversion::toBangLuongDTO)
            .orElse(null);
    }

    @Override
    public List <BangLuongDTO> layBangLuongTheoThang(YearMonth thang) {
        // Dòng này bây giờ sẽ hoạt động vì phương thức đã được khai báo trong repository
        LocalDate month = thang.atDay(1);
        return bangLuongRepository.getBangLuongByThang(month)
            .stream()
            .map(DTOConversion::toBangLuongDTO)
            .collect(Collectors.toList());
    }

    private int getSoDonDaTao(String maNhanVien, LocalDate thang) {
        return donHangRepository.countByNhanVienMaNhanVienAndThoiGianDatHangMonthAndThoiGianDatHangYear(maNhanVien, thang.getMonthValue(), thang.getYear());
    }

    private int getTongDoanhThuThangTruoc() {
        YearMonth thangHienTai = YearMonth.now();
        YearMonth thangTruoc = thangHienTai.minusMonths(1);
        List < DoanhThu > doanhThus = doanhThuRepository.findByThangAndNam(thangTruoc.getMonthValue(), thangTruoc.getYear());
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

    private int tinhLuongThucNhan(String loaiNhanVien, int mucLuong, int soNgayCong,
                               int soNgayNghiKhongCong, int soGioLamThem, int tongDoanhThu) {
        boolean isFullTime = "Full-time".equals(loaiNhanVien);
        int soGioLamMotNgay = isFullTime ? 8 : 5;

        // Tính lương mỗi giờ (với Full-time: mucLuong là theo ngày → chia cho 8)
        // Với Part-time: mucLuong là lương theo giờ
        int mucLuongMotGio = isFullTime ? mucLuong / soGioLamMotNgay : mucLuong;

        int ngayCongThucTe = soNgayCong - soNgayNghiKhongCong;
        int luongChinh = mucLuongMotGio * ngayCongThucTe * soGioLamMotNgay;

        int tienLamThem = soGioLamThem * mucLuongMotGio;
        int thuongDoanhThu = tinhThuongDoanhThu(tongDoanhThu, 50000000);

        return luongChinh + tienLamThem + thuongDoanhThu;
    }


    private void capNhatBangLuongThangTruoc(LocalDate thangTruoc, int tongDoanhThu) {
        List <BangLuong> bangLuongs = bangLuongRepository.getBangLuongByThang(thangTruoc);
        for (BangLuong bangLuong: bangLuongs) {
            int thuongDoanhThu = tinhThuongDoanhThu(tongDoanhThu, 50000000);
            bangLuong.setThuongDoanhThu(thuongDoanhThu);
            bangLuong.setDonDaTao(getSoDonDaTao(bangLuong.getNhanVien().getMaNhanVien(), thangTruoc));

            bangLuong.setLuongThucNhan(tinhLuongThucNhan(bangLuong.getNhanVien().getLoaiNhanVien(), bangLuong.getNhanVien().getMucLuong(),
                bangLuong.getNgayCong(), bangLuong.getNghiKhongCong(), bangLuong.getGioLamThem(), tongDoanhThu));
            bangLuong.setDuocPhepChinhSua("0");
            bangLuongRepository.save(bangLuong);
        }
    }

    /**
     * Tự động tạo bảng lương cho tất cả nhân viên vào 00:05 ngày đầu tiên của mỗi tháng.
     * Cron expression: "0 5 0 1 * ?" (giây phút giờ ngày-trong-tháng tháng ngày-trong-tuần)
     * Chạy sau khi tác vụ tổng hợp doanh thu (nếu có, ví dụ lúc 00:01) đã hoàn thành.
     */
    @Scheduled(cron = "0 5 0 1 * ?")
    @Transactional
    public void tuDongTaoBangLuongDauThang() {
        try {
            int soLuongTaoMoi = taoBangLuongThangHienTai();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int tinhToanLuongThucNhanChoCapNhat(String loaiNhanVien, int mucLuongMoi,
                                            int ngayCongHienTai, int nghiKhongCongHienTai,
                                            int gioLamThemHienTai, int thuongDoanhThuDaNhap) {
        // Gọi lại private method tinhLuongThucNhan với tham số tongDoanhThu (dùng để tính thưởng theo target) là 0
        // vì thưởng theo target (nếu có) đã được tính và lưu trong thuongDoanhThuDaNhap hoặc sẽ được tính vào cuối tháng.
        // Ở đây, chúng ta chỉ tính lại lương cơ bản + làm thêm dựa trên mức lương mới.
        int luongCoBanVaLamThem = tinhLuongThucNhan(
                loaiNhanVien,
                mucLuongMoi,
                ngayCongHienTai,
                nghiKhongCongHienTai,
                gioLamThemHienTai,
                0 // tongDoanhThu (để tính thưởng theo công thức target) là 0
        );
        // Cộng với khoản thưởng đã được nhập thủ công (nếu có)
        return luongCoBanVaLamThem + thuongDoanhThuDaNhap;
    }
}