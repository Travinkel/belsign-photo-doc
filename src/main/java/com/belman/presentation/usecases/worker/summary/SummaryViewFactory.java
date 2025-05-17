package com.belman.presentation.usecases.worker.summary;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating SummaryView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the photo summary screen.
 */
public class SummaryViewFactory extends AbstractViewFactory {

    /**
     * Creates a new SummaryViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public SummaryViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new SummaryView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new SummaryView();
    }
}