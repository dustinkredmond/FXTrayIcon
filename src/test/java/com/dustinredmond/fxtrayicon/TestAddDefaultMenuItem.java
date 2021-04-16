package com.dustinredmond.fxtrayicon;

/*
 *  Copyright 2021  Dustin K. Redmond
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import javafx.application.Application;
import javafx.stage.Stage;

public class TestAddDefaultMenuItem extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("My App");

        FXTrayIcon icon = new FXTrayIcon(primaryStage, getClass().getResource("icons8-link-64.png"));
        icon.addTitleItem(true);
        icon.addExitItem(true);
        icon.show();
    }

    public static void main(String[] args) {
        Application.launch(TestAddDefaultMenuItem.class, args);
    }
}
