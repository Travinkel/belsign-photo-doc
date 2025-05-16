package com.belman.ui.usecases.splash;

import com.belman.ui.core.AbstractViewFactory;
import com.belman.ui.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating SplashView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class SplashViewFactory extends AbstractViewFactory {
    /**
     * Creates a new SplashViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public SplashViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new SplashView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new SplashView();
    }
}