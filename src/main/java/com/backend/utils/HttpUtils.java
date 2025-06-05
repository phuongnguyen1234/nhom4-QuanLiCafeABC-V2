package com.backend.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpUtils {
    private static final HttpClient client = HttpClient.newBuilder()
                .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
                .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
                .build();
    private static final ObjectMapper objectMapper = new ObjectMapper(); 

    public static List<DanhMucKhongMonDTO> getListDanhMucKhongMon() throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/danh-muc/all/no-dish"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<>() {});
        } else {
            throw new IOException("HTTP Error: " + response.statusCode());
        }
    }
}
