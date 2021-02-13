package com.dustinredmond.fxtrayicon.notaskbaricon;

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
