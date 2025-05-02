package com.belman.backbone.core.lifecycle;

/**
 * Interface for Views and ViewModels that want lifecycle hooks.
 */
public interface ControllerLifecycle {
    default void onShow() {}
    default void onHide() {}
}
