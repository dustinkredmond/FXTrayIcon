package com.jfxdev.fxtrayicon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.MenuItem;

public class IconTest extends Application {

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        stage.setScene(new Scene(root));

        // By default, our FXTrayIcon will have an entry with our Application's title in bold font,
        // when clicked, this MenuItem will call stage.show()
        //
        // This can be disabled by simply removing the MenuItem after instantiating the FXTrayIcon
        // though, by convention, most applications implement this functionality.
        stage.setTitle("FXTrayIcon test!");

        // Instantiate the FXTrayIcon providing the parent Stage and a path to an Image file
        FXTrayIcon trayIcon = new FXTrayIcon(stage, getClass().getResource("icons8-link-64.png"));
        trayIcon.show();

        // By default the FXTrayIcon's tooltip will be the parent stage that we passed in the constructor
        // This method can override this
        trayIcon.setTrayIconTooltip("An alternative tooltip!");

        // Note, we need to use the AWT MenuItem, not JavaFX's MenuItem
        MenuItem menuItemTest = new MenuItem("Create some JavaFX component!");
        // If we're going to modify the scene graph of JavaFX, this must be done
        // on the JavaFX application thread. Thus, we must wrap this change in
        // a call to Platform.runLater
        menuItemTest.addActionListener(e -> Platform.runLater(() ->
                new Alert(Alert.AlertType.INFORMATION, "We just called JavaFX from an AWT menu!").showAndWait()));
        trayIcon.addMenuItem(menuItemTest);

        VBox vBox = new VBox(5);
        vBox.getChildren().add(new Label("You should see a tray icon!"));
        Button buttonRemoveTrayIcon = new Button("Remove TrayIcon");
        vBox.getChildren().add(buttonRemoveTrayIcon);

        // Removing the FXTrayIcon, this will also cause the JVM to terminate
        // after the last JavaFX Stage is hidden
        buttonRemoveTrayIcon.setOnAction(e -> trayIcon.hide());

        root.setCenter(vBox);
        stage.sizeToScene();
        stage.show();
    }
}