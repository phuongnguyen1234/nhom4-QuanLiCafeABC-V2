package com.backend.config; 

import com.backend.dto.NhanVienDTO;
import com.backend.service.NhanVienService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final NhanVienService nhanVienService;
    private final ObjectMapper objectMapper;

     // Inject the ObjectMapper bean managed by Spring
    @Autowired 
    public CustomAuthenticationSuccessHandler(NhanVienService nhanVienService, ObjectMapper objectMapper) {
        this.nhanVienService = nhanVienService;
        this.objectMapper = objectMapper; // Use the injected ObjectMapper
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Spring Security đã lưu Authentication vào SecurityContextHolder
        // Cập nhật trạng thái hoạt động
        if (authentication != null && authentication.getName() != null) {
            nhanVienService.capNhatTrangThaiHoatDong(authentication.getName(), "Online");
        }

        Optional<NhanVienDTO> nhanVienDTOOptional = nhanVienService.getCurrentLoggedInNhanVienDTO();

        if (nhanVienDTOOptional.isPresent()) {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), nhanVienDTOOptional.get());
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", "Không thể lấy thông tin người dùng sau khi đăng nhập."));
        }
    }
}
