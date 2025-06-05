package com.backend.config;

import com.backend.service.NhanVienService; // Inject NhanVienService để cập nhật trạng thái
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Sử dụng BCrypt để mã hóa mật khẩu
    }

    @Bean
    // Inject NhanVienService vào đây thay vì constructor
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                               @Lazy NhanVienService nhanVienService,
                                               CustomAuthenticationSuccessHandler successHandler,
                                               AuthenticationFailureHandler failureHandler,
                                               JsonToFormLoginParameterFilter jsonToFormLoginParameterFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(jsonToFormLoginParameterFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/auth/login", "/public/**", "/error").permitAll() // Cho phép truy cập không cần xác thực
                .requestMatchers("/nhanvien/**").hasRole("CHU_QUAN")
                .requestMatchers("/don-hang/**", "/doanh-thu/**","/danh-muc/**", "/mon/**").hasAnyRole("THU_NGAN", "CHU_QUAN") // Thu ngân hoặc chủ quán có thể đặt hàng
                .anyRequest().authenticated() // Các request khác cần xác thực
            )
            .formLogin(formLogin -> formLogin
                .loginProcessingUrl("/auth/login") // URL mà client sẽ POST tới
                .usernameParameter("email") // Tên parameter cho email
                .passwordParameter("matKhau") // Tên parameter cho mật khẩu
                .successHandler(successHandler) // Handler khi thành công
                .failureHandler(failureHandler) // Handler khi thất bại
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout") // Endpoint để logout
                .addLogoutHandler((request, response, authentication) -> {
                    // Sử dụng 'authentication' object được truyền vào
                    if (authentication != null && authentication.getName() != null) {
                        System.out.println("LogoutHandler: User " + authentication.getName() + " is logging out."); // Thêm log để kiểm tra
                        // Gọi NhanVienService để cập nhật trạng thái
                        nhanVienService.capNhatTrangThaiHoatDong(authentication.getName(), "Offline"); // Cập nhật thành "Offline"
                        } else {
                        System.out.println("LogoutHandler: Authentication object is null or name is null."); // Thêm log
                    }
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Xử lý sau khi logout thành công
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    // Xóa cookie JSESSIONID thủ công ở đây nếu cần, mặc dù deleteCookies đã làm
                    response.getWriter().write("{\"message\":\"Logout successful\"}"); // Trả về JSON
                })
                .invalidateHttpSession(true) // Hủy session
                .deleteCookies("JSESSIONID") // Xóa cookie session
                )
             // Cấu hình Access Denied Handler để tùy chỉnh response cho lỗi 403 (ví dụ: do CSRF)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                 .authenticationEntryPoint(authenticationEntryPoint()) // Trả về 401 cho request không xác thực
                 .accessDeniedHandler(accessDeniedHandler())
             )
            .sessionManagement(session -> session
                .maximumSessions(1) // Chỉ cho phép 1 session đồng thời cho mỗi user
                .maxSessionsPreventsLogin(true) // Ngăn chặn đăng nhập mới nếu đã có session
            );
        return http.build();
    }


    // Cấu hình AuthenticationManager để sử dụng QuanLyPhienServiceImpl
    // UserDetailsService sẽ được Spring tự động inject nếu có một @Bean UserDetailsService duy nhất
    // (chính là QuanLyPhienServiceImpl của bạn vì nó được đánh dấu @Service)
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
    
    // Bean AccessDeniedHandler để tùy chỉnh response cho lỗi 403
     // Mặc định AccessDeniedHandlerImpl trả về 403 không có body
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        // Bạn có thể tạo một handler tùy chỉnh ở đây nếu muốn body cho 403
        return new AccessDeniedHandlerImpl(); // Mặc định trả về 403 không body
    }

     @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED); // Trả về 401 Unauthorized
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        // Sử dụng ObjectMapper để tạo JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        return (request, response, exception) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            String errorMessage = "Email hoặc mật khẩu không đúng."; // Thông báo mặc định
            // Kiểm tra thông điệp lỗi thay vì kiểu exception
            // Thông điệp mặc định của Spring Security khi vượt quá số session tối đa thường chứa "Maximum sessions"
            if (exception.getMessage() != null && exception.getMessage().contains("Maximum sessions")) {
                errorMessage = "Tài khoản đã được đăng nhập ở nơi khác!";
            } else {
                 errorMessage += " Chi tiết: " + exception.getMessage();
            }
            objectMapper.writeValue(response.getWriter(), java.util.Map.of("message", errorMessage));
        };
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }
}