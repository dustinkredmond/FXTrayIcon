package com.dustinredmond.fxtrayicon;

import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.junit.Test;

import java.awt.SystemTray;
import static org.junit.Assert.*;

public class TestFXTrayIcon {

    @Test
    public void testTrayIconSupported() {
        assertEquals(SystemTray.isSupported(), FXTrayIcon.isSupported());
    }

    @Test
    public void testNotNullTestResource() {
        try {
            assertNotNull(getClass().getResource(TEST_ICON));
        } catch (Exception e) {
            fail("Error retrieving resources (FXTrayIcon graphic) for unit tests");
        }
    }

    @Test
    public void testInitialization() {
        if (FXTrayIcon.isSupported()) {
            FXTrayIcon icon = new FXTrayIcon(new Stage(), getClass().getResource(TEST_ICON));
            assertNotNull(icon);
        }
    }

    @Test
    public void testShow() {
        if (FXTrayIcon.isSupported()) {
            FXTrayIcon icon = new FXTrayIcon(new Stage(), getClass().getResource(TEST_ICON));
            icon.show();
            assertTrue(icon.isShowing());
            icon.hide();
        }
    }

    @Test
    public void testPopupMenuIsEmptyOnShowMinimal() {
        if (FXTrayIcon.isSupported()) {
            FXTrayIcon icon = new FXTrayIcon(new Stage(), getClass().getResource(TEST_ICON));
            icon.showMinimal();
            assertNull(icon.getMenuItem(0));
            icon.hide();
        }
    }

    @Test
    public void testIconAddIncrementsMenuItemCount() {
        if (FXTrayIcon.isSupported()) {
            FXTrayIcon icon = new FXTrayIcon(new Stage(), getClass().getResource(TEST_ICON));
            icon.showMinimal();
            int iconMenuItemCount = 0;
            while (icon.getMenuItem(iconMenuItemCount) != null) {
                iconMenuItemCount++;
            }

            icon.addMenuItem(new MenuItem("TestItem"));

            int countWithAddedItem = 0;
            while (icon.getMenuItem(countWithAddedItem) != null) {
                countWithAddedItem++;
            }

            assertEquals(iconMenuItemCount + 1, countWithAddedItem);
            icon.hide();
        }
    }

    @Test
    public void testIconRemoveDecrementsMenuItemCount() {
        if (FXTrayIcon.isSupported()) {
            FXTrayIcon icon = new FXTrayIcon(new Stage(), getClass().getResource(TEST_ICON));
            icon.show();

            int initialCount = 0;
            while (icon.getMenuItem(initialCount) != null) {
                initialCount++;
            }

            icon.removeMenuItem(0);

            int afterRemovalCount = 0;
            while (icon.getMenuItem(afterRemovalCount) != null) {
                afterRemovalCount++;
            }

            assertEquals(initialCount - 1, afterRemovalCount);
            icon.hide();
        }
    }

    private static final String TEST_ICON = "icons8-link-64.png";
}
