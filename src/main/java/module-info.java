/**
 * FXTrayIcon java 9+ JPMS compatible
 */
module com.dustinredmond.fxtrayicon{
    requires javafx.controls;
    requires java.desktop;
    requires javafx.swing;

    exports com.dustinredmond.fxtrayicon;
}
