package com.frontend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.backend.dto.DanhMucMonKhongAnhDTO;
import com.backend.dto.MonQLy;
import com.backend.model.DanhMuc;
import com.backend.model.Mon;
import com.backend.utils.ImageUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ChinhSuaMonTrongThucDonUI {
    @FXML
    private TextField tenMonTextField, donGiaTextField;

    @FXML
    private ImageView anhMinhHoaImageView;

    @FXML
    private ComboBox<DanhMucMonKhongAnhDTO> danhMucCombobox;

    @FXML
    private CheckBox trangThaiCheckBox;

    private List<DanhMucMonKhongAnhDTO> danhMucList = new ArrayList<>();

    private MonQLy mon;

    public void setDanhMucList(List<DanhMucMonKhongAnhDTO> danhMucList) {
        this.danhMucList = danhMucList;
        ObservableList<DanhMucMonKhongAnhDTO> observableDanhMucList = FXCollections.observableArrayList(danhMucList);
        danhMucCombobox.setItems(observableDanhMucList);
    }
    
    @FXML
    public void initialize() {
        try {
            danhMucCombobox.setButtonCell(new ListCell<DanhMucMonKhongAnhDTO>() {
            @Override
            protected void updateItem(DanhMucMonKhongAnhDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getTenDanhMuc());  // Cập nhật khi hiển thị trên button
            }
        });
            // Cập nhật cách hiển thị tên danh mục trong ComboBox
            danhMucCombobox.setCellFactory(param -> new ListCell<DanhMucMonKhongAnhDTO>() {
                @Override
                protected void updateItem(DanhMucMonKhongAnhDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getTenDanhMuc());  // Hiển thị TenDanhMuc thay vì đối tượng
                }
            });
            
            // Chọn mặc định nếu cần (tùy chọn)
            if (!danhMucList.isEmpty()) {
                danhMucCombobox.getSelectionModel().selectFirst(); // Chọn mục đầu tiên nếu danh sách không rỗng
            }

            // Khi người dùng chọn danh mục, bạn có thể lấy mã danh mục
            danhMucCombobox.setOnAction(event -> {
                DanhMucMonKhongAnhDTO selectedDanhMuc = danhMucCombobox.getSelectionModel().getSelectedItem();
                if (selectedDanhMuc != null) {
                    int maDanhMuc = selectedDanhMuc.getMaDanhMuc();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMon(MonQLy mon){
        this.mon = mon;
        
        if (mon != null) {
            // Cập nhật tên cà phê và đơn giá
            tenMonTextField.setText(mon.getTenMon());
            donGiaTextField.setText(String.valueOf(mon.getDonGia()));
    
            // Tìm kiếm và chọn danh mục tương ứng trong ComboBox
            for (DanhMucMonKhongAnhDTO item : danhMucCombobox.getItems()) {
                if (item.getMaDanhMuc() == mon.getMaDanhMuc()) {
                    danhMucCombobox.setValue(item); // Chọn mục danh mục trong ComboBox
                    break;
                }
            }            
    
            // Cập nhật ảnh minh họa
            anhMinhHoaImageView.setImage(new Image(new ByteArrayInputStream(ImageUtils.getMonImageByID(mon.getMaMon()))));
            if (mon.getTrangThai().equals("Bán"))
                trangThaiCheckBox.setSelected(true);
            else
                trangThaiCheckBox.setSelected(false);
        }
        
    }

    @FXML
    public void capNhat() {
        if (mon == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không có dữ liệu để cập nhật");
            alert.setContentText("Vui lòng chọn một món để cập nhật!");
            alert.showAndWait();
            return;
        }

        int donGia = 0;

        // Kiểm tra và gán giá trị từ giao diện
        if (tenMonTextField.getText().isEmpty()) {
            MessageUtils.showErrorMessage("Tên món không được để trống!");
            return;
        }

        try {
            donGia = Integer.parseInt(donGiaTextField.getText());
            if (donGia <= 0){
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
        }

        Mon monUpdate = new Mon();
        monUpdate.setMaMon(mon.getMaMon());
        monUpdate.setTenMon(tenMonTextField.getText());
        monUpdate.setDonGia(donGia);
        monUpdate.setTrangThai(trangThaiCheckBox.isSelected() ? "Bán" : "Không bán");

        DanhMuc dm = new DanhMuc();
        dm.setMaDanhMuc(danhMucCombobox.getValue().getMaDanhMuc());
        dm.setTenDanhMuc(danhMucCombobox.getValue().getTenDanhMuc());

        monUpdate.setDanhMuc(dm);
        monUpdate.setAnhMinhHoa(mon.getAnhMinhHoa());
        updateRequest(monUpdate);

        // Thông báo thành công
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Cập nhật món thành công!");
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

    private void updateRequest(Mon mon) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Tạo ObjectMapper để chuyển đổi đối tượng Mon thành JSON
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(mon);

                    // Tạo HttpClient
                    HttpClient client = HttpClient.newHttpClient();

                // Tạo request PATCH
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/mon/" + mon.getMaMon()))
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build();

                // Gửi request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Xử lý phản hồi
                if (response.statusCode() == 200) {
                    System.out.println("Cập nhật thành công!");
                    System.out.println("Phản hồi: " + response.body());
                } else {
                    System.err.println("Lỗi khi cập nhật. Mã trạng thái: " + response.statusCode());
                    System.err.println("Thông điệp: " + response.body());
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Lỗi khi gửi PATCH request: " + e.getMessage());
            }

            return null;
        }
    };

    new Thread(task).start();
    }

    
}
