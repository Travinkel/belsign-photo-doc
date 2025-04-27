package com.belman.belsign.infrastructure.navigation;

public interface ViewLifecycle {
    default void onShow() {}
    default void onHide() {}
}
