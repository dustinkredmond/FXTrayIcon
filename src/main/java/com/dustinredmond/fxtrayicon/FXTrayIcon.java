package com.dustinredmond.fxtrayicon;

import com.dustinredmond.fxtrayicon.annotations.API;
import javafx.application.Platform;
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
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *  Class for creating a JavaFX System Tray Icon.
 *  Allows for a developer to create a tray icon
 *  using JavaFX style API.
 */
public class FXTrayIcon {

    private final SystemTray tray;
    private Stage parentStage;
    private String appTitle;
    private final TrayIcon trayIcon;
    private final PopupMenu popupMenu = new PopupMenu();
    private boolean addExitMenuItem = true;
    private final LinkedList<javafx.scene.control.MenuItem> newMenuItems =
            new LinkedList<>();
    private final boolean isMac;

    @API
    public FXTrayIcon(Stage parentStage, URL iconImagePath) {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException(
                    "SystemTray icons are not "
                            + "supported by the current desktop environment.");
        } else {
            isMac = System.getProperty("os.name")
                    .toLowerCase(Locale.ENGLISH)
                    .contains("mac");

            tray = SystemTray.getSystemTray();
            // Keeps the JVM running even if there are no
            // visible JavaFX Stages, otherwise JVM would
            // exit and we lose the TrayIcon
            Platform.setImplicitExit(false);

            // Set the SystemLookAndFeel as default,
            // let user override if needed
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException
                    | InstantiationException
                    | IllegalAccessException
                    | UnsupportedLookAndFeelException ignored) {}

            try {
                final Image iconImage = ImageIO.read(iconImagePath)
                        // Some OSes do not behave well
                        // if the icon is larger than 16x16
                        .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                this.parentStage = parentStage;
                this.trayIcon =
                        new TrayIcon(iconImage
                                , parentStage.getTitle()
                                , popupMenu);
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Unable to read the Image at the provided path.");
            }
            addMenuItemsThread();
        }
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
                String miTitle = (this.appTitle != null) ?
                        this.appTitle
                        : (parentStage != null && parentStage.getTitle() != null
                        && !parentStage.getTitle().isEmpty()) ?
                        parentStage.getTitle() : "Show application";

                MenuItem miStage = new MenuItem(miTitle);
                miStage.setFont(Font.decode(null).deriveFont(Font.BOLD));
                miStage.addActionListener(e -> Platform.runLater(() -> {
                    if (parentStage != null) {
                        parentStage.show();
                    }
                }));
                this.popupMenu.add(miStage);

                // If Platform.setImplicitExit(false) then the JVM will
                // continue to run after no more Stages remain,
                // thus we provide a way to terminate it by default.
                if (addExitMenuItem) {
                    MenuItem miExit = new MenuItem("Exit program");
                    miExit.addActionListener(e -> {
                        this.tray.remove(this.trayIcon);
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
     * Adds an EventHandler that is called when the FXTrayIcon is
     * single-clicked.
     * @param e The action to be performed.
     * @deprecated since 2.5.0 The behavior of setOnClick() vs setOnAction()
     *             does not significantly differ between platforms.
     *             Prefer setOnAction()
     */
    @Deprecated()
    public void setOnClick(EventHandler<ActionEvent> e) {
        if (this.trayIcon.getMouseListeners().length >= 1) {
            this.trayIcon.removeMouseListener(
                    this.trayIcon.getMouseListeners()[0]);
        }
        this.trayIcon.addMouseListener(getPrimaryClickListener(e));
    }

    /**
     * Adds the icon to the SystemTray. {@code showMinimal()} adds the
     * icon with an empty popup menu, allowing the user to add
     * {@code MenuItem}s from scratch.
     */
    @API
    public void showMinimal() {
        try {
            tray.add(this.trayIcon);
            this.trayIcon.addActionListener(stageShowListener);
        } catch (AWTException e) {
            throw new IllegalStateException("Unable to add TrayIcon", e);
        }
    }

    /**
     * Adds a MenuItem to the {@code FXTrayIcon} that will close the
     * JavaFX application and terminate the JVM. If this is not set
     * to @{code true}, a developer will have to implement this functionality
     * themselves.
     * @param addExitMenuItem If true, the FXTrayIcon's popup menu will display
     *                       an option for exiting the application entirely.
     */
    @API
    public void addExitItem(boolean addExitMenuItem) {
        this.addExitMenuItem = addExitMenuItem;
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
     * Thread to add new MenuItems sequentially, allowing the invokeLater
     * thread to finish before adding the next MenuItem
     */
    private void addMenuItemsThread() {
        new Thread(() -> {
            while(true) {
                if (!newMenuItems.isEmpty()) {
                    javafx.scene.control.MenuItem newMenuItem =
                            newMenuItems.removeLast();
                    int currentCount = popupMenu.getItemCount();
                    if (isUnique(newMenuItem)) {
                        EventQueue.invokeLater(() ->
                                popupMenu.add(
                                        AWTUtils.convertFromJavaFX(newMenuItem)
                                ));
                        while (popupMenu.getItemCount() == currentCount) {
                            // Wait for invokeLater thread to finish adding
                            // the new menuItem
                            try {
                                TimeUnit.MILLISECONDS.sleep(1);
                            } catch (InterruptedException ignored) {}
                        }
                    }
                    else {
                        throw new UnsupportedOperationException(
                                "Menu Item labels must be unique.");
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    /**
     * Adds the specified MenuItem to the FXTrayIcon's menu
     * @param menuItem MenuItem to be added
     */
    @API
    public void addMenuItem(javafx.scene.control.MenuItem menuItem) {
        if (menuItem instanceof Menu) {
            addMenu((Menu) menuItem);
            return;
        }
        newMenuItems.addFirst(menuItem);
    }

    private void addMenu(Menu menu) {
        EventQueue.invokeLater(() -> {
            java.awt.Menu awtMenu = new java.awt.Menu(menu.getText());
            menu.getItems().forEach(subItem ->
                    awtMenu.add(AWTUtils.convertFromJavaFX(subItem)));
            this.popupMenu.add(awtMenu);
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
     * Returns true if the FXTrayIcon's show() or showMinimal()
     * methods have been called.
     * @return true if the FXTrayIcon is a part of the SystemTray.
     */
    @API
    public boolean isShowing() {
        return Arrays.stream(
                tray.getTrayIcons())
                .collect(Collectors.toList()).contains(trayIcon);
    }

    /**
     * Check if a JavaFX menu item's text is unique among those
     * previously added to the AWT PopupMenu
     * @param fxItem A JavaFX MenuItem
     * @return true if the item's text is unique among previously
     *          added items.
     */
    private boolean isUnique(javafx.scene.control.MenuItem fxItem) {
        for (int i = 0; i < popupMenu.getItemCount(); i++) {
            if (popupMenu.getItem(i).getLabel().equals(fxItem.getText())) {
                return false;
            }
        }
        return true;
    }

    private final ActionListener stageShowListener = e -> {
        if (this.parentStage != null) {
            Platform.runLater(this.parentStage::show);
        }
    };

    private MouseListener getPrimaryClickListener(
            EventHandler<ActionEvent> e) {
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
     * Displays a sliding info message similar to what Windows
     * does, but without AWT
     * @param caption The message caption
     * @param message The message text
     * @param type The message type
     */
    private void showMacAlert(String caption, String message, String type) {

        String execute = String.format(
                "display notification \"%s\""
                + " with title \"%s\""
                + " subtitle \"%s\"",
                message != null ? message : "",
                type != null ? type : "",
                caption != null ? caption : ""
        );

        try {
            Runtime.getRuntime().exec(new String[] { "osascript", "-e", execute });
        }
        catch (IOException e) {
            throw new UnsupportedOperationException(
                    "Cannot run osascript with given parameters.");
        }
    }

    /**
     * Displays an info popup message near the tray icon.
     * <p>NOTE: Some systems do not support this.</p>
     * @param caption The caption (header) text
     * @param message The message content text
     */
    @API
    public void showInfoMessage(String caption, String message) {
        if (isMac) {
            showMacAlert(caption, message,"Information");
            System.out.println("Is Mac");
        } else {
            EventQueue.invokeLater(() ->
                    this.trayIcon.displayMessage(
                            caption, message, TrayIcon.MessageType.INFO));
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
     * @param caption The caption (header) text
     * @param message The message content text
     */
    @API
    public void showWarningMessage(String caption, String message) {
        if (isMac) {
            showMacAlert(caption, message,"Warning");
        } else {
            EventQueue.invokeLater(() ->
                    this.trayIcon.displayMessage(
                            caption, message, TrayIcon.MessageType.WARNING));
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
     * @param caption The caption (header) text
     * @param message The message content text
     */
    @API
    public void showErrorMessage(String caption, String message) {
        if (isMac) {
            showMacAlert(caption, message,"Error");
        } else {
            EventQueue.invokeLater(() ->
                    this.trayIcon.displayMessage(
                            caption, message, TrayIcon.MessageType.ERROR));
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
     * @param caption The caption (header) text
     * @param message The message content text
     */
    @API
    public void showMessage(String caption, String message) {
        if (isMac) {
            showMacAlert(caption, message,"Message");
        } else {
            EventQueue.invokeLater(() ->
                    this.trayIcon.displayMessage(
                            caption, message, TrayIcon.MessageType.NONE));
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
}
