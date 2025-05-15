package com.belman.ui.core;

import com.gluonhq.charm.glisten.mvc.View;

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
    View createView();
}