package com.chenum;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static Stage main;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        LOGGER.info("客户端启动...");
        primaryStage.setTitle("BudClient Dev");
        main = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/BasicFXML.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        LOGGER.info("客户端启动成功!");
    }

    public static Stage main(){
        return main;
    }
}
