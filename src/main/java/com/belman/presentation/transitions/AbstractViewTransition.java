package com.belman.presentation.transitions;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * Abstract base class for view transitions.
 * <p>
 * This class provides common functionality for all view transitions,
 * such as duration management and callback handling.
 */
public abstract class AbstractViewTransition implements ViewTransition {
    private final String name;
    private Duration duration = Duration.millis(300);
    private Consumer<View> onStart;
    private Consumer<View> onComplete;

    /**
     * Creates a new AbstractViewTransition with the specified name.
     *
     * @param name the name of the transition
     */
    protected AbstractViewTransition(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Transition name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Invokes the onStart callback if it exists.
     *
     * @param view the view being transitioned to
     */
    protected void fireOnStart(View view) {
        if (onStart != null && view != null) {
            onStart.accept(view);
        }
    }

    /**
     * Invokes the onComplete callback if it exists.
     *
     * @param view the view that was transitioned to
     */
    protected void fireOnComplete(View view) {
        if (onComplete != null && view != null) {
            onComplete.accept(view);
        }
    }

    @Override
    public String toString() {
        return getName() + " (" + getDuration().toMillis() + "ms)";
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public ViewTransition setDuration(Duration duration) {
        if (duration == null) {
            throw new IllegalArgumentException("Duration cannot be null");
        }
        if (duration.lessThan(Duration.ZERO)) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
        this.duration = duration;
        return this;
    }

    @Override
    public ViewTransition setOnStart(Consumer<View> onStart) {
        this.onStart = onStart;
        return this;
    }

    @Override
    public ViewTransition setOnComplete(Consumer<View> onComplete) {
        this.onComplete = onComplete;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }
}