package com.belman.presentation.core;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * A transition that slides the new view in from a specified direction.
 * <p>
 * This transition is commonly used in mobile applications to indicate
 * navigation direction (e.g., sliding from right to left when navigating
 * forward, and from left to right when navigating backward).
 */
public class SlideViewTransition extends AbstractViewTransition {
    private static final String NAME_PREFIX = "Slide";
    private final SlideDirection direction;
    private Consumer<View> onStartCallback;
    private Consumer<View> onCompleteCallback;

    /**
     * Creates a new SlideViewTransition with the default duration and direction.
     */
    public SlideViewTransition() {
        this(SlideDirection.RIGHT_TO_LEFT);
    }

    /**
     * Creates a new SlideViewTransition with the specified direction and default duration.
     *
     * @param direction the slide direction
     */
    public SlideViewTransition(SlideDirection direction) {
        super(NAME_PREFIX + " " + direction.name());
        this.direction = direction;
    }

    /**
     * Creates a new SlideViewTransition with the specified direction and duration.
     *
     * @param direction the slide direction
     * @param duration  the duration of the transition
     */
    public SlideViewTransition(SlideDirection direction, Duration duration) {
        super(NAME_PREFIX + " " + direction.name());
        this.direction = direction;
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

        // Add the new view to the container if it's not already there
        if (!container.getChildren().contains(newView)) {
            container.getChildren().add(newView);
        }

        // Set initial position of the new view
        double width = container.getWidth();
        double height = container.getHeight();

        switch (direction) {
            case LEFT_TO_RIGHT:
                newView.setTranslateX(-width);
                break;
            case RIGHT_TO_LEFT:
                newView.setTranslateX(width);
                break;
            case TOP_TO_BOTTOM:
                newView.setTranslateY(-height);
                break;
            case BOTTOM_TO_TOP:
                newView.setTranslateY(height);
                break;
        }

        // Create the animation
        Timeline timeline = new Timeline();

        // Add keyframes for the new view
        KeyValue endValue;
        switch (direction) {
            case LEFT_TO_RIGHT:
            case RIGHT_TO_LEFT:
                endValue = new KeyValue(newView.translateXProperty(), 0);
                break;
            case TOP_TO_BOTTOM:
            case BOTTOM_TO_TOP:
                endValue = new KeyValue(newView.translateYProperty(), 0);
                break;
            default:
                throw new IllegalStateException("Unknown direction: " + direction);
        }

        timeline.getKeyFrames().add(new KeyFrame(getDuration(), endValue));

        // If there's a current view, add keyframes for it
        if (currentView != null) {
            KeyValue currentEndValue;
            switch (direction) {
                case LEFT_TO_RIGHT:
                    currentEndValue = new KeyValue(currentView.translateXProperty(), width);
                    break;
                case RIGHT_TO_LEFT:
                    currentEndValue = new KeyValue(currentView.translateXProperty(), -width);
                    break;
                case TOP_TO_BOTTOM:
                    currentEndValue = new KeyValue(currentView.translateYProperty(), height);
                    break;
                case BOTTOM_TO_TOP:
                    currentEndValue = new KeyValue(currentView.translateYProperty(), -height);
                    break;
                default:
                    throw new IllegalStateException("Unknown direction: " + direction);
            }

            timeline.getKeyFrames().add(new KeyFrame(getDuration(), currentEndValue));
        }

        // Set up the animation completion handler
        timeline.setOnFinished(event -> {
            // Remove the old view from the container
            if (currentView != null) {
                container.getChildren().remove(currentView);
                // Reset the translation of the current view
                currentView.setTranslateX(0);
                currentView.setTranslateY(0);
            }

            // Fire the onComplete callback
            fireOnComplete(newView);

            // Run the onFinished callback
            if (onFinished != null) {
                onFinished.run();
            }
        });

        // Start the animation
        timeline.play();
    }

    @Override
    public ViewTransition createReverse() {
        SlideViewTransition reverse = new SlideViewTransition(direction.opposite(), getDuration());
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

    /**
     * Gets the slide direction.
     *
     * @return the slide direction
     */
    public SlideDirection getDirection() {
        return direction;
    }
}