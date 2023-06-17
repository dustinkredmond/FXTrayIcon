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

import com.dustinredmond.fxtrayicon.annotations.API;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class for creating a JavaFX System Tray Icon.
 * Allows for a developer to create a tray icon
 * using JavaFX style API.
 */
public class FXTrayIcon {

    private static final Integer winScale = 16;
    private static final Integer macLinScale = 22;
    private boolean shown = false;
    private ActionListener exitMenuItemActionListener;
    private Animation animation;
    private Image icon;
    private static IconScale iconScale = isWin() ? new IconScale(winScale) : new IconScale(macLinScale);
    private static final IconScale coreSize = isWin() ? new IconScale(winScale) : new IconScale(macLinScale);


    /**
     * The default AWT SystemTray
     */
    private final SystemTray tray;

    /**
     * The parent Stage of the FXTrayIcon
     */
    private Stage parentStage;

    /**
     * The application's title, to be used
     * as default tooltip text for the FXTrayIcon
     */
    private String appTitle;

    /**
     * The AWT TrayIcon managed by FXTrayIcon
     */
    protected final TrayIcon trayIcon;

    /**
     * The AWT PopupMenu managed by FXTrayIcon
     */
    private final PopupMenu popupMenu = new PopupMenu();

    /**
     * If true, when the FXTrayIcon's {@code show()}
     * method is called, adds a MenuItem that will allow
     * for the JavaFX program to be terminated and the
     * TrayIcon to be removed.
     * This is set to false by default.
     */
    private boolean addExitMenuItem = false;

    /**
     * If true, when the FXTrayIcon's {@code show()}
     * method is called, adds a MenuItem with the main Stage's title,
     * that will show the main JavaFX stage when clicked.
     * This is set to false by default.
     */
    private boolean addTitleMenuItem = false;

    /**
     * Used for the addExitMenuItem() builder method.
     */
    private String exitMenuItemLabel = "";

    /**
     * This is used to tell the show method to not add the event listener which
     * normally shows the stage when the icon is clicked. On MacOS, this is
     * normally activated with a right click.
     */
    private boolean noDefaultAction = false;

    /**
     * Used for gaining access to AWT components of the library
     */
    private final RestrictedInterface restricted;

    /**
     * Creates a {@code MouseListener} whose
     * single-click action performs the passed
     * JavaFX EventHandler
     *
     * @param e A JavaFX event to be performed
     * @return A MouseListener fired by single-click
     */
    private MouseListener getPrimaryClickListener(EventHandler<ActionEvent> e) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                Platform.runLater(() -> e.handle(new ActionEvent()));
            }

            @Override
            public void mousePressed(MouseEvent ignored) {
                //This is empty on purpose
            }

            @Override
            public void mouseReleased(MouseEvent ignored) {
                //This is empty on purpose
            }

            @Override
            public void mouseEntered(MouseEvent ignored) {
                //This is empty on purpose
            }

            @Override
            public void mouseExited(MouseEvent ignored) {
                //This is empty on purpose
            }
        };
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon and a provided{@code javafx.stage.Stage} as its parent.
     *
     * @param parentStage   The parent Stage of the tray icon.
     * @param iconImagePath A path to an icon image
     * @param iconWidth     optional to set a different icon width
     * @param iconHeight    optional to set a different icon height
     */
    @API
    public FXTrayIcon(Stage parentStage, URL iconImagePath, int iconWidth, int iconHeight) {
        this(parentStage, loadImageFromURL(iconImagePath, iconWidth, iconHeight), true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon and a provided{@code javafx.stage.Stage} as its parent.
     *
     * @param parentStage   The parent Stage of the tray icon.
     * @param iconImagePath A path to an icon image
     */
    @API
    public FXTrayIcon(Stage parentStage, URL iconImagePath) {
        this(parentStage, loadImageFromURL(iconImagePath), true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon File with dimensions and a provided{@code javafx.stage.Stage}
     * as its parent.
     *
     * @param parentStage The parent Stage of the tray icon.
     * @param iconFile    A java.io.File object
     * @param iconWidth   an int, icon width
     * @param iconHeight  an int, icon height
     */
    @API
    public FXTrayIcon(Stage parentStage, File iconFile, int iconWidth, int iconHeight) {
        this(parentStage, loadImageFromFile(iconFile, iconWidth, iconHeight), true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon File and a provided{@code javafx.stage.Stage} as its parent.
     *
     * @param parentStage The parent Stage of the tray icon.
     * @param iconFile    A java.io.File object
     */
    @API
    public FXTrayIcon(Stage parentStage, File iconFile) {
        this(parentStage, loadImageFromFile(iconFile), true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon Image with dimensions and a provided{@code javafx.stage.Stage}
     * as its parent.
     *
     * @param parentStage The parent Stage of the tray icon.
     * @param javaFXImage A javafx.scene.image.Image object
     * @param iconWidth   an int, icon width
     * @param iconHeight  an int, icon height
     */
    @API
    public FXTrayIcon(Stage parentStage, javafx.scene.image.Image javaFXImage, int iconWidth, int iconHeight) {
        this(parentStage, loadImageFromFX(javaFXImage, iconWidth, iconHeight), true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon Image and a provided{@code javafx.stage.Stage} as its parent.
     *
     * @param parentStage The parent Stage of the tray icon.
     * @param javaFXImage A javafx.scene.image.Image object
     */
    @API
    public FXTrayIcon(Stage parentStage, javafx.scene.image.Image javaFXImage) {
        this(parentStage, loadImageFromFX(javaFXImage), true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon and specified dimensions and a provided{@code javafx.stage.Stage} as its parent.
     *
     * @param parentStage The parent Stage of the tray icon. Must not be null.
     * @param image       a java.awt.Image object. Must not be null
     * @param iconWidth   an int, icon Width
     * @param iconHeight  an int, icon Height
     */
    @API
    public FXTrayIcon(Stage parentStage, Image image, int iconWidth, int iconHeight) {
        this(parentStage, loadImageFromAWT(image, iconWidth, iconHeight), true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon image and a provided{@code javafx.stage.Stage} as its parent.
     *
     * @param parentStage The parent Stage of the tray icon. Must not be null.
     * @param image       a java.awt.Image object. Must not be null
     */
    @API
    public FXTrayIcon(Stage parentStage, Image image) {
        this(parentStage, loadImageFromAWT(image), true);
    }

    private FXTrayIcon(Stage parentStage, Image image, boolean finalCall) {
        if (finalCall) {
            Objects.requireNonNull(parentStage, "parentStage must not be null");
            Objects.requireNonNull(image, "icon must not be null");
        }
        ensureSystemTraySupported();

        tray = SystemTray.getSystemTray();
        // Keeps the JVM running even if there are no
        // visible JavaFX Stages, otherwise JVM would
        // exit, and we lose the TrayIcon
        Platform.setImplicitExit(false);

        attemptSetSystemLookAndFeel();

        this.parentStage = parentStage;
        this.restricted = new Restricted(image, parentStage.getTitle(), popupMenu);
        this.trayIcon = this.restricted.getTrayIcon();
        this.trayIcon.setImageAutoSize(true);
    }

    /**
     * Use this method to gain access to the instantiated awt TrayIcon object
     */
    @API
    public RestrictedInterface getRestricted() {
        return restricted;
    }

    /**
     * Use this constructor to have FXTrayIcon use a default graphic for the tray icon.
     * This can be handy for "quick and dirty" runs of the library so that you don't need
     * to worry about setting up a graphic and defining the URL object.
     *
     * @param parentStage Stage for FXTrayIcon
     */
    @API
    public FXTrayIcon(Stage parentStage) {
        this(parentStage, loadDefaultIconImage());
    }

    /**
     * OPTIONAL Builder class that streamlines the instantiation of FXTrayIcon,
     * permitting the passing of a full set of MenuItems, Separators, and every
     * settable option of FXTrayIcon from one Builder line of code.
     */
    @API
    public static class Builder {

        private enum ConstructorImageOption {
            IMAGE, URL, FILE, FX_IMAGE, DEFAULT
        }

        private final ConstructorImageOption cio;
        private final Stage parentStage;
        private URL conImageURL;
        private File conImageFile;
        private Image conImage;
        private javafx.scene.image.Image conFXImage;

        private String tooltip = "";
        private String appTitle;
        private boolean addExitMenuItem = false;
        private String exitMenuItemLabel = "";
        private boolean addTitleMenuItem = false;
        private EventHandler<ActionEvent> event;
        private ActionListener exitMenuItemActionListener;
        private boolean showTrayIcon = false;
        private Image icon;
        private boolean noDefaultAction = false;
        protected LinkedList<Image> imageList;
        protected int frameRateMS;
        protected LinkedList<File> ImageFileList = null;
        protected LinkedList<javafx.scene.image.Image> ImageList = null;


        /**
         * By default, any method that accepts an icon but also does not require the dimensions, uses a default value
         * depending on the operating system and this method will overwrite those values and become the new default
         * values for anything you do concerning icons in FXTrayIcon.
         *
         * @param width  icon width in pixels
         * @param height icon height in pixels
         * @return this Builder
         */
        public Builder setIconSize(int width, int height) {
            iconScale = new IconScale(width, height);
            return this;
        }

        /**
         * Overloaded method, so you can put in just one value for the icon size when the width and height are
         * the same value; please read the discussion about icon sizes in the other method,
         *
         * @param sizeWH icon width AND height in pixels expressed as a single value (W = H)
         * @return this Builder
         */
        public Builder setIconSize(int sizeWH) {
            iconScale = new IconScale(sizeWH, sizeWH);
            return this;
        }

        /**
         * @param parentStage   The parent Stage of the tray icon.
         * @param iconImagePath A path to an icon image
         * @param iconWidth     optional to set a different icon width
         * @param iconHeight    optional to set a different icon height
         * @deprecated Creates an instance of FXTrayIcon with the provided
         * icon and a provided {@code javafx.stage.Stage} as its parent.
         */
        @API
        public Builder(Stage parentStage, URL iconImagePath, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            conImageURL = iconImagePath;
            cio = ConstructorImageOption.URL;
            iconScale = new IconScale(iconWidth, iconHeight);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon and a provided {@code javafx.stage.Stage} as its parent.
         *
         * @param parentStage   The parent Stage of the tray icon.
         * @param iconImagePath A path to an icon image
         */
        @API
        public Builder(Stage parentStage, URL iconImagePath) {
            this.parentStage = parentStage;
            conImageURL = iconImagePath;
            cio = ConstructorImageOption.URL;
        }

        /**
         * @param parentStage The parent Stage of the tray icon.
         * @param iconFile    A java.io.File object
         * @param iconWidth   an int, icon width
         * @param iconHeight  an int, icon height
         * @deprecated Creates an instance of FXTrayIcon with the provided
         * icon File with dimensions and a provided {@code javafx.stage.Stage}
         * as its parent.
         */
        @API
        public Builder(Stage parentStage, File iconFile, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            conImageFile = iconFile;
            cio = ConstructorImageOption.FILE;
            iconScale = new IconScale(iconWidth, iconHeight);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon File and a provided {@code javafx.stage.Stage} as its parent.
         *
         * @param parentStage The parent Stage of the tray icon.
         * @param iconFile    A java.io.File object
         */
        @API
        public Builder(Stage parentStage, File iconFile) {
            this.parentStage = parentStage;
            conImageFile = iconFile;
            cio = ConstructorImageOption.FILE;
        }

        /**
         * @param parentStage The parent Stage of the tray icon.
         * @param javaFXImage A javafx.scene.image.Image object
         * @param iconWidth   an int, icon width
         * @param iconHeight  an int, icon height
         * @deprecated Creates an instance of FXTrayIcon with the provided
         * icon Image with dimensions and a provided {@code javafx.stage.Stage}
         * as its parent.
         */
        @API
        public Builder(Stage parentStage, javafx.scene.image.Image javaFXImage, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            conFXImage = javaFXImage;
            cio = ConstructorImageOption.FX_IMAGE;
            iconScale = new IconScale(iconWidth, iconHeight);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon Image and a provided {@code javafx.stage.Stage} as its parent.
         *
         * @param parentStage The parent Stage of the tray icon.
         * @param javaFXImage A javafx.scene.image.Image object
         */
        @API
        public Builder(Stage parentStage, javafx.scene.image.Image javaFXImage) {
            this.parentStage = parentStage;
            conFXImage = javaFXImage;
            cio = ConstructorImageOption.FX_IMAGE;
        }

        /**
         * @param parentStage The parent Stage of the tray icon. Must not be null.
         * @param image       a java.awt.Image object. Must not be null
         * @param iconWidth   an int, icon Width
         * @param iconHeight  an int, icon Height
         * @deprecated Creates an instance of FXTrayIcon with the provided
         * icon and specified dimensions and a provided {@code javafx.stage.Stage} as its parent.
         */
        @API
        public Builder(Stage parentStage, Image image, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            conImage = image;
            cio = ConstructorImageOption.IMAGE;
            iconScale = new IconScale(iconWidth, iconHeight);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon image and a provided {@code javafx.stage.Stage} as its parent.
         *
         * @param parentStage The parent Stage of the tray icon. Must not be null.
         * @param image       a java.awt.Image object. Must not be null
         */
        @API
        public Builder(Stage parentStage, Image image) {
            this.parentStage = parentStage;
            conImage = image;
            cio = ConstructorImageOption.IMAGE;
        }

        /**
         * Use this constructor to have FXTrayIcon use a default graphic for the tray icon.
         * This can be handy for "quick and dirty" runs of the library so that you don't need
         * to worry about setting up a graphic and defining the URL object.
         *
         * @param parentStage Stage for FXTrayIcon
         */
        @API
        public Builder(Stage parentStage) {
            this.parentStage = parentStage;
            cio = ConstructorImageOption.DEFAULT;
        }

        /**
         * Add an optional animated icon to FXTrayIcon by passing in a LinkedList of JavaFX Image objects where each
         * image is a single frame of the animation, you also need to specify the time delay between frames.
         * filename. Animations are played using Javas animation library, which is extremely efficient and non-blocking.
         * <p>
         * Icon sizes are now using a default value based the OS that is running which can be overridden using the
         * setIconSize() method;
         *
         * @param imageList   - LinkedList containing javafx.scene.image.Image objects
         * @param frameRateMS - this an integer that defines the time delay between each image in milliseconds.
         * @return this builder object.
         */
        @API
        public Builder animate(LinkedList<javafx.scene.image.Image> imageList, int frameRateMS) {
            this.imageList = new LinkedList<>();
            this.ImageList = imageList;
            this.frameRateMS = frameRateMS;
            return this;
        }

        /**
         * Add an optional animated icon to FXTrayIcon by passing in a LinkedList of JavaFX Files where each file
         * is an image for each frame of the animation and optionally set the flag to have that file list sorted by
         * filename, you also need to specify the time delay between frames.
         * <p>
         * Animations are played using Javas animation library, which is extremely efficient and non-blocking.
         * <p>
         * Icon sizes are now using a default value based the OS that is running which can be overridden using the
         * setIconSize() method;
         *
         * @param imageFileList - LinkedList containing java.io.File objects
         * @param frameRateMS   - this an integer that defines the time delay between each image in milliseconds.
         * @param sortList      - boolean indicating that you want the file list sorted by filename.
         * @return this builder object.
         */
        @API
        public Builder animate(LinkedList<File> imageFileList, int frameRateMS, boolean sortList) {
            this.imageList = new LinkedList<>();
            ImageFileList = new LinkedList<>(imageFileList);
            if (sortList)
                ImageFileList.sort(Comparator.comparing(File::getName));
            this.frameRateMS = frameRateMS;
            return this;
        }

        /**
         * Add a MenuItem without passing your own.
         * This can be used repeatedly and the menuItems will be shown in the order you place them in your build sentence.
         *
         * @param label        String containing the name of this MenuItem
         * @param eventHandler - Will execute when the menu is clicked on.
         * @return this Builder object
         */
        @API
        public Builder menuItem(String label, EventHandler<ActionEvent> eventHandler) {
            javafx.scene.control.MenuItem menuItem = new javafx.scene.control.MenuItem(label);
            menuItem.setOnAction(eventHandler);
            BuildOrderUtil.addMenuItem(menuItem);
            return this;
        }

        /**
         * Can be used repeatedly to build your menuItems into FXTrayIcon.
         * The items will appear in the order they are stated in your build sentence.
         *
         * @param menuItem a javafx.scene.control.MenuItem object
         * @return this Builder object
         */
        @API
        public Builder menuItem(javafx.scene.control.MenuItem menuItem) {
            BuildOrderUtil.addMenuItem(menuItem);
            return this;
        }

        /**
         * Can be used to add more than one MenuItem by separating them by commas.
         * The items will appear in the order they are stated in your build sentence.
         *
         * @param menuItems a javafx.scene.control.MenuItem List
         * @return this Builder object
         */
        @API
        public Builder menuItems(javafx.scene.control.MenuItem... menuItems) {
            BuildOrderUtil.addMenuItems(menuItems);
            return this;
        }

        /**
         * Add a CheckMenuItem without passing your own.
         * This can be used repeatedly and the checkMenuItems will be shown in the
         * order you place them in your build sentence.<BR><BR>
         * See the getCheckMenuItem and getCheckMenuItems methods for accessing CheckMenuItems
         *
         * @param label        String containing the name of this CheckMenuItem
         * @param eventHandler - Will execute when the menuItem is clicked on.
         * @return this Builder object
         */
        @API
        public Builder checkMenuItem(String label, EventHandler<ActionEvent> eventHandler) {
            javafx.scene.control.CheckMenuItem checkMenuItem = new javafx.scene.control.CheckMenuItem(label);
            checkMenuItem.setOnAction(eventHandler);
            BuildOrderUtil.addMenuItem(checkMenuItem);
            return this;
        }

        /**
         * Can be used repeatedly to build your checkMenuItems into FXTrayIcon.
         * The items will appear in the order they are stated in your build sentence.<BR><BR>
         * See the getCheckMenuItem and getCheckMenuItems methods for accessing CheckMenuItems
         *
         * @param checkMenuItem a javafx.scene.control.CheckMenuItem object
         * @return this Builder object
         */
        @API
        public Builder checkMenuItem(javafx.scene.control.CheckMenuItem checkMenuItem) {
            BuildOrderUtil.addMenuItem(checkMenuItem);
            return this;
        }

        /**
         * Can be used to add more than one CheckMenuItem by separating them by commas.
         * The items will appear in the order they are stated in your build sentence.<BR><BR>
         * See the getCheckMenuItem and getCheckMenuItems methods for accessing CheckMenuItems
         *
         * @param checkMenuItems a javafx.scene.control.CheckMenuItem List
         * @return this Builder object
         */
        @API
        public Builder checkMenuItems(javafx.scene.control.CheckMenuItem... checkMenuItems) {
            BuildOrderUtil.addMenuItems(checkMenuItems);
            return this;
        }

        /**
         * Can be used to add a sub menu to FXTrayIcon, by passing in the Sub Menu
         * label, then by passing in either individual MenuItems separated by commas
         * or an entire MenuItem[] array.
         *
         * @param label     String for Menu label
         * @param menuItems either a {@code javafx.scene.control.MenuItem[]} array or individual {@code javafx.scene.control.MenuItem} separated by commas
         * @return this Builder object
         */
        @API
        public Builder menu(String label, javafx.scene.control.MenuItem... menuItems) {
            Menu menu = new Menu(label);
            menu.getItems().addAll(menuItems);
            BuildOrderUtil.addMenu(menu);
            return this;
        }

        /**
         * Can be used to add a sub menu to FXTrayIcon by first building a
         * javafx.scene.control.Menu object populated with MenuItems then passing
         * that Menu object into this method.
         *
         * @param menu a {@code javafx.scene.control.Menu} object
         * @return this Builder object
         */
        @API
        public Builder menu(Menu menu) {
            BuildOrderUtil.addMenu(menu);
            return this;
        }

        /**
         * Add separators that visually divides your menuItems into groups.
         * you can stack this along with menu items through your build sentence, and they will be inserted in the order you place them.
         *
         * @return this Builder object
         */
        @API
        public Builder separator() {
            BuildOrderUtil.addSeparator();
            return this;
        }

        /**
         * Set a ToolTip String which pops up for the user when they hover their mouse on FXTrayIcon
         *
         * @param tooltip Your tooltip String
         * @return this Builder object
         */
        @API
        public Builder toolTip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Adds a MenuItem at the top of the menu, with its label set to
         * the {@code FXTrayIcon},that will show the main JavaFX stage when
         * clicked. If this is not set to {@code true}, a developer will
         * have to implement this functionality themselves, if desired.
         *
         * @param addTitleMenuItem true or false
         * @return this Builder object
         */
        @API
        public Builder addTitleItem(boolean addTitleMenuItem) {
            this.addTitleMenuItem = addTitleMenuItem;
            return this;
        }

        /**
         * Creates a menuItem at the top of the menu with the label set to
         * the String in this argument. The menuItem it creates will show
         * the primaryStage.
         *
         * @param appTitle your chosen application title.
         * @return this Builder object
         */
        @API
        public Builder applicationTitle(String appTitle) {
            this.appTitle = appTitle;
            this.addTitleMenuItem = true;
            return this;
        }

        /**
         * Creates a menuItem with 'Exit Application' on
         * the label and puts it at the bottom of the menu.
         * When engaged, it closes the application.
         *
         * @return this Builder object
         */
        @API
        public Builder addExitMenuItem() {
            this.addExitMenuItem = true;
            return this;
        }

        /**
         * Creates a menuItem that always remains as the last menuItem in the tray menu
         * with option for a custom label. When engaged, it closes the application.
         *
         * @param label a {@code String} of desired label for the exit menuItem
         * @return this Builder object
         */
        @API
        public Builder addExitMenuItem(String label) {
            this.exitMenuItemLabel = label;
            this.addExitMenuItem = true;
            return this;
        }

        /**
         * Creates a menuItem that always remains as the last menuItem in the tray menu.
         * You can optionally add your own event action that will execute when the menuItem is engaged.
         *
         * @param label a {@code String} of desired label for the exit menuItem
         * @param event a {@code java.awt.ActionListener} object. Can be built with lambda ex: {@code e-> {}}
         * @return this Builder object
         */
        @API
        public Builder addExitMenuItem(String label, ActionListener event) {
            this.exitMenuItemLabel = label;
            this.addExitMenuItem = true;
            this.exitMenuItemActionListener = event;
            return this;
        }

        /**
         * Adds an EventHandler that is called when the FXTrayIcon's
         * action is called. On Microsoft's Windows 10 and 11, this is invoked
         * by a single-click of the primary mouse button. On Apple's MacOS,
         * this is invoked by a two-finger click on the TrayIcon, while
         * a single click will invoke the context menu.
         *
         * @param event The action to be performed.
         * @return this Builder object
         */
        @API
        public Builder onAction(EventHandler<ActionEvent> event) {
            this.event = event;
            return this;
        }

        /**
         * This is used to tell the show method to not add the event listener which
         * normally shows the stage when the icon is clicked. On MacOS, this is
         * normally activated with a right click. In order to be able to add a
         * right click mouse event to the trayIcon object on MacOS, noDefaultAction
         * needs to be executed.
         *
         * @return this Builder object
         */
        public Builder noDefaultAction() {
            noDefaultAction = true;
            return this;
        }

        /**
         * Sets up Builder so that once FXTrayIcon is instantiated, it will show immediately.
         *
         * @return this Builder object
         */
        @API
        public Builder show() {
            this.showTrayIcon = true;
            return this;
        }

        /**
         * Must be the LAST build statement in your Builder sentence.
         *
         * @return a new instance of FXTrayIcon.
         */
        @API
        public FXTrayIcon build() {
            loadIcon();
            checkAnimation();
            FXTrayIcon fxTrayIcon = new FXTrayIcon(this);
            if (imageList != null) {
                fxTrayIcon.animation = new Animation(fxTrayIcon, imageList, frameRateMS);
            }
            return fxTrayIcon;
        }

        private void loadIcon() {
            switch (cio) {
                case URL: {
                    icon = loadImageFromURL(conImageURL, iconScale.width(), iconScale.height());
                    break;
                }
                case FILE: {
                    icon = loadImageFromFile(conImageFile, iconScale.width(), iconScale.height());
                    break;
                }
                case IMAGE: {
                    icon = loadImageFromAWT(conImage, iconScale.width(), iconScale.height());
                    break;
                }
                case FX_IMAGE: {
                    icon = loadImageFromFX(conFXImage, iconScale.width(), iconScale.height());
                    break;
                }
                case DEFAULT: {
                    icon = loadDefaultIconImage();
                    break;
                }
                default:
            }
        }

        private void checkAnimation() {
            if (ImageList != null) {
                for (javafx.scene.image.Image fxImage : ImageList) {
                    this.imageList.addLast(loadImageFromFX(fxImage, iconScale.width(), iconScale.height()));
                }
            }
            else if (this.ImageFileList != null) {
                for (File file : ImageFileList) {
                    imageList.addLast(loadImageFromFile(file, iconScale.width(), iconScale.height()));
                }
            }

        }
    }

    /**
     * protected constructor called by the Builder class to finalize instantiation
     *
     * @param build Builder class instance
     */
    @API
    protected FXTrayIcon(Builder build) {
        this(build.parentStage, build.icon, true);
        this.icon = build.icon;
        this.parentStage = build.parentStage;
        this.appTitle = build.appTitle;
        this.addExitMenuItem = build.addExitMenuItem;
        this.addTitleMenuItem = build.addTitleMenuItem;
        this.exitMenuItemLabel = build.exitMenuItemLabel;
        this.exitMenuItemActionListener = build.exitMenuItemActionListener;
        this.noDefaultAction = build.noDefaultAction;
        if (!build.tooltip.equals("")) setTooltip(build.tooltip);
        if (build.event != null) setOnAction(build.event);
        for (int i = 0; i < BuildOrderUtil.getItemCount(); i++) {
            switch (BuildOrderUtil.getItemType(i)) {
                case MENU: {
                    addMenu(BuildOrderUtil.getMenu(i));
                    break;
                }
                case MENU_ITEM: {
                    addMenuItem(BuildOrderUtil.getMenuItem(i));
                    break;
                }
                case SEPARATOR: {
                    addSeparator();
                    break;
                }
                default:
            }
        }
        if (build.showTrayIcon) show();
    }

    /**
     * Gets the nested AWT {@link TrayIcon}. This is intended for extended
     * instances of FXTrayIcon which require the access to implement
     * custom features.
     *
     * @return The nest trayIcon within this instance of FXTrayIcon.
     */
    @API
    protected final TrayIcon getTrayIcon() {
        return trayIcon;
    }

    private void ensureSystemTraySupported() {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException(
                    "SystemTray icons are not "
                    + "supported by the current desktop environment.");
        }
    }

    private void attemptSetSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException
               | InstantiationException
               | IllegalAccessException
               | UnsupportedLookAndFeelException ignored) {
            //This is empty on purpose
        }
    }

    private static Image loadImageFromURL(URL iconImagePath) {
        return loadImageFromURL(iconImagePath, coreSize.width(), coreSize.height());
    }

    private static Image loadImageFromURL(URL iconImagePath, int iconWidth, int iconHeight) {
        try {
            return ImageIO.read(iconImagePath)
                    .getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to read the Image at the provided path: " + iconImagePath, e);
        }
    }

    private static Image loadImageFromFile(File file) {
        return loadImageFromFile(file, coreSize.width(), coreSize.height());
    }

    private static Image loadImageFromFile(File file, int iconWidth, int iconHeight) {
        try (InputStream is = new FileInputStream(file)) {
            return ImageIO.read(is)
                    .getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
        }
        catch (FileNotFoundException e) {
            throw new IllegalStateException("Unable to load the Image at the provided path (File not found): " + file.getAbsolutePath(), e);
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to read the Image at the provided path (perhaps not an image file, or it is corrupt): " + file.getAbsolutePath(), e);
        }
    }

    private static Image loadImageFromFX(javafx.scene.image.Image javaFXImage) {
        return loadImageFromFX(javaFXImage, coreSize.width(), coreSize.height());
    }

    private static Image loadImageFromFX(javafx.scene.image.Image javaFXImage, int iconWidth, int iconHeight) {
        return SwingFXUtils.fromFXImage(javaFXImage, null).getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
    }

    private static Image loadImageFromAWT(Image image) {
        return image.getScaledInstance(coreSize.width(), coreSize.height(), Image.SCALE_SMOOTH);
    }

    private static Image loadImageFromAWT(Image image, int iconWidth, int iconHeight) {
        return image.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
    }

    private static Image loadDefaultIconImage() {
        URL defaultIconImagePath = FXTrayIcon.class.getResource("FXIconRedWhite.png");
        return loadImageFromURL(defaultIconImagePath);
    }

    private static boolean isWin() {
        return System.getProperty("os.name")
                .toLowerCase(Locale.ENGLISH)
                .contains("windows");
    }

    private boolean isMac() {
        return System.getProperty("os.name")
                .toLowerCase(Locale.ENGLISH)
                .contains("mac");
    }

    /**
     * Adds the FXTrayIcon to the system tray.
     * This will add the TrayIcon with the image initialized in the
     * {@code FXTrayIcon}'s constructor. By default, an empty popup
     * menu is shown.
     * By default, {@code javafx.application.Platform.setImplicitExit(false)}
     * will be called. This will allow the application to continue running
     * and show the tray icon after no more JavaFX Stages are visible. If
     * this is not the behavior that you intend, call {@code setImplicitExit}
     * to true after calling {@code show()}.
     */
    @API
    public void show() {
        SwingUtilities.invokeLater(() -> {
            try {
                tray.add(this.trayIcon);

                // Add a MenuItem with the main Stage's title, this will
                // show the main JavaFX stage when clicked.
                if (addTitleMenuItem) {
                    String miTitle = (this.appTitle != null) ?
                            this.appTitle
                            : (parentStage != null && parentStage.getTitle() != null
                               && !parentStage.getTitle().isEmpty()) ?
                            parentStage.getTitle() : "Show Application";

                    MenuItem miStage = new MenuItem(miTitle);
                    miStage.setFont(Font.decode(null).deriveFont(Font.BOLD));
                    miStage.addActionListener(e -> Platform.runLater(() -> {
                        if (parentStage != null) {
                            if (!parentStage.isIconified()) {
                                parentStage.show();
                            }
                        }
                    }));
                    //Make sure it's always at the top
                    this.popupMenu.insert(miStage, 0);
                }

                if (addExitMenuItem) {
                    EventQueue.invokeLater(() -> {
                        String label = exitMenuItemLabel.equals("") ? "Exit Application" : exitMenuItemLabel;
                        MenuItem miExit = new MenuItem(label);

                        // If Platform.setImplicitExit(false) then the JVM will
                        // continue to run after no more Stages remain,
                        // thus we provide a way to terminate it by default.
                        ActionListener defaultActionListener;
                        if (isMac()) {
                            defaultActionListener = e -> {
                                this.tray.remove(this.trayIcon);
                                Platform.setImplicitExit(true);
                                Platform.exit();
                                System.exit(0);
                            };
                        }
                        else {
                            defaultActionListener = e -> {
                                this.tray.remove(this.trayIcon);
                                Platform.setImplicitExit(true);
                                Platform.exit();
                            };
                        }
                        miExit.addActionListener((this.exitMenuItemActionListener == null) ? defaultActionListener : this.exitMenuItemActionListener);
                        popupMenu.add(miExit);
                    });
                }

                // Show parent stage when user clicks the icon
                if (!noDefaultAction)
                    this.trayIcon.addActionListener(stageShowListener);
                shown = true;
            }
            catch (AWTException e) {
                throw new IllegalStateException("Unable to add TrayIcon", e);
            }
        });
    }

    /**
     * This is used to tell the show method to not add the event listener which
     * normally shows the stage when the icon is clicked. On MacOS, this is
     * normally activated with a right click. In order to be able to add a
     * right click mouse event to the trayIcon object on MacOS, noDefaultAction
     * needs to be executed.
     */
    public void noDefaultAction() {
        if (!shown)
            noDefaultAction = true;
        else
            trayIcon.removeActionListener(stageShowListener);
    }


    /**
     * Adds an EventHandler that is called when the FXTrayIcon's
     * action is called. On Microsoft's Windows 10, this is invoked
     * by a single-click of the primary mouse button. On Apple's MacOS,
     * this is invoked by a two-finger click on the TrayIcon, while
     * a single click will invoke the context menu.
     *
     * @param e The action to be performed.
     */
    @API
    public void setOnAction(EventHandler<ActionEvent> e) {
        if (this.trayIcon.getMouseListeners().length >= 1) {
            this.trayIcon.removeMouseListener(
                    this.trayIcon.getMouseListeners()[0]);
        }
        this.trayIcon.addMouseListener(getPrimaryClickListener(e));
    }

    /**
     * Adds a MenuItem to the {@code FXTrayIcon} that will close the
     * JavaFX application and terminate the JVM. If this is not set
     * to {@code true}, a developer will have to implement this functionality
     * themselves.
     * This must be called before fxTrayIcon.show() is called.
     *
     * @param addExitMenuItem If true, the FXTrayIcon's popup menu will display
     *                        an option for exiting the application entirely.
     */
    @API
    public void addExitItem(boolean addExitMenuItem) {
        this.addExitMenuItem = addExitMenuItem;
    }

    /**
     * Adds a menuItem to the {@code FXTrayIcon} that always remains as
     * the last menuItem in the tray menu, with option for a custom label.
     * When engaged, it closes the application.
     * This must be called before fxTrayIcon.show() is called.
     *
     * @param label a {@code String} of desired label for the exit menuItem
     */
    @API
    public void addExitItem(String label) {
        this.addExitMenuItem = true;
        this.exitMenuItemLabel = label;
    }

    /**
     * Adds a menuItem to the {@code FXTrayIcon} that always remains as
     * the last menuItem in the tray menu. You can optionally add your own
     * event action that will execute when the menuItem is engaged.
     * This must be called before fxTrayIcon.show() is called.
     *
     * @param label a {@code String} of desired label for the exit menuItem
     * @param event a {@code java.awt.ActionListener} object. Can be built with lambda ex: {@code e-> {}}
     */
    @API
    public void addExitItem(String label, ActionListener event) {
        this.addExitMenuItem = true;
        this.exitMenuItemLabel = label;
        this.exitMenuItemActionListener = event;
    }

    /**
     * Adds a MenuItem with the main Stage's title to the {@code FXTrayIcon},
     * that will show the main JavaFX stage when clicked. If this is not set
     * to {@code true}, a developer will have to implement this functionality
     * themselves.
     * This must be called before fxTrayIcon.show() is called.
     *
     * @param addTitleMenuItem If true, the FXTrayIcon's popup menu will display
     *                         the main stages title and will show the stage on click
     */
    @API
    public void addTitleItem(boolean addTitleMenuItem) {
        this.addTitleMenuItem = addTitleMenuItem;
    }

    /**
     * Removes the MenuItem at the given index
     *
     * @param index Index of the MenuItem to remove
     */
    @API
    public void removeMenuItem(int index) {
        EventQueue.invokeLater(() -> this.popupMenu.remove(index));
    }

    /**
     * Removes the specified item from the FXTrayIcon's menu. Does nothing
     * if the item is not in the menu.
     *
     * @param fxMenuItem The JavaFX MenuItem to remove from the menu.
     */
    @API
    public void removeMenuItem(javafx.scene.control.MenuItem fxMenuItem) {
        EventQueue.invokeLater(() -> {
            MenuItem toBeRemoved = null;
            for (int i = 0; i < this.popupMenu.getItemCount(); i++) {
                MenuItem awtItem = this.popupMenu.getItem(i);
                if (awtItem.getLabel().equals(fxMenuItem.getText()) ||
                    awtItem.getName().equals(fxMenuItem.getText())) {
                    toBeRemoved = awtItem;
                }
            }
            if (toBeRemoved != null) {
                this.popupMenu.remove(toBeRemoved);
            }
        });
    }

    /**
     * Adds a separator line to the Menu at the current position.
     */
    @API
    public void addSeparator() {
        EventQueue.invokeLater(this.popupMenu::addSeparator);
    }

    /**
     * Adds a separator line to the Menu at the given position.
     *
     * @param index The position at which to add the separator
     */
    @API
    public void insertSeparator(int index) {
        EventQueue.invokeLater(() -> this.popupMenu.insertSeparator(index));
    }

    /**
     * Adds the specified MenuItem to the FXTrayIcon's menu
     *
     * @param menuItem MenuItem to be added
     */
    @API
    public void addMenuItem(javafx.scene.control.MenuItem menuItem) {
        EventQueue.invokeLater(() -> addMenuItemPrivately(menuItem));
    }


    /**
     * Adds the ability to add a MenuItem after instantiation without needing to
     * pass in a MenuItem object.
     *
     * @param label        - the text on the MenuItem
     * @param eventHandler - the EventHandler that the MenuItem executes
     */
    @API
    public void addMenuItem(String label, EventHandler<ActionEvent> eventHandler) {
        javafx.scene.control.MenuItem menuItem = new javafx.scene.control.MenuItem(label);
        menuItem.setOnAction(eventHandler);
        addMenuItemPrivately(menuItem);
    }

    /**
     * Adds the specified MenuItems to FXTrayIcon's menu.
     * Pass in as many MenuItems as needed, separated by a comma.
     * ex: addMenuItems(menuItem1, menuItem2, menuItem3);
     *
     * @param menuItems multiple comma separated MenuItem objects
     */
    @API
    public void addMenuItems(javafx.scene.control.MenuItem... menuItems) {
        EventQueue.invokeLater(() -> {
            for (javafx.scene.control.MenuItem menuItem : menuItems) {
                addMenuItemPrivately(menuItem);
            }
        });
    }

    private void addMenuItemPrivately(javafx.scene.control.MenuItem menuItem) {
        if (menuItem instanceof Menu) {
            addMenu((Menu) menuItem);
            return;
        }
        if (isNotUnique(menuItem)) {
            throw new UnsupportedOperationException(
                    "Menu Item labels must be unique.");
        }
        if (addExitMenuItem && shown) {
            int index = this.popupMenu.getItemCount();
            this.popupMenu.insert(AWTUtils.convertFromJavaFX(menuItem), index);
        }
        else {
            this.popupMenu.add(AWTUtils.convertFromJavaFX(menuItem));
        }
    }

    /**
     * Inserts the specified MenuItem into the FXTrayIcon's menu
     * at the supplied index.
     *
     * @param menuItem MenuItem to be inserted
     * @param index    Index to insert the MenuItem at
     */
    @API
    public void insertMenuItem(javafx.scene.control.MenuItem menuItem, int index) {
        EventQueue.invokeLater(() -> {
            if (isNotUnique(menuItem)) {
                throw new UnsupportedOperationException(
                        "Menu Item labels must be unique.");
            }
            this.popupMenu.insert(AWTUtils.convertFromJavaFX(menuItem), index);
        });
    }

    /**
     * Returns the MenuItem at the given index. The MenuItem
     * returned is the AWT MenuItem, and not the JavaFX MenuItem,
     * thus this should only be called when extending the functionality
     * of the AWT MenuItem.
     * <p>
     * NOTE: This should be called via the
     * {@code EventQueue.invokeLater()} method as well as any
     * subsequent operations on the MenuItem that is returned.
     *
     * @param index Index of the MenuItem to be returned.
     * @return The MenuItem at {@code index}
     */
    @API
    public MenuItem getMenuItem(int index) {
        return this.popupMenu.getItem(index);
    }

    /**
     * Sets the FXTrayIcon's tooltip that is displayed on mouse hover.
     *
     * @param tooltip The text of the tooltip
     */
    @API
    public void setTrayIconTooltip(String tooltip) {
        EventQueue.invokeLater(() -> this.trayIcon.setToolTip(tooltip));
    }

    /**
     * Sets the application's title. This is used in the FXTrayIcon
     * where appropriate.
     *
     * @param title The application's title, to be used for
     *              the tooltip text for FXTrayIcon
     */
    @API
    public void setApplicationTitle(String title) {
        this.appTitle = title;
    }

    /**
     * Removes the {@code FXTrayIcon} from the system tray.
     * Also calls {@code Platform.setImplicitExit(true)}, thereby
     * allowing the JVM to terminate after the last JavaFX {@code Stage}
     * is hidden.
     */
    @API
    public void hide() {
        EventQueue.invokeLater(() -> {
            tray.remove(trayIcon);
            Platform.setImplicitExit(true);
        });
    }

    /**
     * Returns true if the tray icon's PopupMenu is visible.
     *
     * @return true if the PopupMenu is visible.
     */
    @API
    public boolean isMenuShowing() {
        for (Iterator<TrayIcon> it =
             Arrays.stream(tray.getTrayIcons()).iterator(); it.hasNext(); ) {
            TrayIcon ti = it.next();
            if (ti.equals(trayIcon)) {
                return ti.getPopupMenu().isEnabled();
            }
        }
        return false;
    }

    /**
     * Returns true if the FXTrayIcon's show() method has been called.
     *
     * @return true if the FXTrayIcon is a part of the SystemTray.
     */
    @API
    public boolean isShowing() {
        return Arrays.stream(
                        tray.getTrayIcons())
                .collect(Collectors.toList()).contains(trayIcon);
    }

    /**
     * Displays an info popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     *
     * @param title   The caption (header) text
     * @param message The message content text
     */
    @API
    public void showInfoMessage(String title, String message) {
        if (isMac()) {
            showMacAlert(title, message, "Information");
        }
        else {
            EventQueue.invokeLater(() ->
                    this.trayIcon.displayMessage(
                            title, message, TrayIcon.MessageType.INFO));
        }
    }

    /**
     * Displays an info popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     *
     * @param message The message content text
     */
    @API
    public void showInfoMessage(String message) {
        this.showInfoMessage(null, message);
    }

    /**
     * Displays a warning popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     *
     * @param title   The caption (header) text
     * @param message The message content text
     */
    @API
    public void showWarningMessage(String title, String message) {
        if (isMac()) {
            showMacAlert(title, message, "Warning");
        }
        else {
            EventQueue.invokeLater(() ->
                    this.trayIcon.displayMessage(
                            title, message, TrayIcon.MessageType.WARNING));
        }
    }

    /**
     * Displays a warning popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     *
     * @param message The message content text
     */
    @API
    public void showWarningMessage(String message) {
        this.showWarningMessage(null, message);
    }

    /**
     * Displays an error popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     *
     * @param title   The caption (header) text
     * @param message The message content text
     */
    @API
    public void showErrorMessage(String title, String message) {
        if (isMac()) {
            showMacAlert(title, message, "Error");
        }
        else {
            EventQueue.invokeLater(() ->
                    this.trayIcon.displayMessage(
                            title, message, TrayIcon.MessageType.ERROR));
        }
    }

    /**
     * Displays an error popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     *
     * @param message The message content text
     */
    @API
    public void showErrorMessage(String message) {
        this.showErrorMessage(null, message);
    }

    /**
     * Displays a popup message near the tray icon.
     * Some systems will display FXTrayIcon's image on this popup.
     * <p>NOTE: Some systems do not support this.</p>
     *
     * @param title   The caption (header) text
     * @param message The message content text
     */
    @API
    public void showMessage(String title, String message) {
        if (isMac()) {
            showMacAlert(title, message, "Message");
        }
        else {
            EventQueue.invokeLater(() ->
                    this.trayIcon.displayMessage(
                            title, message, TrayIcon.MessageType.NONE));
        }
    }

    /**
     * Displays a popup message near the tray icon.
     * Some systems will display FXTrayIcon's image on this popup.
     * <p>NOTE: Some systems do not support this.</p>
     *
     * @param message The message content text
     */
    @API
    public void showMessage(String message) {
        this.showMessage(null, message);
    }

    /**
     * Clears the popupMenu so that it can be rebuilt easily if needed.
     */
    @API
    public void clear() {
        EventQueue.invokeLater(this.popupMenu::removeAll);
    }

    /**
     * Checks whether the system tray icon is supported on the
     * current platform, or not.
     * Just because the system tray is supported, does not mean that the
     * current platform implements all system tray functionality.
     * This will always return true on Windows or MacOS. Check the
     * specific desktop environment for AppIndicator support when
     * calling this on *nix platforms.
     *
     * @return false if the system tray is not supported, true if any
     * part of the system tray functionality is supported.
     */
    @API
    public static boolean isSupported() {
        return Desktop.isDesktopSupported() && SystemTray.isSupported();
    }

    /**
     * Provides the number of menuItems in the popupMenu.
     *
     * @return int getItemCount()
     */
    @API
    public int getMenuItemCount() {
        return this.popupMenu.getItemCount();
    }

    /**
     * Returns a List of java.awt.CheckboxMenuItems, because when you
     * add a JavaFX CheckMenuItem into FXTrayIcon, it gets converted
     * into an AWT object, and if you need to deal with the checked
     * property of those menuItems, you can access them through this list,
     * OR by using the getCheckMenuItem method, using the Label property to
     * specify which menuItem you need to access.
     *
     * @return List <span>&lt;</span>java.awt.CheckboxMenuItem<span>&#62;</span>
     */
    public List<CheckboxMenuItem> getCheckMenuItems() {
        List<CheckboxMenuItem> list = new ArrayList<>();
        for (int x = 0; x < popupMenu.getItemCount(); x++) {
            if (popupMenu.getItem(x) instanceof CheckboxMenuItem) {
                list.add((CheckboxMenuItem) popupMenu.getItem(x));
            }
        }
        list.sort(Comparator.comparing(CheckboxMenuItem::getLabel));
        return list;
    }

    /**
     * Gain access to a CheckMenuItem after FXTrayIcon is instantiated by calling this
     * method and passing the label of the menuItem.
     *
     * @param label - String
     * @return CheckboxMenuItem - your CheckMenuItem after being converted for FXTrayIcon
     */
    public CheckboxMenuItem getCheckMenuItem(String label) {
        for (int x = 0; x < popupMenu.getItemCount(); x++) {
            if (popupMenu.getItem(x) instanceof java.awt.CheckboxMenuItem) {
                if (popupMenu.getItem(x).getLabel().equals(label))
                    return (CheckboxMenuItem) popupMenu.getItem(x);
            }
        }
        return null;
    }

    /**
     * Provides a way to change the TrayIcon image at runtime.
     * by passing in a JavaFX Image object. The image will be
     * scaled to the correct size for the OS.
     *
     * @param javaFXImage javafx.scene.image.Image object
     */
    @API
    public void setGraphic(javafx.scene.image.Image javaFXImage) {
        setFinalGraphic(loadImageFromFX(javaFXImage));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime.
     * by passing in a JavaFX Image object with the option to
     * set the image width and height. If the image is not
     * sized correctly, it can render as a garbled image.
     *
     * @param javaFXImage javafx.scene.image.Image object
     * @param iconWidth   int object
     * @param iconHeight  int object
     */
    @API
    public void setGraphic(javafx.scene.image.Image javaFXImage, int iconWidth, int iconHeight) {
        setFinalGraphic(loadImageFromFX(javaFXImage, iconWidth, iconHeight));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime.
     * by passing in a Java IO File object. The image will be
     * scaled to the correct size for the OS.
     *
     * @param file a java.io.File object
     * @deprecated use setGraphic(File, FXTrayScale)
     */
    @API
    public void setGraphic(File file) {
        setFinalGraphic(loadImageFromFile(file));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime
     * with optional dimensions provided. Dimensions must
     * be correct for the required OS, or it might display as
     * a garbled image.
     *
     * @param file       a java.io.File object
     * @param iconWidth  an int, the width of the icon
     * @param iconHeight an int, the height of the icon
     */
    @API
    public void setGraphic(File file, int iconWidth, int iconHeight) {
        setFinalGraphic(loadImageFromFile(file, iconWidth, iconHeight));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime
     * with optional dimensions provided. Dimensions must
     * be correct for the required OS, or it might display as
     * a garbled image.
     *
     * @param file   a java.io.File object
     * @param iconWH an int, the width and height as one value
     */
    @API
    public void setGraphic(File file, int iconWH) {
        setFinalGraphic(loadImageFromFile(file, iconWH, iconWH));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime
     * by specifying the image URL. The image will be scaled
     * to the correct size for the OS.
     *
     * @param imageURL a java.net.URL object
     */
    @API
    public void setGraphic(URL imageURL) {
        setFinalGraphic(loadImageFromURL(imageURL));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime
     * by specifying the image file via a URL object with the
     * option specifying the dimension of the image. If the image
     * is not dimensioned correctly, it can render as a garbled
     * image.
     *
     * @param imageURL   a java.net.URL object
     * @param iconWidth  an int, the width of the icon
     * @param iconHeight an int, the height of the icon
     */
    @API
    public void setGraphic(URL imageURL, int iconWidth, int iconHeight) {
        setFinalGraphic(loadImageFromURL(imageURL, iconWidth, iconHeight));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime
     * by passing in a Java AWT image object. Dimensions must
     * be correct for the required OS, or it might display as
     * a garbled image.
     *
     * @param image a java.awt.Image object
     */
    @API
    public void setGraphic(Image image) {
        setFinalGraphic(loadImageFromAWT(image));
    }

    /**
     * Sets the TrayIcon's graphic
     *
     * @param image      Image to set
     * @param iconWidth  Icon's width
     * @param iconHeight Icon's height
     */
    @API
    public void setGraphic(Image image, int iconWidth, int iconHeight) {
        setFinalGraphic(loadImageFromAWT(image, iconWidth, iconHeight));
    }

    private void setFinalGraphic(Image img) {
        this.icon = img;
        this.trayIcon.setImage(img);
    }

    /**
     * Sets the FXTrayIcon's tooltip text (shown on mouse hover)
     *
     * @param tooltip String for tooltip
     */
    @API
    public void setTooltip(String tooltip) {
        this.trayIcon.setToolTip(tooltip);
    }

    /**
     * Displays a sliding info message. Behavior is similar to Windows, but without AWT
     *
     * @param subTitle The message caption
     * @param message  The message text
     * @param title    The message title
     */
    private void showMacAlert(String subTitle, String message, String title) {
        String execute = String.format(
                "display notification \"%s\""
                + " with title \"%s\""
                + " subtitle \"%s\"",
                message != null ? message : "",
                title != null ? title : "",
                subTitle != null ? subTitle : ""
        );
        try {
            Runtime.getRuntime()
                    .exec(new String[]{"osascript", "-e", execute});
        }
        catch (IOException e) {
            throw new UnsupportedOperationException(
                    "Cannot run osascript with given parameters.");
        }
    }

    /**
     * Adds a JavaFX Menu to the TrayIcon's PopupMenu
     *
     * @param menu A JavaFX Menu
     */
    private void addMenu(Menu menu) {
        EventQueue.invokeLater(() -> {
            java.awt.Menu awtMenu = new java.awt.Menu(menu.getText());
            menu.getItems().forEach(subItem ->
                    awtMenu.add(AWTUtils.convertFromJavaFX(subItem)));
            if (addExitMenuItem && shown) {
                int index = this.popupMenu.getItemCount();
                this.popupMenu.insert(awtMenu, index);
            }
            else {
                this.popupMenu.add(awtMenu);
            }
        });
    }

    /**
     * Check if a JavaFX menu item's text is unique among those
     * previously added to the AWT PopupMenu
     *
     * @param fxItem A JavaFX MenuItem
     * @return true if the item's text is unique among previously
     * added items.
     */
    private boolean isNotUnique(javafx.scene.control.MenuItem fxItem) {
        boolean result = true;
        for (int i = 0; i < popupMenu.getItemCount(); i++) {
            if (popupMenu.getItem(i).getLabel().equals(fxItem.getText())) {
                result = false;
                break;
            }
        }
        return !result;
    }

    /**
     * An {@code ActionListener} that when called
     * will show the parent JavaFX stage if it is defined.
     */
    private final ActionListener stageShowListener = e -> {
        if (this.parentStage != null) {
            Platform.runLater(this.parentStage::show);
        }
    };


    /**
     * Use this method to add an animation to FXTrayIcon post instantiation, or use it to replace
     * any current animation with a different one. You create animations by either passing into the
     * method a LinkedList of JavaFX Image objects, or just a LinkedList of java.io.File objects
     * where each file would be one frame of your animation. Next, specify the size of the icon and
     * since the Width and Height need to be the same, you only pass that in as a single value.
     * Finally, put the frame rate, which is the amount of time that will lapse between the showing
     * of the frames in succession. Frame rates are usually around a hundred milliseconds our in
     * that general range. Lower than 75 might be pushing it where you might experience dropped
     * frames.
     * <p>
     * A quick not on icon sizing. As of the implementation of animations, FXTrayIcon will now use
     * a default icon size based on your operating system. You can ocerride those values by using
     * the setIconSize() method.
     *
     * @param imageList   - LinkedList of JavaFX Image objects.
     * @param frameRateMS - The framerate of the animation
     */
    @API
    public void newAnimation(LinkedList<javafx.scene.image.Image> imageList, int frameRateMS) {
        LinkedList<Image> list = new LinkedList<>();
        for (javafx.scene.image.Image fxImage : imageList) {
            list.addLast(loadImageFromFX(fxImage, iconScale.width(), iconScale.height()));
        }
        animation = new Animation(this, list, frameRateMS);
    }

    /**
     * Use this method to add an animation to FXTrayIcon post instantiation, or use it to replace
     * any current animation with a different one. You create animations by either passing into the
     * method a LinkedList of JavaFX Image objects, or just a LinkedList of java.io.File objects
     * where each file would be one frame of your animation. Next, specify the size of the icon and
     * since the Width and Height need to be the same, you only pass that in as a single value.
     * Finally, put the frame rate, which is the amount of time that will lapse between the showing
     * of the frames in succession. Frame rates are usually around a hundred milliseconds our in
     * that general range. Lower than 75 might be pushing it where you might experience dropped
     * frames.
     *
     * @param imageFileList - LinkedList of java.nio.File objects each file containing one frame.
     * @param frameRateMS   - The framerate of the animation
     * @param sortList      - Set this to true if you want your file list sorted by filename before the images are created
     */
    @API
    public void newAnimation(LinkedList<File> imageFileList, int frameRateMS, boolean sortList) {
        if (sortList)
            imageFileList.sort(Comparator.comparing(File::getName));
        LinkedList<Image> imageList = new LinkedList<>();
        for (File file : imageFileList) {
            imageList.addLast(loadImageFromFile(file, iconScale.width(), iconScale.height()));
        }
        animation = new Animation(this, imageList, frameRateMS);
    }

    /**
     * Starts the animated icon if you generated one either with the newAnimation() method ir in the Builder sentence..
     */
    @API
    public void play() {
        if (animation != null) {
            animation.play();
        }
    }

    /**
     * Stops the animated icon if it is running.
     */
    @API
    public void stop() {
        if (animation != null && isRunning()) {
                animation.stop();
        }
    }

    /**
     * Stops the animated icon if it is running and resets the icon to the default that you used when starting the
     * library or if you changed it with the setGraphic() method.
     */
    @API
    public void stopReset() {
        if (animation != null) {
            stop();
            setGraphic(icon);
        }
    }

    /**
     * resets the icon to the default that you used when starting the library or if you changed it with the setGraphic() method.
     */
    @API
    public void resetIcon() {
        if (animation != null) {
            if (!isRunning())
                setGraphic(icon);
        }
    }

    /**
     * This is how theAnimation package private class animates the icon,
     * and it needs to be protected because no one else should be using this
     * means of changing icons because without this dedicated means
     * of animating the icon, there were conflicts trying to set the default
     * icon where those settings were overridden by the animator because
     * they were using the same methods and overriding the icon object
     * which is the designated default icon.
     *
     * @param frame the specific frame od the animation.
     */
    @API
    protected void setAnimationFrame(Image frame) {
        this.trayIcon.setImage(frame);
    }

    /**
     * Pauses the animated icon.
     */
    @API
    public void pause() {
        if (animation != null) {
            animation.pause();
        }
    }


    /**
     * Use this method to both pause and resume the animation as
     * it will pause if the animation is running, and it will
     * play the animation it if it is paused.
     */
    @API
    public void pauseResume() {
        if (animation != null && isRunning()) {
            pause();
        }
        else if (isPaused()) {
            play();
        }
    }

    /**
     * Starts the animated icon over from the first frame in the image list.
     */
    @API
    public void playFromStart() {
        if (animation != null) {
            animation.playFromStart();
        }
    }

    /**
     * Lets you check if the icon animation is currently running.
     *
     * @return boolean
     */
    @API
    public boolean isRunning() {
        return animation == null ? false : animation.isRunning();
    }

    /**
     * Lets you check if the icon animation is currently paused.
     *
     * @return boolean
     */
    @API
    public boolean isPaused() {
        return animation == null ? false : animation.isPaused();
    }

    /**
     * Lets you check if the icon animation is currently stopped.
     *
     * @return boolean
     */
    @API
    public boolean isStopped() {
        return animation == null ? false : animation.isStopped();
    }

    /**
     * This method gives you direct access to the animations timeline which you could use
     * to adjust more advanced settings as well as see metrics that might be relevant to you.
     */
    @API
    public Timeline getAnimationTimeline() {
        return animation.timeline();
    }


    /**
     * Starting with the implementation of animations, FXTrayIcon will use default icons sizes based on
     * which operating system you're running and this method lets you override those values.
     *
     * @param width  - icons width in pixels
     * @param height - icons height in pixels
     */
    @API
    public void setIconSize(int width, int height) {
        iconScale = new IconScale(width, height);
    }

    /**
     * Starting with the implementation of animations, FXTrayIcon will use default icons sizes based on
     * which operating system you're running and this method lets you override those values.
     *
     * @param sizeWH - int for the dimensions of the icon using a single value (W = H)
     */
    @API
    public void setIconSize(int sizeWH) {
        iconScale = new IconScale(sizeWH, sizeWH);
    }

}
