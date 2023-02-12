package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

public class TestRestrictedAccess extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage primaryStage) throws Exception {
		createScene(primaryStage);
		FXTrayIcon icon = new FXTrayIcon.Builder(primaryStage,getIcon(),24,24)
				.menuItem("This does nothing", e->{})
				.separator()
				.addExitMenuItem("Exit")
				.show()
				.build();

		/**
		 * This accesses the restricted trayIcon object and adds a right click
		 * event listener, which when clicked, shows the stage.
		 */
		icon.getRestricted().getTrayIcon().addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 3) {
					Platform.runLater(() -> {
						primaryStage.show();
					});
				}
			}

			@Override public void mousePressed(MouseEvent ignored) {

			}

			@Override public void mouseReleased(MouseEvent ignored) {

			}

			@Override public void mouseEntered(MouseEvent ignored) {

			}

			@Override public void mouseExited(MouseEvent ignored) {

			}
		});
	}

	private void createScene(Stage stage) {
		Label label = new Label("Right clicking works!");
		label.setPrefSize(150,55);
		label.setAlignment(Pos.CENTER);
		VBox box = new VBox(label);
		scene = new Scene(box);
		stage.setScene(scene);
	}

	Scene scene;

	private URL getIcon() {
		return getClass().getResource("FXIconRedYellow.png");
	}

}
