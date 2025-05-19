package com.belman.presentation.usecases.qa.done;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating QADoneView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the QA done screen.
 */
public class QADoneViewFactory extends AbstractViewFactory {

    /**
     * Creates a new QADoneViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public QADoneViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new QADoneView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new QADoneView();
    }
}