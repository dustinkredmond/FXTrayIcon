package com.dustinredmond.fxtrayicon;

public class CheckIsSupportedByPlatform {
    public static void main(String[] args) {
        // Known working platforms, ensure that isSupported returns
        // true always for these platforms.
        assert !isWindows() || FXTrayIcon.isSupported();
        assert !isMacOS() || FXTrayIcon.isSupported();
        System.out.println("Checks successful!");
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    private static boolean isMacOS() {
        final String os = System.getProperty("os.name");
        return os.contains("mac") || os.contains("darwin");
    }
}
