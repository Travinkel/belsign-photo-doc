package com.belman.presentation.transitions;

/**
 * Enumeration of slide directions for the SlideViewTransition.
 */
public enum SlideDirection {
    /**
     * Slide from left to right.
     */
    LEFT_TO_RIGHT,

    /**
     * Slide from right to left.
     */
    RIGHT_TO_LEFT,

    /**
     * Slide from top to bottom.
     */
    TOP_TO_BOTTOM,

    /**
     * Slide from bottom to top.
     */
    BOTTOM_TO_TOP;

    /**
     * Gets the opposite direction.
     *
     * @return the opposite direction
     */
    public SlideDirection opposite() {
        switch (this) {
            case LEFT_TO_RIGHT:
                return RIGHT_TO_LEFT;
            case RIGHT_TO_LEFT:
                return LEFT_TO_RIGHT;
            case TOP_TO_BOTTOM:
                return BOTTOM_TO_TOP;
            case BOTTOM_TO_TOP:
                return TOP_TO_BOTTOM;
            default:
                throw new IllegalStateException("Unknown direction: " + this);
        }
    }
}