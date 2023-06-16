package com.dustinredmond.fxtrayicon;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

class Animation {

    private Animation(Builder build) {
        this.trayIcon = build.trayIcon;
        this.timeline = build.timeline;
        this.fixedIcon = build.fixedIcon;
        this.ICON_SIZE_X = build.ICON_SIZE_X;
        this.ICON_SIZE_Y = build.ICON_SIZE_Y;
    }

    private final FXTrayIcon trayIcon;
    private final Timeline timeline;
    private final Image fixedIcon;
    private final int ICON_SIZE_X;
    private final int ICON_SIZE_Y;

    public static class Builder {

        public Builder(FXTrayIcon trayIcon, LinkedList<Image> imageList) {
            this.imageList = imageList;
            this.trayIcon = trayIcon;
        }

        public Builder(FXTrayIcon trayIcon, Image... images) {
            this.imageList = new LinkedList<>(Arrays.asList(images));
            this.trayIcon = trayIcon;
        }

        public Builder(FXTrayIcon trayIcon, LinkedList<File> images, boolean sortFiles) {
            this.imageList = new LinkedList<>();
            if (sortFiles)
                images.sort(Comparator.comparing(File::getName));
            for (File file : images) {
                try {
                    Image image = new Image(file.toURI().toURL().toString());
                    imageList.addLast(image);
                }
                catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            this.trayIcon = trayIcon;
        }

        public Builder(FXTrayIcon trayIcon, boolean sortFiles, File... images) {
            this.imageList = new LinkedList<>();
            LinkedList<File> fileList = new LinkedList<>(Arrays.asList(images));
            if (sortFiles)
                fileList.sort(Comparator.comparing(File::getName));
            for (File file : fileList) {
                try {
                    Image image = new Image(file.toURI().toURL().toString());
                    imageList.addLast(image);
                }
                catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
            this.trayIcon = trayIcon;
        }

        private final LinkedList<Image> imageList;
        private final FXTrayIcon trayIcon;
        private Image fixedIcon;
        private int FRAME_DURATION_MS = 75;
        private int ICON_SIZE_X = 24;
        private int ICON_SIZE_Y = 24;
        private Timeline timeline;

        public Builder fixedIcon(Image icon) {
            this.fixedIcon = icon;
            return this;
        }

        public Builder fixedIcon(File file) {
            try {
                this.fixedIcon = new Image(file.toURI().toURL().toString());
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public Builder iconSize(int size) {
            this.ICON_SIZE_X = size;
            this.ICON_SIZE_Y = size;
            return this;
        }

        public Builder iconSize(int sizeX, int sizeY) {
            this.ICON_SIZE_X = sizeX;
            this.ICON_SIZE_Y = sizeY;
            return this;
        }

        public Builder frameDuration(int durationMS) {
            this.FRAME_DURATION_MS = durationMS;
            return this;
        }

        private Timeline getTimeline() {
            Timeline timeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(Duration.millis(FRAME_DURATION_MS), e -> {
                Image image = imageList.removeFirst();
                trayIcon.setGraphic(image, ICON_SIZE_X, ICON_SIZE_Y);
                imageList.addLast(image);
            });
            timeline.getKeyFrames().add(keyFrame);
            timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
            return timeline;
        }

        public Animation build() {
            this.timeline = getTimeline();
            return new Animation(this);
        }
    }

    public void playFromStart() {
        timeline.playFromStart();
    }

    public void play() {
        timeline.play();
    }

    public void pause() {
        timeline.pause();
    }

    public void stop() {
        timeline.stop();
    }

    public void stopReset() {
        timeline.stop();
        Platform.runLater(() -> trayIcon.setGraphic(fixedIcon, ICON_SIZE_X, ICON_SIZE_Y));
    }

    public void setDefault() {
        Platform.runLater(() -> trayIcon.setGraphic(fixedIcon, ICON_SIZE_X, ICON_SIZE_Y));
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public boolean isRunning() {
        return timeline.getStatus().equals(javafx.animation.Animation.Status.RUNNING);
    }

    public boolean isPaused() {
        return timeline.getStatus().equals(javafx.animation.Animation.Status.PAUSED);
    }

    public boolean isStopped() {
        return timeline.getStatus().equals(javafx.animation.Animation.Status.STOPPED);
    }

}
