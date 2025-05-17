package com.frontend;

import java.util.ArrayList;
import java.util.List;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.model.DanhMuc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

public class QuanLiDanhMucUI {
    @FXML
    private Button btnThem, btnQuayLai;

    @FXML
    private TableColumn<DanhMucKhongMonDTO, Integer> colSTT;

    @FXML
    private TableColumn<DanhMucKhongMonDTO, String> colMaDanhMuc, colTenDanhMuc, colTrangThai;

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

    @FXML
    public void initialize(){
        // Liên kết các cột với thuộc tính của DanhMuc
        colMaDanhMuc.setCellValueFactory(new PropertyValueFactory<>("maDanhMuc"));
        colTenDanhMuc.setCellValueFactory(new PropertyValueFactory<>("tenDanhMuc"));
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
    public void them(){}

    private void sua(DanhMucKhongMonDTO danhMucDTO) {

    }

    @FXML
    public void quayLai(){}
}
