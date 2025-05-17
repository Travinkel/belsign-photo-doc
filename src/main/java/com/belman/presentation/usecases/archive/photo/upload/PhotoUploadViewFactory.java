package com.belman.presentation.usecases.archive.photo.upload;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating PhotoUploadView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class PhotoUploadViewFactory extends AbstractViewFactory {
    /**
     * Creates a new PhotoUploadViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public PhotoUploadViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new PhotoUploadView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new PhotoUploadView();
    }
}