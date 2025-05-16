package com.belman.ui.usecases.common.main;

import com.belman.ui.core.AbstractViewFactory;
import com.belman.ui.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating MainView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class MainViewFactory extends AbstractViewFactory {
    /**
     * Creates a new MainViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public MainViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new MainView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new MainView();
    }
}