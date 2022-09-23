package com.chenum.controller;

import com.chenum.App;
import com.chenum.util.ActionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class BasicFXMLController {




    private static Parent blogManager;
    private static Scene blogScene;
    private static Stage stage;

    public void initialize() throws IOException {
        blogManagerInitialize();
    }

    private void blogManagerInitialize() throws IOException {
        if (Objects.isNull(blogManager)){
            blogManager = FXMLLoader.load(getClass().getResource("/BlogManagerFXML.fxml"));
        }
        if (Objects.isNull(stage)){
            stage = new Stage();
            stage.setTitle("博客管理");
        }
        if (Objects.isNull(blogScene)){
            blogScene = new Scene(blogManager);
            stage.setScene(blogScene);
        }
    }

    @FXML
    private void enterBlogManager(ActionEvent event){
        App.main().hide();
        stage.show();
    }
}
