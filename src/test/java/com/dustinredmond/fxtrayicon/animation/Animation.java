package com.dustinredmond.fxtrayicon.animation;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.sun.javafx.iio.ImageLoader;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;

public class Animation extends Application {

    private FXTrayIcon fxti;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Animation");
        this.primaryStage = primaryStage;
        makeForm();
        setControlActions();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;
    private final double width = 300;
    private final double height = 300;
    private TextArea taInfo;
    private Button btnAction;
    private VBox vbox;
    private String filePath = "com/dustinredmond/fxtrayicon/animate/%s/%s.png";
    private String iconPath = "com/dustinredmond/fxtrayicon/animate/%s/Tray.png";
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnStart;
    private Button btnStop;
    private Button btnPause;
    private Button btnReset;
    private Scene scene;
    private HBox boxButtons;

    private LinkedList<File> animationFiles(int max, String folder) {
        LinkedList<File> fileImages = new LinkedList<>();
        for (int x = 1; x <= max; x++) {
            String fnum = (x < 10) ? "0" + x : String.valueOf(x);
            File file = getResourceFile(String.format(filePath, folder, fnum));
            fileImages.addLast(file);
        }
        return fileImages;
    }

    private void makeForm() {
        btnOne = new Button("Example One (White)");
        btnTwo = new Button("Example Two (Black)");
        btnThree = new Button("Example Three (Different)");
        btnOne.setPrefWidth(200);
        btnTwo.setPrefWidth(200);
        btnThree.setPrefWidth(200);
        btnOne.setOnAction(e -> startOne());
        btnTwo.setOnAction(e -> startTwo());
        btnThree.setOnAction(e -> startThree());
        btnStart = new Button("Start");
        btnStop = new Button("Stop");
        btnPause = new Button("Pause");
        btnReset = new Button("Reset Icon");
        btnStart.setOnAction(e->startAnimation());
        btnStop.setOnAction(e->stopAnimation());
        btnPause.setOnAction(e->pauseAnimation());
        btnReset.setOnAction(e->setDefaultIcon());
        btnStart.setPrefWidth(55);
        btnStop.setPrefWidth(55);
        btnPause.setPrefWidth(55);
        btnReset.setPrefWidth(95);
        boxButtons = new HBox(15,btnStart, btnStop, btnPause, btnReset);
        boxButtons.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(15,btnOne, btnTwo, btnThree, boxButtons);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);
        scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setControlActions() {
    }

    private void startOne() {
        File icon = getResourceFile(String.format(iconPath,"One"));
        LinkedList<File> fileList = animationFiles(48, "One");
        if(fxti == null) {
            startTrayIcon(fileList, icon,100);
        }
        else {
            fxti.stop();
            fxti.newAnimation(fileList ,100, false);
            fxti.setGraphic(icon,24,24);
        }
    }

    private void startTwo() {
        File icon = getResourceFile(String.format(iconPath,"Two"));
        LinkedList<File> fileList = animationFiles(48, "Two");
        if(fxti == null) {
            startTrayIcon(fileList, icon,100);
        }
        else {
            fxti.stop();
            fxti.newAnimation(fileList ,100, false);
            fxti.setGraphic(icon,24,24);
        }
    }

    private void startThree() {
        File icon = getResourceFile(String.format(iconPath,"Three"));
        LinkedList<File> fileList = animationFiles(41, "Three");
        if(fxti == null) {
            startTrayIcon(fileList, icon, 150);
        }
        else {
            fxti.stop();
            fxti.newAnimation(fileList ,150, false);
            if(icon.exists())
                fxti.setGraphic(icon,24,24);
            else {
                System.out.println("File does not exist");
            }
        }
    }


    private void startTrayIcon(LinkedList<File> fileList, File icon, int frameRate) {
        fxti = new FXTrayIcon.Builder(primaryStage, icon)
                .animate(fileList, frameRate, false)
                .menuItem("Start", e -> startAnimation())
                .menuItem("Stop", e -> stopAnimation())
                .separator()
                .addExitMenuItem()
                .show()
                .build();
    }

    private void startAnimation() {
        if(fxti != null) {
            fxti.playFromStart();
        }
    }

    private void stopAnimation() {
        if(fxti != null) {
            fxti.stop();
        }
    }

    private void pauseAnimation() {
        if(fxti != null) {
            fxti.pause();
        }
    }

    private void setDefaultIcon() {
        if(fxti != null) {
            fxti.resetIcon();
        }
    }

    private File getResourceFile(String resourcePath) {
        ClassLoader classLoader = ImageLoader.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(resourcePath);

        if (resourceUrl != null) {
            return new File(resourceUrl.getFile());
        }
        else {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
    }
}
