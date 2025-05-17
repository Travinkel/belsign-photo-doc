package com.belman.presentation.usecases.worker.completed;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating CompletedView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the completion screen.
 */
public class CompletedViewFactory extends AbstractViewFactory {

    /**
     * Creates a new CompletedViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public CompletedViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new CompletedView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new CompletedView();
    }
}