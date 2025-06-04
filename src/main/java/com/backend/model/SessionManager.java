package com.backend.model;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    // ConcurrentHashMap để lưu trữ thông tin của người dùng với sessionId là key
    private static ConcurrentHashMap<String, NhanVien> userSessions = new ConcurrentHashMap<>();
    
    // ThreadLocal để lưu trữ sessionId cho từng thread riêng biệt
    private static ThreadLocal<String> currentSession = new ThreadLocal<>();

    // Thêm session mới vào userSessions
    public static void addSession(String sessionId, NhanVien nhanVien) {
        userSessions.put(sessionId, nhanVien);
        currentSession.set(sessionId); // Gán sessionId cho thread hiện tại
    }

    // Lấy thông tin NhanVien từ sessionId của thread hiện tại
    public static NhanVien getNhanVienByCurrentSession() {
        String sessionId = currentSession.get();
        return userSessions.get(sessionId);
    }

    // Lấy thông tin NhanVien từ sessionId
    public static NhanVien getNhanVienBySession(String sessionId) {
        return userSessions.get(sessionId);
    }

    // Xóa session khi người dùng đăng xuất
    public static void removeSession(String sessionId) {
        userSessions.remove(sessionId);
        if (currentSession.get() != null && currentSession.get().equals(sessionId)) {
            currentSession.remove(); // Xóa sessionId khỏi ThreadLocal
        }
    }

    // Lấy sessionId hiện tại
    public static String getCurrentSessionId() {
        return currentSession.get();
    }

    public static void dangXuatUserHienTai() {
    /*NhanVien nhanVien = getNhanVienByCurrentSession();
    if (nhanVien != null) {
        try {
            DangNhapController.capNhatTrangThaiHoatDong(nhanVien.getEmail(), "0");
            removeSession(getCurrentSessionId());
        } catch (SQLException e) {
            e.printStackTrace();
        } */
    }
}

