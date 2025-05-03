package com.belman.backbone.core.transition;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * Interface for view transitions.
 * <p>
 * A view transition defines how one view is replaced by another in the UI.
 * Transitions can be used to provide a more polished and professional look
 * to view changes, as well as to provide visual cues about navigation direction.
 */
public interface ViewTransition {

    /**
     * Performs the transition from the current view to the new view.
     *
     * @param currentView the current view (may be null if there is no current view)
     * @param newView     the new view to transition to
     * @param onFinished  callback to be invoked when the transition is complete
     */
    void performTransition(View currentView, View newView, Runnable onFinished);

    /**
     * Gets the duration of this transition.
     *
     * @return the duration
     */
    Duration getDuration();

    /**
     * Sets the duration of this transition.
     *
     * @param duration the duration
     * @return this transition for method chaining
     */
    ViewTransition setDuration(Duration duration);

    /**
     * Creates a reverse transition.
     * <p>
     * A reverse transition is used when navigating backward. For example,
     * if the forward transition slides in from right to left, the reverse
     * transition would slide in from left to right.
     *
     * @return a new transition that is the reverse of this one
     */
    ViewTransition createReverse();

    /**
     * Sets a callback to be invoked when the transition starts.
     *
     * @param onStart the callback to invoke
     * @return this transition for method chaining
     */
    ViewTransition setOnStart(Consumer<View> onStart);

    /**
     * Sets a callback to be invoked when the transition completes.
     *
     * @param onComplete the callback to invoke
     * @return this transition for method chaining
     */
    ViewTransition setOnComplete(Consumer<View> onComplete);

    /**
     * Gets the name of this transition.
     *
     * @return the name
     */
    String getName();
}