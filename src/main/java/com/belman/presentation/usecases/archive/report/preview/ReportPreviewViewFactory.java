package com.belman.presentation.usecases.archive.report.preview;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating ReportPreviewView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class ReportPreviewViewFactory extends AbstractViewFactory {
    /**
     * Creates a new ReportPreviewViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public ReportPreviewViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new ReportPreviewView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new ReportPreviewView();
    }
}