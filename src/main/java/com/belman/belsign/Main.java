package com.belman.belsign;

import com.belman.belsign.infrastructure.navigation.Router;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 400, 600);

        Router.getInstance().initialize(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("BelSign");
        primaryStage.show();

        Router.getInstance().navigate("splash");
    }
    public static void main(String[] args) {
        launch(args);
    }
}