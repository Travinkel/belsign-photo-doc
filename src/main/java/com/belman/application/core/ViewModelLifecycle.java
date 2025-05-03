package com.belman.application.core;

/**
 * Interface for Views and ViewModels that want lifecycle hooks.
 */
public interface ViewModelLifecycle {
    default void onShow() {}
    default void onHide() {}
}
