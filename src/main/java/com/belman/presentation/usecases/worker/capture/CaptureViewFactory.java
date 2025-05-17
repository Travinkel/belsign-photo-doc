package com.belman.presentation.usecases.worker.capture;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating CaptureView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the photo capture screen.
 */
public class CaptureViewFactory extends AbstractViewFactory {

    /**
     * Creates a new CaptureViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public CaptureViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new CaptureView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new CaptureView();
    }
}