package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class TestTextChangeInFXItem extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXTrayIcon icon = new FXTrayIcon(primaryStage);
        MenuItem item = new MenuItem("Change Me");
        icon.addMenuItem(item);
        icon.show();
        item.setText("Changed");
    }
}
