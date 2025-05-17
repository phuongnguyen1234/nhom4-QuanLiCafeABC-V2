package com.backend.service;

import java.util.List;
import java.util.Map;

import com.backend.dto.MonQLy;
import com.backend.model.Mon;

public interface MonService {
    Map<Integer, List<Mon>> getAllMonGroupedByDanhMuc();

    List<Mon> getAllMon();

    Mon createMon(MonQLy mon);

    Mon partialUpdate(String maMon, Mon mon);

    byte[] getImage(String maMon) throws Exception;
}
