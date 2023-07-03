package com.dustinredmond.fxtrayicon.issue71;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This test class is meant to illustrate that the problem from
 * <a href="https://github.com/dustinkredmond/FXTrayIcon/issues/71">Issue #71</a> has been fixed.
 */


public class AddMenuItem extends Application {

    private FXTrayIcon fxTrayIcon;

    @Override
    public void start(Stage primaryStage) throws Exception {
        fxTrayIcon = new FXTrayIcon
                .Builder(primaryStage)
                .build();
        show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Label lblAfter;

    private void show() {
        fxTrayIcon.show();
        fxTrayIcon.addExitItem("Exit");
        Stage stage = new Stage();
        double width = 300;
        double height = 225;
        Button btnAdd = new Button("Add");
        Button btnClose = new Button("Close");
        Label lblTitle = new Label("Type in a MenuItem name then press Add");
        TextField tfName = new TextField();
        lblAfter = new Label();
        lblAfter.setPrefWidth(225);
        tfName.setPrefWidth(150);
        VBox vBox = new VBox(10, lblTitle, tfName, btnAdd, lblAfter, btnClose);
        btnAdd.setOnAction(e->{
            addMenuItem(tfName.getText());
            lblAfter.setText("Now try the " + tfName.getText() + " menu item");
        });
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, width, height);
        stage.setScene(scene);
        stage.show();
        btnClose.setOnAction(e->System.exit(0));
    }

    private void addMenuItem(String menuName) {
        MenuItem menuItem = new MenuItem(menuName);
        menuItem.setOnAction(e-> {
            show(menuName);
            lblAfter.setText("");
        });
        fxTrayIcon.addMenuItem(menuItem);
    }

    private void show(String menuItemName) {
        double width = 250;
        double height = 100;
        Button btnOK = new Button("OK");
        Label lblMenuItem = new Label(menuItemName + " Works!");
        lblMenuItem.setPrefWidth(width);
        VBox vBox = new VBox(15, lblMenuItem, btnOK);
        vBox.setPadding(new Insets(20));
        vBox.setAlignment(Pos.CENTER);
        Stage stage = new Stage();
        Scene scene = new Scene(vBox, width, height);
        stage.setScene(scene);
        stage.show();
        btnOK.setOnAction(e->stage.hide());
    }

}
