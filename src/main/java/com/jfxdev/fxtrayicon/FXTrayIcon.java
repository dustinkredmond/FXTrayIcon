package com.jfxdev.fxtrayicon;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class FXTrayIcon {

    private final SystemTray tray = SystemTray.getSystemTray();
    private Stage parentStage;
    private final TrayIcon trayIcon;
    private final PopupMenu popupMenu = new PopupMenu();
    private boolean addExitMenuItem = false;


    public FXTrayIcon(Stage parentStage, URL iconImagePath) {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("SystemTray icons are not " +
                    "supported by the current desktop environment.");
        }

        // Keep the tray icon after all windows are closed
        Platform.setImplicitExit(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ignored) {}
        try {
            final Image iconImage = ImageIO.read(iconImagePath)
                    .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            this.parentStage = parentStage;
            this.trayIcon = new TrayIcon(iconImage, "", popupMenu);
        } catch (IOException e) {
            // will probably get here if the resource is not found or otherwise
            // unable to be read
            throw new RuntimeException(e);
        }
    }


    /**
     * Adds the FXTrayIcon to the system tray.
     * This will add the TrayIcon with the image initialized in the
     * {@code FXTrayIcon}'s constructor. By default, an empty popup
     * menu it shown
     */
    public void show() {
        SwingUtilities.invokeLater(() -> {
            try {
                tray.add(this.trayIcon);
                this.popupMenu.addSeparator();
                String miTitle = parentStage.getTitle().isEmpty() ? "Show application" : parentStage.getTitle();
                MenuItem miStage = new MenuItem(miTitle);
                miStage.setFont(Font.decode(null).deriveFont(Font.BOLD));
                miStage.addActionListener(e -> Platform.runLater(() -> parentStage.show()));
                this.popupMenu.add(miStage);
                if (addExitMenuItem) {
                    MenuItem miExit = new MenuItem("Exit program");
                    miExit.addActionListener(e -> {
                        Platform.exit();
                        System.exit(0);
                    });
                    this.popupMenu.add(miExit);
                }
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        });
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
     * Removes the {@code FXTrayIcon} from the system tray.
     */
    public void hide() {
        EventQueue.invokeLater(() -> tray.remove(trayIcon));
    }

}
