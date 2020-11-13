# FXTrayIcon

Library intended for use in JavaFX applications that makes adding a System Tray icon easier.
The FXTrayIcon class translates JavaFX MenuItems into AWT MenuItems, so no need to import anything
from the AWT package. This is particularly useful because JavaFX does not provide a way to add a 
System Tray icon in their API. 

Check out the [sample application](./src/test/java/com/dustinredmond/fxtrayicon/IconTest.java) in the test directory for an example of how this works. 

---

### How do I add to my project? 

The project is available as a Maven dependency. Add the following to POM.xml

```
<dependency>
  <groupId>com.dustinredmond.fxtrayicon</groupId>
  <artifactId>FXTrayIcon</artifactId>
  <version>2.1-RELEASE</version>
</dependency>
```

---

### Screenshots

![FXTrayIcon example](./img/fxtrayicon-1.png)

An example of FXTrayIcon running on Windows 10, of course, you choose your own icon file.
Here we used a link icon from [Icons8](https://www.icons8.com), they provide thousands of amazing
 icons for developers, both free (with an attribution) and paid.


![FXTrayIcon menu example](./img/fxtrayicon-2.png)

An example of FXTrayIcon's custom context menu, built using JavaFX MenuItems.
Surprise, surprise, JavaFX MenuItems get translated into AWT MenuItems by FXTrayIcon,
so there's no need to use those!

---

### Supported operating systems

So long as the operating system supports tray icons, FXTrayIcon should work.
Call `java.awt.SystemTray.isSupported()` to figure this out for yourself.
FXTrayIcon will also throw an `UnsupportedOperationException` if tray icons
are unavailable on the current desktop environment.