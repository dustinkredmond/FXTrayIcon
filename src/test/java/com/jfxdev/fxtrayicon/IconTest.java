package com.jfxdev.fxtrayicon;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class IconTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        root.setCenter(new Label("This is a test."));
        stage.setScene(new Scene(root));
        stage.setTitle("My Application");
        stage.sizeToScene();
        stage.show();

        FXTrayIcon trayIcon = new FXTrayIcon(stage, getClass().getResource("icons8-link-64.png"));
        trayIcon.persistIconOnLastJavaFXStageHidden(true);
        trayIcon.addExitItem(true);
        trayIcon.show();

    }
}