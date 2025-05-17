package com.frontend;

import java.util.ArrayList;
import java.util.List;

import com.backend.dto.DanhMucKhongMonDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class QuanLiDanhMucUI {
    @FXML
    private Button btnThem, btnQuayLai;

    @FXML
    private TableColumn<DanhMucKhongMonDTO, Integer> colSTT;

    @FXML
    private TableColumn<DanhMucKhongMonDTO, String> colMaDanhMuc, colTenDanhMuc, colLoai, colTrangThai;

    @FXML
    private TableColumn<DanhMucKhongMonDTO, Void> colHanhDong;

    @FXML
    private TableView<DanhMucKhongMonDTO> tableViewDanhMuc;

    private List<DanhMucKhongMonDTO> listDanhMuc = new ArrayList<>();

    private ObservableList<DanhMucKhongMonDTO> list = FXCollections.observableArrayList();

    public void setListDanhMuc(List<DanhMucKhongMonDTO> listDanhMuc) {
        list.clear();
        list.addAll(listDanhMuc);
    }

    public List<DanhMucKhongMonDTO> getListDanhMuc() {
        return listDanhMuc;
    }

    @FXML
    public void initialize(){
        // Liên kết các cột với thuộc tính của DanhMuc
        colMaDanhMuc.setCellValueFactory(new PropertyValueFactory<>("maDanhMuc"));
        colTenDanhMuc.setCellValueFactory(new PropertyValueFactory<>("tenDanhMuc"));
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loai"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        tableViewDanhMuc.setItems(list);
        // Cấu hình cột STT để hiển thị số thứ tự dòng
        colSTT.setCellValueFactory(param -> new javafx.beans.property.SimpleIntegerProperty(tableViewDanhMuc.getItems().indexOf(param.getValue()) + 1).asObject());

        // Cấu hình cột hành động với nút "Sửa"
        colHanhDong.setCellFactory(col -> new TableCell<>() {
            private final Button suaButton = new Button("Sửa");

            {
                // Tạo ImageView và thêm ảnh
                ImageView editIcon = new ImageView(getClass().getResource("/icons/write.png").toExternalForm());
                editIcon.setFitWidth(30); // Chiều rộng ảnh
                editIcon.setFitHeight(30); // Chiều cao ảnh
                suaButton.setGraphic(editIcon);
                suaButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                // Gắn class CSS vào nút
                suaButton.getStyleClass().add("button-edit");
                // Gắn sự kiện onAction cho nút "Sửa"
                suaButton.setOnAction(event -> sua(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(suaButton);
                }
            }
        });

        tableViewDanhMuc.getColumns().forEach(column -> {
            column.setReorderable(false);
        });
    }

    @FXML
    public void them(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/themDanhMuc.fxml"));
            Parent node = loader.load();

            ThemDanhMucUI controller = loader.getController();
            controller.setQuanLiDanhMucUI(this);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
            dialogStage.setTitle("Thêm danh mục");
            dialogStage.setScene(new Scene(node));
            dialogStage.setResizable(false);

            // Hiển thị và chờ người dùng đóng dialog
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sua(DanhMucKhongMonDTO danhMucDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chinhSuaDanhMuc.fxml"));
            Parent node = loader.load();

            ChinhSuaDanhMucUI controller = loader.getController();
            controller.setDanhMuc(danhMucDTO);
            controller.setQuanLiDanhMucUI(this);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
            dialogStage.setTitle("Sửa danh mục");
            dialogStage.setScene(new Scene(node));
            dialogStage.setResizable(false);

            // Hiển thị và chờ người dùng đóng dialog
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void quayLai(){}
}
