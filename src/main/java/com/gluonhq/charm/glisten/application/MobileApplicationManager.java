package com.gluonhq.charm.glisten.application;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.function.Supplier;

/**
 * Manager for MobileApplication.
 * This class is a wrapper around MobileApplication and provides a more convenient API.
 * It is used to replace the deprecated MobileApplication class.
 */
public class MobileApplicationManager {
    private static MobileApplicationManager instance;
    private final MobileApplication mobileApplication;
    private Scene scene;

    private MobileApplicationManager() {
        // Create a new instance of MobileApplication using reflection
        try {
            // Get the MobileApplication class
            Class<?> mobileAppClass = Class.forName("com.gluonhq.charm.glisten.application.MobileApplication");
            
            // Get the getInstance method
            java.lang.reflect.Method getInstanceMethod = mobileAppClass.getMethod("getInstance");
            
            // Call the getInstance method to get the MobileApplication instance
            this.mobileApplication = (MobileApplication) getInstanceMethod.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create MobileApplicationManager", e);
        }
    }

    /**
     * Initializes the MobileApplicationManager.
     * This method should be called once during application startup.
     * 
     * @return the MobileApplicationManager instance
     */
    public static MobileApplicationManager initialize() {
        if (instance == null) {
            instance = new MobileApplicationManager();
        }
        return instance;
    }

    /**
     * Gets the singleton instance of the MobileApplicationManager.
     * 
     * @return the MobileApplicationManager instance
     */
    public static MobileApplicationManager getInstance() {
        if (instance == null) {
            instance = new MobileApplicationManager();
        }
        return instance;
    }

    /**
     * Starts the application.
     * This method should be called from the Application.start method.
     * 
     * @param primaryStage the primary stage
     */
    public void start(Stage primaryStage) {
        // Store the scene from the primaryStage
        this.scene = primaryStage.getScene();
        
        // If the scene is null, create a new one
        if (this.scene == null) {
            // Create a new BorderPane as the root node
            BorderPane root = new BorderPane();
            
            // Create a new Scene with the root node
            this.scene = new Scene(root);
            
            // Set the scene on the primaryStage
            primaryStage.setScene(this.scene);
        }
        
        // Show the stage
        primaryStage.show();
    }

    /**
     * Gets the scene.
     * 
     * @return the scene
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Gets the app bar.
     * 
     * @return the app bar
     */
    public AppBar getAppBar() {
        return mobileApplication.getAppBar();
    }

    /**
     * Gets the drawer.
     * 
     * @return the drawer
     */
    public com.gluonhq.charm.glisten.control.NavigationDrawer getDrawer() {
        return mobileApplication.getDrawer();
    }

    /**
     * Adds a view factory.
     * 
     * @param viewName the view name
     * @param viewFactory the view factory
     */
    public void addViewFactory(String viewName, Supplier<View> viewFactory) {
        mobileApplication.addViewFactory(viewName, viewFactory);
    }

    /**
     * Switches to the specified view.
     * 
     * @param viewName the view name
     */
    public void switchView(String viewName) {
        mobileApplication.switchView(viewName);
    }
}