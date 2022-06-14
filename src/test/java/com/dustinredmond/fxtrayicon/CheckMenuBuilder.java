package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.Stage;

import java.util.List;


public class CheckMenuBuilder  extends Application {

	FXTrayIcon fxti;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Default Icon");

		fxti = new FXTrayIcon.Builder(primaryStage)
				.checkMenuItem("Apples", e-> checkApples())
				.checkMenuItem("Oranges", e-> checkOranges())
				.checkMenuItem("Pears", e-> checkPears())
				.separator()
				.addExitMenuItem()
				.show()
				.build();

	}

	private void checkApples() {
		List<java.awt.CheckboxMenuItem> list = fxti.getCheckMenuItems();
		for (java.awt.CheckboxMenuItem cbmi : list) {
			if (!cbmi.getLabel().equals("Apples")) {
				cbmi.setState(false);
			}
		}
	}

	private void checkOranges() {
		fxti.getCheckMenuItem("Pears").setState(false);
		fxti.getCheckMenuItem("Apples").setState(false);
	}

	private void checkPears() {
		fxti.getCheckMenuItem("Oranges").setState(false);
		fxti.getCheckMenuItem("Apples").setState(false);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
