package com.dustinredmond.fxtrayicon;

public class NoTaskBarIcon_Start {
	public static void main(String[] args) {

		/**
		 * THIS MUST BE THE CLASS THAT LAUNCHES THE APP OR IT WONT REMOVE THE ICON
		 */


		// This is an awt property which removes the icon
		// from the Dock on a mac and the TaskBar on Windows
		System.setProperty("apple.awt.UIElement", "true");
		java.awt.Toolkit.getDefaultToolkit();

		// This is a call to JavaFX application main method.
		// From now on we are transferring control to FX application.
		NoTaskBarIcon_Main.main(args);
	}
}
