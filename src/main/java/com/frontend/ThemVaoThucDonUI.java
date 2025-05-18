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
import com.backend.dto.MonQLy;
import com.backend.utils.HttpUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ThemVaoThucDonUI {
    @FXML
    private TextField tenMonTextField, donGiaTextField;

    @FXML
    private ImageView anhMinhHoaImageView;

    @FXML
    private ComboBox<DanhMucKhongMonDTO> danhMucCombobox;

    @FXML
    private Button btnThemVaoThucDon, btnQuayLai;

    private MonQLy mon;

    private List<DanhMucKhongMonDTO> danhMucList;

    @FXML
    private AnchorPane mainAnchorPane;

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

            // Khi người dùng chọn danh mục, bạn có thể lấy mã danh mục
            danhMucCombobox.setOnAction(event -> {
                DanhMucKhongMonDTO selectedDanhMuc = danhMucCombobox.getSelectionModel().getSelectedItem();
                if (selectedDanhMuc != null) {
                    int maDanhMuc = selectedDanhMuc.getMaDanhMuc();
                }
            });

            Task<List<DanhMucKhongMonDTO>> loadDanhMucTask = new Task<>() {
                @Override
                protected List<DanhMucKhongMonDTO> call() throws Exception {
                    danhMucList = HttpUtils.getListDanhMucKhongMon();
                    return danhMucList;
                }
            };

            // Disable toàn bộ dialog trong khi loading
            mainAnchorPane.setDisable(true);

            // Khi load thành công:
            loadDanhMucTask.setOnSucceeded(event -> {
                List<DanhMucKhongMonDTO> list = loadDanhMucTask.getValue();
                // loai bo cac danh muc co trang thai "Ngừng hoạt động"
                list.removeIf(danhMuc -> danhMuc.getTrangThai().equals("Ngừng hoạt động"));
                danhMucCombobox.getItems().setAll(list);
                mainAnchorPane.setDisable(false); // enable lại
            });

            // Nếu có lỗi:
            loadDanhMucTask.setOnFailed(event -> {
                Throwable ex = loadDanhMucTask.getException();
                ex.printStackTrace(); // hoặc hiện alert
                mainAnchorPane.setDisable(false); // vẫn enable lại để không bị kẹt
            });

            // Chạy task:
            new Thread(loadDanhMucTask).start();

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

    // Kiểm tra đầu vào
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
        MessageUtils.showErrorMessage("Đơn giá phải là số hợp lệ!");
        return;
    }

    if (danhMucCombobox.getValue() == null) {
        MessageUtils.showErrorMessage("Vui lòng chọn danh mục!");
        return;
    }

    if (anhMinhHoaImageView.getImage() != null) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(SwingFXUtils.fromFXImage(anhMinhHoaImageView.getImage(), null), "jpg", byteArrayOutputStream);
            mon.setAnhMinhHoa(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            MessageUtils.showErrorMessage("Lỗi ảnh minh họa!");
            return;
        }
    } else {
        MessageUtils.showErrorMessage("Vui lòng chọn ảnh minh họa!");
        return;
    }

    mon.setMaMon(null);
    mon.setTenMon(tenMonTextField.getText());
    mon.setDonGia(donGia);
    mon.setTrangThai("Bán");
    mon.setMaDanhMuc(danhMucCombobox.getValue().getMaDanhMuc());

    // Disable form khi bắt đầu xử lý
    mainAnchorPane.setDisable(true);
    btnThemVaoThucDon.setDisable(true);
    btnQuayLai.setDisable(true);

    Task<Void> requestTask = createRequest(mon);

    requestTask.setOnSucceeded(event -> {
        MessageUtils.showInfoMessage("Thêm món thành công!");
        btnThemVaoThucDon.setDisable(false);
        btnQuayLai.setDisable(false);
        tenMonTextField.getScene().getWindow().hide();
    });

    requestTask.setOnFailed(event -> {
        Throwable ex = requestTask.getException();
        ex.printStackTrace();
        MessageUtils.showErrorMessage("Lỗi khi thêm món: " + ex.getMessage());
        mainAnchorPane.setDisable(false); // Enable lại nếu lỗi
        btnThemVaoThucDon.setDisable(false);
        btnQuayLai.setDisable(false);
    });

    requestTask.setOnCancelled(e -> {
            btnThemVaoThucDon.setDisable(false);
            btnQuayLai.setDisable(false);
        });

    new Thread(requestTask).start();
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

    private Task<Void> createRequest(MonQLy mon) {
    return new Task<>() {
        @Override
        protected Void call() throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(mon);
            System.out.println("JSON: " + json);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/mon"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201 && response.statusCode() != 200) {
                throw new RuntimeException("Lỗi khi tạo món. Mã trạng thái: " + response.statusCode());
            }

            return null;
        }
    };
}



}
