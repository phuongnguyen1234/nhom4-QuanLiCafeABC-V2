package com.backend.control;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.MonQLy;
import com.backend.model.Mon;
import com.backend.service.MonService;

@RestController
@RequestMapping("/mon")
@CrossOrigin(origins = "*")
public class MonController {
    @Autowired
    private MonService monService;

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
    public ResponseEntity<Mon> createMon(@RequestBody MonQLy mon) {
        Mon createdMon = monService.createMon(mon);
        return ResponseEntity.status(201).body(createdMon); // HTTP 201 Created
    }


    @PatchMapping("/{maMon}")
    public ResponseEntity<Mon> updateMon(@RequestBody MonQLy mon) {
        try {
            Mon updated = monService.updateMon(mon);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{maMon}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable String maMon) {
        try {
            byte[] image = monService.getImage(maMon);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // hoặc IMAGE_PNG nếu ảnh PNG
            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    

}
