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

import javafx.application.Application;

public class Start {

	/**
	 * THIS CLASS MUST BE RUN FIRST!
	 *
	 * Kicks off the com.dustinredmond.fxtrayicon.notaskbaricon.Main runnable test
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		// This is an awt property which removes the icon
		// from the Dock on a mac and the TaskBar on Windows
		System.setProperty("apple.awt.UIElement", "true");
		java.awt.Toolkit.getDefaultToolkit();

		// This is a call to JavaFX application main method.
		// From now on we are transferring control to FX application.
		Application.launch(Main.class, args);
	}

}