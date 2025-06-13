package com.backend.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList; // Thêm import này
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dto.DonHangDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.model.BangLuong;
import com.backend.model.ChiTietDonHang;
import com.backend.model.DonHang; // Thêm import BangLuong
import com.backend.model.Mon;
import com.backend.model.NhanVien;
import com.backend.repository.BangLuongRepository; // Thêm import BangLuongRepository
import com.backend.repository.ChiTietDonHangRepository;
import com.backend.repository.DonHangRepository;
import com.backend.repository.MonRepository;
import com.backend.repository.NhanVienRepository;
import com.backend.service.DonHangService;
import com.backend.utils.DTOConversion;

@Service
public class DonHangServiceImpl implements DonHangService {
    private static final String ADMIN_EMPLOYEE_CODE = "NV000";
    private static final String EDITABLE_STATUS = "1";

    private final DonHangRepository donHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final MonRepository monRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final BangLuongRepository bangLuongRepository; // Inject BangLuongRepository

    public DonHangServiceImpl(DonHangRepository donHangRepository,
        NhanVienRepository nhanVienRepository, MonRepository monRepository,
        ChiTietDonHangRepository chiTietDonHangRepository,
        BangLuongRepository bangLuongRepository) { // Thêm BangLuongRepository vào constructor
        this.donHangRepository = donHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.monRepository = monRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.bangLuongRepository = bangLuongRepository; // Gán BangLuongRepository
    }

    @Override
    public DonHangDTO createDonHang(DonHangDTO donHangDTO) { // Thay đổi kiểu trả về
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
        donHang.setChiTietDonHang(new ArrayList<>()); // Khởi tạo danh sách chi tiết đơn hàng

        // Tối ưu: Lấy tất cả các Mon cần thiết trong một query
        List<String> maMonList = donHangDTO.getDanhSachMonTrongDon().stream()
                                          .map(MonTrongDonDTO::getMaMon)
                                          .collect(Collectors.toList());
        Map<String, Mon> monMap = monRepository.findAllById(maMonList).stream()
                                               .collect(Collectors.toMap(Mon::getMaMon, mon -> mon));

        // Lặp danh sách món
        for (MonTrongDonDTO monDTO : donHangDTO.getDanhSachMonTrongDon()) {
            Mon mon = monMap.get(monDTO.getMaMon());
            if (mon == null) throw new RuntimeException("Không tìm thấy món: " + monDTO.getMaMon());

            int soLuong = monDTO.getSoLuong();
            int donGia = monDTO.getDonGia();
            int tamTinh = soLuong * donGia;

            // Tạo chi tiết đơn hàng
            ChiTietDonHang chiTiet = new ChiTietDonHang();
            // MaChiTietDonHang sẽ được tự động tạo bởi JPA khi persist
            // Không cần setId cho MaChiTietDonHang nữa
            chiTiet.setDonHang(donHang);
            chiTiet.setMon(mon);
            chiTiet.setTenMon(monDTO.getTenMon());
            chiTiet.setDonGia(donGia);
            chiTiet.setSoLuong(soLuong);
            chiTiet.setYeuCauKhac(monDTO.getYeuCauKhac());
            chiTiet.setTamTinh(tamTinh);
            chiTietDonHangRepository.save(chiTiet);
            donHang.getChiTietDonHang().add(chiTiet); // Thêm chi tiết vào danh sách của đơn hàng

            tongTien += tamTinh;
        }

        donHang.setTongTien(tongTien);

        // Lưu lại tổng tiền sau khi tính
        donHangRepository.save(donHang);

        // Cập nhật số đơn đã tạo cho nhân viên trong bảng lương tháng hiện tại
        capNhatSoDonDaTaoChoNhanVien(donHang.getNhanVien().getMaNhanVien(), donHang.getThoiGianDatHang());

        // Chuyển đổi DonHang entity đã hoàn chỉnh thành DonHangDTO trước khi trả về
        return DTOConversion.toDonHangDTO(donHang);
    }

    private void capNhatSoDonDaTaoChoNhanVien(String maNhanVien, LocalDateTime thoiGianDatHang) {
        try {
            // Nếu nhân viên là admin (NV000), không cập nhật bảng lương
            if (ADMIN_EMPLOYEE_CODE.equals(maNhanVien)) {
                System.out.println("INFO: Nhân viên " + maNhanVien + " là admin, không cập nhật bảng lương.");
                return;
            }

            int nam = thoiGianDatHang.getYear();
            int thang = thoiGianDatHang.getMonthValue();

            // Format mã bảng lương phải khớp với format khi tạo bảng lương
            String maBangLuong = String.format("BL%04d%02d-%s", nam, thang, maNhanVien);

            Optional<BangLuong> bangLuongOpt = bangLuongRepository.findById(maBangLuong);

            if (bangLuongOpt.isPresent()) {
                BangLuong bangLuong = bangLuongOpt.get();
                // Chỉ cập nhật nếu bảng lương còn được phép chỉnh sửa (ví dụ: "1" là được phép)
                if (EDITABLE_STATUS.equals(bangLuong.getDuocPhepChinhSua())) {
                    bangLuong.setDonDaTao(bangLuong.getDonDaTao() + 1);
                    bangLuongRepository.save(bangLuong);
                    System.out.println("INFO: Đã cập nhật số đơn đã tạo cho nhân viên " + maNhanVien + " trong bảng lương " + maBangLuong);
                } else {
                    System.out.println("INFO: Bảng lương " + maBangLuong + " của nhân viên " + maNhanVien + " đã khóa, không cập nhật số đơn đã tạo.");
                }
            } else {
                System.out.println("WARN: Không tìm thấy bảng lương " + maBangLuong + " cho nhân viên " + maNhanVien + " để cập nhật số đơn đã tạo. Đơn hàng vẫn được tạo thành công.");
            }
        } catch (Exception e) {
            System.err.println("ERROR: Lỗi khi cập nhật số đơn đã tạo cho nhân viên " + maNhanVien + ": " + e.getMessage());
            e.printStackTrace(); // Log stack trace để debug
            // Không ném lại lỗi để không làm rollback toàn bộ transaction tạo đơn hàng
            // Việc cập nhật số đơn là một tác vụ phụ, nếu lỗi không nên ảnh hưởng đến việc tạo đơn chính.
        }
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

            List<MonTrongDonDTO> monTrongDonDTOList = donHang.getChiTietDonHang().stream().map(DTOConversion::toMonTrongDonDTO).toList();
            donHangDTO.setDanhSachMonTrongDon(monTrongDonDTOList);
            return donHangDTO;
        }).toList();
    }


    // Lấy đơn hàng theo ID
    @Override
    @Transactional(readOnly = true) // Đảm bảo session Hibernate vẫn mở để tải lazy collection
    public Optional<DonHangDTO> getDonHangById(String maDonHang) {
        return donHangRepository.findById(maDonHang).map(DTOConversion::toDonHangDTO);
    }

    @Override
    public List<DonHangDTO> filterByNgay(LocalDate ngay) {
        LocalDateTime startOfDay = ngay.atStartOfDay();
        LocalDateTime endOfDay = ngay.atTime(23, 59, 59, 999999999);

        return donHangRepository.findByThoiGianDatHangBetween(startOfDay, endOfDay)
                .stream()
                .map(DTOConversion::toDonHangDTO) 
                .toList();
    }
}
