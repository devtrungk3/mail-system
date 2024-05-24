package com.email.mailclient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class Edit1Controller extends CommonController implements Initializable {
    private String loginResponse = "";
    private String fullname = "";
    @FXML
    private TextField txtOldInfo;
    @FXML
    private TextField txtNewInfo;
    Properties config;
    private Socket socket;
    private String emailAddress = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
setUserName();
    }

    private void setUserName() {
        String[] responseParts = loginResponse.split(" ", 4);

        if (responseParts.length >= 4 && "OK".equals(responseParts[0])) {
            String username = responseParts[3];
            this.fullname = username;
            txtOldInfo.setText( fullname);
        }
    }

    public Edit1Controller(Properties config, Socket socket, String emailAddress, String loginResponse) {
        this.config = config;
        this.socket = socket;
        this.emailAddress = emailAddress;
        this.loginResponse = loginResponse;
    }

    @FXML
    private void saveChange() {
       String newFullName = txtNewInfo.getText();
        if (newFullName.isEmpty() ) {
            showError("Vui lòng đièn đầy đủ các trường!");
            return;
        }
        if (!isValidFullName(newFullName)) {
            showError("Tên không hợp lệ. Vui lòng kiểm tra lại.");
            return;
        }
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF("CHNAME "+ newFullName);
            String Response = dis.readUTF();
            System.out.println(Response);

        } catch (IOException e) {
            showError("Lỗi khi giao tiếp với máy chủ: " + e.getMessage());
        }


    }

    private boolean isValidFullName(String fullName) {
        return fullName.matches("^[a-zA-Z\\\\p{L}]+(\\\\s[a-zA-Z\\\\p{L}]+)*$");

//        [a-zA-Z\\p{L}]:          Một ký tự thuộc bảng chữ cái tiếng Anh hoặc bất kỳ ký tự chữ cái nào thuộc bảng mã Unicode (được đại diện bởi \\p{L}).
//        +:                       Ít nhất một ký tự chữ cái hoặc ký tự chữ cái Unicode.
//        (\\s[a-zA-Z\\p{L}]+)*:   Một nhóm con có thể lặp lại, bao gồm khoảng trắng (\\s) theo sau là một hoặc nhiều ký tự chữ cái hoặc ký tự chữ cái Unicode.
//        $:                       Kết thúc của chuỗi.
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
