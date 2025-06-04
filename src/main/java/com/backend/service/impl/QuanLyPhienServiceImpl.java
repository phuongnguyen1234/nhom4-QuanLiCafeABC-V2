package com.backend.service.impl;

import com.backend.model.NhanVien;
import com.backend.repository.NhanVienRepository;
import com.backend.service.QuanLyPhienService;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuanLyPhienServiceImpl implements QuanLyPhienService, UserDetailsService {

    private final NhanVienRepository nhanVienRepository;

    public QuanLyPhienServiceImpl(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    // Phương thức này được gọi bởi Spring Security để tải thông tin người dùng
    @Override
    @Transactional(readOnly = true) // Đảm bảo chỉ đọc dữ liệu
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NhanVien nhanVien = nhanVienRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy nhân viên với email: " + email));

                 // Kiểm tra trạng thái tài khoản (ví dụ: "Đi làm", "Nghỉ việc")
        // Nếu TrangThai là "Nghỉ việc" (hoặc giá trị tương ứng), tài khoản không được kích hoạt
        boolean isEnabled = "Đi làm".equalsIgnoreCase(nhanVien.getTrangThai());

        // Lấy quyền từ cột ViTri và chuẩn hóa tên vai trò
        String viTriName = nhanVien.getViTri(); // Ví dụ: THU_NGÂN, CHU_QUAN
        String roleIdentifier = viTriName.toUpperCase().replace(" ", "_");

        // Chuẩn hóa trường hợp đặc biệt "THU_NGÂN" thành "THU_NGAN" để khớp SecurityConfig
        if ("THU_NGÂN".equals(roleIdentifier)) {
            roleIdentifier = "THU_NGAN";
        }
        if ("CHỦ_QUÁN".equals(roleIdentifier)) {
            roleIdentifier = "CHU_QUAN";
        }
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleIdentifier));

        // Mật khẩu từ DB phải là mật khẩu đã được mã hóa (BCrypt)
        return new User(nhanVien.getEmail(), nhanVien.getMatKhau(),
                        isEnabled, // enabled (dựa vào cột TrangThai, ví dụ: "Đi làm")
                        true, // accountNonExpired
                        true, // credentialsNonExpired
                        true, // accountNonLocked
                         authorities); 
    }

    // Bạn có thể thêm các phương thức khác của QuanLyPhienService ở đây nếu cần
    // Ví dụ:
    // public NhanVien getCurrentLoggedInUser() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
    //         UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    //         String email = userDetails.getUsername();
    //         return nhanVienRepository.findByEmail(email).orElse(null);
    //     }
    //     return null;
    // }
}