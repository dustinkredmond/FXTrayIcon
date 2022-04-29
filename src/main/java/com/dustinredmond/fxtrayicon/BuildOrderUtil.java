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

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.util.HashMap;
import java.util.Map;

class BuildOrderUtil {

	private static Integer                      index       = 0;
	private static final Map<Integer, MenuObject> objectMap = new HashMap<>();

	/**
	 * Used by Builder class to add a {@code javafx.scene.control.Menu}
	 * object into FXTrayIcon, maintaining the order in which it is added
	 * within the build sentence.
	 * @param menu a {@code javafx.scene.control.Menu} object
	 */
	static void addMenu(Menu menu) {
		objectMap.put(index, new MenuObject(menu));
		index++;
	}

	/**
	 * Used by Builder class to add a {@code javafx.scene.control.MenuItem}
	 * object into FXTrayIcon, maintaining the order in which it is added
	 * within the build sentence.
	 * @param menuItem a {@code javafx.scene.control.MenuItem} object
	 */
	static void addMenuItem(MenuItem menuItem) {
		objectMap.put(index, new MenuObject(menuItem));
		index++;
	}

	/**
	 * Used by Builder class to add many {@code javafx.scene.control.MenuItem}
	 * objects into FXTrayIcon, maintaining the order in which it is added
	 * within the build sentence.
	 * @param menuItems an array of {@code javafx.scene.control.MenuItem} objects
	 */
	static void addMenuItems(MenuItem... menuItems) {
		for(MenuItem menuItem : menuItems) {
			addMenuItem(menuItem);
		}
	}

	/**
	 * Used by Builder class to add a separator object to FXTrayIcon while
	 * maintaining the order in which the separator was added in the build sentence.
	 */
	static void addSeparator() {
		objectMap.put(index, new MenuObject());
		index++;
	}

	/**
	 * Used by the Builder class to get the number of objects that were
	 * passed via the build sentence. Used for iteration to extract each
	 * object then add it into FXTrayIcon after instantiation.
	 */
	static Integer getItemCount() {
		return index; //since current index value is not used, this number is accurate
	}

	/**
	 * Used by the Builder class during iteration of the objects that are
	 * stored in this class.
	 * @param index an Integer
	 */
	static Menu getMenu(Integer index) {
		return objectMap.get(index).getMenu();
	}

	/**
	 * Used by the Builder class during iteration of the objects that are
	 * stored in this class.
	 * @param index an Integer
	 */
	static MenuItem getMenuItem(Integer index) {
		return objectMap.get(index).getMenuItem();
	}

	/**
	 * Used by the Builder class during iteration of the objects that are
	 * stored in this class.
	 * @param index an Integer
	 */
	static ItemType getItemType(Integer index) {
		return objectMap.get(index).getItemType();
	}

	/**
	 * This is a pseudo record class that is used by class
	 * BuildOrderUtil in a Map, so that the objects that are passed
	 * to Builder in the build sentence can have their serial
	 * order maintained.
	 */
	private static class MenuObject {

		private Menu menu;
		private MenuItem menuItem;
		private final ItemType itemType;

		/**
		 * class constructor that only accepts a {@code javafx.scene.control.Menu} object
		 * @param menu a {@code javafx.scene.control.Menu} object
		 */
		MenuObject(Menu menu) {
			this.menu = menu;
			this.itemType = ItemType.MENU;
		}

		/**
		 * class constructor that only accepts a {@code javafx.scene.control.MenuItem} object
		 * @param menuItem a {@code javafx.scene.control.MenuItem} object
		 */
		MenuObject(MenuItem menuItem) {
			this.menuItem = menuItem;
			this.itemType = ItemType.MENU_ITEM;
		}

		/**
		 * Default class constructor, used to track when the Builder build sentence
		 * had a separator passed into the Builder class.
		 */
		MenuObject() {
			this.itemType = ItemType.SEPARATOR;
		}

		/**
		 * @return ItemType
		 */
		ItemType getItemType() {
			return itemType;
		}

		/**
		 * @return {@code javafx.scene.control.Menu} object
		 */
		Menu getMenu() {
			return menu;
		}

		/**
		 * @return {@code javafx.scene.control.MenuItem} object
		 */
		MenuItem getMenuItem() {
			return menuItem;
		}

	}
}
