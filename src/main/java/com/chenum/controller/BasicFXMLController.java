package com.chenum.controller;

import com.chenum.App;
import com.chenum.util.ActionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class BasicFXMLController {

    private static Parent blogManager;

    public void initialize() throws IOException {
        blogManager = FXMLLoader.load(getClass().getResource("/BlogManagerFXML.fxml"));
    }

    @FXML
    private void enterBlogManager(ActionEvent event) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Stage stage = new Stage();
        stage.setTitle("博客管理");
        App.main().hide();
        Scene scene = new Scene(blogManager);
        stage.setScene(scene);
        stage.show();
    }
}
