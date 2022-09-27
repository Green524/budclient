package com.chenum.hanlder;

import com.chenum.po.Article;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DateValueFactory<S,T> implements Callback<TableColumn<S,T>, TableCell<S,T>> {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        return new TableCell<>(){
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty){
                    Date date = (Date) this.getItem();
                    if (Objects.nonNull(date)){
                        this.setText(sdf.format(date));
                    }
                }

            }
        };
    }
}
