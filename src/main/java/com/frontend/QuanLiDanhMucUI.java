package com.frontend;

import java.util.ArrayList;
import java.util.List;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.utils.HttpUtils;
import com.backend.utils.JavaFXUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
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

    private final List<DanhMucKhongMonDTO> listDanhMuc = new ArrayList<>();

    private final ObservableList<DanhMucKhongMonDTO> list = FXCollections.observableArrayList();

    public List<DanhMucKhongMonDTO> getListDanhMuc() {
        return listDanhMuc;
    }

    public void loadDanhSachDanhMuc() {
        // Tạo một Task để thực hiện việc tải danh sách danh mục
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                setDisableItems(true); // Vô hiệu hóa các nút trong khi tải dữ liệu

                // Gọi phương thức tải danh sách danh mục từ HttpUtils
                listDanhMuc.clear();
                listDanhMuc.addAll(HttpUtils.getListDanhMucKhongMon());
                return null;
            }

            @Override
            protected void succeeded() {
                setDisableItems(false); // Bật lại các nút sau khi tải xong
                super.succeeded();
                // Cập nhật danh sách vào TableView
                list.setAll(listDanhMuc);
            }

            @Override
            protected void failed() {
                super.failed();
                // Xử lý lỗi nếu cần thiết
            }
        };
        new Thread(task).start();
    }

    @FXML
    public void initialize(){
        tableViewDanhMuc.setPlaceholder(JavaFXUtils.createPlaceholder("Đang tải...", "/icons/loading.png"));

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

        JavaFXUtils.disableHorizontalScrollBar(tableViewDanhMuc);

        loadDanhSachDanhMuc();
    }

    @FXML
    public void them(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/themDanhMuc.fxml"));
            Parent node = loader.load();

            ThemDanhMucUI controller = loader.getController();
            controller.setQuanLiDanhMucUI(this);

            Stage dialogStage = JavaFXUtils.createDialog("Thêm danh mục", node, "/icons/add.png");
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

            Stage dialogStage = JavaFXUtils.createDialog("Sửa danh mục", node, "/icons/edit-text.png");
            dialogStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void quayLai(){
        btnQuayLai.getScene().getWindow().hide();
    }

    private void setDisableItems(boolean disable) {
        tableViewDanhMuc.setDisable(disable);
        btnThem.setDisable(disable);
        btnQuayLai.setDisable(disable);
    }
}
