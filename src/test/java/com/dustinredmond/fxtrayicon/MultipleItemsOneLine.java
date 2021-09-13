package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MultipleItemsOneLine extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Many From One");
		FXTrayIcon icon = new FXTrayIcon(primaryStage, getClass().getResource("icons8-link-64.png"));
		icon.show();
		MenuItem menu1     = new MenuItem("Option 1");
		MenuItem menu2     = new MenuItem("Option 2");
		MenuItem exitMenu  = new MenuItem("Exit");
		menu1.setOnAction(e -> menu1());
		menu2.setOnAction(e -> menu2());
		exitMenu.setOnAction(e -> System.exit(0));
		icon.addMenuItems(menu1, menu2, exitMenu);
	}

	private void menu1() {System.out.println("Option 1");}
	private void menu2() {System.out.println("Option 2");}

	public static void main(String[] args) {
		launch(args);
	}
}
