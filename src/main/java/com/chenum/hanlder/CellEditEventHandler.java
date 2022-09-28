package com.chenum.hanlder;

import javafx.event.Event;
import javafx.event.EventHandler;

public class CellEditEventHandler<T extends Event> implements EventHandler<T> {
    @Override
    public void handle(T event) {
        System.out.println(event);
    }
}
