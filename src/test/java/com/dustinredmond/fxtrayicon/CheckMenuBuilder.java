package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.stage.Stage;
import java.util.List;

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
