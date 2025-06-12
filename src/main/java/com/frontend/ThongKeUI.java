package com.frontend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.backend.dto.ThongKeDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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

    private TrangChuUI trangChuUI;

    public void setTrangChuUI(TrangChuUI trangChuUI) {
        this.trangChuUI = trangChuUI;
    }

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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

        // Cấu hình trục X cho biểu đồ cột
        String[] slots = {
            "00-02", "02-04", "04-06", "06-08", "08-10", "10-12",
            "12-14", "14-16", "16-18", "18-20", "20-22", "22-00"
        };
        barXAxis.setCategories(FXCollections.observableArrayList(slots));

        // Gọi kiểm tra và tổng hợp doanh thu tháng trước
        Task<String> checkAggregateTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/doanh-thu/kiem-tra-tong-hop-thang-truoc"))
                        .POST(HttpRequest.BodyPublishers.noBody()) // Sử dụng POST
                        .timeout(Duration.ofSeconds(25)) // Tăng timeout phòng trường hợp tổng hợp lâu
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return response.body();
                } else {
                    System.err.println("Lỗi khi kiểm tra/tổng hợp doanh thu tháng trước: " + response.statusCode() + " - " + response.body());
                    throw new IOException("Lỗi từ server: " + response.statusCode() + " - " + response.body());
                }
            }
        };

        checkAggregateTask.setOnSucceeded(event -> {
            String resultMessage = checkAggregateTask.getValue();
            System.out.println("Kết quả kiểm tra/tổng hợp doanh thu tháng trước: " + resultMessage);
            // Có thể hiển thị thông báo nhỏ cho người dùng nếu muốn
            // MessageUtils.showInfoMessage("Thông báo", resultMessage);
            hienThiSoLieuThongKe(); // Sau đó mới hiển thị số liệu thống kê
        });

        checkAggregateTask.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi khi kiểm tra hoặc tổng hợp doanh thu tháng trước: " + checkAggregateTask.getException().getMessage());
            checkAggregateTask.getException().printStackTrace();
            hienThiSoLieuThongKe(); // Vẫn cố gắng hiển thị số liệu thống kê hiện có
        });

        new Thread(checkAggregateTask).start();
    }

    public void hienThiSoLieuThongKe() {
        Task<ThongKeDTO> task = new Task<>() {
            @Override
            protected ThongKeDTO call() throws Exception {
                return layDuLieuThongKe();
            }
        };

        task.setOnSucceeded(event -> {
            ThongKeDTO dto = task.getValue();
            if (dto != null) {
                Platform.runLater(() -> {
                    // Hiển thị số liệu tổng quan
                    tieuDeDoanhThuHomNayText.setText("Doanh thu hôm nay (" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
                    doanhThuHomNayText.setText(String.format("%,d VND", dto.getDoanhThuHomNay()));

                    LocalDate prevMonth = LocalDate.now().minusMonths(1);
                    String thangTruocFormatted = prevMonth.format(DateTimeFormatter.ofPattern("MM/yyyy"));

                    tieuDeDoanhThuThangTruocText.setText("Doanh thu tháng " + thangTruocFormatted);
                    doanhThuThangTruocText.setText(String.format("%,d VND", dto.getTongDoanhThuThangTruoc()));

                    tieuDeSoMonBanRaThangTruocText.setText("Số món bán ra tháng " + thangTruocFormatted);
                    soMonBanRaThangTruocText.setText(String.valueOf(dto.getSoMon()));

                    tieuDeSoDonDaTaoThangTruocText.setText("Số đơn đã tạo tháng " + thangTruocFormatted);
                    soDonDaTaoThangTruocText.setText(String.valueOf(dto.getSoDon()));

                    // Top 5 món bán chạy tháng trước
                    top5CaPheBanChayNhatThangTruocLabel.setText("Top 5 món bán chạy nhất tháng " + thangTruocFormatted);
                    List<Map.Entry<String, Integer>> top5MonList = dto.getTop5MonBanChay().entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                            .limit(5)
                            .collect(Collectors.toList());

                    Text[] tenMonTexts = {tenMonTop1Text, tenMonTop2Text, tenMonTop3Text, tenMonTop4Text, tenMonTop5Text};
                    Text[] soMonTexts = {soMonTop1Text, soMonTop2Text, soMonTop3Text, soMonTop4Text, soMonTop5Text};
                    for (int i = 0; i < tenMonTexts.length; i++) {
                        if (i < top5MonList.size()) {
                            tenMonTexts[i].setText(top5MonList.get(i).getKey());
                            soMonTexts[i].setText(String.valueOf(top5MonList.get(i).getValue()));
                        } else {
                            tenMonTexts[i].setText("-");
                            soMonTexts[i].setText("-");
                        }
                    }

                    // Top 3 nhân viên tạo đơn nhiều nhất tháng trước
                    top3NhanVienTaoDonNhieuNhatThangTruocLabel.setText("Top 3 nhân viên tạo đơn nhiều nhất tháng " + thangTruocFormatted);
                    List<Map.Entry<String, Integer>> top3NhanVienList = dto.getTop3NhanVien().entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                            .limit(3)
                            .collect(Collectors.toList());

                    Text[] tenNhanVienTexts = {tenNhanVienTop1Text, tenNhanVienTop2Text, tenNhanVienTop3Text};
                    Text[] soDonNhanVienTexts = {soDonTop1Text, soDonTop2Text, soDonTop3Text};
                    for (int i = 0; i < tenNhanVienTexts.length; i++) {
                        if (i < top3NhanVienList.size()) {
                            tenNhanVienTexts[i].setText(top3NhanVienList.get(i).getKey());
                            soDonNhanVienTexts[i].setText(String.valueOf(top3NhanVienList.get(i).getValue()));
                        } else {
                            tenNhanVienTexts[i].setText("-");
                            soDonNhanVienTexts[i].setText("-");
                        }
                    }

                    // Biểu đồ đường biến động doanh thu 6 tháng gần nhất
                    bienDongDoanhThuLineChart.getData().clear();
                    XYChart.Series<String, Integer> lineSeries = new XYChart.Series<>();
                    lineSeries.setName("Doanh thu");
                    if (dto.getDoanhThuTrongThoiGian() != null) {
                        // Sắp xếp theo key (tháng-năm) để đảm bảo thứ tự đúng trên biểu đồ
                        dto.getDoanhThuTrongThoiGian().entrySet().stream()
                           .sorted(Map.Entry.comparingByKey())
                           .forEach(entry -> lineSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
                    }
                    bienDongDoanhThuLineChart.getData().add(lineSeries);
                    // Tiêu đề nên phản ánh đúng khoảng thời gian của dữ liệu (6 tháng trước, không bao gồm tháng hiện tại)
                    LocalDate endDateForTitle = LocalDate.now().minusMonths(1); // Tháng trước
                    LocalDate startDateForTitle = endDateForTitle.minusMonths(5); // 5 tháng trước của tháng trước
                    bienDongDoanhThuLineChart.setTitle("Biến động doanh thu từ " +
                        startDateForTitle.format(DateTimeFormatter.ofPattern("MM/yyyy")) + " - " +
                        endDateForTitle.format(DateTimeFormatter.ofPattern("MM/yyyy")));

                    // Biểu đồ cột khoảng thời gian đặt đơn nhiều nhất tháng trước
                    khoangThoiGianDatDonNhieuNhatThangTruocBarChart.getData().clear();
                    XYChart.Series<String, Integer> barSeries = new XYChart.Series<>();
                    barSeries.setName("Số đơn");
                     if (dto.getKhoangThoiGianDatDonNhieuNhat() != null) {
                        // Sắp xếp các slot theo thứ tự đã định nghĩa để hiển thị đúng trên biểu đồ
                        String[] slotOrder = {
                            "00-02", "02-04", "04-06", "06-08", "08-10", "10-12",
                            "12-14", "14-16", "16-18", "18-20", "20-22", "22-00"
                        };
                        Map<String, Integer> thoiGianData = dto.getKhoangThoiGianDatDonNhieuNhat();
                        for (String slotKey : slotOrder) {
                            barSeries.getData().add(new XYChart.Data<>(slotKey, thoiGianData.getOrDefault(slotKey, 0)));
                        }
                    }
                    khoangThoiGianDatDonNhieuNhatThangTruocBarChart.getData().add(barSeries);
                    khoangThoiGianDatDonNhieuNhatThangTruocBarChart.setTitle("Khoảng thời gian đặt đơn nhiều nhất tháng " + thangTruocFormatted);
                });
            } else {
                MessageUtils.showErrorMessage("Không thể tải dữ liệu thống kê.");
            }
        });

        task.setOnFailed(event -> {
            MessageUtils.showErrorMessage("Lỗi khi tải dữ liệu thống kê: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    @FXML
    public void xemChiTiet(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/sub_forms/chiTietDoanhThu.fxml"));
            Parent root = loader.load();

            ChiTietDoanhThuUI controller = loader.getController();
            controller.setTrangChuUI(trangChuUI);
            trangChuUI.getMainBorderPane().setCenter(root);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private ThongKeDTO layDuLieuThongKe() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/doanh-thu/tong-quan"))
                .GET()
                .timeout(Duration.ofSeconds(15))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), ThongKeDTO.class);
        } else {
            System.err.println("Lỗi khi lấy dữ liệu thống kê: " + response.statusCode() + " - " + response.body());
            throw new IOException("Lỗi khi lấy dữ liệu thống kê: " + response.statusCode() + " - " + response.body());
        }
    }
}
