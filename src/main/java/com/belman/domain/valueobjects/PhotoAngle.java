package com.belman.domain.valueobjects;

import java.util.Objects;

/**
 * Value object representing the angle at which a photo was taken in the BelSign system.
 * 
 * The PhotoAngle value object encapsulates the concept of a photo's orientation or
 * perspective. It supports two modes of representation:
 * 
 * 1. Named angles: Predefined standard angles (FRONT, RIGHT, BACK, LEFT) that correspond
 *    to specific degree values (0°, 90°, 180°, 270°).
 * 
 * 2. Custom angles: Any arbitrary angle specified in degrees within the range [0, 360).
 * 
 * This dual representation allows for both standardized categorization of common angles
 * and flexibility for capturing photos from any specific perspective.
 * 
 * PhotoAngle objects are immutable and validate their values upon creation to ensure
 * they represent valid angles.
 */
public record PhotoAngle(double degrees, NamedAngle namedAngle) {
    /**
     * Primary constructor for PhotoAngle that validates the angle values.
     * This constructor is called by the record's canonical constructor and
     * performs validation to ensure the angle is valid.
     * 
     * @param degrees the angle in degrees, must be in range [0, 360)
     * @param namedAngle the named angle, or null for custom angles
     * @throws IllegalArgumentException if degrees is outside the valid range or
     *         if namedAngle is provided but degrees doesn't match its value
     */
    public PhotoAngle {
        if (degrees < 0 || degrees >= 360) {
            throw new IllegalArgumentException("Photo angle must be in [0, 360)");
        }
        if (namedAngle != null && degrees != namedAngle.getDegrees()) {
            throw new IllegalArgumentException("Degrees must match the named angle's degrees");
        }
    }

    /**
     * Creates a custom PhotoAngle with the specified degrees.
     * This constructor is used when the angle doesn't correspond to a standard named angle.
     * 
     * @param degrees the angle in degrees, must be in range [0, 360)
     * @throws IllegalArgumentException if degrees is outside the valid range
     */
    public PhotoAngle(double degrees) {
        this(degrees, null);
    }

    /**
     * Creates a PhotoAngle from a predefined named angle.
     * This constructor is used for standard angles like FRONT, RIGHT, BACK, and LEFT.
     * 
     * @param namedAngle the named angle to use
     * @throws NullPointerException if namedAngle is null
     */
    public PhotoAngle(NamedAngle namedAngle) {
        this(Objects.requireNonNull(namedAngle, "Named angle cannot be null").getDegrees(), namedAngle);
    }

    /**
     * @return true if this is a named angle, false if it's a custom angle
     */
    public boolean isNamedAngle() {
        return namedAngle != null;
    }

    /**
     * @return the display name of this angle (named angle or degrees)
     */
    public String getDisplayName() {
        return isNamedAngle() ? namedAngle.name() : degrees + "°";
    }

    /**
     * Returns the value of the photo angle as a string.
     * For named angles, it returns the name; for custom angles, it returns the degrees.
     */
    public String getValue() {
        return getDisplayName();
    }

    /**
     * Predefined named angles for common photo positions.
     */
    public enum NamedAngle {
        FRONT(0.0),
        RIGHT(90.0),
        BACK(180.0),
        LEFT(270.0);

        private final double degrees;

        NamedAngle(double degrees) {
            this.degrees = degrees;
        }

        /**
         * Returns the degree value associated with this named angle.
         * Each named angle corresponds to a specific degree value:
         * - FRONT: 0°
         * - RIGHT: 90°
         * - BACK: 180°
         * - LEFT: 270°
         * 
         * @return the degree value for this named angle
         */
        public double getDegrees() {
            return degrees;
        }
    }
}
