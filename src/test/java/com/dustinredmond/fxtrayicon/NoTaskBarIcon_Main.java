package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class NoTaskBarIcon_Main extends Application {

	/**
	 *
	 * YOU MUST LAUNCH THE APP USING THE START CLASS FOR THIS TO WORK
	 *
	 */

	private static final String TEST_ICON = "icons8-link-64.png";
	private static FXTrayIcon icon;

	@Override
	public void start(Stage primaryStage) throws Exception {
		if (FXTrayIcon.isSupported()){
			icon = new FXTrayIcon(primaryStage, getClass().getResource(TEST_ICON));
			MenuItem menuShowStage = new MenuItem("Show Scene");
			MenuItem menuShowMessage = new MenuItem("Show Message");
			MenuItem menuExit = new MenuItem("Exit");
			menuShowStage.setOnAction(e->primaryStage.show());
			menuShowMessage.setOnAction(e->showMessage());
			menuExit.setOnAction(e-> System.exit(0));
			icon.addMenuItem(menuShowStage);
			icon.addMenuItem(menuShowMessage);
			icon.addMenuItem(menuExit);
			icon.showMinimal();
		}
	}

	private void showMessage() {
		icon.showInfoMessage("Check It Out!","Look Ma, No Taskbar Icon!");
	}


	public static void main(String[] args) {
		launch(args);
	}
}
