package com.backend.control;

import com.backend.service.NhanVienService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final NhanVienService nhanVienService;

    public AuthController(NhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email không được để trống."));
        }
        try {
            nhanVienService.initiatePasswordReset(email);
            // Luôn trả về thông báo chung để tránh lộ thông tin email có tồn tại hay không
            return ResponseEntity.ok(Map.of("message", "Nếu email của bạn tồn tại trong hệ thống và hợp lệ, một mã OTP đã được gửi."));
        } catch (UsernameNotFoundException e) {
            // Không thông báo lỗi cụ thể cho client
            return ResponseEntity.ok(Map.of("message", "Nếu email của bạn tồn tại trong hệ thống và hợp lệ, một mã OTP đã được gửi."));
        } catch (RuntimeException e) {
            // Lỗi gửi mail hoặc lỗi khác từ service
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Lỗi hệ thống khi xử lý yêu cầu quên mật khẩu."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");
        String newPassword = payload.get("newPassword");
        String confirmPassword = payload.get("confirmPassword");

        if (email == null || otp == null || newPassword == null || confirmPassword == null ||
            email.trim().isEmpty() || otp.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Vui lòng điền đầy đủ thông tin."));
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mật khẩu mới và xác nhận mật khẩu không khớp."));
        }
       
        try {
            boolean success = nhanVienService.completePasswordReset(email, otp, newPassword);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập bằng mật khẩu mới."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Mã OTP không hợp lệ hoặc đã hết hạn."));
            }
        } catch (UsernameNotFoundException e) {
            // Email không tồn tại (dù không nên xảy ra nếu OTP đã được gửi thành công trước đó)
            return ResponseEntity.badRequest().body(Map.of("message", "Yêu cầu không hợp lệ hoặc email không tồn tại."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Lỗi hệ thống khi đặt lại mật khẩu."));
        }
    }

    @PostMapping("/verify-otp")
public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> payload) {
    String email = payload.get("email");
    String otp = payload.get("otp");
    if (email == null || otp == null) {
        return ResponseEntity.badRequest().body(Map.of("message", "Email và OTP là bắt buộc."));
    }
    boolean isValid = nhanVienService.verifyOtpForPasswordReset(email, otp);
    return ResponseEntity.ok(isValid); // Trả về true hoặc false dưới dạng text
}
}
