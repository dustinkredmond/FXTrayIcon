package com.dustinredmond.fxtrayicon.animation;

import java.awt.*;

public class Launcher {

    public static void main(String[] args) {
        System.setProperty("apple.awt.UIElement", "false");
        Toolkit.getDefaultToolkit();
        Animation.main(args);
    }

}
