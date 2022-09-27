package com.chenum.hanlder;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class IDValueFactory<S,T> implements Callback<TableColumn<S,T>, TableCell<S,T>> {

    @Override
    public TableCell<S, T> call(TableColumn<S, T> param) {
        TableCell<S,T> cell = new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    int rowIndex = this.getIndex() + 1;
                    this.setText(String.valueOf(rowIndex));
                }
            }

        };
        return cell;
    }
}
