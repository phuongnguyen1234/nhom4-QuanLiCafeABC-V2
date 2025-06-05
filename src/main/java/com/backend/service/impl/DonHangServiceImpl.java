package com.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // ⚠️ LƯU ĐƠN HÀNG TRƯỚC để đảm bảo có trong DB
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

    @Override
    public List<DonHangDTO> getAllDonHang() {
        return donHangRepository.findAll().stream().map(donHang -> {
            DonHangDTO donHangDTO = new DonHangDTO();
            donHangDTO.setMaDonHang(donHang.getMaDonHang());
            donHangDTO.setMaNhanVien(donHang.getNhanVien().getMaNhanVien());
            donHangDTO.setHoTen(donHang.getHoTen()); // Hoặc donHang.getNhanVien().getHoTen() tùy theo logic
            donHangDTO.setThoiGianDatHang(donHang.getThoiGianDatHang());
            donHangDTO.setTongTien(donHang.getTongTien());

            List<MonTrongDonDTO> monTrongDonDTOList = donHang.getChiTietDonHang().stream().map(chiTiet -> {
                MonTrongDonDTO monDTO = new MonTrongDonDTO();
                monDTO.setMaMon(chiTiet.getMon().getMaMon());
                monDTO.setTenMon(chiTiet.getTenMon());
                monDTO.setSoLuong(chiTiet.getSoLuong());
                monDTO.setDonGia(chiTiet.getDonGia());
                monDTO.setYeuCauKhac(chiTiet.getYeuCauKhac());
                monDTO.setTamTinh(chiTiet.getTamTinh());
                return monDTO;
            }).toList();
            donHangDTO.setDanhSachMonTrongDon(monTrongDonDTOList);
            return donHangDTO;
        }).toList();
    }


    // Lấy đơn hàng theo ID
    @Override
    @Transactional(readOnly = true) // Đảm bảo session Hibernate vẫn mở để tải lazy collection
    public Optional<DonHangDTO> getDonHangById(String maDonHang) {
        return donHangRepository.findById(maDonHang).map(this::convertToDonHangDTO);
    }

    @Override
    public List<DonHangDTO> filterByNgay(LocalDate ngay) {
        LocalDateTime startOfDay = ngay.atStartOfDay();
        LocalDateTime endOfDay = ngay.atTime(23, 59, 59, 999999999);

        return donHangRepository.findByThoiGianDatHangBetween(startOfDay, endOfDay)
                .stream()
                .map(this::convertToDonHangDTO) 
                .toList();
    }

    // Phương thức helper để chuyển đổi DonHang sang DonHangDTO
    private DonHangDTO convertToDonHangDTO(DonHang donHang) {
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
