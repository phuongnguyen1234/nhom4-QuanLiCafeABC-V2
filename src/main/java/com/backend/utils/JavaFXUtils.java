package com.backend.utils;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    public static void disableHorizontalScrollBar(TableView<?> tableView) {
        Platform.runLater(() -> {
            for (Node node : tableView.lookupAll(".scroll-bar:horizontal")) {
                if (node instanceof ScrollBar scrollBar) {
                    scrollBar.setDisable(true);      // Vô hiệu hóa cuộn
                    scrollBar.setOpacity(0);         // Ẩn khỏi mắt người dùng
                    scrollBar.setPrefHeight(0);      // Không chiếm chỗ
                    scrollBar.setMaxHeight(0);
                }
            }
        });
    }

    private static final Image VIEW_ICON;
    private static final Image HIDE_ICON;

    static {
        Image view = null;
        Image hide = null;
        try {
            // Đảm bảo đường dẫn chính xác khi sử dụng getResourceAsStream
            view = new Image(JavaFXUtils.class.getResourceAsStream("/icons/view.png"));
            hide = new Image(JavaFXUtils.class.getResourceAsStream("/icons/hide.png"));
        } catch (Exception e) {
            System.err.println("LỖI NGHIÊM TRỌNG: Không thể tải icon ẩn/hiện mật khẩu trong JavaFXUtils. " + e.getMessage());
            // Cân nhắc việc ném một RuntimeException ở đây nếu các icon này là thiết yếu
        }
        VIEW_ICON = view;
        HIDE_ICON = hide;
    }

    public static boolean togglePasswordVisibility(boolean currentVisibility, TextField matKhauTextField, PasswordField matKhauPWField, Hyperlink xemMKHyperlink) {
        boolean newVisibility = !currentVisibility;

        // Thiết lập thuộc tính hiển thị và quản lý dựa trên newVisibility
        matKhauTextField.setVisible(newVisibility);
        matKhauTextField.setManaged(newVisibility);
        matKhauPWField.setVisible(!newVisibility);
        matKhauPWField.setManaged(!newVisibility);

        Node graphic = xemMKHyperlink.getGraphic();
        if (graphic instanceof ImageView iconImageView) {
        if (newVisibility) {
                // Mật khẩu SẼ được hiển thị (trong TextField)
            matKhauTextField.setText(matKhauPWField.getText());
                if (HIDE_ICON != null) {
                    iconImageView.setImage(HIDE_ICON);
                } else {
                    System.err.println("Lỗi: HIDE_ICON chưa được tải trong JavaFXUtils.");
            }
        } else {
                // Mật khẩu SẼ được ẩn (trong PasswordField)
            matKhauPWField.setText(matKhauTextField.getText());
                if (VIEW_ICON != null) {
                    iconImageView.setImage(VIEW_ICON);
                } else {
                    System.err.println("Lỗi: VIEW_ICON chưa được tải trong JavaFXUtils.");
            }
        }
        } else {
            System.err.println("Cảnh báo: Hyperlink để ẩn/hiện mật khẩu không có ImageView làm graphic. Icon sẽ không thay đổi. ID: " + xemMKHyperlink.getId());
        }
        return newVisibility; // Trả về trạng thái mới
    }
}
