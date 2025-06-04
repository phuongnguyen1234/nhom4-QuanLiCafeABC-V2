package com.backend.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.image.Image;

public class ImageUtils {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    // Lưu ảnh theo mã món
    private static final Map<String, Image> imageCache = new ConcurrentHashMap<>();

    // Lấy ảnh từ cache hoặc tải nếu chưa có
    public static Image getMonImage(String maMon) {
        if (imageCache.containsKey(maMon)) {
            return imageCache.get(maMon);
        }

        try {
            String url = "http://localhost:8080/mon/" + maMon + "/image";
            Image image = new Image(url, true); // true để load không đồng bộ
            imageCache.put(maMon, image);
            return image;
        } catch (Exception e) {
            return new Image(ImageUtils.class.getResource("/icons/loading.png").toExternalForm());
        }
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