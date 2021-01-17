package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Serves as a basic smoke test for FXTrayIcon and associated
 * helper classes.
 */
public class TestFXTrayIcon extends Application {

    @Test
    public void runTestOnFXApplicationThread() {
        if (!Desktop.isDesktopSupported()) {
            System.err.println("Tests unable to be run on headless environment.");
            return;
        }
        if (System.getenv("CI").equalsIgnoreCase("true")) {
            System.err.println("Tests unable to be run on headless CI platform.");
            return;
        }
        Application.launch(TestFXTrayIcon.class, (String) null);
    }

    @Override
    public void start(Stage stage) throws Exception {
        testShouldConvertSuccessful();
        testShouldConvertFail();
        testTrayIconSupported();
        testNotNullTestResource();
        testInitialization();
        Platform.exit();;
    }

    public void testShouldConvertSuccessful() {
        MenuItem fxItem = new MenuItem("SomeText");
        fxItem.setDisable(true);
        fxItem.setOnAction(e -> {/* ignored */});

        java.awt.MenuItem awtItem = AWTUtils.convertFromJavaFX(fxItem);
        assertEquals(fxItem.getText(), awtItem.getLabel());
        assertEquals(fxItem.isDisable(), !awtItem.isEnabled());
        assertEquals(1, awtItem.getActionListeners().length);
    }

    public void testShouldConvertFail() {
        MenuItem fxItem = new MenuItem();
        fxItem.setGraphic(new Label());
        try {
            java.awt.MenuItem awtItem = AWTUtils.convertFromJavaFX(fxItem);
            fail("Should not be able to assign graphic in AWTUtils");
        } catch (Exception ignored) { /* should always reach here */ }
    }

    public void testTrayIconSupported() {
        assertEquals(SystemTray.isSupported(), FXTrayIcon.isSupported());
    }

    public void testNotNullTestResource() {
        try {
            assertNotNull(getClass().getResource(TEST_ICON));
        } catch (Exception e) {
            fail("Error retrieving resources (FXTrayIcon graphic) for unit tests");
        }
    }

    public void testInitialization() {
        if (FXTrayIcon.isSupported()) {
            FXTrayIcon icon = new FXTrayIcon(new Stage(), getClass().getResource(TEST_ICON));
            assertNotNull(icon);
        }
    }

    private static final String TEST_ICON = "icons8-link-64.png";

}
