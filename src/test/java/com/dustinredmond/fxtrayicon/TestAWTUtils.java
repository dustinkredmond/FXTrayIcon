package com.dustinredmond.fxtrayicon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestAWTUtils extends Application {

    @BeforeClass
    public static void setup() {
        Application.launch(TestAWTUtils.class, (String) null);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Will get a "Toolkit not initialized" exception
        // if JavaFX hasn't yet been started.
        Platform.exit();
    }

    @Test
    public void testShouldConvertSuccessful() {
        MenuItem fxItem = new MenuItem("SomeText");
        fxItem.setDisable(true);
        fxItem.setOnAction(e -> {/* ignored */});

        java.awt.MenuItem awtItem = AWTUtils.convertFromJavaFX(fxItem);
        assertEquals(fxItem.getText(), awtItem.getLabel());
        assertEquals(fxItem.isDisable(), !awtItem.isEnabled());
        assertEquals(1, awtItem.getActionListeners().length);
    }

    @Test
    public void testShouldConvertFail() {
        MenuItem fxItem = new MenuItem();
        fxItem.setGraphic(new Label());
        try {
            java.awt.MenuItem awtItem = AWTUtils.convertFromJavaFX(fxItem);
            fail("Should not be able to assign graphic in AWTUtils");
        } catch (Exception ignored) { /* should always reach here */ }
    }
}
