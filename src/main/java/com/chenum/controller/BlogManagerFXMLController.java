package com.chenum.controller;

import com.browniebytes.javafx.control.DateTimePicker;
import com.chenum.App;
import com.chenum.cache.Cache;
import com.chenum.config.Config;
import com.chenum.config.ExecutorThreadPool;
import com.chenum.model.PageData;
import com.chenum.model.ResultWrap;
import com.chenum.po.Article;
import com.chenum.util.HttpUtil;
import com.chenum.util.JsonUtil;
import com.chenum.util.TimeUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlogManagerFXMLController{

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogManagerFXMLController.class);

    @FXML
    private MenuBar menuBar;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ListView<Article> blogListView;
    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane centerPane;
    @FXML
    private SplitPane splitPane;
    @FXML
    private SplitPane centerPaneSplitPane;
    @FXML
    private StackPane centerStackPaneTop;
    @FXML
    private StackPane centerStackPaneBottom;
    @FXML
    private VBox centerStackPaneBottomVbox;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField title;
    @FXML
    private HBox authorBox;
    @FXML
    private HBox contentTagBox;
    @FXML
    private HBox contributorBox;
    @FXML
    private RadioButton isCommentRBtn;
    @FXML
    private RadioButton isLikeRBtn;
    @FXML
    private RadioButton isAdmireRBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button publishBtn;
    @FXML
    private DateTimePicker createTimePK;
    @FXML
    private DateTimePicker publishTimePK;
    @FXML
    private RadioMenuItem newArticleMenuItem;


    public static final String ADMIN_QUERY = Config.get("api.request.article.admin-query");
    public static final String ADMIN_ADD = Config.get("api.request.article.admin-add");
    public static final String ADMIN_MODIFY= Config.get("api.request.article.admin-modify");
    public static final String ADMIN_RECALL = Config.get("api.request.article.admin-recall");

    public void initialize() {
        this.setStyle();
        this.configureListView();
    }

    @FXML
    public void toSave(ActionEvent event){
        boolean isNewArticle = newArticleMenuItem.isSelected();
        int selectIndex = 0;
        String[] result = new String[1];
        if (isNewArticle){
            result[0] = this.saveAndPublish(event);
        }else{
            Article article = blogListView.getSelectionModel().getSelectedItem();
            selectIndex = blogListView.getSelectionModel().getSelectedIndex();
            Alert alert;
            if (article == null){
                alert = new Alert(Alert.AlertType.INFORMATION, "????????????????????????!");
                alert.showAndWait();
            }else{
                alert = new Alert(Alert.AlertType.CONFIRMATION, "???????????????" + title.getText() + "?????????????");
                alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
                    @Override
                    public void accept(ButtonType buttonType) {
                        if (buttonType == ButtonType.OK) {
                            result[0] = BlogManagerFXMLController.this.edit(event,article.getId());
                        }
                    }
                });
            }
        }
        if (result[0] == null){
            return;
        }

        Map<String,Object> map = JsonUtil.jsonToObject(result[0], Map.class);
        int code = (int) map.get("code");
        if (code == 200){
            this.addArticleList();
            blogListView.getSelectionModel().select(selectIndex);
        }else{
            String message = (String) map.get("message");
            Alert alert = new Alert(Alert.AlertType.ERROR,message);
            alert.showAndWait();
        }
    }

    private String edit(ActionEvent event,String id){
        Button button = (Button) event.getSource();
        String btnId = button.getId();
        boolean isPublish = false;
        if ("publishBtn".equals(btnId)){
            isPublish = true;
        }
        boolean finalIsPublish = isPublish;
        Future<String> future = ExecutorThreadPool.service().submit(new Callable<String>() {
            @Override
            public String call() {
                Map<String,Object> params = getViewArticle();
                params.put("isPublish", finalIsPublish);
                params.put("id",id);
                params.put("publishTime", TimeUtil.format(publishTimePK.dateTimeProperty().get()));
                try {
                    return HttpUtil.put(ADMIN_MODIFY,params,new Header[]{getHeader()});
                } catch (IOException e) {
                    LOGGER.error("??????????????????:{}",e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("????????????????????????:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String saveAndPublish(ActionEvent event){
        Button button = (Button) event.getSource();
        String btnId = button.getId();
        boolean isPublish = false;
        if ("publishBtn".equals(btnId)){
            isPublish = true;
        }
        boolean finalIsPublish = isPublish;
        Future<String> future = ExecutorThreadPool.service().submit(new Callable<String>() {
            @Override
            public String call() {
                Map<String,Object> params = getViewArticle();
                params.put("isPublish", finalIsPublish);
                params.put("publishTime", TimeUtil.format(publishTimePK.dateTimeProperty().get()));
                try {
                    return HttpUtil.post(ADMIN_ADD,params,new Header[]{getHeader()});
                } catch (IOException e) {
                    LOGGER.error("??????????????????:{}",e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("????????????????????????:{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Map<String,Object> getViewArticle(){
        Map<String,Object> params = new HashMap<>();
        params.put("title",title.getText());
        params.put("content",textArea.getText());
        params.put("isLike",isLikeRBtn.isSelected());
        params.put("isComment",isCommentRBtn.isSelected());
        params.put("isAdmiration",isAdmireRBtn.isSelected());
        params.put("author",this.fromLabel(authorBox));
        params.put("contentTag",this.fromTagLabel(contentTagBox));
        params.put("contribution",this.fromLabel(contributorBox));
        params.put("creator","");
        params.put("isPublish", false);
        return params;
    }

    private void addArticleList(){
        blogListView.getItems().clear();
        blogListView.getItems().addAll(queryItems());
    }

    private void configureListView(){
        this.addArticleList();
        Callback<ListView<Article>, ListCell<Article>> callback = TextFieldListCell.forListView(new StringConverter<Article>() {
            @Override
            public String toString(Article article) {
                String status = switch (article.getStatus()){
                    case 100 -> "??????";
                    case 110 -> "?????????";
                    case 120 -> "?????????";
                    default -> "unknown";
                };
                return article.getTitle() + "------" + status;
            }

            @Override
            public Article fromString(String string) {
                return null;
            }
        });
        blogListView.setCellFactory(callback);
        blogListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {
            @Override
            public void changed(ObservableValue<? extends Article> observable, Article oldValue, Article newValue) {
                if (newValue == null){
                    newValue = oldValue;
                }
                textArea.setText(newValue.getContent());
                title.setText(newValue.getTitle());
                ObservableList<Node> children = authorBox.getChildren();
                if (children.size() > 2){
                    children.remove(1,children.size() - 1);
                }
                children.addAll(1,getLabel(newValue.getAuthor(),(Label) children.get(0)));

                ObservableList<Node> contributorBoxChildren = contributorBox.getChildren();
                if (contributorBoxChildren.size() > 2){
                    contributorBoxChildren.remove(1,contributorBoxChildren.size() - 1);
                }
                contributorBoxChildren.addAll(1,getLabel(newValue.getContribution(),(Label) contributorBoxChildren.get(0)));

                ObservableList<Node> contentTagBoxChildren = contentTagBox.getChildren();
                if (contentTagBoxChildren.size() > 2){
                    contentTagBoxChildren.remove(1,contentTagBoxChildren.size()- 1);
                }
                contentTagBoxChildren.addAll(1,getContentTagLabel(newValue.getContentTag(),(Label) contentTagBoxChildren.get(0)));
                if (newValue.getIsComment()){
                    isCommentRBtn.setSelected(true);
                }else{
                    isCommentRBtn.setSelected(false);
                }
                if (newValue.getIsLike()){
                    isLikeRBtn.setSelected(true);
                }else{
                    isLikeRBtn.setSelected(false);
                }
                if (newValue.getIsAdmiration()){
                    isAdmireRBtn.setSelected(true);
                }else{
                    isAdmireRBtn.setSelected(false);
                }
                createTimePK.dateTimeProperty().set(TimeUtil.parse(newValue.getCreateTime()));
                if (newValue.getPublishTime() == null){
                    publishTimePK.dateTimeProperty().set(LocalDateTime.MIN);
                }else{
                    publishTimePK.dateTimeProperty().set(TimeUtil.parse(newValue.getPublishTime()));
                }
            }
        });
        ContextMenu contextMenu = new ContextMenu();
        MenuItem recallItem = new MenuItem("??????");
        recallItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Article article = blogListView.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"???????????????"+article.getTitle()+"??????");
                alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
                    @Override
                    public void accept(ButtonType buttonType) {
                        if (buttonType == ButtonType.OK){
                            String result = recall(article.getId());
                            Map<String,Object> map = JsonUtil.jsonToObject(result, Map.class);
                            int code = (int) map.get("code");
                            boolean data = (boolean) map.get("data");
                            if (code == 200 && data){
                                blogListView.getSelectionModel().select(0);
                                BlogManagerFXMLController.this.addArticleList();
                            }else{
                                String message = (String) map.get("message");
                                Alert alert = new Alert(Alert.AlertType.ERROR,message);
                                alert.showAndWait();
                            }
                        }
                    }
                });
            }
        });
        contextMenu.getItems().add(recallItem);
        blogListView.setContextMenu(contextMenu);
    }

    private String recall(String id){
        Future<String> future = ExecutorThreadPool.service().submit(new Callable<String>() {
            @Override
            public String call(){
                try {
                    return HttpUtil.delete(ADMIN_RECALL.replace("${id}",id),new Header[]{getHeader()});
                } catch (IOException e) {
                    LOGGER.error("??????????????????");
                    throw new RuntimeException(e.getMessage());
                }
            }
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("????????????????????????");
            throw new RuntimeException(e.getMessage());
        }
    }

    @FXML
    public void pushLabel(ActionEvent actionEvent){
        Button button = (Button) actionEvent.getSource();
        String btnId = button.getId();
        ObservableList<Node> children = switch (btnId){
            case "addAuthorBtn" ->  authorBox.getChildren();
            case "addTagBtn" ->  contentTagBox.getChildren();
            case "addCtbBtn" ->  contributorBox.getChildren();
            default -> FXCollections.emptyObservableList();
        };
        for (int i = 1; i <= children.size()-1; i++) {
            if (children.get(i) instanceof TextField){
                return;
            }
        }
        TextField textField = new TextField();
        textField.setPrefHeight(30);
        textField.setPrefWidth(80);
        textField.focusedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                ReadOnlyBooleanProperty readOnlyBooleanProperty = (ReadOnlyBooleanProperty) observable;
                Object bean = readOnlyBooleanProperty.getBean();
                boolean value = readOnlyBooleanProperty.getValue();
                if (!value && bean instanceof TextField){
                    String v = ((TextField)(readOnlyBooleanProperty.getBean())).getText();
                    if (v == null || "".equals(v)){
                        return;
                    }
                    children.remove(bean);
                    Label label = new Label(v);
                    label.setPrefHeight(38);
                    label.setTooltip(new Tooltip(v));
                    label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            if (mouseEvent.getButton().compareTo(MouseButton.SECONDARY) == 0){
                                MenuItem menuItem = new MenuItem("??????");
                                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent actionEvent) {
                                        children.remove(label);
                                    }
                                });
                                label.setContextMenu(new ContextMenu(menuItem));
                            }
                        }
                    });

                    children.add(children.size() - 1,label);
                }
            }
        });
        children.add(children.size() -1,textField);
    }

    private List<Label> getLabel(String context,Label template){
        if (context == null || context.isEmpty()){
            return Collections.emptyList();
        }
        List<Map<String,Object>> authorList = JsonUtil.jsonToObject(context,List.class);
        return authorList.stream().map(new Function<Map<String, Object>, Label>() {
            @Override
            public Label apply(Map<String, Object> stringObjectMap) {
                String name = (String) stringObjectMap.get("name");
                Label label = new Label(name);
                label.setTooltip(new Tooltip(name));
                label.setPrefWidth(template.getPrefWidth());
                label.setPrefHeight(template.getPrefHeight());
                return label;
            }
        }).collect(Collectors.toList());
    }
    private List<Label> getContentTagLabel(String context,Label template){
        if (context == null || context.isEmpty()){
            return Collections.emptyList();
        }
        String[] tag = context.split(",");
        return Arrays.stream(tag).map(new Function<String, Label>() {
            @Override
            public Label apply(String s) {
                Label label = new Label(s);
                label.setPrefWidth(template.getPrefWidth());
                label.setPrefHeight(template.getPrefHeight());
                return label;
            }
        }).collect(Collectors.toList());
    }
    private List<Map<String,Object>> fromLabel(HBox authorBox){
        ObservableList<Node> labels = authorBox.getChildren();
        if (labels.size() == 2){
            return Collections.emptyList();
        }
        List<Map<String,Object>> author = new ArrayList<>(labels.size());
        for (int i = 1; i <= labels.size() - 2; i++) {
            Label label = (Label) labels.get(i);
            Map<String,Object> map = new HashMap<>(1);
            map.put("name",label.getText());
            author.add(map);
        }
        return author;
    }
    private String fromTagLabel(HBox contentTagBox){
        ObservableList<Node> labels = contentTagBox.getChildren();
        if (labels.size() == 1){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= labels.size() - 2; i++) {
            Label label = (Label) labels.get(i);
            sb.append(label.getText()).append(",");
        }
        return sb.toString().endsWith(",") ? sb.substring(0,sb.length()) : sb.toString();
    }


    private void setStyle(){
        menuBar.prefWidthProperty().bind(anchorPane.widthProperty());
        splitPane.prefWidthProperty().bind(anchorPane.widthProperty());
        splitPane.prefHeightProperty().bind(anchorPane.heightProperty());
        splitPane.setDividerPosition(0,0.2);
        splitPane.setDividerPosition(1,1);
        centerPaneSplitPane.setDividerPosition(0,0.6);
        centerPaneSplitPane.setDividerPosition(1,1);
        centerPaneSplitPane.prefWidthProperty().bind(centerPane.widthProperty());
        centerPaneSplitPane.prefHeightProperty().bind(centerPane.heightProperty());
        centerStackPaneBottomVbox.prefWidthProperty().bind(centerStackPaneBottom.widthProperty());
        centerStackPaneBottomVbox.prefHeightProperty().bind(centerStackPaneBottom.heightProperty());
        blogListView.prefWidthProperty().bind(leftPane.widthProperty());
        blogListView.prefHeightProperty().bind(leftPane.heightProperty());
        textArea.setWrapText(true);
        newArticleMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (newArticleMenuItem.isSelected()){
                    textArea.setText(null);
                    title.setText(null);
                    authorBox.getChildren().remove(1,authorBox.getChildren().size() - 1);
                    contentTagBox.getChildren().remove(1,contentTagBox.getChildren().size() - 1);
                    contributorBox.getChildren().remove(1,contributorBox.getChildren().size() - 1);
                    createTimePK.dateTimeProperty().set(LocalDateTime.now());
                    publishTimePK.dateTimeProperty().set(LocalDateTime.now());
                    isCommentRBtn.setSelected(false);
                    isLikeRBtn.setSelected(false);
                    isAdmireRBtn.setSelected(false);
                }
            }
        });
    }

    private List<Article> queryItems(){
        Future<List<Article>> future =  ExecutorThreadPool.service().submit(new Callable<List<Article>>() {
            @Override
            public List<Article> call(){
                try {
                    Map<String,Object> params1 = new HashMap<>(2);
                    params1.put("pageNum",1);
                    params1.put("pageSize",20);
                    String result = HttpUtil.get(ADMIN_QUERY,params1,new Header[]{getHeader()});
                    ResultWrap<PageData<Article>> wrapper = JsonUtil.jsonToObject(result, new TypeReference<>() {});
                    return wrapper.getData().getList();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Header getHeader(){
        return new BasicHeader("ch_access_token", (String) Cache.get("access_token"));
    }


    @FXML
    public void showMainStage() {
        App.main().show();
    }

}
