package com.backend.service;

import com.backend.dto.DonHangDTO;
import com.backend.model.DonHang;

public interface DonHangService {
    DonHang createDonHang(DonHangDTO donHangDTO);
}
