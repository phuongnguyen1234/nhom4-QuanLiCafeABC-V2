package com.backend.service.impl;

import com.backend.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Mã OTP đặt lại mật khẩu - Quản Lý Cafe ABC");
            message.setText("Mã OTP của bạn là: " + otp + "\n"
                          + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.");
            mailSender.send(message);
            System.out.println("Email OTP đã được gửi tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email OTP: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email OTP.", e);
        }
    }
}