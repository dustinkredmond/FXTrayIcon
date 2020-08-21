package com.jfxdev.fxtrayicon;

import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class FXTrayIcon {

    private final SystemTray tray = SystemTray.getSystemTray();
    private TrayIcon trayIcon;
    private PopupMenu popupMenu = new PopupMenu();
    private boolean addExitMenuItem = false;


    public FXTrayIcon(URL iconImagePath) {
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("SystemTray icons are not " +
                    "supported by the current desktop environment.");
        }
        try {
            this.trayIcon = new TrayIcon(
                    ImageIO.read(iconImagePath)
                            .getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            // will probably get here if the resource is not found or otherwise
            // unable to be read
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds the FXTrayIcon to the system tray.
     */
    public void show() {
        EventQueue.invokeLater(() -> {
            try {
                if (addExitMenuItem) {
                    MenuItem miExit = new Menu("Exit program");
                    miExit.addActionListener(e -> Platform.runLater(Platform::exit));
                    this.trayIcon.getPopupMenu().add(miExit);
                }
                tray.add(this.trayIcon);
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

    /**
     * Setting to {@code true} keeps the JVM from terminating when
     * the last JavaFX {@code Stage} is hidden. This allows the {@code FXTrayIcon}
     * to remain visible even after no remaining JavaFX {@code Stage}s are visible.
     * @param enabled Set to true to persist after no JavaFX {@code Stage}s are visible.
     */
    public void persistIconOnLastJavaFXStageHidden(boolean enabled) {
        Platform.setImplicitExit(!enabled);
    }
}
