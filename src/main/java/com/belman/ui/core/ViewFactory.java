package com.belman.ui.core;

import javafx.scene.Parent;

/**
 * Interface for creating views.
 * This is part of the Factory Method pattern for view creation.
 */
public interface ViewFactory {
    /**
     * Creates a view.
     *
     * @return the created view
     */
    Parent createView();
}