package com.dustinredmond.fxtrayicon;

import javafx.application.Platform;
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
import java.io.IOException;
import java.net.URL;

/**
 *  Class for creating a JavaFX System Tray Icon.
 *  Uses JavaFX controls to create the icon.
 *  Allows for a developer to create a tray icon
 *  using JavaFX code, without having to access
 *  the AWT API.
 */
@SuppressWarnings("unused")
public class FXTrayIcon {

    private final SystemTray tray;
    private final Stage parentStage;
    private String appTitle;
    private final TrayIcon trayIcon;
    private boolean showing;
    private final PopupMenu popupMenu = new PopupMenu();
    private boolean addExitMenuItem = true;

    public FXTrayIcon(Stage parentStage, URL iconImagePath) {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("SystemTray icons are not "
                    + "supported by the current desktop environment.");
        } else {
            tray = SystemTray.getSystemTray();
        }

        // Keeps the JVM running even if there are no
        // visible JavaFX Stages
        Platform.setImplicitExit(false);

        // Set the SystemLookAndFeel as default, let user override if needed
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ignored) {}

        try {
            final Image iconImage = ImageIO.read(iconImagePath)
                    // Some OSes do not behave well if the icon is larger than 16x16
                    // Image.SCALE_SMOOTH will provide the best quality icon in most instances
                    .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            this.parentStage = parentStage;
            this.trayIcon = new TrayIcon(iconImage, parentStage.getTitle(), popupMenu);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read the Image at the provided path.");
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
    public void show() {
        SwingUtilities.invokeLater(() -> {
            try {
                tray.add(this.trayIcon);
                this.showing = true;

                // Add a MenuItem with the main Stage's title, this will show the
                // main JavaFX stage when clicked.
                String miTitle;
                if (this.appTitle != null) {
                    miTitle = appTitle;
                } else {
                    if (parentStage == null
                        || parentStage.getTitle() == null
                        || parentStage.getTitle().isEmpty()) {
                        miTitle = "Show application";
                    } else {
                        miTitle = parentStage.getTitle();
                    }
                }
                MenuItem miStage = new MenuItem(miTitle);
                miStage.setFont(Font.decode(null).deriveFont(Font.BOLD));
                miStage.addActionListener(e -> Platform.runLater(() -> {
                    if (parentStage != null) {
                        parentStage.show();
                    }
                }));
                this.popupMenu.add(miStage);

                // If Platform.setImplicitExit(false) then the JVM will continue to run after
                // no more Stages remain, thus we provide a way to terminate it by default.
                // User will be able to override this by calling new FXTrayIcon(...).addExitItem(false)
                if (addExitMenuItem) {
                    MenuItem miExit = new MenuItem("Exit program");
                    miExit.addActionListener(e -> {
                        this.tray.remove(this.trayIcon);
                        Platform.exit();
                    });
                    this.popupMenu.add(miExit);
                }

                // Show parent stage when user double-clicks the icon
                this.trayIcon.addActionListener(e -> {
                    if (this.parentStage != null) {
                        Platform.runLater(this.parentStage::show);
                    }
                });
            } catch (AWTException e) {
                throw new IllegalStateException("Unable to add TrayIcon", e);
            }
        });
    }

    /**
     * Adds the icon to the SystemTray. {@code showMinimal()} adds the icon with
     * an empty popup menu, allowing the user to add {@code MenuItem}s from scratch.
     */
    public void showMinimal() {
        try {
            tray.add(this.trayIcon);
            this.showing = true;
            this.trayIcon.addActionListener(e -> {
                if (this.parentStage != null) {
                    Platform.runLater(this.parentStage::show);
                }
            });
        } catch (AWTException e) {
            throw new IllegalStateException("Unable to add TrayIcon", e);
        }
    }

    /**
     * Adds a MenuItem to the {@code FXTrayIcon} that will close the JavaFX application
     * and terminate the JVM. If this is not set to @{code true}, a developer will have
     * to implement this functionality themselves.
     * @param addExitMenuItem If true, the FXTrayIcon's popup menu will display an option for
     *                        exiting the application entirely.
     */
    public void addExitItem(boolean addExitMenuItem) {
        this.addExitMenuItem = addExitMenuItem;
    }

    /**
     * Removes the MenuItem at the given index
     * @param index Index of the MenuItem to remove
     */
    public void removeMenuItem(int index) {
        EventQueue.invokeLater(() -> this.popupMenu.remove(index));
    }

    /**
     * Removes the specified item from the FXTrayIcon's menu. Does nothing
     * if the item is not in the menu.
     * @param fxMenuItem The JavaFX MenuItem to remove from the menu.
     */
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
    public void addSeparator() {
        EventQueue.invokeLater(this.popupMenu::addSeparator);
    }

    /**
     * Adds a separator line to the Menu at the given position.
     * @param index The position at which to add the separator
     */
    public void insertSeparator(int index) {
        EventQueue.invokeLater(() -> this.popupMenu.insertSeparator(index));
    }

    /**
     * Adds the specified MenuItem to the FXTrayIcon's menu
     * @param menuItem MenuItem to be added
     */
    public void addMenuItem(javafx.scene.control.MenuItem menuItem) {
        if (menuItem instanceof Menu) {
            addMenu((Menu) menuItem);
            return;
        }
        if (!isUnique(menuItem)) {
            throw new UnsupportedOperationException("Menu Item labels must be unique.");
        }
        EventQueue.invokeLater(() -> this.popupMenu.add(AWTUtils.convertFromJavaFX(menuItem)));
    }

    private void addMenu(Menu menu) {
        EventQueue.invokeLater(() -> {
            java.awt.Menu awtMenu = new java.awt.Menu(menu.getText());
            menu.getItems().forEach(subItem -> awtMenu.add(AWTUtils.convertFromJavaFX(subItem)));
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
    public MenuItem getMenuItem(int index) {
        return this.popupMenu.getItem(index);
    }

    /**
     * Sets the FXTrayIcon's tooltip that is displayed on mouse hover.
     * @param tooltip The text of the tooltip
     */
    public void setTrayIconTooltip(String tooltip) {
        EventQueue.invokeLater(() -> this.trayIcon.setToolTip(tooltip));
    }

    /**
     * Sets the application's title. This is used in the FXTrayIcon where appropriate.
     * @param title The application's title, to be used for
     *              the tooltip text for FXTrayIcon
     */
    public void setApplicationTitle(String title) {
        this.appTitle = title;
    }

    /**
     * Removes the {@code FXTrayIcon} from the system tray.
     * Also calls {@code Platform.setImplicitExit(true)}, thereby
     * allowing the JVM to terminate after the last JavaFX {@code Stage}
     * is hidden.
     */
    public void hide() {
        EventQueue.invokeLater(() -> {
            tray.remove(trayIcon);
            this.showing = false;
            Platform.setImplicitExit(true);
        });
    }

    /**
     * Returns true if the SystemTray icon is visible.
     * @return true if the SystemTray icon is visible.
     */
    public boolean isShowing() {
        return this.showing;
    }

    /**
     * Check if a JavaFX menu item's text is unique among those
     * previously added to the AWT PopupMenu
     * @param fxItem A JavaFX MenuItem
     * @return true if the item's text is unique among previously
     *          added items.
     */
    private boolean isUnique(javafx.scene.control.MenuItem fxItem) {
        if (this.popupMenu.getItemCount() == 0) {
            return true;
        }
        for (int i = 0; i < this.popupMenu.getItemCount(); i++) {
            if (this.popupMenu.getItem(i).getName().equals(fxItem.getText())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether or not the system tray icon is supported on the
     * current platform.
     *
     * Just because the system tray is supported, does not mean that the
     * current platform implements all of the system tray functionality.
     * @return false if the system tray is not supported, true if some
     *          or part of the system tray functionality is supported.
     */
    public static boolean isSupported() {
        return Desktop.isDesktopSupported() && SystemTray.isSupported();
    }
}
