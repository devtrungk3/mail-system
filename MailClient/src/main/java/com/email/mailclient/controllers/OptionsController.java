package com.email.mailclient.controllers;


import com.email.mailclient.visuals.ColorTheme;
import com.email.mailclient.visuals.FontSize;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionsController extends CommonController implements Initializable {

    @FXML
    private ChoiceBox<ColorTheme> colorThemeChoiceBox;

    @FXML
    private Slider fontSizeSlider;

    public OptionsController() {

    }

    @FXML
    void saveButtonClicked() {

    }



    @FXML
    void cancelButtonClicked() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColorThemeChoiceBox();
        setFontSizeSlider();
    }

    private void setFontSizeSlider() {
        fontSizeSlider.setMin(0);
        fontSizeSlider.setMax(FontSize.values().length-1);

        fontSizeSlider.setMinorTickCount(0);
        fontSizeSlider.setMajorTickUnit(1);
        fontSizeSlider.setBlockIncrement(1);
        fontSizeSlider.setSnapToTicks(true);
        fontSizeSlider.setShowTickMarks(true);
        fontSizeSlider.setShowTickLabels(true);
        fontSizeSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double aDouble) {
                int i = aDouble.intValue();
                return FontSize.values()[i].toString();
            }

            @Override
            public Double fromString(String s) {
                return null;
            }
        });
        fontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            fontSizeSlider.setValue(newVal.intValue());
        });
    }

    private void setColorThemeChoiceBox(){
        colorThemeChoiceBox.setItems(FXCollections.observableArrayList(ColorTheme.values()));

    }
}