package com.dustinredmond.fxtrayicon.issue71;

import java.awt.*;

public class Launcher {

    public static void main(String[] args) {
        System.setProperty("apple.awt.UIElement", "false");
        Toolkit.getDefaultToolkit();
        AddMenuItem.main(args);
    }

}
