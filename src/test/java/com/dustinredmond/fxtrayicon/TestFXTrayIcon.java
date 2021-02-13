package com.dustinredmond.fxtrayicon;

/*
 * Copyright (c) 2021 Dustin K. Redmond & contributors
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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Serves as a basic smoke test for FXTrayIcon and associated
 * helper classes.
 */
public class TestFXTrayIcon extends Application {

    /**
     * Entry point for runnable tests.
     * Determines if we're running the tests in a headless
     * environment or an environment without desktop support
     * and runs the appropriate tests.
     */
    @Test
    public void runTestOnFXApplicationThread() {
        if (!Desktop.isDesktopSupported()) {
            System.err.println("Tests unable to be run on headless environment.");
            return;
        }
        if (System.getenv("CI") != null) {
            System.err.println("Tests unable to be run on headless CI platform.");
            return;
        }
        Application.launch(TestFXTrayIcon.class, (String) null);
    }

    @Override
    public void start(Stage stage) {
        testShouldConvertSuccessful();
        testShouldConvertFail();
        testTrayIconSupported();
        testNotNullTestResource();
        testInitialization();
        Platform.exit();
    }

    /**
     * Test for making sure that we're able to 'convert' a
     * JavaFX MenuItem into an AWT MenuItem via AWTUtils.convertFromJavaFX()
     */
    public void testShouldConvertSuccessful() {
        MenuItem fxItem = new MenuItem("SomeText");
        fxItem.setDisable(true);
        fxItem.setOnAction(e -> {/* ignored */});

        java.awt.MenuItem awtItem = AWTUtils.convertFromJavaFX(fxItem);
        assertEquals(fxItem.getText(), awtItem.getLabel());
        assertEquals(fxItem.isDisable(), !awtItem.isEnabled());
        assertEquals(1, awtItem.getActionListeners().length);
    }

    /**
     * Test that an Exception is thrown when AWTUtils cannot
     * translate and AWT MenuItem behavior over to JavaFX MenuItem
     */
    public void testShouldConvertFail() {
        MenuItem fxItem = new MenuItem();
        fxItem.setGraphic(new Label());
        try {
            //noinspection unused
            java.awt.MenuItem awtItem = AWTUtils.convertFromJavaFX(fxItem);
            fail("Should not be able to assign graphic in AWTUtils");
        } catch (Exception ignored) { /* should always reach here */ }
    }

    /**
     * Sanity test to make sure that FXTrayIcon.isSupported() does not
     * give a different result than java.awt.SystemTray.isSupported()
     */
    public void testTrayIconSupported() {
        assertEquals(SystemTray.isSupported(), FXTrayIcon.isSupported());
    }

    /**
     * Make sure that our test icon is not null. If this fails, other
     * tests will not run successfully.
     */
    public void testNotNullTestResource() {
        try {
            assertNotNull(getClass().getResource(TEST_ICON));
        } catch (Exception e) {
            fail("Error retrieving resources (FXTrayIcon graphic) for unit tests");
        }
    }

    /**
     * Stupid sanity test, if this fails for any reason, we have issues.
     */
    public void testInitialization() {
        if (FXTrayIcon.isSupported()) {
            FXTrayIcon icon = new FXTrayIcon(new Stage(), getClass().getResource(TEST_ICON));
            assertNotNull(icon);
        }
    }

    private static final String TEST_ICON = "icons8-link-64.png";

}
