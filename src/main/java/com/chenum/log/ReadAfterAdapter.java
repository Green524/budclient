package com.chenum.log;

import javafx.scene.control.Control;

public interface ReadAfterAdapter {

    void handle(String text);

    void setObject(Control control);
}
