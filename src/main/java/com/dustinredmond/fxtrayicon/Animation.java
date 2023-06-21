package com.dustinredmond.fxtrayicon;

/*
 * Copyright (c) 2022 Dustin K. Redmond & contributors
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.awt.*;
import java.util.LinkedList;

/**
 * This class is documented in the FXTrayIcon classes Builder class.
 * The commands are documented in the FXTrayIcon class
 */
class Animation {

    private final LinkedList<Image> imageList;
    private final int frameRateMS;
    private final FXTrayIcon trayIcon;
    private final Timeline timeline;

    private Timeline getTimeline() {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(frameRateMS), e -> updateImage()));
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        return timeline;
    }

    private void updateImage() {
        Image image = imageList.removeFirst();
        trayIcon.setAnimationFrame(image);
        imageList.addLast(image);
    }

    Animation(FXTrayIcon trayIcon, LinkedList<Image> imageList, int frameRateMS) {
        this.imageList = imageList;
        this.frameRateMS = frameRateMS;
        this.trayIcon = trayIcon;
        this.timeline = this.getTimeline();
    }

    public Timeline timeline() {
        return timeline;
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
