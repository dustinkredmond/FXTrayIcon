## FXTrayIcon Builder Class
### This document outlines the various ways you can instantiate FXTrayIcon using its Builder class

Perhaps the most convenient way to instantiate FXTray Icon is through the Builder class. With the Builder,
you can set any option, add menuItems and even subMenus to your tray icon, all in one line of code, and
FXTrayIcon will build the menu in the order in which you add the items into your build sentence.

Here are the constructor options for the Builder
```Java
FXTrayIcon trayIcon = new FXTrayIcon.Builder(primaryStage, URL iconImagePath)
FXTrayIcon trayIcon = new FXTrayIcon.Builder(primaryStage, java.io.File iconImageFile)
FXTrayIcon trayIcon = new FXTrayIcon.Builder(primaryStage, javafx.scene.image.Image iconJavaFXImage)
FXTrayIcon trayIcon = new FXTrayIcon.Builder(primaryStage, java.awt.Image iconJavaAWTImage)
```

OR any of the above options with the addition of the icon size. For example:
```java
FXTrayIcon trayIcon = new FXTrayIcon.Builder(primaryStage, URL iconImagePath, int width, int height)
```
After you state the constructor, you have these options available to build your tray icon:

Add menuItems dynamically without needing to pre-build them, by passing in the label and the
event that the menuItem will execute:
```java
new FXTrayIcon.Builder(stage,icon).menuItem("My Menu", e -> myMenuMethod()).build();
```
OR, you can pre-build them and add them into the Builder like this
```java
MenuItem myMenuItem = new MenuItem;
myMenuItem.setOnAction(e-> myMenuMethod());
new FXTrayIcon.Builder(stage,icon).menuItem(myMenuItem).build();
```

You can also include multiple menuItems in one statement like this

```java
new FXTrayIcon.Builder(stage,icon)
    .menuItem("My Menu 1", e -> myMenuMethod1())
    .menuItem("My Menu 2", e -> myMenuMethod2())
    .build();
```
OR pre-built
```java
MenuItem myMenuItem1 = new MenuItem;
myMenuItem1.setOnAction(e-> myMenuMethod1());

MenuItem myMenuItem2 = new MenuItem;
myMenuItem2.setOnAction(e-> myMenuMethod2());

new FXTrayIcon.Builder(stage,icon).menuItems(myMenuItem1, myMenuItem2).build();
```

Or you can even pre-build an entire Menu with MenuItems, then add that in as a branched submenu:

```java
Menu menu = new Menu("My Sub Menu");

MenuItem myMenuItem1 = new MenuItem;
myMenuItem1.setOnAction(e-> myMenuMethod1());

MenuItem myMenuItem2 = new MenuItem;
myMenuItem2.setOnAction(e-> myMenuMethod2());

menu.getItems().addAll(myMenuItem1, myMenuItem2);

new FXTrayIcon.Builder(stage,icon).menu(menu).build();
```

You can also add a branched sub-menu dynamically by calling the menu Builder method, giving
the sub-menu a label then passing in MenuItem objects like this

```java
MenuItem myMenuItem1 = new MenuItem;
myMenuItem1.setOnAction(e-> myMenuMethod1());

MenuItem myMenuItem2 = new MenuItem;
myMenuItem2.setOnAction(e-> myMenuMethod2());

new FXTrayIcon.Builder(stage,icon).menu("My Sub Menu", menuItem1, menuItem2).build();
```

Add menu separator lines like this and FXTray Icon will place the separators (and all
objects you include), in the order that you have them in your build sentence.

```java
new FXTrayIcon.Builder(stage,icon)
    .menuItem("My Menu 1", e -> myMenuMethod1())
    .menuItem("My Menu 2", e -> myMenuMethod2())
    .separator()
    .menuItem("My Menu 3", e -> myMenuMethod3())
    .separator()
    .menuItem("My Menu 4", e -> myMenuMethod4())
    .build();
```

Add a tooltip that will display when the mouse pointer is hovered over the trayIcon

```java
new FXTrayIcon.Builder(stage,icon)
    .menuItem("My Menu 1", e -> myMenuMethod1())
    .tooltip("This is your Tray Icon")
    .build();
```

FXTray Icon has something called a TitleItem, that is simply a MenuItem with the label
set to the Applications Title String, that will call the Stage that you passed into the
main constructor and bring it into view. When you use this option, that menuItem will
always be at the top of your menu list.
```java
new FXTrayIcon.Builder(stage,icon).addTitleItem(true).build();
```

Alternatively, you can do the same thing, but assign the actual AppTitle string with
the call
```java
new FXTrayIcon.Builder(stage,icon).applicationTitle("My Application Title").build();
```

Next, we have the onAction option. This is an option that will run an event that you pass
into the argument and will run differently depending on the operating system. Under Windows,
a simple left click on the tray Icon will invoke the event. On a Mac, it will be necessary
to click both the left and right mouse buttons at the same time.
```java
new FXTrayIcon.Builder(stage,icon).onAction(e -> myMethod()).build();
```

Then we have the Exit menu item. This is a special MenuItem that will ALWAYS be placed
at the bottom of the menu list, even if you add or remove items from the list during
runtime. Invoking the menuItem will close the application. There are three ways to use
this option:

With the default label
```java
new FXTrayIcon.Builder(stage,icon).addExitMenuItem().build();
```

With a label of your choosing
```java
new FXTrayIcon.Builder(stage,icon).addExitMenuItem("My Exit Menu Label").build();
```

Or by passing in your own event that will close your app however you decide it needs to
be closed
```java
new FXTrayIcon.Builder(stage,icon).addExitMenuItem("My Exit Label", e -> myExitMethod()).build();
```

Last, we have the show() option, which will simply cause FXTrayIcon to show the icon
immediately after it's built so that you don't have to add call the show method after you
create your build sentence.

So for example, this Builder call will build the trayIcon, and show it all at the same time
```java
new FXTrayIcon.Builder(stage,icon)
    .menuItem("My Menu 1", e -> myMenuMethod1())
    .menuItem("My Menu 2", e -> myMenuMethod2())
    .separator()
    .menuItem("My Menu 3", e -> myMenuMethod3())
    .separator()
    .menuItem("My Menu 4", e -> myMenuMethod4())
    .addExitMenuItem()
    .show()
    .build();
```

Have Fun!
