package com.email.mailclient.controllers;

import com.email.mailclient.model.EmailMessage;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class MainController extends CommonController implements Initializable {


    private String personalEmail = "";
    private String username = "";
    private Properties config;
    private Socket socket;
    static DataInputStream dis;
    static DataOutputStream dos;
    private String loginResponse;
    private String reloadCommand = "";
    private EmailMessage[] emailMessages;

    private List<EmailMessage> listSelectedEmail = new ArrayList<>();
    EmailMessage selectedEmail;
    @FXML
    private AnchorPane mainComponent;

    @FXML
    private VBox listEmailComponent;

    Popup popup;

    @FXML
    private HBox menuButton;

    @FXML
    void reloadListEmail() {
        fetchListEmail(reloadCommand);
        menuButton.getChildren().clear();
    }


    @FXML
    private TableView<EmailMessage> tableViewEmails;
    @FXML
    private TableColumn<EmailMessage, StringProperty> fromColumn;
    @FXML
    private TableColumn<EmailMessage, StringProperty> subjectColumn;
    @FXML
    private TableColumn<EmailMessage, Date> dateColumn;
//    @FXML
//    private TableColumn<EmailMessage, Boolean> isReadColumn;

    @FXML
    private TextField txtSearchInformation;
    @FXML
    private Label txtUserName;

    @FXML
    void loadReceived() {
        fetchListEmail("SELECT inbox");
        reloadCommand = "SELECT inbox";
        menuButton.getChildren().clear();
    }

    @FXML
    void loadSent() {
        fetchListEmail("SELECT outbox");
        reloadCommand = "SELECT outbox";
        menuButton.getChildren().clear();
    }

    @FXML
    void loadTrash() {
        fetchListEmail("SELECT recycle");
        reloadCommand = "SELECT recycle";
        menuButton.getChildren().clear();
    }


    public MainController(Properties config, Socket socket, String personalEmail, String loginResponse) {
        super();
        this.config = config;
        this.personalEmail = personalEmail;
        this.socket = socket;
        this.loginResponse = loginResponse;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUserName();
        initializeTableColumns();
        fetchListEmail("SELECT inbox");
        // Khởi tạo listSelectedEmail
        listSelectedEmail = new ArrayList<>();
        // Sự kiện click và double click trên tableViewEmails
        tableViewEmails.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                // Xử lý sự kiện click
                selectEmail();
            } else if (event.getClickCount() == 2) {
                // Xử lý sự kiện double click
                loadEmailDetail();
            }
        });

// phân biệt thư đã đọc và chưa đọc
//        tableViewEmails.setRowFactory(tv -> {
//            TableRow<EmailMessage> row = new TableRow<>();
//            row.itemProperty().addListener((obs, oldItem, newItem) -> {
//                if (newItem != null) {
//                    // Kiểm tra xem email đã đọc hay chưa và đặt màu sắc tương ứng
//                    if (newItem.isRead()) {
//                        row.setStyle("-fx-background-color: #F0F0F0;"); // Màu cho email đã đọc
//                    }
//                    else {
//                        row.setStyle("-fx-background-color: #F0F0F0;"); // Màu cho email chưa đọc
//                    }
//                }
//            });
//            return row;
//        });


        // hover
//        tableViewEmails.setRowFactory(tv -> {
//            TableRow<EmailMessage> row = new TableRow<>();
//            row.setOnMouseEntered(event -> {
//                if (!row.isEmpty()) {
//                    showPopupOnHover(row, row.getItem());
//                }
//            });
//            row.setOnMouseExited(event -> {
//                // Ẩn Popup khi rời đi
//                popup.hide();
//            });
//            return row;
//        });


        txtUserName.setOnMouseEntered(event -> {

            popup = new Popup();
            Label popupLabel = new Label("This is a Popup!");
            popupLabel.setStyle("-fx-background-color: white; -fx-padding: 10px;");
            popup.getContent().add(popupLabel);
            // Lấy tọa độ của nhãn trên màn hình
            double x = txtUserName.localToScreen(txtUserName.getBoundsInLocal()).getMinX() + 150;
            double y = txtUserName.localToScreen(txtUserName.getBoundsInLocal()).getMinY() + 50;

            // Hiển thị Popup tại vị trí của nhãn
            popup.show(txtUserName, x, y);
        });

        txtUserName.setOnMouseExited(event -> {
            popup.hide();
        });
    }


    private void selectEmail() {
        // Lấy dòng được chọn
        selectedEmail = tableViewEmails.getSelectionModel().getSelectedItem();

        if (selectedEmail != null) {
            if (listSelectedEmail.contains(selectedEmail)) {
                // Nếu email đã được chọn, hủy chọn và xóa nút
                listSelectedEmail.remove(selectedEmail);
                menuButton.getChildren().clear();
            } else {

                listSelectedEmail.clear();
                menuButton.getChildren().clear();
                // Nếu email chưa được chọn, thêm vào danh sách và tạo nút xóa
                listSelectedEmail.add(selectedEmail);

                if (reloadCommand.equals("SELECT recycle")) {
                    Button btnRestore = new Button("Khôi phục");
                    btnRestore.setOnAction(e -> handleRestoreEmail(selectedEmail.getId()));
                    menuButton.getChildren().add(btnRestore);


                    Button btnDelete = new Button("Xóa");
                    btnDelete.setOnAction(e -> handleDeleteEmail(selectedEmail.getId()));
                    menuButton.getChildren().add(btnDelete);
                } else {

                    Button btnDelete = new Button("Xóa");
                    btnDelete.setOnAction(e -> handleDeleteEmail(selectedEmail.getId()));
                    menuButton.getChildren().add(btnDelete);
                }

            }
        }
    }

    private void handleDeleteEmail(int idEmail) {

        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF("DELETE " + idEmail);

            System.out.println("DELETE " + idEmail);
            String Response = dis.readUTF();

            System.out.println(Response);
            if (Response.equals("OK")) {

                // cập nhật lại danh sách email
                fetchListEmail(reloadCommand);
                //hủy chọn
                listSelectedEmail.remove(selectedEmail);
                //xóa nút xóa
                menuButton.getChildren().clear();
            } else {
                System.out.println("Có lỗi xảy ra");
            }
        } catch (IOException e) {
            showError("Lỗi khi giao tiếp với máy chủ: " + e.getMessage());
        }

    }


    private void handleRestoreEmail(int idEmail) {

        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF("RESTORE " + idEmail);

            System.out.println("RESTORE " + idEmail);
            String Response = dis.readUTF();

            System.out.println(Response);
            if (Response.equals("OK")) {

                // cập nhật lại danh sách email
                fetchListEmail(reloadCommand);
                //hủy chọn
                listSelectedEmail.remove(selectedEmail);
                //xóa các nút
                menuButton.getChildren().clear();
            } else {
                System.out.println("Có lỗi xảy ra");
            }
        } catch (IOException e) {
            showError("Lỗi khi giao tiếp với máy chủ: " + e.getMessage());
        }

    }


    private void initializeTableColumns() {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));
//        isReadColumn.setCellValueFactory(new PropertyValueFactory<>("isRead"));
    }

    private void fetchListEmail(String command) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF(command);
            String Response = dis.readUTF();

            String[] responseParts = Response.split(" ", 2);
            if (responseParts.length >= 2) {
                String jsonPart = responseParts[1];
                generateEmailList(jsonPart);
            } else {
                System.out.println("Có lỗi xảy ra");
            }
        } catch (IOException e) {
            showError("Lỗi khi giao tiếp với máy chủ: " + e.getMessage());
        }
    }

    private void setUserName() {
        String[] responseParts = loginResponse.split(" ", 4);

        if (responseParts.length >= 4 && "OK".equals(responseParts[0])) {
            String username = responseParts[3];
            this.username = username;
            txtUserName.setText("Chào mừng, " + username);
        }
    }


    private void generateEmailList(String jsonResponse) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            showError("Phản hồi từ máy chủ trống hoặc null.");
            return;
        }

        Platform.runLater(() -> {
            try {
                Gson gson = new Gson();

                emailMessages = gson.fromJson(jsonResponse, EmailMessage[].class);

                if (emailMessages != null && emailMessages.length > 0) {
                    ObservableList<EmailMessage> emailList = FXCollections.observableArrayList(emailMessages);

                    if (this.tableViewEmails != null) {
                        this.tableViewEmails.setItems(emailList);
                    } else {
                        System.out.println("Hộp thư trống. (tableViewEmails is null)");
                        // Hoặc hiển thị thông báo lỗi khác tùy thuộc vào yêu cầu của bạn
                    }
                } else {
                    if (this.tableViewEmails != null) {
                        // Đặt tableViewEmails thành một danh sách rỗng khi không có email nào
                        this.tableViewEmails.setItems(FXCollections.emptyObservableList());
                    }
//                    showError("Hộp thư trống!");
                }
            } catch (JsonParseException e) {
                showError("Lỗi khi chuyển đổi dữ liệu JSON: " + e.getMessage());
            }
        });


    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void loadEmailDetail() {
        // Lấy dòng được chọn
        selectedEmail = tableViewEmails.getSelectionModel().getSelectedItem();

        if (selectedEmail != null) {
            System.out.println("Bạn đã chọn email  " + selectedEmail.getId());


            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/email/mailclient/email-details.fxml"));
                loader.setControllerFactory(controllerClass -> new EmailDetailsController(config, socket, selectedEmail, loginResponse));
                Parent root = loader.load();

                // Create a new stage
                Stage composeStage = new Stage();
                composeStage.initStyle(StageStyle.DECORATED);
                composeStage.initModality(Modality.APPLICATION_MODAL);
                composeStage.setTitle("Chi Tiết Thư");

                // Set the scene
                Scene scene = new Scene(root);
                composeStage.setScene(scene);

                // Show the stage
                composeStage.show();
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
    }


    @FXML
    public void loadComposeEmail() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/email/mailclient/compose-email.fxml"));
            loader.setControllerFactory(c -> new ComposeEmailController(config, personalEmail));
            Parent root = loader.load();

            // Create a new stage
            Stage composeStage = new Stage();
            composeStage.initStyle(StageStyle.DECORATED);
            composeStage.initModality(Modality.APPLICATION_MODAL);
            composeStage.setTitle("Compose Email");

            // Set the scene
            Scene scene = new Scene(root);
            composeStage.setScene(scene);

            // Show the stage
            composeStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

    }


    @FXML
    private void searchAction() {
        String keyword = txtSearchInformation.getText();

        // Kiểm tra xem có từ khóa tìm kiếm hay không
        if (!keyword.isEmpty()) {
            // Thực hiện tìm kiếm trong danh sách emailMessages
            EmailMessage[] results = searchEmailsByTitle(keyword);

            // Cập nhật giao diện người dùng với kết quả tìm kiếm
            if (results.length > 0) {
                ObservableList<EmailMessage> emailList = FXCollections.observableArrayList(results);

                if (this.tableViewEmails != null) {
                    this.tableViewEmails.setItems(emailList);
                } else {
                    System.out.println("Hộp thư trống.");
                }
            } else {
                showError("Không tìm thấy kết quả cho từ khóa tìm kiếm.");
            }
        } else {
            // Hiển thị thông báo nếu người dùng chưa nhập từ khóa
            showError("Vui lòng nhập từ khóa tìm kiếm.");
        }
    }

    private EmailMessage[] searchEmailsByTitle(String keyword) {
        List<EmailMessage> results = new ArrayList<>();

        // Kiểm tra xem danh sách emailMessages có tồn tại và không rỗng
        if (emailMessages != null && emailMessages.length > 0) {
            // Thực hiện tìm kiếm trong danh sách hiện tại
            for (EmailMessage message : emailMessages) {
                if (message.getSubject().toLowerCase().contains(keyword.toLowerCase())) {
                    results.add(message);
                }
            }
        }

        return results.toArray(new EmailMessage[0]);
    }


    @FXML
    private void loadProfile() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/email/mailclient/profile.fxml"));
            loader.setControllerFactory(c -> new ProfileController(config, socket, personalEmail, loginResponse));
            Parent root = loader.load();

            // Create a new stage
            Stage profileStage = new Stage();
            profileStage.initStyle(StageStyle.DECORATED);
            profileStage.initModality(Modality.APPLICATION_MODAL);
            profileStage.setTitle("Tài Khoản");

            // Set the scene
            Scene scene = new Scene(root);
            profileStage.setScene(scene);

            // Show the stage
            profileStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

    }

//    private void showPopupOnHover(TableRow<EmailMessage> row, EmailMessage message) {
//        if (!message.isRead()) {
//            // Hiển thị Popup chỉ khi thư chưa đọc
//            Label popupLabel = new Label("Chưa đọc");
//            popupLabel.setStyle("-fx-background-color: white; -fx-padding: 10px;");
//
//            popup.getContent().clear();
//            popup.getContent().add(popupLabel);
//
//            // Lấy tọa độ của dòng trên màn hình
//            double x = row.localToScreen(row.getBoundsInLocal()).getMinX() + 50;
//            double y = row.localToScreen(row.getBoundsInLocal()).getMinY() + 20;
//
//            // Hiển thị Popup tại vị trí của dòng
//            popup.show(row, x, y);
//        }
//
//        else {
//            // Hiển thị Popup chỉ khi thư chưa đọc
//            Label popupLabel = new Label("Đã đọc");
//            popupLabel.setStyle("-fx-background-color: white; -fx-padding: 10px;");
//
//            popup.getContent().clear();
//            popup.getContent().add(popupLabel);
//
//            // Lấy tọa độ của dòng trên màn hình
//            double x = row.localToScreen(row.getBoundsInLocal()).getMinX() + 50;
//            double y = row.localToScreen(row.getBoundsInLocal()).getMinY() + 20;
//
//            // Hiển thị Popup tại vị trí của dòng
//            popup.show(row, x, y);
//        }
//    }


}
