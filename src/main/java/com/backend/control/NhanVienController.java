package com.backend.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.NhanVienDTO;
import com.backend.service.NhanVienService;

import java.util.List;

@RestController
@RequestMapping("/nhanvien")
@CrossOrigin(origins = "*") // Cho phép frontend truy cập từ domain khác 
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;
    
    /**
     * Lấy danh sách tất cả nhân viên (bao gồm cả đang làm và đã nghỉ).
     * Endpoint này sẽ được sử dụng bởi NhanVienUI khi không có từ khóa tìm kiếm.
     */
    @GetMapping("/all")
    public ResponseEntity<List<NhanVienDTO>> getAllNhanVienComplete() {
        List<NhanVienDTO> nhanVienList = nhanVienService.getAllNhanVienCompleteList();
        return ResponseEntity.ok(nhanVienList);
    }


    //Lấy nhân viên theo mã nhân viên
    @GetMapping("/{maNhanVien}")
    public ResponseEntity<NhanVienDTO> getNhanVienByMaNhanVien(@PathVariable String maNhanVien) {
        return nhanVienService.getNhanVienByMaNhanVien(maNhanVien)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Tạo mới nhân viên
    @PostMapping
    public ResponseEntity<NhanVienDTO> createNhanVien(@RequestBody NhanVienDTO nhanVienDTO) {
        // Kiểm tra xem email đã tồn tại chưa 
        // if (nhanVienService.findByEmail(nhanVienDTO.getEmail()).isPresent()) {
        //     return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại");
        // }
        NhanVienDTO createdNhanVien = nhanVienService.createNhanVien(nhanVienDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNhanVien);
    }

    //Cập nhật nhân viên
    @PutMapping("/{maNhanVien}")
    public ResponseEntity<NhanVienDTO> updateNhanVien(@PathVariable String maNhanVien, @RequestBody NhanVienDTO nhanVienDTO) {
        return nhanVienService.updateNhanVien(maNhanVien, nhanVienDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Xóa nhân viên
    @DeleteMapping("/{maNhanVien}")
    public ResponseEntity<Void> deleteNhanVien(@PathVariable String maNhanVien) {
        if (nhanVienService.deleteNhanVien(maNhanVien)) {
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Tìm kiếm nhân viên theo tên trong toàn bộ danh sách (bao gồm cả đang làm và đã nghỉ).
     * Endpoint này sẽ được sử dụng bởi NhanVienUI khi có từ khóa tìm kiếm.
     * @param ten Tên nhân viên cần tìm.
     */
    @GetMapping("/search")
    public ResponseEntity<List<NhanVienDTO>> searchNhanVienByNameComplete(@RequestParam("ten") String ten) {
        List<NhanVienDTO> nhanVienList = nhanVienService.searchNhanVienByNameInCompleteList(ten);
        return ResponseEntity.ok(nhanVienList);
    }

}
