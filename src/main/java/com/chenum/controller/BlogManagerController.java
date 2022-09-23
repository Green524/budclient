package com.chenum.controller;

import com.chenum.App;
import com.chenum.model.Item;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.function.Consumer;

public class BlogManagerController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<Item> tableView;


    private static String[] itemKey = {"sku","descr","price","taxable"};
    private static int keyIndex = 0;

    public void initialize() throws IOException {
        menuBar.prefWidthProperty().bind(anchorPane.widthProperty());
        tableInitialize();
    }

    private void tableInitialize(){
        tableView.prefWidthProperty().bind(anchorPane.widthProperty());
//        tableView.getItems().addAll(
//                new Item("KBD-0455892", "Mechanical Keyboard", 100.0f, true),
//                new Item( "145256", "Product Docs", 0.0f, false ),
//                new Item( "OR-198975", "O-Ring (100)", 10.0f, true)
//        );

//        tableView.getColumns().forEach(itemTableColumn -> itemTableColumn.setCellValueFactory(new PropertyValueFactory<>(itemKey[keyIndex++])));
    }



    @FXML
    public void showMainStage() {
        App.main().show();
    }
}
