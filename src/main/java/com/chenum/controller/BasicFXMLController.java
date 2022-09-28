package com.chenum.controller;

import com.chenum.App;
import com.chenum.cache.Cache;
import com.chenum.config.Config;
import com.chenum.config.ExecutorThreadPool;
import com.chenum.util.HttpUtil;
import com.chenum.util.JsonUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BasicFXMLController {

    private static Parent blogManager;
    private static Scene blogScene;
    private static Stage stage;

    public void initialize(){
        login();
    }

    private void blogManagerInitialize() throws IOException {
        if (Objects.isNull(blogManager)){
            blogManager = FXMLLoader.load(getClass().getResource("/fxml/BlogManagerFXML.fxml"));
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

    private void login() {
        ExecutorThreadPool.service().execute(new Runnable() {
            @Override
            public void run() {
                Map<String,Object> params = new HashMap<>(2);
                params.put("username","chenum");
                params.put("password","www.chenum.com");
                String token = null;
                try {
                    token = HttpUtil.post(Config.get("api.request.user.admin-login"),params);
                } catch (IOException e) {
                    throw new RuntimeException("登录失败");
                }
                if (Strings.isEmpty(token)){
                    return;
                }
                Map<String,Object> map = JsonUtil.jsonToObject(token, Map.class);
                Map<String,Object> data = (Map<String, Object>) map.get("data");
                String chAccessToken = (String) data.get("access_token");
                Cache.put("access_token",chAccessToken);
            }
        });
    }

    @FXML
    private void enterBlogManager(ActionEvent event) throws IOException {
        App.main().hide();
        blogManagerInitialize();
        stage.show();
    }
}
