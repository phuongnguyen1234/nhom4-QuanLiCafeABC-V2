package com.backend.service;

public interface EmailService {

    /**
     * Gửi email chứa mã OTP đến địa chỉ email được chỉ định.
     *
     * @param toEmail Địa chỉ email người nhận.
     * @param otp     Mã OTP cần gửi.
     * @throws RuntimeException nếu có lỗi xảy ra trong quá trình gửi email.
     */
    void sendOtpEmail(String toEmail, String otp);
}