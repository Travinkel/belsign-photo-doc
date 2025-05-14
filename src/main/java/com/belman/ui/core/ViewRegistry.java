package com.belman.ui.core;

import javafx.scene.Parent;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for view factories.
 * This is part of the Factory Method pattern for view creation.
 */
public class ViewRegistry {
    private static ViewRegistry instance;
    private final Map<String, ViewFactory> viewFactories = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ViewRegistry() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the singleton instance of the ViewRegistry.
     *
     * @return the singleton instance
     */
    public static ViewRegistry getInstance() {
        if (instance == null) {
            instance = new ViewRegistry();
        }
        return instance;
    }

    /**
     * Registers a view factory with the registry.
     *
     * @param viewId  the view ID
     * @param factory the view factory
     */
    public void registerView(String viewId, ViewFactory factory) {
        viewFactories.put(viewId, factory);
    }

    /**
     * Creates a view using the registered factory.
     *
     * @param viewId the view ID
     * @return the created view
     * @throws IllegalArgumentException if no factory is registered for the view ID
     */
    public Parent createView(String viewId) {
        ViewFactory factory = viewFactories.get(viewId);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for view ID: " + viewId);
        }
        return factory.createView();
    }
}