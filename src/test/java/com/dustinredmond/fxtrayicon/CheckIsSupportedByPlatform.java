package com.dustinredmond.fxtrayicon;

public class CheckIsSupportedByPlatform {
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    public static boolean isMacOS() {
        final String os = System.getProperty("os.name");
        return os.contains("mac") || os.contains("darwin");
    }
}
