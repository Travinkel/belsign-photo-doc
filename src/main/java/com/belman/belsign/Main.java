package com.belman.belsign;

import com.belman.belsign.framework.athomefx.navigation.Router;
import com.belman.belsign.presentation.views.splash.SplashView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {

        Router.setPrimaryStage(primaryStage);
        primaryStage.setTitle("BelSign");
        Router.navigateTo(SplashView.class);

    }
    public static void main(String[] args) {
        launch(args);
    }
}