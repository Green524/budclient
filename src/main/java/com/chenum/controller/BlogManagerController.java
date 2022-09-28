package com.chenum.controller;

import com.chenum.App;
import com.chenum.cache.Cache;
import com.chenum.config.Config;
import com.chenum.config.ExecutorThreadPool;
import com.chenum.hanlder.CellEditEventHandler;
import com.chenum.hanlder.DateValueFactory;
import com.chenum.hanlder.IDValueFactory;
import com.chenum.model.PageData;
import com.chenum.model.ResultWrap;
import com.chenum.po.Article;
import com.chenum.util.HttpUtil;
import com.chenum.util.JsonUtil;
import com.chenum.util.SerializationUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogManagerController {

    @FXML
    private MenuBar menuBar;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<Article> tableView;

    @FXML
    private TableColumn<Article,String> title;


    private SerializationUtil serializationUtil = new SerializationUtil();
    private static String[] itemKey = {"","id","title","markdown","author","contentTag","wordCount","readTime","contribution","isLike","isComment","isAdmiration","status","publishTime","creator","createTime","updateTime","lastReviewer"};
    private static int keyIndex = 0;

    public void initialize() throws IOException {
        this.setTableStyle();
        this.tableInitialize();
    }

    private void setTableStyle(){
        menuBar.prefWidthProperty().bind(anchorPane.widthProperty());

        title.setCellFactory(TextFieldTableCell.forTableColumn());
        title.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Article, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Article, String> stringStringCellEditEvent) {
                ((stringStringCellEditEvent.getTableView().getItems().get(stringStringCellEditEvent.getTablePosition().getRow()))).setTitle(stringStringCellEditEvent.getNewValue());
            }
        });
    }
    private void tableInitialize(){
        this.tableView.prefWidthProperty().bind(anchorPane.widthProperty());
        ExecutorThreadPool.service().execute(() -> {
            try {
                setItems();
            } catch (IOException e) {
                throw new RuntimeException("设置列表信息失败");
            }
        });
    }
    private void setItems() throws IOException {
        this.tableView.getItems().addAll(queryItems());
        this.setCellFormat();
        this.setRowFactory();
    }

    /**
     * 设置行处理
     */
    private void setRowFactory(){
        this.tableView.setRowFactory(param -> {
            final TableRow<Article> tableRow = new TableRow<>();
            tableRow.setEditable(true);
            tableRow.setOnMouseClicked(event -> {
                int secondary = event.getButton().compareTo(MouseButton.SECONDARY);
                if (secondary == 0){
                    final ContextMenu contextMenu = new ContextMenu();
                    MenuItem menuItem = new MenuItem("查看");
                    MenuItem editItem = new MenuItem("编辑");
                    MenuItem delItem = new MenuItem("删除");
                    MenuItem recallItem = new MenuItem("撤回");
                    contextMenu.getItems().addAll(menuItem,editItem,delItem,recallItem);
                    tableRow.setContextMenu(contextMenu);
                }
            });
            return tableRow;
        });
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private void setCellFormat(){
        this.tableView.getColumns().forEach(itemTableColumn -> {
            itemTableColumn.setCellValueFactory(new PropertyValueFactory<>(itemKey[keyIndex++]));
        });
        this.tableView.getVisibleLeafColumn(0).setCellFactory(new IDValueFactory<>());
        DateValueFactory dateValueFactory = new DateValueFactory<>();
        this.tableView.getVisibleLeafColumn(13).setCellFactory(dateValueFactory);
        this.tableView.getVisibleLeafColumn(15).setCellFactory(dateValueFactory);
        this.tableView.getVisibleLeafColumn(16).setCellFactory(dateValueFactory);
        this.tableView.setEditable(true);
    }
    private List<Article> queryItems() throws IOException {
        Header header = new BasicHeader("ch_access_token", (String) Cache.get("access_token"));
        Map<String,Object> params1 = new HashMap<>(2);
        params1.put("pageNum",1);
        params1.put("pageSize",20);
        String result = HttpUtil.get(Config.get("api.request.article.admin-query"),params1,new Header[]{header});
        ResultWrap<PageData<Article>> wrapper = JsonUtil.jsonToObject(result, new TypeReference<>() {});
        return wrapper.getData().getList();
    }



    @FXML
    public void showMainStage() {
        App.main().show();
    }
}
