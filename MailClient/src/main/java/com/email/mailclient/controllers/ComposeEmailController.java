package com.email.mailclient.controllers;


import com.email.mailclient.model.EmailMessage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class ComposeEmailController implements Initializable {
    private Properties config;
    static DataInputStream dis;
    static DataOutputStream dos;

    private String personalEmail = "";
    List<File> selectedFiles;
    String ftpCommand = "";

    String sender = "" ;
    String recipient = "" ;
    String subject = "" ;
    String content = "" ;

    private File attachedFile;
    @FXML
    private VBox attachmentArea;




    @FXML
    private TextField txtRecipient, txtSubject;
    @FXML
    private HTMLEditor txtContent;
    @FXML
    private Button btnAttachFile;

    public ComposeEmailController(Properties config, String personalEmail) {

        this.config = config;
        this.personalEmail = personalEmail+config.getProperty("default.domain");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khởi tạo các thiết lập ban đầu nếu cần
    }

    @FXML
    private void btnAttachFileAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn Tệp Đính Kèm");

        // Cho phép chọn nhiều tệp
      selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            // Lưu danh sách tệp đã chọn
            for (File file : selectedFiles) {
                // Check if the file size is less than 1KB
                if (file.length() < 1024) {
                    System.out.println("Đã chọn tệp đính kèm: " + file.getAbsolutePath());

                    // Tạo Label cho tệp và thêm vào HBox
                    Label fileLabel = new Label(file.getName());
                    fileLabel.getStyleClass().add("attached-file-label"); // (Optional) Thêm kiểu CSS nếu muốn
                    attachmentArea.getChildren().add(fileLabel);
                } else {
                    // Show an alert for files larger than 1KB
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                            "File '" + file.getName() + "' is larger than 1KB and won't be attached.",
                            ButtonType.OK);
                    alert.showAndWait();
                }
            }
            System.out.println(selectedFiles);

            ftpCommand = listToColonSeparatedString(selectedFiles);



        }
    }


    @FXML
    private void btnSendAction() throws IOException {
        sender = personalEmail;
       recipient = txtRecipient.getText()+config.getProperty("default.domain");
       subject = txtSubject.getText();
         content = txtContent.getHtmlText();

        // Check if recipient, subject, and content are not empty
        if (recipient.isEmpty() || subject.isEmpty() || content.isEmpty()) {
            showError("Vui lòng đièn đầy đủ các trường!");
            return;
        }


            String serverIP = config.getProperty("server.ip");
            int serverPort = Integer.parseInt(config.getProperty("smtp.port"));

            Socket socket = new Socket(serverIP, serverPort);
            dis = new DataInputStream(socket.getInputStream());
             dos = new DataOutputStream(socket.getOutputStream());

        communicate("HELO");
        communicate("MAIL FROM:"+sender);
        communicate("RCPT TO:"+recipient);
        communicate("DATA");
        communicate("SUBJECT:"+subject);
        communicate("CONTENT:"+content);
        communicate("ATTACH:"+ftpCommand);
        dos.writeUTF("QUIT");


            if (dis.readUTF().equals("OK") && !ftpCommand.isEmpty()) {

                Socket socketFTP = new Socket(config.getProperty("server.ip"), Integer.parseInt(config.getProperty("ftp.port")));
                DataOutputStream dos2 = new DataOutputStream(socketFTP.getOutputStream());
                DataInputStream dis2 = new DataInputStream(socketFTP.getInputStream());
                dos2.writeUTF("FTP " + sender + " " + recipient);
                if (dis2.readUTF().equals("OK")) {
                    OutputStream os = socketFTP.getOutputStream();
                    for (File  file : selectedFiles) {

                        dos2.writeUTF("PUT " + file.getName());
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                            break;
                        }
                        String resp = dis2.readUTF();
                        System.out.println(resp);
                        if (!resp.equals("OK")) break;
                    }
                    // end session
                    dos2.writeUTF("QUIT");
                    System.out.println("QUIT FTP " + dis2.readUTF());
                }
            }

            txtRecipient.setText("");
            txtSubject.setText("");
            txtContent.setHtmlText("");
            showSuccessAlert("Đã gửi");


    }

    public static void communicate(String request) throws IOException {
        dos.writeUTF(request);
        System.out.println("client said: " + request);
        System.out.println("client said: " + request + " - server said: " + dis.readUTF());
    }



    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

        private static String listToColonSeparatedString(List<File> fileList) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < fileList.size(); i++) {
            result.append(fileList.get(i).getName()); // Lấy tên file thay vì đường dẫn

            if (i < fileList.size() - 1) {
                result.append(":");
            }
        }

        return result.toString();
    }
}
