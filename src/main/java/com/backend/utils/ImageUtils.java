package com.backend.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ImageUtils {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static byte[] getMonImageByID(String maMon){
        try{
            HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/mon/" + maMon + "/image"))
                                .GET()
                                .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getNhanVienImageByID(String maNhanVien){
        try{
            HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/nhanvien/" + maNhanVien + "/image"))
                                .GET()
                                .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
}