package com.dustinredmond.fxtrayicon.notaskbaricon;

/*
 * Copyright (c) 2021 Michael Sims, Dustin Redmond and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.dustinredmond.fxtrayicon.RunnableTest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.net.URL;

public class Main extends Application {

	/**
	 * Launch the Start class FIRST!
	 * @param stage The default JavaFX Stage
	 */
	@Override
	public void start(Stage stage) {
		URL iconFile = new RunnableTest().getIcon();
		stage.initStyle(StageStyle.UTILITY);
		stage.setHeight(0);
		stage.setWidth(0);
		Stage mainStage = new Stage();
		mainStage.initOwner(stage);
		mainStage.initStyle(StageStyle.UNDECORATED);

		Label label = new Label("No TaskBar Icon");
		Label label2 = new Label("Type a message and click the button");
		label2.setAlignment(Pos.CENTER_LEFT);
		TextField tfInput = new TextField();
		Button button = new Button("Show Alert");
		button.setOnAction(e -> showMessage(tfInput.getText()));

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10,20,10,20));
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(20);
		vbox.getChildren().addAll(label, label2, tfInput, button);

		StackPane root = new StackPane();
		root.getChildren().add(vbox);
		mainStage.setScene(new Scene(root, 250, 200));
		mainStage.initStyle(StageStyle.UTILITY); //This is what makes the icon disappear in Windows.
		if (FXTrayIcon.isSupported()) {
			icon = new FXTrayIcon(stage, iconFile);

			MenuItem menuShowStage   = new MenuItem("Show Stage");
			MenuItem menuHideStage   = new MenuItem("Hide Stage");
			MenuItem menuShowMessage = new MenuItem("Show Message");
			MenuItem menuExit        = new MenuItem("Exit");
			menuShowStage.setOnAction(e -> {
				Platform.runLater(()-> com.sun.javafx.application.PlatformImpl.setTaskbarApplication(false));
				mainStage.show();
			});
			menuHideStage.setOnAction(e -> {
				Platform.runLater(() -> com.sun.javafx.application.PlatformImpl.setTaskbarApplication(true));
				mainStage.hide();
			});
			menuShowMessage.setOnAction(e -> showMessage());
			menuExit.setOnAction(e -> System.exit(0));
			icon.addMenuItem(menuShowStage);
			icon.addMenuItem(menuHideStage);
			icon.addMenuItem(menuShowMessage);
			icon.addMenuItem(menuExit);
			icon.show();
		}

	}

	private FXTrayIcon icon;

	private void showMessage() {
		icon.showInfoMessage("Check It Out!","Look Ma, No Taskbar Icon!");
	}

	private void showMessage(String message) {
		icon.showInfoMessage("Message For You!",message);
	}
}