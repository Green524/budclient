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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BasicFXMLController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicFXMLController.class);

    private static Parent blogManager;
    private static Scene blogScene;
    private static Stage stage;

    private static Parent logView;
    private static Scene logScene;
    private static Stage logStage;

    public void initialize(){
        login();
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
                LOGGER.info("登录成功!");
            }
        });
    }
    @FXML
    private void enterBlogManager(ActionEvent event) throws IOException {
        App.main().hide();
        blogManagerInitialize();
        stage.show();
    }

    @FXML
    private void enterBlogView(ActionEvent event) throws IOException {
        blogViewInitialize();
        logStage.show();
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

    private void blogViewInitialize() throws IOException {
        if (Objects.isNull(logView)){
            logView = FXMLLoader.load(getClass().getResource("/fxml/LogFXML.fxml"));
        }
        if (Objects.isNull(logStage)){
            logStage = new Stage();
            logStage.setTitle("日志");
        }
        if (Objects.isNull(logScene)){
            logScene = new Scene(logView);
            logStage.setScene(logScene);
        }
    }
}
