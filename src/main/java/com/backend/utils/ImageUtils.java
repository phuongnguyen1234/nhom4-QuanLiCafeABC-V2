package com.backend.utils;

import java.io.File;
import java.net.URL;

import javafx.scene.image.Image;

public class ImageUtils {
    /**
     * Tải một đối tượng Image từ đường dẫn file trên hệ thống.
     * @param duongDanAnh Đường dẫn tuyệt đối hoặc tương đối đến file ảnh trên hệ thống file.
     * @return Đối tượng Image, hoặc null nếu đường dẫn không hợp lệ hoặc file không tồn tại.
     */
    public static Image getImage(String duongDanAnh) {
        try {
            File imageFile = new File(duongDanAnh);
            if (imageFile.exists() && imageFile.isFile()) {
                return new Image(imageFile.toURI().toString());
            } else {
                System.err.println("ImageUtils.getImage: File không tồn tại hoặc không phải là file: " + duongDanAnh);
                return null;
            }
        } catch (Exception e) {
            System.err.println("ImageUtils.getImage: Lỗi khi tải ảnh từ đường dẫn file: " + duongDanAnh + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Tải một đối tượng Image từ đường dẫn resource trong classpath.
     * Nếu không thành công hoặc resourcePath rỗng, sẽ thử tải từ defaultResourcePath.
     *
     * @param resourcePath Đường dẫn đến file ảnh chính trong classpath (ví dụ: "/images/mon/ten_anh.jpg").
     * @param defaultResourcePath Đường dẫn đến file ảnh mặc định trong classpath (ví dụ: "/icons/default_product.png").
     * @return Đối tượng Image đã tải, hoặc null nếu cả hai đường dẫn đều không hợp lệ hoặc không tải được.
     */
    public static Image loadFromResourcesOrDefault(String resourcePath, String defaultResourcePath) {
        Image image = loadImageFromResource(resourcePath);

        if (image == null) {
            // Ghi log nếu ảnh chính được cung cấp nhưng không tải được và phải dùng ảnh mặc định
            if (resourcePath != null && !resourcePath.isEmpty()) {
                 System.err.println("ImageUtils: Không tải được resource '" + resourcePath + "'. Đang thử ảnh mặc định '" + defaultResourcePath + "'.");
            }
            image = loadImageFromResource(defaultResourcePath);
            if (image == null && defaultResourcePath != null && !defaultResourcePath.isEmpty()) {
                System.err.println("ImageUtils: Không tải được cả ảnh mặc định: " + defaultResourcePath);
            }
        }
        return image;
    }

    /**
     * Helper method để tải ảnh từ một resource path cụ thể.
     * @param resourcePath Đường dẫn resource.
     * @return Image object hoặc null nếu không tải được.
     */
    private static Image loadImageFromResource(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            return null;
        }
        try {
            URL imageUrl = ImageUtils.class.getResource(resourcePath);
            if (imageUrl != null) {
                return new Image(imageUrl.toExternalForm());
            } else {
                // Không cần log ở đây vì loadFromResourcesOrDefault sẽ log nếu cần
                return null;
            }
        } catch (Exception e) {
            System.err.println("ImageUtils.loadImageFromResource: Lỗi khi tải resource: " + resourcePath + " - " + e.getMessage());
            return null;
        }
    }
}