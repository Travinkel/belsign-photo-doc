package com.belman.presentation.usecases.admin.settings;

import com.belman.presentation.core.ViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating SystemSettingsView instances.
 * This factory is used by the ViewStackManager to create views on demand.
 */
public final class SystemSettingsViewFactory implements ViewFactory {
    private final ViewDependencies viewDependencies;

    /**
     * Creates a new SystemSettingsViewFactory.
     *
     * @param viewDependencies the view dependencies
     */
    public SystemSettingsViewFactory(ViewDependencies viewDependencies) {
        this.viewDependencies = viewDependencies;
    }

    /**
     * Creates a new SystemSettingsView.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new SystemSettingsView();
    }
}
