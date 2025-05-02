package com.belman.domain.valueobjects;

import java.util.Objects;

/**
 * Value object representing the angle of a photo.
 * Can be specified either as a named angle (FRONT, BACK, LEFT, RIGHT) or as a custom angle in degrees.
 */
public record PhotoAngle(double degrees, NamedAngle namedAngle) {
    public PhotoAngle {
        if (degrees < 0 || degrees >= 360) {
            throw new IllegalArgumentException("Photo angle must be in [0, 360)");
        }
        if (namedAngle != null && degrees != namedAngle.getDegrees()) {
            throw new IllegalArgumentException("Degrees must match the named angle's degrees");
        }
    }

    public PhotoAngle(double degrees) {
        this(degrees, null);
    }

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

        public double getDegrees() {
            return degrees;
        }
    }
}
