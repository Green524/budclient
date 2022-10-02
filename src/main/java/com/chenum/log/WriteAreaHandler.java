package com.chenum.log;

import javafx.scene.control.Control;
import javafx.scene.control.TextArea;

public class WriteAreaHandler implements ReadAfterAdapter {

    private TextArea textArea;

    @Override
    public void handle(String text) {
        if (textArea != null){
            textArea.setText(textArea.getText() + text);
        }
    }

    @Override
    public void setObject(Control control) {
        this.textArea = (TextArea) control;
    }
}
