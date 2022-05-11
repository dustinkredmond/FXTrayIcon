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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *  Class for creating a JavaFX System Tray Icon.
 *  Allows for a developer to create a tray icon
 *  using JavaFX style API.
 */
public class FXTrayIcon {

    private static final Integer WinScale  = 16;
    private static final Integer CoreScale = 22;
    private boolean shown = false;
    private ActionListener exitMenuItemActionListener;


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
     * Creates a {@code MouseListener} whose
     * single-click action performs the passed
     * JavaFX EventHandler
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
            public void mousePressed(MouseEvent ignored) { }
            @Override
            public void mouseReleased(MouseEvent ignored) { }
            @Override
            public void mouseEntered(MouseEvent ignored) { }
            @Override
            public void mouseExited(MouseEvent ignored) { }
        };
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon and a provided{@code javafx.stage.Stage} as its parent.
     * @param parentStage The parent Stage of the tray icon.
     * @param iconImagePath A path to an icon image
     * @param iconWidth optional to set a different icon width
     * @param iconHeight optional to set a different icon height
     */
    @API
    public FXTrayIcon(Stage parentStage, URL iconImagePath, int iconWidth, int iconHeight) {
        this(parentStage, loadImageFromURL(iconImagePath, iconWidth, iconHeight),true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon and a provided{@code javafx.stage.Stage} as its parent.
     * @param parentStage The parent Stage of the tray icon.
     * @param iconImagePath A path to an icon image
     */
    @API
    public FXTrayIcon(Stage parentStage, URL iconImagePath) {
        this(parentStage, loadImageFromURL(iconImagePath),true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon File with dimensions and a provided{@code javafx.stage.Stage}
     * as its parent.
     * @param parentStage The parent Stage of the tray icon.
     * @param iconFile A java.io.File object
     * @param iconWidth an int, icon width
     * @param iconHeight an int, icon height
     */
    @API
    public FXTrayIcon(Stage parentStage, File iconFile, int iconWidth, int iconHeight) {
        this(parentStage, loadImageFromFile(iconFile, iconWidth, iconHeight),true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon File and a provided{@code javafx.stage.Stage} as its parent.
     * @param parentStage The parent Stage of the tray icon.
     * @param iconFile A java.io.File object
     */
    @API
    public FXTrayIcon(Stage parentStage, File iconFile) {
        this(parentStage, loadImageFromFile(iconFile),true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon Image with dimensions and a provided{@code javafx.stage.Stage}
     * as its parent.
     * @param parentStage The parent Stage of the tray icon.
     * @param javaFXImage A javafx.scene.image.Image object
     * @param iconWidth an int, icon width
     * @param iconHeight an int, icon height
     */
    @API
    public FXTrayIcon(Stage parentStage, javafx.scene.image.Image javaFXImage, int iconWidth, int iconHeight) {
        this(parentStage,loadImageFromFX(javaFXImage, iconWidth, iconHeight),true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon Image and a provided{@code javafx.stage.Stage} as its parent.
     * @param parentStage The parent Stage of the tray icon.
     * @param javaFXImage A javafx.scene.image.Image object
     */
    @API
    public FXTrayIcon(Stage parentStage, javafx.scene.image.Image javaFXImage) {
        this(parentStage,loadImageFromFX(javaFXImage),true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon and specified dimensions and a provided{@code javafx.stage.Stage} as its parent.
     * @param parentStage The parent Stage of the tray icon. Must not be null.
     * @param image a java.awt.Image object. Must not be null
     * @param iconWidth an int, icon Width
     * @param iconHeight an int, icon Height
     */
    @API
    public FXTrayIcon(Stage parentStage, Image image, int iconWidth, int iconHeight) {
        this(parentStage,loadImageFromAWT(image, iconWidth, iconHeight),true);
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon image and a provided{@code javafx.stage.Stage} as its parent.
     * @param parentStage The parent Stage of the tray icon. Must not be null.
     * @param image a java.awt.Image object. Must not be null
     */
    @API
    public FXTrayIcon(Stage parentStage, Image image) {
        this(parentStage, loadImageFromAWT(image), true);
    }

    private FXTrayIcon(Stage parentStage, Image image, boolean finalCall) {
        if(finalCall) {
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
        this.trayIcon = new TrayIcon(image, parentStage.getTitle(), popupMenu);
    }

    /**
     * Use this constructor to have FXTrayIcon use a default graphic for the tray icon.
     * This can be handy for "quick and dirty" runs of the library so that you don't need
     * to worry about setting up a graphic and defining the URL object.
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

        private final Stage                     parentStage;
        private       String                    tooltip           = "";
        private       String                    appTitle;
        private       boolean                   addExitMenuItem   = false;
        private       String                    exitMenuItemLabel = "";
        private       boolean                   addTitleMenuItem  = false;
        private       EventHandler<ActionEvent> event;
        private       ActionListener            exitMenuItemActionListener;
        private       boolean                   showTrayIcon      = false;
        private final Image                     icon;


        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon and a provided {@code javafx.stage.Stage} as its parent.
         * @param parentStage The parent Stage of the tray icon.
         * @param iconImagePath A path to an icon image
         * @param iconWidth optional to set a different icon width
         * @param iconHeight optional to set a different icon height
         */
        @API
        public Builder(Stage parentStage, URL iconImagePath, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            icon = loadImageFromURL(iconImagePath, iconWidth, iconHeight);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon and a provided {@code javafx.stage.Stage} as its parent.
         * @param parentStage The parent Stage of the tray icon.
         * @param iconImagePath A path to an icon image
         */
        @API
        public Builder(Stage parentStage, URL iconImagePath) {
            this.parentStage = parentStage;
            icon = loadImageFromURL(iconImagePath);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon File with dimensions and a provided {@code javafx.stage.Stage}
         * as its parent.
         * @param parentStage The parent Stage of the tray icon.
         * @param iconFile A java.io.File object
         * @param iconWidth an int, icon width
         * @param iconHeight an int, icon height
         */
        @API
        public Builder(Stage parentStage, File iconFile, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            icon = loadImageFromFile(iconFile, iconWidth, iconHeight);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon File and a provided {@code javafx.stage.Stage} as its parent.
         * @param parentStage The parent Stage of the tray icon.
         * @param iconFile A java.io.File object
         */
        @API
        public Builder(Stage parentStage, File iconFile) {
            this.parentStage = parentStage;
            icon = loadImageFromFile(iconFile);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon Image with dimensions and a provided {@code javafx.stage.Stage}
         * as its parent.
         * @param parentStage The parent Stage of the tray icon.
         * @param javaFXImage A javafx.scene.image.Image object
         * @param iconWidth an int, icon width
         * @param iconHeight an int, icon height
         */
        @API
        public Builder(Stage parentStage, javafx.scene.image.Image javaFXImage, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            icon = loadImageFromFX(javaFXImage, iconWidth, iconHeight);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon Image and a provided {@code javafx.stage.Stage} as its parent.
         * @param parentStage The parent Stage of the tray icon.
         * @param javaFXImage A javafx.scene.image.Image object
         */
        @API
        public Builder(Stage parentStage, javafx.scene.image.Image javaFXImage) {
            this.parentStage = parentStage;
            icon = loadImageFromFX(javaFXImage);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon and specified dimensions and a provided {@code javafx.stage.Stage} as its parent.
         * @param parentStage The parent Stage of the tray icon. Must not be null.
         * @param image a java.awt.Image object. Must not be null
         * @param iconWidth an int, icon Width
         * @param iconHeight an int, icon Height
         */
        @API
        public Builder(Stage parentStage, Image image, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            icon = loadImageFromAWT(image, iconWidth, iconHeight);
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon image and a provided {@code javafx.stage.Stage} as its parent.
         * @param parentStage The parent Stage of the tray icon. Must not be null.
         * @param image a java.awt.Image object. Must not be null
         */
        @API
        public Builder(Stage parentStage, Image image) {
            this.parentStage = parentStage;
            icon = loadImageFromAWT(image);
        }

        /**
         * Use this constructor to have FXTrayIcon use a default graphic for the tray icon.
         * This can be handy for "quick and dirty" runs of the library so that you don't need
         * to worry about setting up a graphic and defining the URL object.
         * @param parentStage Stage for FXTrayIcon
         */
        @API
        public Builder(Stage parentStage) {
            this.parentStage = parentStage;
            icon = loadDefaultIconImage();
        }

        /**
         * Add a MenuItem without passing your own.
         * This can be used repeatedly and the menuItems will be shown in the order you place them in your build sentence.
         * @param label String containing the name of this MenuItem
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
         * @param menuItems a javafx.scene.control.MenuItem List
         * @return this Builder object
         */
        @API
        public Builder menuItems(javafx.scene.control.MenuItem ... menuItems) {
            BuildOrderUtil.addMenuItems(menuItems);
            return this;
        }

        /**
         * Can be used to add a sub menu to FXTrayIcon, by passing in the Sub Menu
         * label, then by passing in either individual MenuItems separated by commas
         * or an entire MenuItem[] array.
         * @param label String for Menu label
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
         * @return this Builder object
         */
        @API
        public Builder separator() {
            BuildOrderUtil.addSeparator();
            return this;
        }

        /**
         * Set a Tooltip String which pops up for the user when they hover their mouse on FXTrayIcon
         * @param tooltip Your tooltip String
         * @return this Builder object
         */
        @API
        public Builder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        /**
         * Adds a MenuItem at the top of the menu, with its label set to
         * the {@code FXTrayIcon},that will show the main JavaFX stage when
         * clicked. If this is not set to {@code true}, a developer will
         * have to implement this functionality themselves, if desired.
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
         * @param event The action to be performed.
         * @return this Builder object
         */
        @API
        public Builder onAction(EventHandler<ActionEvent> event) {
            this.event = event;
            return this;
        }

        /**
         * Sets up Builder so that once FXTrayIcon is instantiated, it will show immediately.
         * @return this Builder object
         */
        @API
        public Builder show() {
            this.showTrayIcon = true;
            return this;
        }

        /**
         * Must be the LAST build statement in your Builder sentence.
         * @return a new instance of FXTrayIcon.
         */
        @API
        public FXTrayIcon build() {
            return new FXTrayIcon(this);
        }
    }

    /**
     * protected constructor called by the Builder class to finalize instantiation
     * @param build Builder class instance
     */
    @API
    protected FXTrayIcon(Builder build) {
        this(build.parentStage, build.icon,true);
        this.parentStage                = build.parentStage;
        this.appTitle                   = build.appTitle;
        this.addExitMenuItem            = build.addExitMenuItem;
        this.addTitleMenuItem           = build.addTitleMenuItem;
        this.exitMenuItemLabel          = build.exitMenuItemLabel;
        this.exitMenuItemActionListener = build.exitMenuItemActionListener;
        if (!build.tooltip.equals("")) setTooltip(build.tooltip);
        if (build.event != null) setOnAction(build.event);
        for (int i = 0; i < BuildOrderUtil.getItemCount(); i++) {
            switch(BuildOrderUtil.getItemType(i)) {
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
        } catch (ClassNotFoundException
                 | InstantiationException
                 | IllegalAccessException
                 | UnsupportedLookAndFeelException ignored) {}
    }

    private static Image loadImageFromURL(URL iconImagePath) {
        if (isWin()) return loadImageFromURL(iconImagePath, WinScale, WinScale);
        else return loadImageFromURL(iconImagePath, CoreScale, CoreScale);
    }

    private static Image loadImageFromURL(URL iconImagePath, int iconWidth, int iconHeight) {
        try {
            return ImageIO.read(iconImagePath)
                          .getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read the Image at the provided path: " + iconImagePath, e);
        }
    }

    private static Image loadImageFromFile(File file) {
        if (isWin()) return loadImageFromFile(file, WinScale, WinScale);
        else return loadImageFromFile(file, CoreScale, CoreScale);
    }

    private static Image loadImageFromFile(File file, int iconWidth, int iconHeight) {
        try (InputStream is = new FileInputStream(file)){
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
        if(isWin()) return loadImageFromFX(javaFXImage,WinScale,WinScale);
        else return loadImageFromFX(javaFXImage,CoreScale,CoreScale);
    }

    private static Image loadImageFromFX(javafx.scene.image.Image javaFXImage, int iconWidth, int iconHeight) {
        return SwingFXUtils.fromFXImage(javaFXImage, null).getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
    }

    private static Image loadImageFromAWT(Image image) {
        if(isWin()) return image.getScaledInstance(WinScale, WinScale, Image.SCALE_SMOOTH);
        else return image.getScaledInstance(CoreScale, CoreScale, Image.SCALE_SMOOTH);
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
                            parentStage.show();
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
                        if(isMac()) {
                            defaultActionListener = e->{
                                this.tray.remove(this.trayIcon);
                                Platform.setImplicitExit(true);
                                Platform.exit();
                                System.exit(0);
                            };
                        }
                        else {
                            defaultActionListener = e->{
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
                this.trayIcon.addActionListener(stageShowListener);
                shown = true;
            } catch (AWTException e) {
                throw new IllegalStateException("Unable to add TrayIcon", e);
            }
        });
    }

    /**
     * Adds an EventHandler that is called when the FXTrayIcon's
     * action is called. On Microsoft's Windows 10, this is invoked
     * by a single-click of the primary mouse button. On Apple's MacOS,
     * this is invoked by a two-finger click on the TrayIcon, while
     * a single click will invoke the context menu.
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
     * @param addExitMenuItem If true, the FXTrayIcon's popup menu will display
     *                       an option for exiting the application entirely.
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
     * @param addTitleMenuItem If true, the FXTrayIcon's popup menu will display
     *                         the main stages title and will show the stage on click
     */
    @API
    public void addTitleItem(boolean addTitleMenuItem) {
        this.addTitleMenuItem = addTitleMenuItem;
    }

    /**
     * Removes the MenuItem at the given index
     * @param index Index of the MenuItem to remove
     */
    @API
    public void removeMenuItem(int index) {
        EventQueue.invokeLater(() -> this.popupMenu.remove(index));
    }

    /**
     * Removes the specified item from the FXTrayIcon's menu. Does nothing
     * if the item is not in the menu.
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
     * @param index The position at which to add the separator
     */
    @API
    public void insertSeparator(int index) {
        EventQueue.invokeLater(() -> this.popupMenu.insertSeparator(index));
    }

    /**
     * Adds the specified MenuItem to the FXTrayIcon's menu
     * @param menuItem MenuItem to be added
     */
    @API
    public void addMenuItem(javafx.scene.control.MenuItem menuItem) {
        EventQueue.invokeLater(() -> addMenuItemPrivately(menuItem));
    }

    /**
     * Adds the specified MenuItems to FXTrayIcon's menu.
     * Pass in as many MenuItems as needed, separated by a comma.
     * ex: addMenuItems(menuItem1, menuItem2, menuItem3);
     * @param menuItems multiple comma separated MenuItem objects
     */
    @API
    public void addMenuItems(javafx.scene.control.MenuItem... menuItems ) {
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
            this.popupMenu.insert(AWTUtils.convertFromJavaFX(menuItem), this.popupMenu.getItemCount() - 1);
        }
        else {
            this.popupMenu.add(AWTUtils.convertFromJavaFX(menuItem));
        }
    }

    /**
     * Inserts the specified MenuItem into the FXTrayIcon's menu
     * at the supplied index.
     * @param menuItem MenuItem to be inserted
     * @param index Index to insert the MenuItem at
     */
    @API
    public void insertMenuItem(javafx.scene.control.MenuItem menuItem,int index) {
        EventQueue.invokeLater(() -> {
            if (isNotUnique(menuItem)) {
                throw new UnsupportedOperationException(
                        "Menu Item labels must be unique.");
            }
            this.popupMenu.insert(AWTUtils.convertFromJavaFX(menuItem),index);
        });
    }

    /**
     * Returns the MenuItem at the given index. The MenuItem
     * returned is the AWT MenuItem, and not the JavaFX MenuItem,
     * thus this should only be called when extending the functionality
     * of the AWT MenuItem.
     * <p>
     *     NOTE: This should be called via the
     *     {@code EventQueue.invokeLater()} method as well as any
     *     subsequent operations on the MenuItem that is returned.
     * @param index Index of the MenuItem to be returned.
     * @return The MenuItem at {@code index}
     */
    @API
    public MenuItem getMenuItem(int index) {
        return this.popupMenu.getItem(index);
    }

    /**
     * Sets the FXTrayIcon's tooltip that is displayed on mouse hover.
     * @param tooltip The text of the tooltip
     */
    @API
    public void setTrayIconTooltip(String tooltip) {
        EventQueue.invokeLater(() -> this.trayIcon.setToolTip(tooltip));
    }

    /**
     * Sets the application's title. This is used in the FXTrayIcon
     * where appropriate.
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
     * @return true if the PopupMenu is visible.
     */
    @API
    public boolean isMenuShowing() {
        for (Iterator<TrayIcon> it =
             Arrays.stream(tray.getTrayIcons()).iterator(); it.hasNext();) {
            TrayIcon ti = it.next();
            if (ti.equals(trayIcon)) {
                return ti.getPopupMenu().isEnabled();
            }
        }
        return false;
    }

    /**
     * Returns true if the FXTrayIcon's show() method has been called.
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
     * @param title The caption (header) text
     * @param message The message content text
     */
    @API
    public void showInfoMessage(String title, String message) {
        if (isMac()) {
            showMacAlert(title, message,"Information");
        } else {
            EventQueue.invokeLater(() ->
                                           this.trayIcon.displayMessage(
                                                   title, message, TrayIcon.MessageType.INFO));
        }
    }

    /**
     * Displays an info popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     * @param message The message content text
     */
    @API
    public void showInfoMessage(String message) {
        this.showInfoMessage(null, message);
    }

    /**
     * Displays a warning popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     * @param title The caption (header) text
     * @param message The message content text
     */
    @API
    public void showWarningMessage(String title, String message) {
        if (isMac()) {
            showMacAlert(title, message,"Warning");
        } else {
            EventQueue.invokeLater(() ->
                                           this.trayIcon.displayMessage(
                                                   title, message, TrayIcon.MessageType.WARNING));
        }
    }

    /**
     * Displays a warning popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     * @param message The message content text
     */
    @API
    public void showWarningMessage(String message) {
        this.showWarningMessage(null, message);
    }

    /**
     * Displays an error popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     * @param title The caption (header) text
     * @param message The message content text
     */
    @API
    public void showErrorMessage(String title, String message) {
        if (isMac()) {
            showMacAlert(title, message,"Error");
        } else {
            EventQueue.invokeLater(() ->
                                           this.trayIcon.displayMessage(
                                                   title, message, TrayIcon.MessageType.ERROR));
        }
    }

    /**
     * Displays an error popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
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
     * @param title The caption (header) text
     * @param message The message content text
     */
    @API
    public void showMessage(String title, String message) {
        if (isMac()) {
            showMacAlert(title, message,"Message");
        } else {
            EventQueue.invokeLater(() ->
                                           this.trayIcon.displayMessage(
                                                   title, message, TrayIcon.MessageType.NONE));
        }
    }

    /**
     * Displays a popup message near the tray icon.
     * Some systems will display FXTrayIcon's image on this popup.
     * <p>NOTE: Some systems do not support this.</p>
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
     *
     * Just because the system tray is supported, does not mean that the
     * current platform implements all system tray functionality.
     * @return false if the system tray is not supported, true if any
     *          part of the system tray functionality is supported.
     */
    @API
    public static boolean isSupported() {
        return Desktop.isDesktopSupported() && SystemTray.isSupported();
    }

    /**
     * Provides the number of menuItems in the popupMenu.
     * @return int getItemCount()
     */
    @API
    public int getMenuItemCount() {
        return this.popupMenu.getItemCount();
    }

    /**
     * Provides a way to change the TrayIcon image at runtime.
     * by passing in a JavaFX Image object. The image will be
     * scaled to the correct size for the OS.
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
     * @param javaFXImage javafx.scene.image.Image object
     * @param iconWidth int object
     * @param iconHeight int object
     */
    @API
    public void setGraphic(javafx.scene.image.Image javaFXImage, int iconWidth, int iconHeight) {
        setFinalGraphic(loadImageFromFX(javaFXImage, iconWidth, iconHeight));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime.
     * by passing in a Java IO File object. The image will be
     * scaled to the correct size for the OS.
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
     * @param file a java.io.File object
     * @param iconWidth an int, the width of the icon
     * @param iconHeight an int, the height of the icon
     */
    @API
    public void setGraphic(File file, int iconWidth, int iconHeight) {
        setFinalGraphic(loadImageFromFile(file, iconWidth, iconHeight));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime
     * by specifying the image URL. The image will be scaled
     * to the correct size for the OS.
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
     * @param imageURL a java.net.URL object
     * @param iconWidth an int, the width of the icon
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
     * @param image a java.awt.Image object
     */
    @API
    public void setGraphic(Image image) {
        setFinalGraphic(loadImageFromAWT(image));
    }

    @API
    public void setGraphic(Image image, int iconWidth, int iconHeight) {
        setFinalGraphic(loadImageFromAWT(image, iconWidth, iconHeight));
    }

    private void setFinalGraphic(Image img) {
        this.trayIcon.setImage(img);
    }

    /**
     * Sets the FXTrayIcon's tooltip text (shown on mouse hover)
     * @param tooltip String for tooltip
     */
    @API
    public void setTooltip(String tooltip) {
        this.trayIcon.setToolTip(tooltip);
    }

    /**
     * Displays a sliding info message. Behavior is similar to Windows, but without AWT
     * @param subTitle The message caption
     * @param message The message text
     * @param title The message title
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
                   .exec(new String[] { "osascript", "-e", execute });
        } catch (IOException e) {
            throw new UnsupportedOperationException(
                    "Cannot run osascript with given parameters.");
        }
    }

    /**
     * Adds a JavaFX Menu to the TrayIcon's PopupMenu
     * @param menu A JavaFX Menu
     */
    private void addMenu(Menu menu) {
        EventQueue.invokeLater(() -> {
            java.awt.Menu awtMenu = new java.awt.Menu(menu.getText());
            menu.getItems().forEach(subItem ->
                                            awtMenu.add(AWTUtils.convertFromJavaFX(subItem)));
            if(addExitMenuItem && shown) {
                this.popupMenu.insert(awtMenu, this.popupMenu.getItemCount() - 1);
            }
            else {
                this.popupMenu.add(awtMenu);
            }
        });
    }

    /**
     * Check if a JavaFX menu item's text is unique among those
     * previously added to the AWT PopupMenu
     * @param fxItem A JavaFX MenuItem
     * @return true if the item's text is unique among previously
     *          added items.
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

}
