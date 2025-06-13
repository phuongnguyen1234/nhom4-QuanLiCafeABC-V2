package com.backend.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.dto.MonDTO;
import com.backend.dto.NhanVienDTO;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
import com.backend.model.NhanVien;
import com.backend.repository.DanhMucRepository;
import com.backend.repository.MonRepository;
import com.backend.repository.NhanVienRepository;
import com.backend.service.MonService;
import com.backend.service.NhanVienService;
import com.backend.utils.DTOConversion;

@Service
public class MonServiceImpl implements MonService {
    private final MonRepository monRepository;
    private final DanhMucRepository danhMucRepository;
    private final NhanVienRepository nhanVienRepository;
    private final NhanVienService nhanVienService;

    private static final String VI_TRI_THU_NGAN = "Thu ngân";
    private static final String TRANG_THAI_ONLINE = "Online";
    public static final String ERROR_MESSAGE_OTHER_CASHIER_ONLINE = "Không thể thực hiện thao tác. Có nhân viên thu ngân khác đang trực tuyến.";

    public MonServiceImpl(MonRepository monRepository, DanhMucRepository danhMucRepository, NhanVienRepository nhanVienRepository, NhanVienService nhanVienService) {
        this.monRepository = monRepository;
        this.danhMucRepository = danhMucRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.nhanVienService = nhanVienService;
    }

    @Override
    public Map<Integer, List<Mon>> getAllMonGroupedByDanhMuc() {
        List<Mon> allMon = monRepository.findAll();
        return allMon.stream().collect(Collectors.groupingBy(mon -> mon.getDanhMuc().getMaDanhMuc()));
    }

    @Override
    public List<Mon> getAllMon() {
        return monRepository.findAll();
    }

    //sua lai theo DTO moi
    @Override
    public Mon createMon(MonDTO dto) {
        // Kiểm tra xem có thu ngân nào khác đang online không
        kiemTraCoThuNganKhacDangOnline();

        Mon mon = DTOConversion.toMon(dto);

        // Tạo mã tự động nếu cần
        String newMaMon = generateNewMaMon();
        mon.setMaMon(newMaMon);

        // Tìm đối tượng danh mục từ mã danh mục
        DanhMuc danhMuc = danhMucRepository.findById(dto.getMaDanhMuc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + dto.getMaDanhMuc()));
        mon.setDanhMuc(danhMuc);

        return monRepository.save(mon);
    }

    @Override
    public Mon updateMon(MonDTO monUpdate) {
        // Kiểm tra xem có thu ngân nào khác đang online không
        kiemTraCoThuNganKhacDangOnline();

        Mon mon = DTOConversion.toMon(monUpdate);

        // Tìm đối tượng danh mục từ mã danh mục
        DanhMuc danhMuc = danhMucRepository.findById(monUpdate.getMaDanhMuc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + monUpdate.getMaDanhMuc()));
        mon.setDanhMuc(danhMuc);

        return monRepository.save(mon);
    }

    private String generateNewMaMon() {
        List<Mon> allMon = monRepository.findAll();

        int max = allMon.stream()
                .map(Mon::getMaMon)
                .filter(ma -> ma != null && ma.matches("M\\d{3}"))
                .map(ma -> Integer.parseInt(ma.substring(1)))
                .max(Comparator.naturalOrder())
                .orElse(0);

        int next = max + 1;
        return String.format("M%03d", next); // M001, M002, ...
    }

    private void kiemTraCoThuNganKhacDangOnline() {
        Optional<NhanVienDTO> currentUserOpt = nhanVienService.getCurrentLoggedInNhanVienDTO();
        String currentMaNhanVien = currentUserOpt.map(NhanVienDTO::getMaNhanVien).orElse(null);

        List<NhanVien> onlineCashiers = nhanVienRepository.findByViTriAndTrangThaiHoatDong(VI_TRI_THU_NGAN, TRANG_THAI_ONLINE);

        boolean otherCashierOnline;
        if (currentMaNhanVien != null) {
            // Người dùng hiện tại đã đăng nhập
            otherCashierOnline = onlineCashiers.stream()
                                    .anyMatch(nv -> !nv.getMaNhanVien().equals(currentMaNhanVien));
        } else {
            // Không có người dùng nào đăng nhập (trường hợp này ít xảy ra đối với các tác vụ cần xác thực)
            // Nếu có bất kỳ thu ngân nào online, coi như có "thu ngân khác" đang online.
            otherCashierOnline = !onlineCashiers.isEmpty();
        }

        if (otherCashierOnline) {
            throw new RuntimeException(ERROR_MESSAGE_OTHER_CASHIER_ONLINE);
        }
    }

}
