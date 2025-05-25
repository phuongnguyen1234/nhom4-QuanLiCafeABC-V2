package com.backend.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.dto.MonDTO;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
import com.backend.repository.DanhMucRepository;
import com.backend.repository.MonRepository;
import com.backend.service.MonService;

@Service
public class MonServiceImpl implements MonService {
    private final MonRepository monRepository;
    private final DanhMucRepository danhMucRepository;

    public MonServiceImpl(MonRepository monRepository, DanhMucRepository danhMucRepository) {
        this.monRepository = monRepository;
        this.danhMucRepository = danhMucRepository;
    }

    @Override
    public Map<Integer, List<Mon>> getAllMonGroupedByDanhMuc() {
        List<Mon> allMon = monRepository.findAll();
        return allMon.stream().collect(Collectors.groupingBy(mon -> mon.getDanhMuc().getMaDanhMuc()));
    }

    @Override
    public List<Mon> getAllMon() {
        return monRepository.findAll();
    }

    @Override
    public Mon createMon(MonDTO dto) {
        Mon mon = new Mon();

        // Tạo mã tự động nếu cần
        String newMaMon = generateNewMaMon();
        mon.setMaMon(newMaMon);

        // Gán các thuộc tính
        mon.setTenMon(dto.getTenMon());
        mon.setDonGia(dto.getDonGia());
        mon.setTrangThai(dto.getTrangThai());
        mon.setAnhMinhHoa(dto.getAnhMinhHoa());

        // Tìm đối tượng danh mục từ mã danh mục
        DanhMuc danhMuc = danhMucRepository.findById(dto.getMaDanhMuc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + dto.getMaDanhMuc()));
        mon.setDanhMuc(danhMuc);

        return monRepository.save(mon);
    }

    @Override
    public Mon updateMon(MonDTO monUpdate) {
        Mon mon = new Mon();
        mon.setMaMon(monUpdate.getMaMon());
        mon.setTenMon(monUpdate.getTenMon());
        mon.setDonGia(monUpdate.getDonGia());
        mon.setTrangThai(monUpdate.getTrangThai());
        mon.setAnhMinhHoa(monUpdate.getAnhMinhHoa());
        // Tìm đối tượng danh mục từ mã danh mục
        DanhMuc danhMuc = danhMucRepository.findById(monUpdate.getMaDanhMuc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với mã: " + monUpdate.getMaDanhMuc()));
        mon.setDanhMuc(danhMuc);

        return monRepository.save(mon);
    }

    private String generateNewMaMon() {
        List<Mon> allMon = monRepository.findAll();

        int max = allMon.stream()
                .map(Mon::getMaMon)
                .filter(ma -> ma != null && ma.matches("M\\d{3}"))
                .map(ma -> Integer.parseInt(ma.substring(1)))
                .max(Comparator.naturalOrder())
                .orElse(0);

        int next = max + 1;
        return String.format("M%03d", next); // M001, M002, ...
    }


}
