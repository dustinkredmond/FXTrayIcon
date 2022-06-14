package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.Stage;

import java.util.List;

public class CheckMenu extends Application {

	CheckMenuItem cmuApples  = new CheckMenuItem("Apples");
	CheckMenuItem cmuOranges = new CheckMenuItem("Oranges");
	CheckMenuItem cmuPears   = new CheckMenuItem("Pears");

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Default Icon");

		FXTrayIcon fxti = new FXTrayIcon.Builder(primaryStage)
				.show()
				.build();

		cmuApples.setOnAction(e -> {
			List<java.awt.CheckboxMenuItem> list = fxti.getCheckMenuItems();
			for (java.awt.CheckboxMenuItem cbmi : list) {
				if (!cbmi.getLabel().equals("Apples")) {
					cbmi.setState(false);
				}
			}
		});

		cmuOranges.setOnAction(e -> {
			fxti.getCheckMenuItem("Pears").setState(false);
			fxti.getCheckMenuItem("Apples").setState(false);
		});

		cmuPears.setOnAction(e -> {
			fxti.getCheckMenuItem("Oranges").setState(false);
			fxti.getCheckMenuItem("Apples").setState(false);
		});

		fxti.addMenuItem(cmuApples);
		fxti.addMenuItem(cmuOranges);
		fxti.addMenuItem(cmuPears);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
