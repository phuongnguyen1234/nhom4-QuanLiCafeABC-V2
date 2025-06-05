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
    
}
