package com.frontend;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import com.backend.dto.DanhMucKhongMonDTO;
import com.backend.dto.MonDTO;
import com.backend.utils.HttpUtils;
import com.backend.utils.ImageUtils;
import com.backend.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class ChinhSuaMonTrongThucDonUI {

    @FXML
    private TextField tenMonTextField, donGiaTextField;

    @FXML
    private ImageView anhMinhHoaImageView;

    @FXML
    private ComboBox<DanhMucKhongMonDTO> danhMucCombobox;

    @FXML
    private CheckBox trangThaiCheckBox;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Button btnCapNhat, btnQuayLai;

    private MonDTO mon;

    private List<DanhMucKhongMonDTO> danhMucList;

    private File newSelectedImageFile; // Lưu trữ file ảnh mới được chọn (nếu có)

    @FXML
    public void initialize() {
        // Set cách hiển thị của ComboBox
        danhMucCombobox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DanhMucKhongMonDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTenDanhMuc());
            }
        });

        danhMucCombobox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DanhMucKhongMonDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTenDanhMuc());
            }
        });

        // Load danh mục từ backend
        Task<List<DanhMucKhongMonDTO>> loadTask = new Task<>() {
            @Override
            protected List<DanhMucKhongMonDTO> call() throws Exception {
                danhMucList = HttpUtils.getListDanhMucKhongMon();
                return danhMucList;
            }
        };

        mainAnchorPane.setDisable(true);

        loadTask.setOnSucceeded(event -> {
            List<DanhMucKhongMonDTO> list = loadTask.getValue();
            list.removeIf(danhMuc -> danhMuc.getTrangThai().equals("Ngừng hoạt động"));
            danhMucCombobox.getItems().setAll(list);
            if (!danhMucCombobox.getItems().isEmpty()) {
                danhMucCombobox.getSelectionModel().selectFirst();
            }
            mainAnchorPane.setDisable(false);

        });

        loadTask.setOnFailed(event -> {
            event.getSource().getException().printStackTrace();
            MessageUtils.showErrorMessage("Lỗi load danh muc: " + loadTask.getException().getMessage());
            mainAnchorPane.setDisable(false);
        });

        new Thread(loadTask).start();
    }

    public void setMon(MonDTO mon) {
        this.mon = mon;

        if (mon == null) return;

        tenMonTextField.setText(mon.getTenMon());
        donGiaTextField.setText(String.valueOf(mon.getDonGia()));
        trangThaiCheckBox.setSelected("Bán".equals(mon.getTrangThai()));

        // Sử dụng ImageUtils để tải ảnh từ resource path
        Image existingImage = ImageUtils.loadFromResourcesOrDefault(mon.getAnhMinhHoa(), "/icons/loading.png");
        anhMinhHoaImageView.setImage(existingImage);

        // Delay chọn danh mục nếu danh sách chưa load
        Task<Void> waitForComboBoxLoad = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (danhMucCombobox.getItems().isEmpty()) {
                    Thread.sleep(50);
                }
                return null;
            }
        };

        waitForComboBoxLoad.setOnSucceeded(e -> {
            for (DanhMucKhongMonDTO item : danhMucCombobox.getItems()) {
                if (item.getMaDanhMuc() == mon.getMaDanhMuc()) {
                    danhMucCombobox.setValue(item);
                    break;
                }
            }
        });

        new Thread(waitForComboBoxLoad).start();
    }

    @FXML
    public void capNhat() {
        if (mon == null) {
            MessageUtils.showErrorMessage("Không có món nào để cập nhật!");
            return;
        }

        String tenMon = tenMonTextField.getText().trim();
        if (tenMon.isEmpty()) {
            MessageUtils.showErrorMessage("Tên món không được để trống!");
            return;
        }

        int donGia;
        try {
            donGia = Integer.parseInt(donGiaTextField.getText());
            if (donGia <= 0) {
                MessageUtils.showErrorMessage("Đơn giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtils.showErrorMessage("Đơn giá phải là số nguyên hợp lệ!");
            return;
        }

        DanhMucKhongMonDTO selectedDanhMuc = danhMucCombobox.getValue();
        if (selectedDanhMuc == null) {
            MessageUtils.showErrorMessage("Vui lòng chọn danh mục!");
            return;
        }

        MonDTO monUpdate = new MonDTO();
        monUpdate.setMaMon(mon.getMaMon());
        monUpdate.setTenMon(tenMon);
        monUpdate.setDonGia(donGia);
        monUpdate.setTrangThai(trangThaiCheckBox.isSelected() ? "Bán" : "Không bán");
        monUpdate.setMaDanhMuc(selectedDanhMuc.getMaDanhMuc());
        
        // Xử lý ảnh minh họa
        if (this.newSelectedImageFile != null) { // Nếu người dùng đã chọn ảnh mới
            String originalFileName = newSelectedImageFile.getName();
            String fileExtension = "";
            int lastDotIndex = originalFileName.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
                fileExtension = originalFileName.substring(lastDotIndex);
            }
            String newImageName = UUID.randomUUID().toString() + fileExtension;
            Path targetDirectory = Paths.get("src/main/resources/images/mon");

            try {
                if (!Files.exists(targetDirectory)) {
                    Files.createDirectories(targetDirectory);
                }
                Path targetPath = targetDirectory.resolve(newImageName);
                Files.copy(newSelectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                monUpdate.setAnhMinhHoa("/images/mon/" + newImageName);
            } catch (IOException e) {
                e.printStackTrace();
                MessageUtils.showErrorMessage("Lỗi khi lưu ảnh minh họa mới! " + e.getMessage());
                return;
            }
        } else { // Giữ lại ảnh cũ nếu không chọn ảnh mới
            monUpdate.setAnhMinhHoa(this.mon.getAnhMinhHoa());
        }
        
        //debug
        System.out.println("Cập nhật món: " + monUpdate.getTenMon());
        System.out.println("Đơn giá: " + monUpdate.getDonGia());
        System.out.println("Mã danh mục: " + monUpdate.getMaDanhMuc());
        System.out.println("Trạng thái: " + monUpdate.getTrangThai());
        
        // Disable form khi bắt đầu xử lý
        mainAnchorPane.setDisable(true);
        btnCapNhat.setDisable(true);
        btnQuayLai.setDisable(true);

        Task<Void> requestTask = updateRequest(monUpdate);

        requestTask.setOnSucceeded(event -> {
            MessageUtils.showInfoMessage("Cập nhật thành công!");
            btnCapNhat.setDisable(false);
            btnQuayLai.setDisable(false);
            mainAnchorPane.setDisable(false);
            tenMonTextField.getScene().getWindow().hide();
        });

        requestTask.setOnFailed(event -> {
            Throwable ex = requestTask.getException();
            ex.printStackTrace();
            MessageUtils.showErrorMessage("Lỗi khi cập nhật: " + ex.getMessage());
            btnCapNhat.setDisable(false);
            btnQuayLai.setDisable(false);
            mainAnchorPane.setDisable(false); // Enable lại nếu lỗi
        });

        requestTask.setOnCancelled(event -> {
            btnCapNhat.setDisable(false);
            btnQuayLai.setDisable(false);
        });

        new Thread(requestTask).start();
    }

    @FXML
    public void quayLai() {
        tenMonTextField.getScene().getWindow().hide();
    }

    @FXML
    private void chonHinhAnh() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif")
        );

        File file = fileChooser.showOpenDialog(tenMonTextField.getScene().getWindow());

        if (file != null) {
            this.newSelectedImageFile = file; // Lưu file ảnh mới được chọn
            try {
                Image img = new Image(file.toURI().toString());
                anhMinhHoaImageView.setImage(img);
            } catch (Exception e) {
                e.printStackTrace();
                this.newSelectedImageFile = null; // Reset nếu có lỗi
                MessageUtils.showErrorMessage("Không thể tải ảnh: " + e.getMessage());
            }
        }
    }

    private Task<Void> updateRequest(MonDTO mon) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(mon);

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/mon/" + mon.getMaMon()))
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                        .header("Content-Type", "application/json")
                        .build();

                    //lay string JSON gui di
                    System.out.println("JSON gửi đi: " + json);
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() != 200) {
                        System.err.println("Lỗi cập nhật: " + response.statusCode());
                        System.err.println("Phản hồi: " + response.body());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Lỗi khi gửi PATCH request: " + e.getMessage());
                }
                return null;
            }
        };
    }
}
