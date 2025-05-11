package com.belman.ui.core;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * A transition that fades out the current view and fades in the new view.
 * <p>
 * This transition is simple and effective, and works well on both desktop
 * and mobile platforms.
 */
public class FadeViewTransition extends AbstractViewTransition {
    private static final String NAME = "Fade";
    private Consumer<View> onStartCallback;
    private Consumer<View> onCompleteCallback;

    /**
     * Creates a new FadeViewTransition with the default duration.
     */
    public FadeViewTransition() {
        super(NAME);
    }

    /**
     * Creates a new FadeViewTransition with the specified duration.
     *
     * @param duration the duration of the transition
     */
    public FadeViewTransition(Duration duration) {
        super(NAME);
        setDuration(duration);
    }

    @Override
    public void performTransition(View currentView, View newView, Runnable onFinished) {
        if (newView == null) {
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        // Fire the onStart callback
        fireOnStart(newView);

        // Get the parent container
        StackPane container = getContainer(currentView, newView);
        if (container == null) {
            // If there's no container, just show the new view
            if (onFinished != null) {
                onFinished.run();
            }
            fireOnComplete(newView);
            return;
        }

        // Set initial opacity
        newView.setOpacity(0);

        // Add the new view to the container if it's not already there
        if (!container.getChildren().contains(newView)) {
            container.getChildren().add(newView);
        }

        // Create the animation
        SequentialTransition transition;

        if (currentView != null) {
            // Fade out the current view and fade in the new view
            FadeTransition fadeOut = new FadeTransition(getDuration(), currentView);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            FadeTransition fadeIn = new FadeTransition(getDuration(), newView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            transition = new SequentialTransition(
                    fadeOut,
                    fadeIn
            );
        } else {
            // Just fade in the new view
            FadeTransition fadeIn = new FadeTransition(getDuration(), newView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            transition = new SequentialTransition(fadeIn);
        }

        // Set up the animation completion handler
        transition.setOnFinished(event -> {
            // Remove the old view from the container
            if (currentView != null) {
                container.getChildren().remove(currentView);
            }

            // Fire the onComplete callback
            fireOnComplete(newView);

            // Run the onFinished callback
            if (onFinished != null) {
                onFinished.run();
            }
        });

        // Start the animation
        transition.play();
    }

    @Override
    public ViewTransition createReverse() {
        FadeViewTransition reverse = new FadeViewTransition(getDuration());
        reverse.setOnStart(onStartCallback);
        reverse.setOnComplete(onCompleteCallback);
        return reverse;
    }

    @Override
    public ViewTransition setOnStart(Consumer<View> onStart) {
        this.onStartCallback = onStart;
        return super.setOnStart(onStart);
    }

    @Override
    public ViewTransition setOnComplete(Consumer<View> onComplete) {
        this.onCompleteCallback = onComplete;
        return super.setOnComplete(onComplete);
    }

    /**
     * Gets the container for the views.
     *
     * @param currentView the current view
     * @param newView     the new view
     * @return the container, or null if it can't be determined
     */
    private StackPane getContainer(View currentView, View newView) {
        // Try to get the container from the current view
        if (currentView != null && currentView.getParent() instanceof StackPane) {
            return (StackPane) currentView.getParent();
        }

        // Try to get the container from the new view
        if (newView != null && newView.getParent() instanceof StackPane) {
            return (StackPane) newView.getParent();
        }

        return null;
    }
}