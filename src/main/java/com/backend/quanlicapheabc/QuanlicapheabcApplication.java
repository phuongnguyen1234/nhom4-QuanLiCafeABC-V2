package com.backend.quanlicapheabc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.backend.repository")
@EntityScan(basePackages = "com.backend.model")
@ComponentScan(basePackages = {"com.backend.control", "com.backend.service"})
public class QuanlicapheabcApplication extends Application {

    private ConfigurableApplicationContext springContext;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/trangChu.fxml"));
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

