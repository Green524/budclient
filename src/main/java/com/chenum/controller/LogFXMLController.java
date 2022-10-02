package com.chenum.controller;

import com.chenum.constant.LogLevel;
import com.chenum.log.LogReader;
import com.chenum.log.ReadAfterAdapter;
import com.chenum.log.WriteAreaHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LogFXMLController {

    private static Logger LOGGER = LoggerFactory.getLogger(LoggerFactory.class);

    @FXML
    AnchorPane anchorPane;
    @FXML
    RadioButton infoBtn;
    @FXML
    RadioButton warnBtn;
    @FXML
    RadioButton debugBtn;
    @FXML
    RadioButton errorBtn;
    @FXML
    RadioButton traceBtn;
    @FXML
    TextArea consArea;

    private LogReader logReader = new LogReader();

    public void initialize(){
        this.setAreaStyle();
        this.setToggleGroup();
        infoBtn.setSelected(true);
    }

    private void setAreaStyle(){
        consArea.prefWidthProperty().bind(anchorPane.widthProperty());
        consArea.prefHeightProperty().bind(anchorPane.heightProperty());
        consArea.setEditable(false);
    }

    private void setToggleGroup(){
        ToggleGroup toggleGroup = new ToggleGroup();
        infoBtn.setToggleGroup(toggleGroup);
        warnBtn.setToggleGroup(toggleGroup);
        debugBtn.setToggleGroup(toggleGroup);
        errorBtn.setToggleGroup(toggleGroup);
        traceBtn.setToggleGroup(toggleGroup);
        ReadAfterAdapter readAfterAdapter = new WriteAreaHandler();
        readAfterAdapter.setObject(consArea);
        logReader.setReadAfterHandler(readAfterAdapter);
        toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                try {
                    logReader.setPointer(0L);
                    consArea.setText("");
                    logReader.read(selectedLevel(newValue));
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private LogLevel selectedLevel(Toggle toggle){
        ToggleButton toggleButton = (ToggleButton) toggle;
        String text = toggleButton.getText();
        switch (text){
            case "warn" -> {
                return LogLevel.WARN;
            }
            case "error" -> {
                return LogLevel.ERROR;
            }
            case "trace" -> {
                return LogLevel.TRACE;
            }
            case "debug" -> {
                return LogLevel.DEBUG;
            }
            default -> {
                return LogLevel.INFO;
            }
        }
    }

}
