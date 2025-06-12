package com.backend.service.impl;

import com.backend.dto.NhanVienDTO;
import com.backend.model.NhanVien;
import com.backend.repository.NhanVienRepository;
import com.backend.service.EmailService;
import com.backend.service.NhanVienService;

import com.backend.utils.DTOConversion; // Import DTOConversion
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class NhanVienServiceImpl implements NhanVienService {
    
    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final Cache otpCache;

    // Lớp nội bộ để lưu trữ OTP và thời gian hết hạn trong cache
    private static class OtpData {
        final String otp;
        final LocalDateTime expiryTime;

        OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    public NhanVienServiceImpl(NhanVienRepository nhanVienRepository,
                               PasswordEncoder passwordEncoder,
                               EmailService emailService,
                               CacheManager cacheManager) {
        this.nhanVienRepository = nhanVienRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpCache = cacheManager.getCache("otpCache"); // Tên cache "otpCache"
    }

    // Phương thức cập nhật trạng thái hoạt động của nhân viên
    @Override
    @Transactional // Đảm bảo thao tác được thực hiện trong một transaction
    public void capNhatTrangThaiHoatDong(String email, String trangThai) {
        NhanVien nhanVien = nhanVienRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhân viên với email: " + email));
        nhanVien.setTrangThaiHoatDong(trangThai);
        nhanVienRepository.save(nhanVien);
    }

    // Triển khai các phương thức của NhanVienService tại đây
    
     @Override
    @Transactional(readOnly = true)
    public Optional<NhanVienDTO> getNhanVienByMaNhanVien(String maNhanVien) {
        return nhanVienRepository.findById(maNhanVien)
                .map(DTOConversion::toNhanVienDTO);
    }

    @Override
    @Transactional
    public NhanVienDTO createNhanVien(NhanVienDTO nhanVienDTO) {
        NhanVien nhanVien = DTOConversion.toNhanVien(nhanVienDTO);

        // Tự động tạo mã nhân viên nếu chưa có
        if (nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien().trim().isEmpty()) {
            nhanVien.setMaNhanVien(generateNewMaNhanVien());
        }

        // Mã hóa mật khẩu
        if (nhanVienDTO.getMatKhau() != null && !nhanVienDTO.getMatKhau().isEmpty()) {
            nhanVien.setMatKhau(passwordEncoder.encode(nhanVienDTO.getMatKhau()));
        }
        // Mặc định trạng thái hoạt động khi tạo mới ( "Offline" và "Đi làm")
        if (nhanVien.getTrangThaiHoatDong() == null) {
            nhanVien.setTrangThaiHoatDong("Offline"); 
        }
         // Mặc định trạng thái làm việc (ví dụ: "Đi làm")
        if (nhanVien.getTrangThai() == null) {
            nhanVien.setTrangThai("Đi làm");
        }

        NhanVien savedNhanVien = nhanVienRepository.save(nhanVien);
        return DTOConversion.toNhanVienDTO(savedNhanVien);
    }

    @Override
    @Transactional
    public Optional<NhanVienDTO> updateNhanVien(String maNhanVien, NhanVienDTO nhanVienDTO) {
        return nhanVienRepository.findById(maNhanVien)
                .map(existingNhanVien -> {
                    // Cập nhật các trường từ DTO, trừ mật khẩu và mã nhân viên
                    existingNhanVien.setHoTen(nhanVienDTO.getTenNhanVien());
                    existingNhanVien.setAnhChanDung(nhanVienDTO.getAnhChanDung());
                    existingNhanVien.setGioiTinh(nhanVienDTO.getGioiTinh());
                    existingNhanVien.setNgaySinh(nhanVienDTO.getNgaySinh());
                    existingNhanVien.setQueQuan(nhanVienDTO.getQueQuan());
                    existingNhanVien.setDiaChi(nhanVienDTO.getDiaChi());
                    existingNhanVien.setSoDienThoai(nhanVienDTO.getSoDienThoai());
                    existingNhanVien.setLoaiNhanVien(nhanVienDTO.getLoaiNhanVien());
                    existingNhanVien.setViTri(nhanVienDTO.getViTri()); // ViTri sẽ là role
                    existingNhanVien.setThoiGianVaoLam(nhanVienDTO.getThoiGianVaoLam());
                    existingNhanVien.setMucLuong(nhanVienDTO.getMucLuong());
                    existingNhanVien.setEmail(nhanVienDTO.getEmail());
                    existingNhanVien.setTrangThai(nhanVienDTO.getTrangThai()); // Trạng thái làm việc (Đi làm, Nghỉ việc)
                    // existingNhanVien.setTrangThaiHoatDong(nhanVienDTO.getTrangThaiHoatDong()); // Trạng thái hoạt động (Online/Offline) thường được quản lý riêng

                    // Xử lý cập nhật mật khẩu dựa trên vị trí mới
                    if ("Pha chế".equals(nhanVienDTO.getViTri()) || "Phục vụ".equals(nhanVienDTO.getViTri())) {
                        // Nếu vị trí mới là Pha chế hoặc Phục vụ, set mật khẩu về null
                        existingNhanVien.setMatKhau(null);
                    } else {
                        // Nếu vị trí khác, chỉ cập nhật mật khẩu nếu nó được cung cấp trong DTO (không null và không rỗng)
                        // Điều này cho phép giữ nguyên mật khẩu cũ nếu trường mật khẩu trên UI trống
                        if (nhanVienDTO.getMatKhau() != null && !nhanVienDTO.getMatKhau().isEmpty()) {
                        existingNhanVien.setMatKhau(passwordEncoder.encode(nhanVienDTO.getMatKhau()));
                    }
                }
                    NhanVien updatedNhanVien = nhanVienRepository.save(existingNhanVien);
                    return DTOConversion.toNhanVienDTO(updatedNhanVien);
                 });
    }

    @Override
    @Transactional
    public boolean deleteNhanVien(String maNhanVien) {
         Optional<NhanVien> nhanVienOptional = nhanVienRepository.findById(maNhanVien);
        if (nhanVienOptional.isPresent()) {
            NhanVien nhanVien = nhanVienOptional.get();
            nhanVien.setTrangThai("Nghỉ việc"); // Cập nhật trạng thái thành "Nghỉ việc"
            nhanVien.setTrangThaiHoatDong("Offline"); // Cập nhật trạng thái hoạt động
            nhanVien.setMatKhau(null); // Set mật khẩu về null để vô hiệu hóa tài khoản
            nhanVienRepository.save(nhanVien); // Lưu thay đổi
            return true;
        }
        return false;
    }

    // Phương thức để lấy thông tin nhân viên đang đăng nhập
    @Override
    @Transactional(readOnly = true)
    public Optional<NhanVienDTO> getCurrentLoggedInNhanVienDTO() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            Object principal = authentication.getPrincipal();
            String email;

            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            } else {
                email = principal.toString();
            }
            return nhanVienRepository.findByEmail(email).map(DTOConversion::toNhanVienDTO);
        }
        return Optional.empty();
    }

    // Phương thức để lấy thông tin nhân viên theo email
    private String generateNewMaNhanVien() {
        String lastMaNhanVien = nhanVienRepository.findMaxMaNhanVien();
        if (lastMaNhanVien == null) {
            return "NV000";
        }
        // Định dạng là NVXXX
        int num = Integer.parseInt(lastMaNhanVien.substring(2));
        num++;
        return String.format("NV%03d", num);
    }

    // Phương thức mới để lấy tất cả nhân viên không phân biệt trạng thái
    @Override
    @Transactional(readOnly = true)
    public List<NhanVienDTO> getAllNhanVienCompleteList() {
        return nhanVienRepository.findAll().stream()
                .map(DTOConversion::toNhanVienDTO)
                .collect(Collectors.toList());
    }

    // Phương thức mới để tìm kiếm nhân viên theo tên trong toàn bộ danh sách
    @Override
    @Transactional(readOnly = true)
    public List<NhanVienDTO> searchNhanVienByNameInCompleteList(String ten) {
        return nhanVienRepository.findByHoTenContainingIgnoreCase(ten).stream()
                .map(DTOConversion::toNhanVienDTO)
                .collect(Collectors.toList());
    }

     @Override
    @Transactional // Read-only vì chỉ đọc NhanVien, việc ghi vào cache không phải là DB transaction
    public void initiatePasswordReset(String email) {
        NhanVien nhanVien = nhanVienRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhân viên với email: " + email));

        if (!"Đi làm".equalsIgnoreCase(nhanVien.getTrangThai())) {
            throw new UsernameNotFoundException("Tài khoản này không hoạt động.");
        }

        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(10); // OTP hết hạn sau 10 phút
        
        otpCache.put(email, new OtpData(otp, expiryTime)); // Lưu vào cache

        emailService.sendOtpEmail(email, otp); // Gửi email chứa OTP
    }

    @Override
    @Transactional // Cần transactional để cập nhật mật khẩu vào DB
    public boolean completePasswordReset(String email, String otp, String newPassword) {
        NhanVien nhanVien = nhanVienRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhân viên với email: " + email));

        Cache.ValueWrapper valueWrapper = otpCache.get(email);
        if (valueWrapper == null || valueWrapper.get() == null) {
            return false; // Không có OTP trong cache cho email này
        }

        OtpData storedOtpData = (OtpData) valueWrapper.get();

        if (!storedOtpData.otp.equals(otp)) {
            return false; // OTP không khớp
        }

        // OTP hợp lệ, đặt lại mật khẩu
        nhanVien.setMatKhau(passwordEncoder.encode(newPassword));
        nhanVienRepository.save(nhanVien);
        
        otpCache.evict(email); // Xóa OTP đã sử dụng khỏi cache
        return true;
    }

    private String generateOtp() {
        Random random = new Random();
        int otpNumber = 100000 + random.nextInt(900000); // Tạo OTP 6 chữ số
        return String.valueOf(otpNumber);
    }
}
