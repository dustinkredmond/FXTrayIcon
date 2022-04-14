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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Class for creating a JavaFX System Tray Icon.
 *  Allows for a developer to create a tray icon
 *  using JavaFX style API.
 */
public class FXTrayIcon {

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
     * Set to true if the end-user's operating
     * system is MacOS.
     *
     * This is used in determining how to handle
     * the notifications (AWT or AppleScript)
     */
    private boolean isMac;

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
        this(parentStage, loadImageFromFile(iconImagePath, iconWidth, iconHeight));
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon and a provided{@code javafx.stage.Stage} as its parent.
     * @param parentStage The parent Stage of the tray icon.
     * @param iconImagePath A path to an icon image
     */
    @API
    public FXTrayIcon(Stage parentStage, URL iconImagePath) {
        this(parentStage, loadImageFromFile(iconImagePath));
    }

    /**
     * Use this constructor to have FXTrayIcon use a default graphic for the tray icon.
     * This can be handy for "quick and dirty" runs of the library so that you don't need
     * to worry about setting up a graphic and defining the URL object.
     * @param parentStage Stage for FXTrayIcon
     */
    @API
    public FXTrayIcon(Stage parentStage) {
        this(parentStage, loadImageFromFile(null));
    }

    /**
     * Creates an instance of FXTrayIcon with the provided
     * icon and a provided{@code javafx.stage.Stage} as its parent.
     * @param parentStage The parent Stage of the tray icon. Must not be null.
     * @param icon The image to use as the tray icon. Must not be null.
     */
    @API
    public FXTrayIcon(Stage parentStage, Image icon) {
      Objects.requireNonNull(parentStage, "parentStage must not be null");
      Objects.requireNonNull(icon, "icon must not be null");

      ensureSystemTraySupported();

      tray = SystemTray.getSystemTray();
      // Keeps the JVM running even if there are no
      // visible JavaFX Stages, otherwise JVM would
      // exit, and we lose the TrayIcon
      Platform.setImplicitExit(false);

      attemptSetSystemLookAndFeel();

      this.parentStage = parentStage;
      this.trayIcon = new TrayIcon(icon, parentStage.getTitle(), popupMenu);
    }

    /**
     * OPTIONAL Builder class that streamlines the instantiation of FXTrayIcon,
     * permitting the passing of a full set of MenuItems, Separators, and every
     * settable option of FXTrayIcon from one Builder line of code.
     */
    @API
    public static class Builder {

        private final Stage                                       parentStage;
        private       URL                                         iconImagePath;
        private       int                                         iconWidth;
        private       int                                         iconHeight;
        private       Image                                       icon;
        private       boolean                                     isMac;
        private       String                                      tooltip            = "";
        private       String                                      appTitle;
        private       boolean                                     addExitMenuItem    = false;
        private       String                                      exitMenuItemLabel  = "";
        private       boolean                                     addTitleMenuItem   = false;
        private       EventHandler<ActionEvent>                   event;
        private final Map<Integer, javafx.scene.control.MenuItem> menuItemMap        = new HashMap<>();
        private final List<Integer>                               separatorIndexList = new ArrayList<>();
        private       boolean                                     showTrayIcon       = false;
        private       boolean                                     useDefaultIcon     = false;
        private       Integer                                     index              = 0;
        private final URL                                         defaultIconPath    = getClass().getResource("FXIconRedWhite.png");


        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon and a provided{@code javafx.stage.Stage} as its parent.
         * @param parentStage The parent Stage of the tray icon.
         * @param iconImagePath A path to an icon image
         * @param iconWidth optional to set a different icon width
         * @param iconHeight optional to set a different icon height
         */
        @API
        public Builder(Stage parentStage, URL iconImagePath, int iconWidth, int iconHeight) {
            this.parentStage = parentStage;
            this.iconImagePath = iconImagePath;
            this.iconWidth = iconWidth;
            this.iconHeight = iconHeight;
        }

        /**
         * Creates an instance of FXTrayIcon with the provided
         * icon and a provided{@code javafx.stage.Stage} as its parent.
         * @param parentStage The parent Stage of the tray icon.
         * @param iconImagePath A path to an icon image
         */
        @API
        public Builder(Stage parentStage, URL iconImagePath) {
            this.parentStage = parentStage;
            this.iconImagePath = iconImagePath;
        }

        /**
         * Using this constructor will cause FXTrayIcon to use a default, built in icon graphic.
         * Perfect for quick tests when you don't want to mess with adding your own
         * URL path and graphic.
         * @param parentStage your stage or your apps default parentStage.
         */
        @API
        public Builder(Stage parentStage) {
            this.parentStage = parentStage;
            useDefaultIcon = true;
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
            menuItemMap.put(index,menuItem);
            index++;
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
            menuItemMap.put(index,menuItem);
            index++;
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
            for (javafx.scene.control.MenuItem menuItem : menuItems) {
                menuItemMap.put(index,menuItem);
                index++;
            }
            return this;
        }

        /**
         * Add separators that visually divides your menuItems into groups.
         * you can stack this along with menu items through your build sentence, and they will be inserted in the order you place them.
         * @return this Builder object
         */
        @API
        public Builder separator() {
            separatorIndexList.add(index);
            index++;
            return this;
        }

        /**
         * Set a ToolTip String which pops up for the user when they hover their mouse on FXTrayIcon
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
         * Creates a menuItem with option for a custom
         * label and puts it at the bottom of the menu.
         * When engaged, it closes the application.
         * @return this Builder object
         */
        @API
        public Builder addExitMenuItem(String label) {
            this.exitMenuItemLabel = label;
            this.addExitMenuItem = true;
            return this;
        }

        /**
         * Adds an EventHandler that is called when the FXTrayIcon's
         * action is called. On Microsoft's Windows 10, this is invoked
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
            isMac = System.getProperty("os.name")
                          .toLowerCase(Locale.ENGLISH)
                          .contains("mac");

            if (iconWidth == 0 || iconHeight == 0) {
                if (isMac) {
                    iconWidth = 22;
                    iconHeight = 22;
                } else {
                    iconWidth = 16;
                    iconHeight = 16;
                }
            }
            if (useDefaultIcon) {
                icon = loadImageFromFile(defaultIconPath,iconWidth,iconHeight);
            } else {
                icon = loadImageFromFile(iconImagePath,iconWidth,iconHeight);
            }

            return new FXTrayIcon(this);
        }
    }

    /**
     * protected constructor called by the Builder class to finalize instantiation
     * @param build Builder class instance
     */
    @API
    protected FXTrayIcon(Builder build) {
        this(build.parentStage, build.icon);
        this.parentStage = build.parentStage;
        this.isMac = build.isMac;
        this.appTitle = build.appTitle;
        this.addExitMenuItem = build.addExitMenuItem;
        this.addTitleMenuItem = build.addTitleMenuItem;
        this.exitMenuItemLabel = build.exitMenuItemLabel;
        if (build.showTrayIcon) show();
        if (!build.tooltip.equals("")) setTooltip(build.tooltip);
        if (build.event != null) setOnAction(build.event);
        for (int x = 0; x <= build.index; x++) {
            if (build.menuItemMap.containsKey(x)) {
                addMenuItem(build.menuItemMap.get(x));
            } else if (build.separatorIndexList.contains(x)) {
                addSeparator();
            }
        }
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

    private static Image loadImageFromFile(URL iconImagePath) {
        URL defaultIconImagePath = FXTrayIcon.class.getResource("FXIconRedWhite.png");
        if (isMac()) return loadImageFromFile((iconImagePath == null) ? defaultIconImagePath : iconImagePath, 22, 22);
        else return loadImageFromFile((iconImagePath == null) ? defaultIconImagePath : iconImagePath, 16, 16);
    }

    private static Image loadImageFromFile(URL iconImagePath, int iconWidth, int iconHeight) {
      try {
        return ImageIO.read(iconImagePath)
                      .getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH);
      } catch (IOException e) {
        throw new IllegalStateException("Unable to read the Image at the provided path: " + iconImagePath, e);
      }
    }

    private static boolean isMac() {
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

                // If Platform.setImplicitExit(false) then the JVM will
                // continue to run after no more Stages remain,
                // thus we provide a way to terminate it by default.
                if (addExitMenuItem) {
                    String label = exitMenuItemLabel.equals("") ? "Exit Application" : exitMenuItemLabel;
                    MenuItem miExit = new MenuItem(label);
                    miExit.addActionListener(e -> {
                        this.tray.remove(this.trayIcon);
                        Platform.setImplicitExit(true);
                        Platform.exit();
                    });
                    this.popupMenu.add(miExit);
                }

                // Show parent stage when user clicks the icon
                this.trayIcon.addActionListener(stageShowListener);
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
        EventQueue.invokeLater(() -> {
            if (menuItem instanceof Menu) {
                addMenu((Menu) menuItem);
                return;
            }
            if (isNotUnique(menuItem)) {
                throw new UnsupportedOperationException(
                        "Menu Item labels must be unique.");
            }
            this.popupMenu.add(AWTUtils.convertFromJavaFX(menuItem));
        });
    }

    /**
     * Adds the specified MenuItems to FXTrayIcon's menu.
     * Pass in as many MenuItems as needed separated by a comma.
     * ex: addMenuItems(menuItem1, menuItem2, menuItem3);
     * @param menuItems multiple comma separated MenuItem objects
     */
    @API
    public void addMenuItems(javafx.scene.control.MenuItem... menuItems ) {
        EventQueue.invokeLater(() -> {
            for (javafx.scene.control.MenuItem menuItem : menuItems) {
                if (menuItem instanceof Menu) {
                    addMenu((Menu) menuItem);
                    return;
                }
                if (isNotUnique(menuItem)) {
                    throw new UnsupportedOperationException(
                            "Menu Item labels must be unique.");
                }
                this.popupMenu.add(AWTUtils.convertFromJavaFX(menuItem));
            }
        });
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
        if (isMac) {
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
        if (isMac) {
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
        if (isMac) {
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
        if (isMac) {
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
     * Checks whether or not the system tray icon is supported on the
     * current platform.
     *
     * Just because the system tray is supported, does not mean that the
     * current platform implements all of the system tray functionality.
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
     * @param img JavaFX Image
     */
    @API
    public void setGraphic(javafx.scene.image.Image img) {
        setGraphic(SwingFXUtils.fromFXImage(img, null));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime.
     * @param file a java.io.Fil object
     */
    @API
    public void setGraphic(File file) {
        javafx.scene.image.Image img = new javafx.scene.image.Image(file.getAbsolutePath());
        setGraphic(SwingFXUtils.fromFXImage(img, null));
    }

    /**
     * Provides a way to change the TrayIcon image at runtime.
     * @param file a java.io.Fil object
     * @param iconWidth an int, the width of the icon
     * @param iconHeight an int, the height of the icon
     */
    @API
    public void setGraphic(File file, int iconWidth, int iconHeight) {
        try {
            URL url = new URL("file:" + file.getAbsolutePath());
            Image image = loadImageFromFile(url,iconWidth, iconHeight);
            setGraphic(image);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Provides a way to change the TrayIcon image at runtime.
     * @param URLString a String of the URL to the image file
     * @param iconWidth an int, the width of the icon
     * @param iconHeight an int, the height of the icon
     */
    @API
    public void setGraphic(String URLString, int iconWidth, int iconHeight) {
        try {
            URL url = new URL(URLString);
            Image image = loadImageFromFile(url,iconWidth, iconHeight);
            setGraphic(image);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Provides a way to change the TrayIcon image at runtime.
     * @param img Java AWT Image
     */
    @API
    public void setGraphic(Image img) {
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
     * Displays a sliding info message similar to what Windows
     * does, but without AWT
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
            this.popupMenu.add(awtMenu);
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
