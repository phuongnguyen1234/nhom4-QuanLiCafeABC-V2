package com.frontend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import javax.imageio.ImageIO;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.MonTrongDonDTO;
import com.backend.dto.MonQLy;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ThemVaoThucDonUI {
    @FXML
    private TextField tenMonTextField, donGiaTextField;

    @FXML
    private ImageView anhMinhHoaImageView;

    @FXML
    private ComboBox<DanhMucKhongMonDTO> danhMucCombobox;

    private MonQLy mon;

    private List<DanhMucKhongMonDTO> danhMucList;

    public void setDanhMucList(List<DanhMucKhongMonDTO> danhMucList) {
        this.danhMucList = danhMucList;
        ObservableList<DanhMucKhongMonDTO> observableDanhMucList = FXCollections.observableArrayList(danhMucList);
        danhMucCombobox.setItems(observableDanhMucList);
    }

    @FXML
    public void initialize() {
        try{
            // Cập nhật cách hiển thị tên danh mục trong ComboBox
            danhMucCombobox.setCellFactory(param -> new ListCell<DanhMucKhongMonDTO>() {
                @Override
                protected void updateItem(DanhMucKhongMonDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getTenDanhMuc());  // Hiển thị TenDanhMuc thay vì đối tượng
                }
            });

            danhMucCombobox.setButtonCell(new ListCell<DanhMucKhongMonDTO>() {
                @Override
                protected void updateItem(DanhMucKhongMonDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getTenDanhMuc());  // Cập nhật khi hiển thị trên button
                }
            });
            

            // Chọn mặc định nếu cần (tùy chọn)
            //if (!danhMucList.isEmpty()) {
                //danhMucCombobox.getSelectionModel().selectFirst(); // Chọn mục đầu tiên nếu danh sách không rỗng
            //}

            // Khi người dùng chọn danh mục, bạn có thể lấy mã danh mục
            danhMucCombobox.setOnAction(event -> {
                DanhMucKhongMonDTO selectedDanhMuc = danhMucCombobox.getSelectionModel().getSelectedItem();
                if (selectedDanhMuc != null) {
                    int maDanhMuc = selectedDanhMuc.getMaDanhMuc();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void themVaoThucDon() {
        if (mon == null) {
            mon = new MonQLy();
        }

        int donGia = 0;

        // Kiểm tra và gán giá trị từ giao diện
        if (tenMonTextField.getText().isEmpty()) {
            MessageUtils.showErrorMessage("Tên cà phê không được để trống!");
            return;
        }

        try {
            donGia = Integer.parseInt(donGiaTextField.getText());
            if (donGia <= 0) {
                MessageUtils.showErrorMessage("Đơn giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtils.showErrorMessage("Đơn giá phải là một số nguyên hợp lệ!");
            return;
        }

        if (danhMucCombobox.getValue() == null) {
            MessageUtils.showErrorMessage("Vui lòng chọn danh mục!");
            return;
        }

            // Xử lý ảnh
        if (anhMinhHoaImageView.getImage() != null) {  
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIO.write(SwingFXUtils.fromFXImage(anhMinhHoaImageView.getImage(), null), "jpg", byteArrayOutputStream);
                mon.setAnhMinhHoa(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                MessageUtils.showErrorMessage("Lỗi khi xử lý ảnh minh họa!");
                return;
            }
        } else {
            MessageUtils.showErrorMessage("Vui lòng chọn ảnh minh họa!");
            return;
        }

        mon.setMaMon(null); // Đặt mã món là null để server tự sinh mã mới
        mon.setTenMon(tenMonTextField.getText());
        mon.setDonGia(donGia);
        mon.setTrangThai("Bán");
        mon.setMaDanhMuc(danhMucCombobox.getValue().getMaDanhMuc());

        createRequest(mon);


        // Thông báo thành công
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Thêm cà phê vào thực đơn thành công!");
        alert.showAndWait();

        // Ẩn cửa sổ sau khi cập nhật
        tenMonTextField.getScene().getWindow().hide();
    }


    @FXML
    public void quayLai(){
        tenMonTextField.getScene().getWindow().hide();
    }


    @FXML
    private void chonHinhAnh() {
        // Tạo đối tượng FileChooser
        FileChooser fileChooser = new FileChooser();

        // Cấu hình bộ lọc chỉ cho phép chọn các file ảnh
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        // Hiển thị hộp thoại chọn file
        File selectedFile = fileChooser.showOpenDialog(tenMonTextField.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Đọc file ảnh đã chọn và chuyển thành đối tượng Image
                Image selectedImage = new Image(selectedFile.toURI().toString());

                // Hiển thị ảnh trong ImageView
                anhMinhHoaImageView.setImage(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
                // Thông báo lỗi nếu không thể tải ảnh
                System.err.println("Không thể tải ảnh: " + e.getMessage());
            }
        }
    }

    private void createRequest(MonQLy mon) {
    Task<Void> task = new Task<>() {
        @Override
        protected Void call() throws Exception {
            try {
                // Tạo ObjectMapper để chuyển đối tượng Mon thành JSON
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(mon);
                System.out.println("JSON gửi đi: " + json);
                // Tạo HttpClient
                HttpClient client = HttpClient.newHttpClient();

                // Tạo request POST
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/mon")) // endpoint tạo mới
                        .POST(HttpRequest.BodyPublishers.ofString(json)) // dùng POST thay vì PATCH
                        .header("Content-Type", "application/json")
                        .build();

                // Gửi request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Xử lý phản hồi
                if (response.statusCode() == 201 || response.statusCode() == 200) {
                    System.out.println("Tạo món thành công!");
                    System.out.println("Phản hồi: " + response.body());
                } else {
                    System.err.println("Lỗi khi tạo món. Mã trạng thái: " + response.statusCode());
                    System.err.println("Thông điệp: " + response.body());
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi gửi POST request: " + e.getMessage());
            }

            return null;
        }
    };

    new Thread(task).start();
}


}
