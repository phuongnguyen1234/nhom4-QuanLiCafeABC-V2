package com.backend.service;

import java.util.List;
import java.util.Map;

import com.backend.dto.MonDTO;
import com.backend.model.Mon;

public interface MonService {
    Map<Integer, List<Mon>> getAllMonGroupedByDanhMuc();

    List<Mon> getAllMon();

    Mon createMon(MonDTO mon);

    Mon updateMon(MonDTO mon);
}
