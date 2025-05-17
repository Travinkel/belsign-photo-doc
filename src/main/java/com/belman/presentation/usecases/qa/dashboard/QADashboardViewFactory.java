package com.belman.presentation.usecases.qa.dashboard;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating QADashboardView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the QA dashboard screen.
 */
public class QADashboardViewFactory extends AbstractViewFactory {

    /**
     * Creates a new QADashboardViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public QADashboardViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new QADashboardView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new QADashboardView();
    }
}