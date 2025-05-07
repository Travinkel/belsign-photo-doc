package com.belman.domain.order.photo;

/**
 * Value object representing the angle at which a photo was taken.
 * This can be either a named standard angle or a custom angle in degrees.
 * This is specific to the photo bounded context.
 */
public record PhotoAngle(String name, Integer degrees) {

    /**
     * Standard named angle: Front view
     */
    public static final PhotoAngle FRONT = new PhotoAngle("FRONT", 0);

    /**
     * Standard named angle: Back view
     */
    public static final PhotoAngle BACK = new PhotoAngle("BACK", 180);

    /**
     * Standard named angle: Left side view
     */
    public static final PhotoAngle LEFT = new PhotoAngle("LEFT", 270);

    /**
     * Standard named angle: Right side view
     */
    public static final PhotoAngle RIGHT = new PhotoAngle("RIGHT", 90);

    /**
     * Standard named angle: Top view
     */
    public static final PhotoAngle TOP = new PhotoAngle("TOP", null);

    /**
     * Standard named angle: Bottom view
     */
    public static final PhotoAngle BOTTOM = new PhotoAngle("BOTTOM", null);

    /**
     * Creates a new PhotoAngle with the specified name and degrees.
     * Either name or degrees can be null, but not both.
     *
     * @param name    the name of the angle (e.g., "FRONT", "LEFT")
     * @param degrees the angle in degrees (0-359)
     * @throws IllegalArgumentException if both name and degrees are null, or if degrees is outside the valid range
     */
    public PhotoAngle {
        if (name == null && degrees == null) {
            throw new IllegalArgumentException("Either name or degrees must be specified");
        }

        if (degrees != null && (degrees < 0 || degrees > 359)) {
            throw new IllegalArgumentException("Degrees must be between 0 and 359");
        }
    }

    /**
     * Creates a custom angle specified in degrees.
     *
     * @param degrees the angle in degrees (0-359)
     * @return a new PhotoAngle with the specified degrees
     * @throws IllegalArgumentException if degrees is outside the valid range
     */
    public static PhotoAngle ofDegrees(int degrees) {
        return new PhotoAngle("CUSTOM", degrees);
    }

    /**
     * Returns a string representation of this photo angle.
     * For named angles, returns the name. For custom angles, returns the degrees.
     *
     * @return a string representation of this photo angle
     */
    @Override
    public String toString() {
        if (name != null && !name.equals("CUSTOM")) {
            return name;
        } else if (degrees != null) {
            return degrees + "°";
        } else {
            return "UNDEFINED";
        }
    }
}