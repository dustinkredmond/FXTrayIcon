package com.dustinredmond.fxtrayicon;

/*
 * Copyright (c) 2022 Dustin K. Redmond & contributors
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.awt.*;

/**
 * This is a package local class
 */
class Restricted implements RestrictedInterface {

	/**
	 * This is the working instantiation of the TrayIcon object.
	 * This is the only instantiation that will exist for any
	 * instantiation of the FXTrayIcon class.
	 */
	private final TrayIcon trayIcon;

	/**
	 * Class Constructor used by FXTrayIcon class
	 */
	public Restricted(Image image, String title, PopupMenu popupMenu  ) {
		this.trayIcon = new TrayIcon(image, title, popupMenu);
	}

	/**
	 * public getter for the trayIcon object. This method is only
	 * obtainable through the getRestricted() method in FXTrayIcon class.
	 */
	@Override
	public TrayIcon getTrayIcon() {
		return trayIcon;
	}
}
