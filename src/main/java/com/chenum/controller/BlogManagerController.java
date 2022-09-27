package com.chenum.controller;

import com.chenum.App;
import com.chenum.cache.Cache;
import com.chenum.config.Config;
import com.chenum.config.ExecutorThreadPool;
import com.chenum.hanlder.IDCell;
import com.chenum.model.PageData;
import com.chenum.model.ResultWrap;
import com.chenum.po.Article;
import com.chenum.util.HttpUtil;
import com.chenum.util.JsonUtil;
import com.chenum.util.SerializationUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
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


    private SerializationUtil serializationUtil = new SerializationUtil();
    private static String[] itemKey = {"","id","title","markdown","author","contentTag","wordCount","readTime","contribution","isLike","isComment","isAdmiration","status","publishTime","creator","createTime","updateTime","lastReviewer"};
    private static int keyIndex = 0;

    public void initialize() throws IOException {
        menuBar.prefWidthProperty().bind(anchorPane.widthProperty());
        tableInitialize();
    }

    private void tableInitialize(){
        tableView.prefWidthProperty().bind(anchorPane.widthProperty());
        ExecutorThreadPool.service().execute(() -> {
            try {
                setItems();
            } catch (IOException e) {
                throw new RuntimeException("设置列表信息失败");
            }
        });
    }
    private void setItems() throws IOException {
        List<Article> list = queryItems();
        tableView.getItems().addAll(list);
        tableView.getColumns().forEach(itemTableColumn -> {
            itemTableColumn.setCellValueFactory(new PropertyValueFactory<>(itemKey[keyIndex++]));
        });
        tableView.getVisibleLeafColumn(0).setCellFactory(new IDCell<>());
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
