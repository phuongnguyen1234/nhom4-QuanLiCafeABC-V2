package com.backend.service.impl;

<<<<<<< HEAD
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
=======
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
>>>>>>> origin/manh-hoadon
import org.springframework.stereotype.Service;

import com.backend.dto.DonHangDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.model.ChiTietDonHang;
import com.backend.model.ChiTietDonHangId;
import com.backend.model.DonHang;
import com.backend.model.Mon;
import com.backend.model.NhanVien;
import com.backend.repository.ChiTietDonHangRepository;
import com.backend.repository.DonHangRepository;
import com.backend.repository.MonRepository;
import com.backend.repository.NhanVienRepository;
import com.backend.service.DonHangService;

@Service
public class DonHangServiceImpl implements DonHangService {
    private final DonHangRepository donHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final MonRepository monRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    public DonHangServiceImpl(DonHangRepository donHangRepository,
        NhanVienRepository nhanVienRepository, MonRepository monRepository,
        ChiTietDonHangRepository chiTietDonHangRepository) {
        this.donHangRepository = donHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.monRepository = monRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
    }

    @Override
    public DonHang createDonHang(DonHangDTO donHangDTO) {
        DonHang donHang = new DonHang();

        // Tạo mã đơn hàng
        String maDonHang = donHangDTO.getMaDonHang();
        if (maDonHang == null || maDonHang.trim().isEmpty()) {
            String maxMaDonHang = donHangRepository.findMaxMaDonHang();
            maDonHang = generateNextCode(maxMaDonHang, "DH");
        }
        donHang.setMaDonHang(maDonHang);

        // Gán nhân viên
        NhanVien nhanVien = nhanVienRepository.findById(donHangDTO.getMaNhanVien())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        donHang.setNhanVien(nhanVien);
        donHang.setHoTen(donHangDTO.getHoTen());

        // Thời gian đặt hàng
        donHang.setThoiGianDatHang(LocalDateTime.now());

        // Tạm tính tổng tiền
        int tongTien = 0;

<<<<<<< HEAD
        // ⚠️ LƯU ĐƠN HÀNG TRƯỚC để đảm bảo có trong DB
=======
        //LƯU ĐƠN HÀNG TRƯỚC để đảm bảo có trong DB
>>>>>>> origin/manh-hoadon
        donHang = donHangRepository.save(donHang);
        // Lặp danh sách món
        for (MonTrongDonDTO monDTO : donHangDTO.getDanhSachMonTrongDon()) {
            Mon mon = monRepository.findById(monDTO.getMaMon())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món: " + monDTO.getMaMon()));

            int soLuong = monDTO.getSoLuong();
            int donGia = monDTO.getDonGia();
            int tamTinh = soLuong * donGia;

            // Tạo chi tiết đơn hàng
            ChiTietDonHang chiTiet = new ChiTietDonHang();
            chiTiet.setId(new ChiTietDonHangId(donHang.getMaDonHang(), mon.getMaMon()));
            chiTiet.setDonHang(donHang);
            chiTiet.setMon(mon);
            chiTiet.setTenMon(monDTO.getTenMon());
            chiTiet.setDonGia(donGia);
            chiTiet.setSoLuong(soLuong);
            chiTiet.setYeuCauKhac(monDTO.getYeuCauKhac());
            chiTiet.setTamTinh(tamTinh);
            chiTietDonHangRepository.save(chiTiet);

            tongTien += tamTinh;
        }

        donHang.setTongTien(tongTien);

        // Lưu lại tổng tiền sau khi tính
        donHangRepository.save(donHang);

        return donHang;
    }


    private String generateNextCode(String currentMaxCode, String prefix) {
        if (currentMaxCode == null) {
            return prefix + "0001";
        }

        int number = Integer.parseInt(currentMaxCode.substring(prefix.length()));
        number++;
        return String.format("%s%04d", prefix, number);
    }
    @Override
<<<<<<< HEAD
    public int getTongDoanhThuHomNay() {
        return donHangRepository.tinhDoanhThuHomNay();
    }
    @Override
    public List<NhanVien> getTop3NhanVienTheoThang() {
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();
        if (donHangRepository.top3MaNhanVienTheoThang(thang, nam).isEmpty()) return null;
        return nhanVienRepository.findAllById(donHangRepository.top3MaNhanVienTheoThang(thang, nam));
    }
    @Override
    public List<String> getTop5MonBanChayTheoThang() {
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();
        if (donHangRepository.top5MonTheoThangNam(thang, nam).isEmpty()) return null;
        return donHangRepository.top5MonTheoThangNam(thang, nam);
    }
=======
    public List<DonHang> getAllDonHang() {
        return donHangRepository.findAll();
    }

    // Lấy đơn hàng theo ID
    @Override
    public Optional<DonHang> getDonHangById(String maDonHang) {
        return donHangRepository.findById(maDonHang);
    }

    // Xóa đơn hàng
    @Override
    public void deleteDonHang(String maDonHang) {
        donHangRepository.deleteById(maDonHang);
    }

    // Phân trang
    @Override
    public Page<DonHang> getAllDonHangPaginated(Pageable pageable) {
        return donHangRepository.findAll(pageable);
    }

    // Lọc theo thời gian
    //@Override
    //public List<DonHang> filterByDate(LocalDateTime start, LocalDateTime end) {
    //    return donHangRepository.findByThoiGianDatHangBetween(start, end);
    //}

    // Tìm kiếm theo mã đơn hoặc tên nhân viên
    //@Override
    //public List<DonHang> searchDonHang(String keyword) {
    //    return donHangRepository.findByMaDonHangContainingOrNhanVien_HoTenContaining(keyword, keyword);
    //}
>>>>>>> origin/manh-hoadon
}


