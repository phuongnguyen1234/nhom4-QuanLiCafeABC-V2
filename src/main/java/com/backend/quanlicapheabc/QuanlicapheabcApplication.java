package com.backend.quanlicapheabc;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.backend.repository")
@EntityScan(basePackages = "com.backend.model")
@ComponentScan(basePackages = {"com.backend.control", "com.backend.service", "com.backend.config"})
@EnableScheduling // Thêm annotation này để kích hoạt scheduling
public class QuanlicapheabcApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private static final CookieManager COOKIE_MANAGER = new CookieManager();

    // Khối khởi tạo tĩnh để cấu hình CookieManager và đặt nó làm mặc định
    static {
        COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ALL); // Chấp nhận tất cả cookie
        CookieHandler.setDefault(COOKIE_MANAGER); // Đặt làm mặc định cho các HttpClient
    }

    // Phương thức tĩnh công khai để lấy CookieManager đã cấu hình
    public static CookieManager getCookieManager() {
        return COOKIE_MANAGER;
    }

    public static void main(String[] args) {
        // Chạy JavaFX (sẽ gọi lại phương thức start)
        launch(args);
    }

    @Override
    public void init() {
        // Khởi tạo Spring Boot
        springContext = new SpringApplicationBuilder(QuanlicapheabcApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải giao diện đăng nhập ban đầu của bạn
        // Đường dẫn FXML có thể cần điều chỉnh cho đúng với cấu trúc dự án của bạn
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/dangNhap.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Quản lý Cà phê ABC");
        primaryStage.setResizable(false);
        // Đặt icon cho cửa sổ
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/coffee-cup.png")));
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}

