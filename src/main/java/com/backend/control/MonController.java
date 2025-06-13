package com.backend.control;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.MonDTO;
import com.backend.model.Mon;
import com.backend.service.MonService;
import com.backend.service.impl.MonServiceImpl;

@RestController
@RequestMapping("/mon")
@CrossOrigin(origins = "*")
public class MonController {
    private final MonService monService;

    public MonController(MonService monService) {
        this.monService = monService;
    }

    @GetMapping("/theo-danh-muc")
    public ResponseEntity<Map<Integer, List<Mon>>> getMonGroupedByDanhMuc() {
        Map<Integer, List<Mon>> result = monService.getAllMonGroupedByDanhMuc();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Mon>> getAllMon() {
        List<Mon> result = monService.getAllMon();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> createMon(@RequestBody MonDTO mon) {
        try {
            Mon createdMon = monService.createMon(mon);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMon);
        } catch (RuntimeException e) {
            if (MonServiceImpl.ERROR_MESSAGE_OTHER_CASHIER_ONLINE.equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } else if (e.getMessage() != null && e.getMessage().startsWith("Không tìm thấy danh mục")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            // Log lỗi không mong muốn
            System.err.println("Lỗi không mong muốn khi tạo món: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ không mong muốn khi tạo món.");
        }
    }

    @PatchMapping("/{maMon}")
    public ResponseEntity<?> updateMon(@PathVariable String maMon, @RequestBody MonDTO monDetails) {
        try {
            if (monDetails.getMaMon() == null || monDetails.getMaMon().isEmpty()) {
                monDetails.setMaMon(maMon);
            } else if (!monDetails.getMaMon().equals(maMon)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(Map.of("message", "Mã món trong URL và nội dung yêu cầu không khớp."));
            }
            Mon updated = monService.updateMon(monDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (MonServiceImpl.ERROR_MESSAGE_OTHER_CASHIER_ONLINE.equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            } else if (e.getMessage() != null && e.getMessage().startsWith("Không tìm thấy danh mục")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            System.err.println("Lỗi không mong muốn khi cập nhật món " + maMon + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi máy chủ không mong muốn khi cập nhật món.");
        }
    }
}
