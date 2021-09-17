package com.dustinredmond.fxtrayicon;

/*
 * Copyright (c) 2021 Dustin K. Redmond & contributors
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

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.Random;

import static javafx.scene.layout.AnchorPane.*;
import static javafx.scene.layout.AnchorPane.setRightAnchor;

public class TestSwitchIconsOnTheFly extends Application {


	private ClassLoader resource = TestSwitchIconsOnTheFly.class.getClassLoader();

	private final String fileName1 = "FXIconRedWhite.png";
	private final String fileName2 = "FXIconRedYellow.png";
	private final String fileName3 = "FXIconBlueWhite.png";
	private final String fileName4 = "FXIconBlueYellow.png";
	private final String fileName5 = "FXIconGreenWhite.png";
	private final String fileName6 = "FXIconGreenYellow.png";



	URL icon1 = getClass().getResource(fileName1);
	URL icon2 = getClass().getResource(fileName2);
	URL icon3 = getClass().getResource(fileName3);
	URL icon4 = getClass().getResource(fileName4);
	URL icon5 = getClass().getResource(fileName5);
	URL icon6 = getClass().getResource(fileName6);

	private final String name1 = "Red-White";
	private final String name2 = "Red-Yellow";
	private final String name3 = "Blue-White";
	private final String name4 = "Blue-Yellow";
	private final String name5 = "Green-White";
	private final String name6 = "Green-Yellow";

	private int width;
	private int height;

	private FXTrayIcon trayIcon  = null;
	private URL[]      imageURLs = new URL[]{icon1,icon2,icon3,icon4,icon5,icon6};
	private ObservableList<String> nameList = FXCollections.observableArrayList(Arrays.asList(name1,name2,name3,name4,name5,name6));
	public final String style1 = "-fx-background-color: radial-gradient(radius 180%, orange, derive(darkred, -30%), derive(yellow, 30%));";
	public final String style2 = "-fx-background-color: radial-gradient(radius 180%, pink, derive(purple, -30%), derive(purple, 30%));";
	public final String style3 = "-fx-background-color: radial-gradient(radius 180%, yellow, derive(darkorange, -30%), derive(lightsalmon, 30%));";
	public final String style4 = "-fx-background-color: radial-gradient(radius 180%, cyan, derive(darkgreen, -30%), derive(ghostwhite, 30%));";

	private ImageView iView;

	private void choseRandomIcon() {
		Random random = new Random();
		changeIcon(imageURLs[random.nextInt(6)]);
	}
	AnchorPane root;
	@Override public void start(Stage stage) {
		root = new AnchorPane();
		root.setStyle(style3);
		stage.setWidth(650);
		stage.setHeight(450);

		width = (System.getProperty("os.name").contains("Windows")) ? 16 : 26; //Windows likes 16 x 16 icons
		height = (System.getProperty("os.name").contains("Windows")) ? 16 : 26;

		stage.setScene(new Scene(root));

		// By default, our FXTrayIcon will have an entry with our Application's title in bold font,
		// when clicked, this MenuItem will call stage.show()
		//
		// This can be disabled by simply removing the MenuItem after instantiating the FXTrayIcon
		// though, by convention, most applications implement this functionality.
		stage.setTitle("FXTrayIcon Switch Icons On The Fly Test");

		// Instantiate the FXTrayIcon providing the parent Stage and a path to an Image file
		MenuItem menuRandom = new MenuItem("Random Icon");
		MenuItem menuExit = new MenuItem("Exit Application");
		menuRandom.setOnAction(e->choseRandomIcon());
		menuExit.setOnAction(e-> System.exit(0));

		// With the Builder class, we can quickly create our FXTrayIcon Menu
		trayIcon = new FXTrayIcon.Builder(stage, icon1,width,height)
				.toolTip("Chose a random icon")
				.menuItem("RandomIcon",e->choseRandomIcon())
				.separator()
				.menuItem("Exit Application",e-> System.exit(0))
				.show()
				.build();

		ChoiceBox<String> iconChoiceBox = new ChoiceBox<>(nameList);
		iconChoiceBox.setOnAction(e-> newIconChoice(iconChoiceBox.getValue()));

		// We can also nest menus, below is an Options menu with sub-items
		VBox vBox = new VBox(5);
		Label lblChoose = new Label("Chose an Icon");
		iView = new ImageView();
		iView.setPreserveRatio(true);
		//setNodePosition(getNode(root,iView),10,10,50,50);
		setNodePosition(getNode(root,lblChoose),10,-1,12.5,-1);
		setNodePosition(getNode(root,iconChoiceBox),95,-1,10,-1);


		Button buttonDefaultMsg = new Button("Show a \"Default\" message");

		// showDefaultMessage uses the FXTrayIcon image in the notification
		buttonDefaultMsg.setOnAction(e -> trayIcon.showMessage("A caption text", "Some content text."));

		Button buttonInfoMsg = new Button("Show a \"Info\" message");
		// other showXXX methods use an icon appropriate for the message type
		buttonInfoMsg.setOnAction(e -> trayIcon.showInfoMessage("A caption text", "Some content text"));

		Button buttonWarnMsg = new Button("Show a \"Warn\" message");
		buttonWarnMsg.setOnAction(e -> trayIcon.showWarningMessage("A caption text", "Some content text"));

		Button buttonErrorMsg = new Button("Show a \"Error\" message");
		buttonErrorMsg.setOnAction(e -> trayIcon.showErrorMessage("A caption text", "Some content text"));

		HBox hBox = new HBox(5, buttonDefaultMsg, buttonInfoMsg, buttonWarnMsg, buttonErrorMsg);
		vBox.getChildren().addAll(iView,hBox);
		setNodePosition(getNode(root,vBox),10,10,-1,10);
		stage.show();
	}

	private void changeIcon(URL iconURL) {
			javafx.scene.image.Image image = new Image(iconURL.toExternalForm());
			iView.setImage(image);
			iView.setPreserveRatio(true);
			double w = iView.getScene().getWindow().getWidth();
			double h = iView.getScene().getWindow().getHeight();
			//iView.setFitWidth(w - (w * .25));
			iView.setFitHeight(h - (h * .25));
			trayIcon.setGraphic(image);
	}

	private void newIconChoice(String iconName) {
		int index;
		switch(iconName) {
			case name2:
				index = 1;
				root.setStyle(style1);
				break;

			case name3:
				index = 2;
				root.setStyle(style1);
				break;

			case name4:
				index = 3;
				root.setStyle(style2);
				break;

			case name5:
				index = 4;
				root.setStyle(style3);
				break;

			case name6:
				index = 5;
				root.setStyle(style3);
				break;

			default:
				index = 0;
				root.setStyle(style1);
				break;
		}
		System.out.println(index);
		changeIcon(imageURLs[index]);
	}


	private Node getNode(AnchorPane root, VBox control) {
		root.getChildren().add(control);
		return root.getChildren().get(root.getChildren().indexOf(control));
	}
	private Node getNode(AnchorPane root, Label control) {
		root.getChildren().add(control);
		return root.getChildren().get(root.getChildren().indexOf(control));
	}
	private Node getNode(AnchorPane root, Button control) {
		root.getChildren().add(control);
		return root.getChildren().get(root.getChildren().indexOf(control));
	}
	private Node getNode(AnchorPane root, ChoiceBox<String> control) {
		root.getChildren().add(control);
		return root.getChildren().get(root.getChildren().indexOf(control));
	}
	private Node getNode(AnchorPane root, ImageView control) {
		root.getChildren().add(control);
		return root.getChildren().get(root.getChildren().indexOf(control));
	}
	private void setNodePosition(Node node, double left, double right, double top, double bottom) {
		if (top != -1) setTopAnchor(node, top);
		if (bottom != -1) setBottomAnchor(node, bottom);
		if (left != -1) setLeftAnchor(node, left);
		if (right != -1) setRightAnchor(node, right);
	}
}
