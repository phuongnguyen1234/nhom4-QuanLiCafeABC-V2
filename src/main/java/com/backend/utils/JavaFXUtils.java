package com.backend.utils;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class JavaFXUtils {
    public static Stage createDialog(String title, Parent node, String iconPath) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
        dialogStage.setTitle(title);
        dialogStage.setScene(new Scene(node));
        dialogStage.setResizable(false);
    
        if (iconPath != null && !iconPath.isEmpty()) {
            dialogStage.getIcons().add(new Image(MessageUtils.class.getResource(iconPath).toExternalForm()));
        }
    
        return dialogStage;
    }

    public static Node createPlaceholder(String title, String iconPath) {
        if ("Đang tải...".equals(title)) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(10);
        if (iconPath != null && !iconPath.isEmpty()) {
                try {
                    Image image = new Image(JavaFXUtils.class.getResource(iconPath).toExternalForm());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(30); // Kích thước nhỏ hơn cho HBox loading
                    imageView.setFitHeight(30);
                    hbox.getChildren().add(imageView);
                } catch (NullPointerException e) {
                    System.err.println("Không thể tải icon (HBox) từ đường dẫn: " + iconPath + ". Kiểm tra lại đường dẫn resource.");
                }
            }
            Label label = new Label(title);
            label.setFont(Font.font("Open Sans", 16)); // Font nhỏ hơn cho HBox loading
            hbox.getChildren().add(label);
            return hbox;
        } else {
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setSpacing(15); // Tăng khoảng cách một chút

            if (iconPath != null && !iconPath.isEmpty()) {
                try {
                    Image image = new Image(JavaFXUtils.class.getResource(iconPath).toExternalForm());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(110); // Tăng kích thước icon cho VBox
                    imageView.setFitHeight(110);
                    vbox.getChildren().add(imageView);
                } catch (NullPointerException e) {
                    System.err.println("Không thể tải icon (VBox) từ đường dẫn: " + iconPath + ". Kiểm tra lại đường dẫn resource.");
                }
            }
            Label label = new Label(title);
            label.setFont(Font.font("Open Sans", 18));
            vbox.getChildren().add(label);
            return vbox;
        }
    }
    
}
