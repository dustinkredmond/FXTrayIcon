# FXTrayIcon

Library intended for use in JavaFX applications that makes adding a System Tray icon easier.
The FXTrayIcon class translates JavaFX MenuItems into AWT MenuItems, so no need to import anything
from the AWT package.

Check out the [sample application](./src/test/java/com/jfxdev/fxtrayicon/IconTest.java) in the test directory for an example of how this works. 

---

### How do I add to my project? 

I'm not going to bother keeping up with compiled JARs or add the repo to Maven central.
Since FXTrayIcon consists of one class file, simply download [FXTrayIcon.java](./src/main/java/com/jfxdev/fxtrayicon/FXTrayIcon.java).

---

***Note: this library will be totally obsolete once JavaFX finally implements their native version of the 
SystemTray Icon, but since we're stuck without an implementation, this at least helps.**
