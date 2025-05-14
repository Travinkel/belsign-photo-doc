package com.belman.ui.usecases.common.main;

import com.belman.ui.core.ViewFactory;
import javafx.scene.Parent;

/**
 * Factory for creating MainView instances.
 * This is part of the Factory Method pattern for view creation.
 */
public class MainViewFactory implements ViewFactory {
    /**
     * Creates a MainView.
     *
     * @return the created MainView
     */
    @Override
    public Parent createView() {
        return new MainView();
    }
}