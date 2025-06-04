package com.backend.control;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.MonDTO;
import com.backend.model.Mon;
import com.backend.service.MonService;

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
    public ResponseEntity<Mon> createMon(@RequestBody MonDTO mon) {
        Mon createdMon = monService.createMon(mon);
        return ResponseEntity.status(201).body(createdMon); // HTTP 201 Created
    }


    @PatchMapping("/{maMon}")
    public ResponseEntity<Mon> updateMon(@RequestBody MonDTO mon) {
        try {
            Mon updated = monService.updateMon(mon);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
