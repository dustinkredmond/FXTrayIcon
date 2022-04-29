package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NestedSubMenus extends Application {


	private Label lblRightMessage;
	private Label lblLeftMessage;
	private TextField tfLeftMessage;
	private TextField tfRightMessage;

	@Override public void start(Stage stage) throws Exception {
		SplitPane splitPane;
		Label lblRight = new Label("Right Side");
		Label lblLeft = new Label("Left Side");
		lblRightMessage = new Label();
		lblLeftMessage = new Label();
		Label lblRightText = new Label("Type message, then use tray menu to send it to the left");
		Label lblLeftText = new Label("Type message, then use tray menu to send it to the right");
		tfLeftMessage = new TextField();
		tfRightMessage = new TextField();
		VBox vboxLeft = new VBox(10,lblLeft, tfLeftMessage, lblLeftMessage, lblLeftText);
		vboxLeft.setPadding(new Insets(5,15,5,15));
		VBox vboxRight = new VBox(10,lblRight, tfRightMessage, lblRightMessage, lblRightText);
		vboxRight.setPadding(new Insets(5,15,5,15));
		splitPane = new SplitPane(vboxLeft, vboxRight);
		Scene scene = new Scene(splitPane);
		stage.setScene(scene);

		new FXTrayIcon.Builder(stage)
				.applicationTitle("Nested Sub Menus")
				.addTitleItem(true)
				.menu("Send Right", newMenuItem("Send Text", e-> sendTextRight()), newMenuItem("Clear Text", e->clearRightMessage()))
				.menu("Send Left", newMenuItem("Send Text", e-> sendTextLeft()), newMenuItem("Clear Text", e->clearLeftMessage()))
				.addExitMenuItem()
				.show()
				.build();
	}

	private MenuItem newMenuItem(String label, EventHandler<ActionEvent> event) {
		MenuItem menuItem = new MenuItem(label);
		menuItem.setOnAction(event);
		return menuItem;
	}

	private void sendTextRight() {
		Platform.runLater(() -> lblRightMessage.setText(tfLeftMessage.getText()));
	}

	private void sendTextLeft() {
		Platform.runLater(() -> lblLeftMessage.setText(tfRightMessage.getText()));
	}

	private void clearRightMessage() {
		Platform.runLater(() -> lblRightMessage.setText(""));
	}

	private void clearLeftMessage() {
		Platform.runLater(() -> lblLeftMessage.setText(""));
	}
}
