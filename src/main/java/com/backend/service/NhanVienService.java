package com.backend.service;

import com.backend.dto.NhanVienDTO;
import com.backend.model.NhanVien; // Nên trả về DTO thay vì Entity trực tiếp ra service layer API

import java.util.List;
import java.util.Optional;

public interface NhanVienService {
    // Phương thức để cập nhật trạng thái hoạt động của nhân viên
    void capNhatTrangThaiHoatDong(String email, String trangThai);

    Optional<NhanVienDTO> getNhanVienByMaNhanVien(String maNhanVien);

    NhanVienDTO createNhanVien(NhanVienDTO nhanVienDTO);

    Optional<NhanVienDTO> updateNhanVien(String maNhanVien, NhanVienDTO nhanVienDTO);

    boolean deleteNhanVien(String maNhanVien);

    // Lấy thông tin DTO của nhân viên đang đăng nhập
    Optional<NhanVienDTO> getCurrentLoggedInNhanVienDTO();

    List<NhanVienDTO> getAllNhanVienCompleteList(); // Lấy tất cả nhân viên không phân biệt trạng thái

    List<NhanVienDTO> searchNhanVienByNameInCompleteList(String ten); // Tìm kiếm trong tất cả nhân viên
    
    /**
     * Khởi tạo quá trình quên mật khẩu: tạo OTP, lưu vào cache và gửi email.
     * @param email Email của nhân viên.
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException nếu email không tồn tại hoặc tài khoản không hoạt động.
     * @throws RuntimeException nếu có lỗi khi gửi email.
     */
    void initiatePasswordReset(String email);

    /**
     * Hoàn tất việc đặt lại mật khẩu bằng OTP.
     * @param email Email của nhân viên.
     * @param otp Mã OTP người dùng nhập.
     * @param newPassword Mật khẩu mới.
     * @return true nếu đặt lại thành công, false nếu OTP không hợp lệ hoặc hết hạn.
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException nếu email không tồn tại.
     */
    boolean completePasswordReset(String email, String otp, String newPassword);
    boolean verifyOtpForPasswordReset(String email, String otp);
}
