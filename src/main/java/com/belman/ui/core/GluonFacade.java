package com.belman.ui.core;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Supplier;

/**
 * Facade for Gluon Mobile functionality.
 * This class provides a clean API for the rest of the codebase to use,
 * while keeping the MobileApplicationManager class in the presentation layer.
 */
public class GluonFacade {
    private static final GluonFacade instance = new GluonFacade();

    private GluonFacade() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Gets the singleton instance of the GluonFacade.
     *
     * @return the GluonFacade instance
     */
    public static GluonFacade getInstance() {
        return instance;
    }

    /**
     * Initializes the Gluon Mobile application.
     *
     * @return the GluonFacade instance
     */
    public static GluonFacade initialize() {
        MobileApplicationManager.initialize();
        return instance;
    }

    /**
     * Starts the Gluon Mobile application.
     *
     * @param primaryStage the primary stage
     */
    public void start(Stage primaryStage) {
        MobileApplicationManager.getInstance().start(primaryStage);
    }

    /**
     * Gets the scene.
     *
     * @return the scene
     */
    public Scene getScene() {
        return MobileApplicationManager.getInstance().getScene();
    }

    /**
     * Gets the app bar.
     *
     * @return the app bar
     */
    public AppBar getAppBar() {
        return MobileApplicationManager.getInstance().getAppBar();
    }

    /**
     * Gets the drawer.
     *
     * @return the drawer
     */
    public com.gluonhq.charm.glisten.control.NavigationDrawer getDrawer() {
        return MobileApplicationManager.getInstance().getDrawer();
    }

    /**
     * Adds a view factory.
     *
     * @param viewName    the view name
     * @param viewFactory the view factory
     */
    public void addViewFactory(String viewName, Supplier<View> viewFactory) {
        MobileApplicationManager.getInstance().addViewFactory(viewName, viewFactory);
    }

    /**
     * Switches to the specified view.
     *
     * @param viewName the view name
     */
    public void switchView(String viewName) {
        MobileApplicationManager.getInstance().switchView(viewName);
    }
}