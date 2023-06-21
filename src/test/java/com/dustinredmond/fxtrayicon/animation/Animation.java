package com.dustinredmond.fxtrayicon.animation;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.sun.javafx.iio.ImageLoader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

public class Animation extends Application {

    private FXTrayIcon fxTrayIcon;
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
            try {
                String fileNumber = (x < 10) ? "0" + x : String.valueOf(x);
                File file = getResourceFile(String.format(filePath, folder, fileNumber));
                Image image = new Image(file.toURI().toURL().toString());
                fileImages.addLast(file);
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return fileImages;
    }

    private LinkedList<Image> animationImages(int max, String folder) {
        LinkedList<Image> fxImages = new LinkedList<>();
        for (int x = 1; x <= max; x++) {
            try {
                String fileNumber = (x < 10) ? "0" + x : String.valueOf(x);
                File file = getResourceFile(String.format(filePath, folder, fileNumber));
                Image image = new Image(file.toURI().toURL().toString());
                fxImages.addLast(image);
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return fxImages;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Animation");
        this.primaryStage = primaryStage;
        makeForm();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;

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
        btnStart.setOnAction(e -> startAnimation());
        btnStop.setOnAction(e -> stopAnimation());
        btnPause.setOnAction(e -> pauseAnimation());
        btnReset.setOnAction(e -> setDefaultIcon());
        btnStart.setPrefWidth(55);
        btnStop.setPrefWidth(55);
        btnPause.setPrefWidth(55);
        btnReset.setPrefWidth(95);
        boxButtons = new HBox(15, btnStart, btnStop, btnPause, btnReset);
        boxButtons.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(15, btnOne, btnTwo, btnThree, boxButtons);
        vbox.setPadding(new Insets(25));
        vbox.setAlignment(Pos.CENTER);
        scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startOne() {
        File icon = getResourceFile(String.format(iconPath, "One"));
        LinkedList<Image> fileList = animationImages(48, "One");
        if (fxTrayIcon == null) {
            startTrayIcon(fileList, icon, 100);
        }
        else {
            fxTrayIcon.stop();
            fxTrayIcon.newAnimation(fileList, 100);
            fxTrayIcon.setGraphic(icon, 24, 24);
        }
    }

    private void startTwo() {
        File icon = getResourceFile(String.format(iconPath, "Two"));
        LinkedList<Image> fileList = animationImages(48, "Two");
        if (fxTrayIcon == null) {
            startTrayIcon(fileList, icon, 100);
        }
        else {
            fxTrayIcon.stop();
            fxTrayIcon.newAnimation(fileList, 100);
            fxTrayIcon.setGraphic(icon, 24, 24);
        }
    }

    private void startThree() {
        File icon = getResourceFile(String.format(iconPath, "Three"));
        LinkedList<Image> fileList = animationImages(41, "Three");
        if (fxTrayIcon == null) {
            startTrayIcon(fileList, icon, 150);
        }
        else {
            fxTrayIcon.stop();
            fxTrayIcon.newAnimation(fileList, 150);
            if (icon.exists())
                fxTrayIcon.setGraphic(icon, 24, 24);
            else {
                System.out.println("File does not exist");
            }
        }
    }


    private void startTrayIcon(LinkedList<File> fileList, File icon, int frameRate, boolean sortList) {
        fxTrayIcon = new FXTrayIcon.Builder(primaryStage, icon)
                .animate(fileList, frameRate, sortList)
                .menuItem("Start", e -> startAnimation())
                .menuItem("Stop", e -> stopAnimation())
                .separator()
                .addExitMenuItem()
                .show()
                .build();
    }

    private void startTrayIcon(LinkedList<Image> imageList, File icon, int frameRate) {
        fxTrayIcon = new FXTrayIcon.Builder(primaryStage, icon)
                .animate(imageList, frameRate)
                .menuItem("Start", e -> startAnimation())
                .menuItem("Stop", e -> stopAnimation())
                .separator()
                .addExitMenuItem()
                .show()
                .build();
    }

    private void startAnimation() {
        if (fxTrayIcon != null) {
            fxTrayIcon.playFromStart();
        }
    }

    private void stopAnimation() {
        if (fxTrayIcon != null) {
            fxTrayIcon.stop();
        }
    }

    private void pauseAnimation() {
        if (fxTrayIcon != null) {
            fxTrayIcon.pause();
        }
    }

    private void setDefaultIcon() {
        if (fxTrayIcon != null) {
            fxTrayIcon.resetIcon();
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
