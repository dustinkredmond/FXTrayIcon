package com.dustinredmond.fxtrayicon;

/*
 * Copyright (c) 2022 Dustin Redmond and contributors
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
