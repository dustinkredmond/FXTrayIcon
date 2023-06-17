package com.dustinredmond.fxtrayicon;

public class IconScale {

    public IconScale(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public IconScale(int sizeWH) {
        this.width = sizeWH;
        this.height = sizeWH;
    }

    private final int width;
    private final int height;


    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
