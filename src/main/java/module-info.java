/**
 * FXTrayIcon java 9+ JPMS compatible
 */
module com.dustinredmond.fxtrayicon{
    requires javafx.controls;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.graphics;


    exports com.dustinredmond.fxtrayicon;
}
