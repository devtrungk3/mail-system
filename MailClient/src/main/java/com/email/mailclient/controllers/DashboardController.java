package com.email.mailclient.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController extends CommonController implements Initializable {




    public DashboardController() {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }




    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
