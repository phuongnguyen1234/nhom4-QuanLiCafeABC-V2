package com.frontend;

import com.backend.model.DoanhThu;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ChiTietDoanhThuUI {
    @FXML
    private ComboBox thangCombobox, namCombobox;

    @FXML
    private TableView tableViewChiTietDoanhThu;

    @FXML
    private TableColumn<DoanhThu, String> colThoiGian, colTangTruongDoanhThu, colThoiGianTongHopDoanhThu;

    @FXML
    private TableColumn<DoanhThu, Integer> colSoDon, colSoMon;

    @FXML
    private TableColumn<DoanhThu, Double> colTrungBinhMoiDon;

    @FXML
    private Pagination phanTrang;

    @FXML
    public void initialize(){}

    @FXML
    public void loc(){}
}
