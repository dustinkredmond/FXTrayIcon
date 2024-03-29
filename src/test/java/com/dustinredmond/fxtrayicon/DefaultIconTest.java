package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.stage.Stage;

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

public class DefaultIconTest extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Default Icon");

		new FXTrayIcon
				.Builder(primaryStage)
				.menuItem("Option 1", e -> menu1())
				.menuItem("Option 2", e -> menu2())
				.separator()
				.menuItem("Exit", e -> System.exit(0))
				.show()
				.build();
	}

	private void menu1() {System.out.println("Option 1");}
	private void menu2() {System.out.println("Option 2");}

	public static void main(String[] args) {
		launch(args);
	}
}