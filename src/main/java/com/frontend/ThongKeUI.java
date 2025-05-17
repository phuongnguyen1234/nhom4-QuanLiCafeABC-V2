package com.frontend;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class ThongKeUI {
    @FXML
    private Label top5CaPheBanChayNhatThangTruocLabel, top3NhanVienTaoDonNhieuNhatThangTruocLabel;

    @FXML
    private Text tieuDeDoanhThuHomNayText, doanhThuHomNayText, 
    tieuDeDoanhThuThangTruocText, doanhThuThangTruocText, tieuDeSoMonBanRaThangTruocText, 
    soMonBanRaThangTruocText, tieuDeSoDonDaTaoThangTruocText, soDonDaTaoThangTruocText,
    tenMonTop1Text, tenMonTop2Text, tenMonTop3Text, tenMonTop4Text, tenMonTop5Text,
    soMonTop1Text, soMonTop2Text, soMonTop3Text, soMonTop4Text, soMonTop5Text,
    tenNhanVienTop1Text, tenNhanVienTop2Text, tenNhanVienTop3Text,
    soDonTop1Text, soDonTop2Text,soDonTop3Text;

    @FXML
    private LineChart<String, Integer> bienDongDoanhThuLineChart;

    @FXML
    private BarChart<String, Integer> khoangThoiGianDatDonNhieuNhatThangTruocBarChart;

    @FXML
    private CategoryAxis lineXAxis, barXAxis;

    @FXML
    private NumberAxis lineYAxis, barYAxis;

    @FXML
    private Hyperlink hplinkXemChiTiet;
/* 
    private PhanTichHoatDongController controller;

    public void setPhanTichHoatDongController(PhanTichHoatDongController controller){
        this.controller = controller;
    }

    public PhanTichHoatDongController getController(){
        return controller;
    }

    @FXML
    public void initialize(){
        lineXAxis.setLabel("Tháng-Năm");
        lineYAxis.setLabel("Doanh thu (VND)");
        lineYAxis.setTickUnit(1);

        barXAxis.setLabel("Khung giờ");
        barYAxis.setLabel("Số lượng đơn");
        barYAxis.setTickUnit(1);

        bienDongDoanhThuLineChart.getData().clear();
        khoangThoiGianDatDonNhieuNhatThangTruocBarChart.getData().clear();

        // Tạo danh sách các mốc thời gian từ 6:00 đến 23:00
        List<String> timeLabels = new ArrayList<>();
        for (int i = 6; i <= 23; i++) {
            timeLabels.add(String.format("%02d:00", i));  // Định dạng giờ như "06:00", "07:00"...
        }
        timeLabels.add("00:00");  // Thêm 00:00 sáng hôm sau

        // Cấu hình trục X
        barXAxis.setCategories(FXCollections.observableArrayList(timeLabels));

    }

    public void hienThiSoLieuThongKe(){
    //hien thi so lieu 
        tieuDeDoanhThuHomNayText.setText("Doanh thu hôm nay " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        doanhThuHomNayText.setText(String.valueOf(controller.layDoanhThuHomNay()));

        String thangTruoc = LocalDate.now().minusMonths(1).getMonthValue() + "/" + LocalDate.now().minusMonths(1).getYear();

        tieuDeDoanhThuThangTruocText.setText("Doanh thu tháng " + thangTruoc);
        doanhThuThangTruocText.setText(String.valueOf(controller.layDoanhThuThangTruoc()));

        tieuDeSoMonBanRaThangTruocText.setText("Số cà phê bán ra tháng " + thangTruoc);
        soMonBanRaThangTruocText.setText(String.valueOf(controller.laySoCaPheBanRaThangTruoc()));

        tieuDeSoDonDaTaoThangTruocText.setText("Số đơn đã tạo tháng " + thangTruoc);
        soDonDaTaoThangTruocText.setText(String.valueOf(controller.laySoDonDaTaoThangTruoc()));

    //top 5 ca phe ban chay thang truoc
        List<Map<String, Object>> top5CaPhe = controller.layTop5CaPheBanChayThangTruoc();
        top5CaPheBanChayNhatThangTruocLabel.setText("Top 5 cà phê bán chạy nhất tháng "+thangTruoc);

        if (top5CaPhe.size() > 0) {
            tenMonTop1Text.setText((String) top5CaPhe.get(0).get("TenCaPhe"));
            soMonTop1Text.setText(String.valueOf(top5CaPhe.get(0).get("TongSoLuong")));
        }
        if (top5CaPhe.size() > 1) {
            tenMonTop2Text.setText((String) top5CaPhe.get(1).get("TenCaPhe"));
            soMonTop2Text.setText(String.valueOf(top5CaPhe.get(1).get("TongSoLuong")));
        }
        if (top5CaPhe.size() > 2) {
            tenMonTop3Text.setText((String) top5CaPhe.get(2).get("TenCaPhe"));
            soMonTop3Text.setText(String.valueOf(top5CaPhe.get(2).get("TongSoLuong")));
        }
        if (top5CaPhe.size() > 3) {
            tenMonTop4Text.setText((String) top5CaPhe.get(3).get("TenCaPhe"));
            soMonTop4Text.setText(String.valueOf(top5CaPhe.get(3).get("TongSoLuong")));
        }
        if (top5CaPhe.size() > 4) {
            tenMonTop5Text.setText((String) top5CaPhe.get(4).get("TenCaPhe"));
            soMonTop5Text.setText(String.valueOf(top5CaPhe.get(4).get("TongSoLuong")));
        }

    //top 3 nhan vien tao nhieu don nhat thang truoc
        List<Map<String, Object>> top3NhanVien = controller.layTop3NhanVienTaoDonNhieuNhatThangTruoc();
        top3NhanVienTaoDonNhieuNhatThangTruocLabel.setText("Top 3 nhân viên thu ngân tạo đơn nhiều nhất tháng "+thangTruoc);

        if (top3NhanVien.size() > 0) {
            tenNhanVienTop1Text.setText((String) top3NhanVien.get(0).get("TenNhanVien"));
            soDonTop1Text.setText(String.valueOf(top3NhanVien.get(0).get("SoDon")));
        }
        if (top3NhanVien.size() > 1) {
            tenNhanVienTop2Text.setText((String) top3NhanVien.get(1).get("TenNhanVien"));
            soDonTop2Text.setText(String.valueOf(top3NhanVien.get(1).get("SoDon")));
        }

        if (top3NhanVien.size() > 2) {
            tenNhanVienTop3Text.setText((String) top3NhanVien.get(2).get("TenNhanVien"));
            soDonTop3Text.setText(String.valueOf(top3NhanVien.get(2).get("SoDon")));
        }

    //bieu do duong bien dong doanh thu 6 thang
        List<Map<String, Object>> doanhThuList = controller.layBienDongDoanhThu6Thang();
    
        ObservableList<XYChart.Data<String, Integer>> lineData = FXCollections.observableArrayList();
        
        for (Map<String, Object> item : doanhThuList) {
            int thang = (int) item.get("Thang");
            double doanhThu = (double) item.get("DoanhThu");
            lineData.add(new XYChart.Data<>(String.valueOf(thang), (int) doanhThu));
        }
        
        XYChart.Series<String, Integer> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Doanh thu");
        lineSeries.setData(lineData);
        
        bienDongDoanhThuLineChart.getData().clear();
        bienDongDoanhThuLineChart.getData().add(lineSeries);
        String sauThangTruoc = LocalDate.now().minusMonths(6).getMonthValue() + "/" + LocalDate.now().minusMonths(1).getYear();
        bienDongDoanhThuLineChart.setTitle("Biến động doanh thu tháng "+sauThangTruoc+" - "+thangTruoc);

    //bieu do cot khoang thoi gian dat don nhieu nhat thang truoc
        List<Map<String, Object>> thoiGianDatDonList = controller.layKhoangThoiGianDatDonNhieuNhatThangTruoc();
        
        ObservableList<XYChart.Data<String, Integer>> barData = FXCollections.observableArrayList();
        
        for (Map<String, Object> item : thoiGianDatDonList) {
            int gio = (int) item.get("Gio");
            int soDon = (int) item.get("SoDon");
            barData.add(new XYChart.Data<>(String.format("%02d:00", gio), soDon));
        }
        
        XYChart.Series<String, Integer> barSeries = new XYChart.Series<>();
        barSeries.setName("Số đơn");
        barSeries.setData(barData);
        
        khoangThoiGianDatDonNhieuNhatThangTruocBarChart.getData().clear();
        khoangThoiGianDatDonNhieuNhatThangTruocBarChart.getData().add(barSeries);
        khoangThoiGianDatDonNhieuNhatThangTruocBarChart.setTitle("Khoảng thời gian khách đặt đơn nhiều nhất tháng "+thangTruoc);
    } */
}
