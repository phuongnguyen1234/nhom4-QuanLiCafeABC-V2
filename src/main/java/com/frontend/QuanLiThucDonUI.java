package com.frontend;

import java.util.ArrayList;
import java.util.List;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.DanhMucMonKhongAnhDTO;
import com.backend.dto.MonKhongAnhDTO;
import com.backend.dto.MonQLy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class QuanLiThucDonUI {
    @FXML
    private Button btnDanhMuc, btnThemMon, btnQuayLai;

    @FXML
    private TableColumn<MonQLy, Integer> colSTT, colDonGia;

    @FXML
    private TableColumn<MonQLy, String> colMaMon, colTenMon, colTrangThai, colDanhMuc;

    @FXML
    private TableColumn<MonQLy, Void> colHanhDong;

    @FXML
    private TableView<MonQLy> tableViewMon;

    @FXML
    private Pagination phanTrang;

    private final ObservableList<MonQLy> list = FXCollections.observableArrayList();

    private List<DanhMucMonKhongAnhDTO> listDanhMuc = new ArrayList<>();

    private TrangChuUI trangChuUI;

    public void setTrangChuUI(TrangChuUI trangChuUI) {
        this.trangChuUI = trangChuUI;
    }

    public List<DanhMucMonKhongAnhDTO> getListDanhMuc() {
        return listDanhMuc;
    }

    public QuanLiThucDonUI getQuanLiThucDonUI() {
        return this;
    }

    @FXML
    public void initialize(){
        // Liên kết các cột với thuộc tính của Mon
        colMaMon.setCellValueFactory(new PropertyValueFactory<>("maMon"));
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colDanhMuc.setCellValueFactory(new PropertyValueFactory<>("tenDanhMuc"));

        // Cấu hình cột STT để hiển thị số thứ tự dòng
        colSTT.setCellValueFactory(param -> new javafx.beans.property.SimpleIntegerProperty(tableViewMon.getItems().indexOf(param.getValue()) + 1).asObject());

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

        tableViewMon.getColumns().forEach(column -> {
            column.setReorderable(false);
        });
    }

    public void setListMon(List<DanhMucMonKhongAnhDTO> listDanhMuc) {
        this.listDanhMuc = listDanhMuc;
        list.clear();

        //dua danh sach mon tung danh muc vao list
        for (DanhMucMonKhongAnhDTO danhMuc : listDanhMuc) {
            List<MonKhongAnhDTO> listMon = danhMuc.getMonList();
            // Chuyển đổi danh sách Mon thành danh sách MonQLy
            for (MonKhongAnhDTO mon : listMon) {
                MonQLy monQLy = new MonQLy();
                monQLy.setMaMon(mon.getMaMon());
                monQLy.setTenMon(mon.getTenMon());
                monQLy.setDonGia(mon.getDonGia());
                monQLy.setTrangThai(mon.getTrangThai());
                monQLy.setAnhMinhHoa(null);
                monQLy.setMaDanhMuc(danhMuc.getMaDanhMuc());
                monQLy.setTenDanhMuc(danhMuc.getTenDanhMuc());
                list.add(monQLy);
            }
        }
        tableViewMon.setItems(list);
    }

    public void sua(MonQLy mon) {
        //mo dialog danh muc
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chinhSuaMonTrongThucDon.fxml"));
            Parent node = loader.load();

            ChinhSuaMonTrongThucDonUI controller = loader.getController();
            controller.setDanhMucList(listDanhMuc);
            controller.setMon(mon);
            // Tạo Stage dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
            dialogStage.setTitle("Danh mục");
            dialogStage.setScene(new Scene(node));
            dialogStage.setResizable(false);

        // Hiển thị và chờ người dùng đóng dialog
        dialogStage.showAndWait();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void danhMuc(){
        //mo dialog danh muc
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/quanLiDanhMuc.fxml"));
            Parent node = loader.load();

            QuanLiDanhMucUI controller = loader.getController();

            List<DanhMucKhongMonDTO> list = new ArrayList<>();
            for (DanhMucMonKhongAnhDTO danhMuc : listDanhMuc) {
                DanhMucKhongMonDTO dto = new DanhMucKhongMonDTO();
                dto.setMaDanhMuc(danhMuc.getMaDanhMuc());
                dto.setTenDanhMuc(danhMuc.getTenDanhMuc());
                dto.setLoai(danhMuc.getLoai());
                dto.setTrangThai(danhMuc.getTrangThai());
                list.add(dto);
            }
            controller.setListDanhMuc(list);
            // Tạo Stage dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
            dialogStage.setTitle("Danh mục");
            dialogStage.setScene(new Scene(node));
            dialogStage.setResizable(false);

            // Hiển thị và chờ người dùng đóng dialog
            dialogStage.showAndWait();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    public void themMon(){
        //mo dialog danh muc
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/themVaoThucDon.fxml"));
            Parent node = loader.load();

            ThemVaoThucDonUI controller = loader.getController();
            List<DanhMucKhongMonDTO> list = new ArrayList<>();
            for (DanhMucMonKhongAnhDTO danhMuc : listDanhMuc) {
                DanhMucKhongMonDTO dto = new DanhMucKhongMonDTO();
                dto.setMaDanhMuc(danhMuc.getMaDanhMuc());
                dto.setTenDanhMuc(danhMuc.getTenDanhMuc());
                dto.setLoai(danhMuc.getLoai());
                dto.setTrangThai(danhMuc.getTrangThai());
                list.add(dto);
            }
            controller.setDanhMucList(list);
            // Tạo Stage dialog
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác ngoài
            dialogStage.setTitle("Thêm vào thực đơn");
            dialogStage.setScene(new Scene(node));
            dialogStage.setResizable(false);

            // Hiển thị và chờ người dùng đóng dialog
            dialogStage.showAndWait();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    private void quayLai() {
        trangChuUI.thucDon(); // Gọi lại phương thức load giao diện Thực đơn
    }


    @FXML
    public void timKiem(){}
}
