package com.belman.backbone.core.lifecycle;

/**
 * Interface for Views and ViewModels that want lifecycle hooks.
 */
public interface ViewLifecycle {
    default void onShow() {}
    default void onHide() {}
}
