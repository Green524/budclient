package com.chenum.util;

import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionUtil {

    public static void closeStage(Event event) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
        Method sourceMethod = event.getClass().getMethod("getSource");
        Object node = sourceMethod.invoke(event);
        Method sceneMethod = node.getClass().getMethod("getScene");
        Scene scene = (Scene) sceneMethod.invoke(node);
        ((Stage) scene.getWindow()).close();
    }
}
