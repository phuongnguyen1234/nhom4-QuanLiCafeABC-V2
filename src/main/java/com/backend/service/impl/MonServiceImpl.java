package com.backend.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.dto.MonQLy;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
import com.backend.repository.DanhMucRepository;
import com.backend.repository.MonRepository;
import com.backend.service.MonService;

@Service
public class MonServiceImpl implements MonService {
    @Autowired
    private MonRepository monRepository;

    @Autowired
    private DanhMucRepository danhMucRepository;

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
    public Mon createMon(MonQLy dto) {
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
    public Mon partialUpdate(String maMon, Mon monUpdate) {
        Optional<Mon> optionalMon = monRepository.findById(maMon);
        if (!optionalMon.isPresent()) {
            throw new RuntimeException("Không tìm thấy món với mã: " + maMon);
        }

        Mon mon = optionalMon.get();

        // Cập nhật các trường không null / hợp lệ
        if (monUpdate.getTenMon() != null) mon.setTenMon(monUpdate.getTenMon());
        if (monUpdate.getDonGia() != -1) mon.setDonGia(monUpdate.getDonGia());
        if (monUpdate.getTrangThai() != null) mon.setTrangThai(monUpdate.getTrangThai());
        if (monUpdate.getAnhMinhHoa() != null) mon.setAnhMinhHoa(monUpdate.getAnhMinhHoa());

        if (monUpdate.getDanhMuc() != null && monUpdate.getDanhMuc().getMaDanhMuc() != 0) {
            int maDanhMuc = monUpdate.getDanhMuc().getMaDanhMuc();
            DanhMuc danhMuc = danhMucRepository.findById(maDanhMuc)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có mã: " + maDanhMuc));
            mon.setDanhMuc(danhMuc);
        }

        return monRepository.save(mon);
    }

    @Override
    public byte[] getImage(String maMon) throws Exception {
        Optional<Mon> optionalMon = monRepository.findById(maMon);
        if (optionalMon.isEmpty() || optionalMon.get().getAnhMinhHoa() == null) {
            throw new Exception("Không tìm thấy ảnh minh họa cho món có mã: " + maMon);
        }
        return optionalMon.get().getAnhMinhHoa();
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
