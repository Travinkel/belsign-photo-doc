package com.belman.ui.usecases.qa.dashboard;

import com.belman.ui.core.AbstractViewFactory;
import com.belman.ui.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating QADashboardView instances.
 * This class is part of the Factory Method pattern for view creation.
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