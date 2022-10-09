package com.chenum.controller;

import com.chenum.cache.Cache;
import com.chenum.constant.LogLevel;
import com.chenum.log.LogReader;
import com.chenum.log.ReadAfterAdapter;
import com.chenum.log.WriteAreaHandler;
import com.chenum.task.AutoRefreshLogTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;

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
    @FXML
    CheckMenuItem autoRefreshMenu;

    private LogReader logReader = new LogReader();
    private LogLevel logLevel = LogLevel.INFO;
    private Timer timer = new Timer();

    public void initialize() {
        this.setAreaStyle();
        this.setToggleGroup();
        infoBtn.setSelected(true);

    }

    void setCloseEvent(Stage stage){
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.println("监听到关闭时间");
                timer.cancel();
            }
        });
    }

    private void setAreaStyle() {
        consArea.prefWidthProperty().bind(anchorPane.widthProperty());
        consArea.prefHeightProperty().bind(anchorPane.heightProperty());
        consArea.setEditable(false);
    }

    private void setToggleGroup() {
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
                logLevel = selectedLevel(newValue);
                Cache.put("logLevel",logLevel);
                logReader.setPointer(0L);
                consArea.setText("");
                try {
                    logReader.read(logLevel);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        autoRefreshMenu.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    timer = new Timer("自动刷新日志任务");
                    timer.schedule(new AutoRefreshLogTask(logReader), 1000L, 2000L);
                }else{
                    timer.cancel();
                }
            }
        });
    }

    private LogLevel selectedLevel(Toggle toggle) {
        ToggleButton toggleButton = (ToggleButton) toggle;
        String text = toggleButton.getText();
        switch (text) {
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
