package com.belman.presentation.transitions;

import com.belman.common.platform.PlatformUtils;
import javafx.util.Duration;

/**
 * Provides common transition presets for view navigation.
 * <p>
 * This class contains factory methods for creating common transitions
 * with predefined settings.
 */
public class TransitionPresets {
    // Default durations
    private static final Duration DEFAULT_DURATION = Duration.millis(300);
    private static final Duration FAST_DURATION = Duration.millis(150);
    private static final Duration SLOW_DURATION = Duration.millis(500);

    // Prevent instantiation
    private TransitionPresets() {
    }

    /**
     * Creates a fade transition with the specified duration.
     *
     * @param duration the duration of the transition
     * @return a fade transition
     */
    public static ViewTransition fade(Duration duration) {
        return new FadeViewTransition(duration);
    }

    /**
     * Creates a fast fade transition.
     *
     * @return a fast fade transition
     */
    public static ViewTransition fastFade() {
        return new FadeViewTransition(FAST_DURATION);
    }

    /**
     * Creates a slow fade transition.
     *
     * @return a slow fade transition
     */
    public static ViewTransition slowFade() {
        return new FadeViewTransition(SLOW_DURATION);
    }

    /**
     * Creates a slide transition with the specified direction and duration.
     *
     * @param direction the slide direction
     * @param duration  the duration of the transition
     * @return a slide transition
     */
    public static ViewTransition slide(SlideDirection direction, Duration duration) {
        return new SlideViewTransition(direction, duration);
    }

    /**
     * Creates a fast slide transition with the specified direction.
     *
     * @param direction the slide direction
     * @return a fast slide transition
     */
    public static ViewTransition fastSlide(SlideDirection direction) {
        return new SlideViewTransition(direction, FAST_DURATION);
    }

    /**
     * Creates a slow slide transition with the specified direction.
     *
     * @param direction the slide direction
     * @return a slow slide transition
     */
    public static ViewTransition slowSlide(SlideDirection direction) {
        return new SlideViewTransition(direction, SLOW_DURATION);
    }

    /**
     * Creates a transition preset appropriate for forward navigation.
     * <p>
     * On mobile platforms, this is typically a slide from right to left.
     * On desktop platforms, this is typically a fade transition.
     *
     * @return a transition preset for forward navigation
     */
    public static ViewTransition forward() {
        if (PlatformUtils.isRunningOnMobile()) {
            return slideRightToLeft();
        } else {
            return fade();
        }
    }

    /**
     * Creates a slide transition from right to left with the default duration.
     *
     * @return a slide transition from right to left
     */
    public static ViewTransition slideRightToLeft() {
        return new SlideViewTransition(SlideDirection.RIGHT_TO_LEFT);
    }

    /**
     * Creates a fade transition with the default duration.
     *
     * @return a fade transition
     */
    public static ViewTransition fade() {
        return new FadeViewTransition();
    }

    /**
     * Creates a transition preset appropriate for backward navigation.
     * <p>
     * On mobile platforms, this is typically a slide from left to right.
     * On desktop platforms, this is typically a fade transition.
     *
     * @return a transition preset for backward navigation
     */
    public static ViewTransition backward() {
        if (PlatformUtils.isRunningOnMobile()) {
            return slideLeftToRight();
        } else {
            return fade();
        }
    }

    /**
     * Creates a slide transition from left to right with the default duration.
     *
     * @return a slide transition from left to right
     */
    public static ViewTransition slideLeftToRight() {
        return new SlideViewTransition(SlideDirection.LEFT_TO_RIGHT);
    }

    /**
     * Creates a transition preset appropriate for upward navigation (e.g., to a parent view).
     * <p>
     * On mobile platforms, this is typically a slide from bottom to top.
     * On desktop platforms, this is typically a fade transition.
     *
     * @return a transition preset for upward navigation
     */
    public static ViewTransition upward() {
        if (PlatformUtils.isRunningOnMobile()) {
            return slideBottomToTop();
        } else {
            return fade();
        }
    }

    /**
     * Creates a slide transition from bottom to top with the default duration.
     *
     * @return a slide transition from bottom to top
     */
    public static ViewTransition slideBottomToTop() {
        return new SlideViewTransition(SlideDirection.BOTTOM_TO_TOP);
    }

    /**
     * Creates a transition preset appropriate for downward navigation (e.g., to a child view).
     * <p>
     * On mobile platforms, this is typically a slide from top to bottom.
     * On desktop platforms, this is typically a fade transition.
     *
     * @return a transition preset for downward navigation
     */
    public static ViewTransition downward() {
        if (PlatformUtils.isRunningOnMobile()) {
            return slideTopToBottom();
        } else {
            return fade();
        }
    }

    /**
     * Creates a slide transition from top to bottom with the default duration.
     *
     * @return a slide transition from top to bottom
     */
    public static ViewTransition slideTopToBottom() {
        return new SlideViewTransition(SlideDirection.TOP_TO_BOTTOM);
    }
}
