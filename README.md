# FXTrayIcon

Library intended for use in JavaFX applications that makes adding a System Tray icon easier.
Using FXTrayIcon uses the AWT TrayIcon in the background, but makes managing your code much easier and
less messy.

Check out the test application in the test directory for an example of how this works. I plan to abstract away
the AWT MenuItem in a future release, this way a user will be able to "add" JavaFX MenuItems to the TrayIcon and
we'll translate those to an AWT MenuItem in the background.

***Note: this library will be totally obsolete once JavaFX finally implements their native version of the 
SystemTray Icon, but since we're stuck with AWT icons, this at least helps.**
