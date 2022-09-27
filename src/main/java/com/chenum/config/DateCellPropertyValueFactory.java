package com.chenum.config;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class DateCellPropertyValueFactory extends PropertyValueFactory implements Callback {

    public DateCellPropertyValueFactory(String s) {
        super(s);
    }

    @Override
    public ObservableValue call(TableColumn.CellDataFeatures cellDataFeatures) {
        System.out.println("call");
        return super.call(cellDataFeatures);
    }

    @Override
    public Object call(Object o) {
        System.out.println("call1");
        return null;
    }
}
