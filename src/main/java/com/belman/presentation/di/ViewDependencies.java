package com.belman.presentation.di;

import com.belman.presentation.core.ViewRegistry;
import com.belman.presentation.navigation.RoleBasedNavigationService;

public class ViewDependencies {

    private final RoleBasedNavigationService navigationService;
    private final ViewRegistry viewRegistry;

    public ViewDependencies(RoleBasedNavigationService navigationService, ViewRegistry viewRegistry) {
        this.navigationService = navigationService;
        this.viewRegistry = viewRegistry;
    }

    public RoleBasedNavigationService getNavigationService() {
        return navigationService;
    }

    public ViewRegistry getViewRegistry() {
        return viewRegistry;
    }
}
