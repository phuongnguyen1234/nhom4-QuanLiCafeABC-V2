package com.frontend;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.backend.dto.BangLuongDTO;
import com.backend.quanlicapheabc.QuanlicapheabcApplication;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class ChinhSuaBangLuongUI {
    @FXML
    private Text maBangLuongText, tenNhanVienText, loaiNhanVienText, viTriText, thangText, soDonDaTaoText, thuongDoanhThuText, luongThucNhanText;

    @FXML
    private Spinner<Integer> ngayCongSpinner, nghiCoCongSpinner, nghiKhongCongSpinner, gioLamThemSpinner;

    @FXML
    private TextArea ghiChuTextArea;

    @FXML
    private AnchorPane mainAnchorPane;

    private BangLuongDTO bangLuong;

    private boolean dataChanged = false; // Biến để theo dõi thay đổi dữ liệu

    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(QuanlicapheabcApplication.getCookieManager()) // Sử dụng CookieManager chung
            .connectTimeout(Duration.ofSeconds(10)) // Optional: Thêm timeout
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public void initialize() {
        // Thiết lập giá trị tối thiểu, tối đa và bước nhảy
        SpinnerValueFactory<Integer> valueFactoryNgayCong =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 0, 1);
    
        SpinnerValueFactory<Integer> valueFactoryNghiCoCong =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 0, 1);
    
        SpinnerValueFactory<Integer> valueFactoryNghiKhongCong =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 40, 0, 1);
    
        SpinnerValueFactory<Integer> valueFactoryGioLamThem =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0, 1);

        // Gán giá trị ban đầu (giá trị mặc định)
        ngayCongSpinner.setValueFactory(valueFactoryNgayCong);
        nghiCoCongSpinner.setValueFactory(valueFactoryNghiCoCong);
        nghiKhongCongSpinner.setValueFactory(valueFactoryNghiKhongCong);
        gioLamThemSpinner.setValueFactory(valueFactoryGioLamThem);

        // Ràng buộc logic giữa số ngày công và số ngày nghỉ không công
        nghiKhongCongSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (bangLuong != null) {
                int ngayCongThucTe = bangLuong.getNgayCong() - newValue;
                ngayCongSpinner.getValueFactory().setValue(Math.max(ngayCongThucTe, 0));
            }
        });
    }

    public void setBangLuong(BangLuongDTO bangLuong) {
        this.bangLuong = bangLuong;
        loadBangLuongData(); // Load dữ liệu lên form
    }

    private void loadBangLuongData() {
        if (bangLuong != null) {
            maBangLuongText.setText("Mã bảng lương: " + bangLuong.getMaBangLuong());
            tenNhanVienText.setText("Tên nhân viên: " + bangLuong.getHoTen());
            loaiNhanVienText.setText("Loại nhân viên: " + bangLuong.getLoaiNhanVien());
            viTriText.setText("Vị trí: " + bangLuong.getViTri());
            ngayCongSpinner.getValueFactory().setValue(bangLuong.getNgayCong());
            nghiCoCongSpinner.getValueFactory().setValue(bangLuong.getNghiCoCong());
            nghiKhongCongSpinner.getValueFactory().setValue(bangLuong.getNghiKhongCong());
            gioLamThemSpinner.getValueFactory().setValue(bangLuong.getGioLamThem());
            thangText.setText("Tháng: " + bangLuong.getThang().toString());
            soDonDaTaoText.setText("Số đơn đã tạo: " + String.valueOf(bangLuong.getDonDaTao()));
            thuongDoanhThuText.setText("Thưởng doanh thu: " + String.valueOf(String.format("%,d", bangLuong.getThuongDoanhThu())));
            luongThucNhanText.setText("Lương thực nhận: " + String.valueOf(String.format("%,d",bangLuong.getLuongThucNhan())));
            ghiChuTextArea.setText(bangLuong.getGhiChu());
        }
    }

    @FXML
    private void capNhat() {
        if (bangLuong != null) {
            bangLuong.setNgayCong(ngayCongSpinner.getValue());
            bangLuong.setNghiCoCong(nghiCoCongSpinner.getValue());
            bangLuong.setNghiKhongCong(nghiKhongCongSpinner.getValue());
            bangLuong.setGioLamThem(gioLamThemSpinner.getValue());
            bangLuong.setGhiChu(ghiChuTextArea.getText());

            setDisableItems(true); // Khóa UI trong khi gửi request

            // Gửi PUT request dưới dạng task
            Task<Void> updateTask = updateRequest();
            updateTask.setOnSucceeded(event -> {
                setDisableItems(false);
                this.dataChanged = true; // Đánh dấu dữ liệu đã thay đổi
                MessageUtils.showInfoMessage("Cập nhật thành công");
                // Đóng cửa sổ
                maBangLuongText.getScene().getWindow().hide();
            });

            updateTask.setOnFailed(event -> {
                setDisableItems(false);
                Throwable e = updateTask.getException();
                e.printStackTrace();
                // Có thể hiện thông báo lỗi ở đây
                MessageUtils.showErrorMessage("Lỗi khi cập nhật bảng lương!");
            });

            new Thread(updateTask).start();
        }
    }


    @FXML
    private void quayLai(){
        maBangLuongText.getScene().getWindow().hide();
    }

    private void setDisableItems(boolean value){
        mainAnchorPane.setDisable(value);
    }

    private Task<Void> updateRequest() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                String url = "http://localhost:8080/bang-luong/" + bangLuong.getMaBangLuong();
                String requestBody = objectMapper.writeValueAsString(bangLuong);

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Cập nhật thất bại với mã lỗi: " + response.statusCode());
                }

                return null;
            }
        };
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

}
