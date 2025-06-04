package com.frontend;

import java.io.IOException;

import com.backend.model.NhanVien;
import com.backend.model.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TrangChuUI {
    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Button btnThucDon, btnHoaDon, btnNhanVien, btnBangLuong, btnThongKe;

    @FXML
    private ImageView profileImage;

    @FXML
    private Text tenNguoiDungText;

    //khoi tao 1 controller cho cac chuc nang
    private ThucDonUI thucDonController = new ThucDonUI();
    private QuanLiThucDonUI quanLiThucDonController = new QuanLiThucDonUI();
    private QuanLiDanhMucUI quanLiDanhMucController = new QuanLiDanhMucUI();
    private HoaDonUI hoaDonController = new HoaDonUI();
    private NhanVienUI nhanVienController = new NhanVienUI();
    private BangLuongUI bangLuongController = new BangLuongUI();
    private ThongKeUI thongKeUI = new ThongKeUI();

    private NhanVien nhanVien;

    public TrangChuUI getTrangChuUI() {
        return this;
    }

    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    @FXML
    public void initialize() {
       /*  try {
            // Khai báo và lấy thông tin nhân viên (chỉ gọi 1 lần)
            
            nhanVien = SessionManager.getNhanVienByCurrentSession();
            if (nhanVien != null) {
                // Cập nhật tên người dùng
                tenNguoiDungText.setText(nhanVien.getTenNhanVien());

                // Cập nhật ảnh đại diện (nếu có)
                byte[] anhChanDung = nhanVien.getAnhChanDung();
                if (anhChanDung != null && anhChanDung.length > 0) {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(anhChanDung);
                    profileImage.setImage(new Image(byteArrayInputStream));
                } else {
                    profileImage.setImage(new Image("/icons/profile.png"));
                }

                // Thiết lập quyền truy cập
                if (nhanVien.getViTri().equalsIgnoreCase("Chủ quán")) {
                    btnNhanVien.setDisable(true);
                    btnBangLuong.setDisable(true);
                    btnThongKe.setDisable(true);
                } else if (nhanVien.getViTri().equalsIgnoreCase("Thu ngân")) {
                    btnNhanVien.setDisable(false);
                    btnBangLuong.setDisable(false);
                    btnThongKe.setDisable(false);
                }

                // Đảm bảo mainBorderPane đã được khởi tạo
                Platform.runLater(() -> {
                    if (mainBorderPane != null) {
                        Stage currentStage = (Stage) mainBorderPane.getScene().getWindow();
                        currentStage.setOnCloseRequest(event -> {
                            try {
                                String currentSessionId = SessionManager.getCurrentSessionId();
                                if(currentSessionId != null){
                                    NhanVien nhanVienHienTai = SessionManager.getNhanVienByCurrentSession();
                                    if(nhanVien!=null){
                                        //NhanVienController.capNhatTrangThaiHoatDong(nhanVien.getEmail(), "0");
                                    SessionManager.removeSession(SessionManager.getCurrentSessionId());
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Platform.exit();
                        });
                    }
                });
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi khởi tạo giao diện: " + e.getMessage());
        } */
        thucDon();

    }

    @FXML
    public void thucDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/thucDon.fxml"));
            Node content = loader.load();

            // Lấy controller từ FXML đã tải
            ThucDonUI screenController = loader.getController();
            // Truyền đối tượng TaoDonGoiDoMoiController đã khởi tạo từ trước
            //screenController.setThucDonUI(taoDonController);
            //screenController.setCapNhatThucDonController(capNhatThucDonController);
            
            screenController.setTrangChuUI(this);
            //screenController.setNhanVien(nhanVien);

            // Hiển thị danh sách đồ uống trong đơn hàng
            screenController.hienThiDanhSachMonTrongDon();

            // Hiển thị thực đơn
            //screenController.hienThiThucDon(taoDonController.layThucDon("all"));
            //screenController.datLai();

            // Đặt nội dung vào phần center của BorderPane
            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/thucDon.fxml");
        }
    }


    @FXML
    private void hoaDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/hoaDon.fxml"));
            Node content = loader.load();
            
            // Lấy controller từ FXML đã tải
            HoaDonUI screenController = loader.getController();

            //screenController.setDonHangController(donHangController);
            //screenController.setNhanVien(nhanVien);
            //screenController.hienThiDanhSachDonHang(donHangController.loadDonHangFromDatabase());
            // Đặt nội dung vào phần center của BorderPane
            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/hoaDon.fxml");
        }
    }

    @FXML
    private void nhanVien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/nhanVien.fxml"));
            Node content = loader.load();

            NhanVienUI screen = loader.getController();

            //screen.setQuanLiHoSoController(quanLiHoSoController);
            //screen.hienThiDanhSachNhanVien(quanLiHoSoController.layDanhSachNhanVien());

            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/nhanVien.fxml");
        }
    }

    @FXML
    private void bangLuong() {
        // Tải nội dung FXML và lấy controller của nó
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/bangLuong.fxml"));
            Node content = loader.load();
            
            // Lấy controller từ FXML đã tải
            BangLuongUI screenController = loader.getController();

            //screenController.setTaoLuongController(taoLuongController);
            //screenController.hienThiDanhSachBangLuong(taoLuongController.layDanhSachBangLuongThangNay());
            // Đặt nội dung vào phần center của BorderPane
            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/bangLuong.fxml");
        }
    }


    @FXML
    private void thongKe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_screen/thongKe.fxml"));
            Node content = loader.load();
            
            // Lấy controller từ FXML đã tải
            ThongKeUI screenController = loader.getController();

            //screenController.setPhanTichHoatDongController(phanTichHoatDongController);
            //screenController.hienThiSoLieuThongKe();
            // Đặt nội dung vào phần center của BorderPane
            mainBorderPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Không thể tải file FXML: /fxml/main_screen/dashboard.fxml");
        }
    }

    // Method to load center content based on action
    private void loadCenterContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            mainBorderPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method for setting user access rights (disable buttons based on role)
    @FXML
    public void thietLapQuyenTruyCap(NhanVien nhanVien) {
        if (nhanVien.getViTri().equalsIgnoreCase("Thu ngân")) {
            // Disable buttons for user role
            btnNhanVien.setDisable(true);
            btnBangLuong.setDisable(true);
            btnThongKe.setDisable(true);
        }
    }

    @FXML
    private void dangXuat(ActionEvent event) {
        // Gọi phương thức logout
        SessionManager.dangXuatUserHienTai();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dangNhapScreen.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Đăng nhập");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}
