package com.belman.presentation.usecases.worker.photocube;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating PhotoCubeView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the photo cube screen.
 */
public class PhotoCubeViewFactory extends AbstractViewFactory {

    /**
     * Creates a new PhotoCubeViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public PhotoCubeViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new PhotoCubeView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new PhotoCubeView();
    }
}