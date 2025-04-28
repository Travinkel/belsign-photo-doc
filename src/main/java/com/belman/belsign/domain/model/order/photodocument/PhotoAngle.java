package com.belman.belsign.domain.model.order.photodocument;

import java.util.Objects;

/**
 * Value object representing the angle of a photo.
 * Can be specified either as a named angle (FRONT, BACK, LEFT, RIGHT) or as a custom angle in degrees.
 */
public final class PhotoAngle {
    private final double degrees;
    private final NamedAngle namedAngle;

    /**
     * Creates a PhotoAngle with a specific degree value.
     * @param degrees the angle in degrees, must be in [0, 360)
     */
    public PhotoAngle(double degrees) {
        if (degrees < 0 || degrees >= 360) {
            throw new IllegalArgumentException("Photo angle must be in [0, 360)");
        }
        this.degrees = degrees;
        this.namedAngle = null;
    }

    /**
     * Creates a PhotoAngle with a named angle (FRONT, BACK, LEFT, RIGHT).
     * @param namedAngle the named angle
     */
    public PhotoAngle(NamedAngle namedAngle) {
        Objects.requireNonNull(namedAngle, "Named angle cannot be null");
        this.namedAngle = namedAngle;
        this.degrees = namedAngle.getDegrees();
    }

    /**
     * @return the angle in degrees
     */
    public double getDegrees() {
        return degrees;
    }

    /**
     * @return the named angle, or null if this is a custom angle
     */
    public NamedAngle getNamedAngle() {
        return namedAngle;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoAngle that = (PhotoAngle) o;
        return Double.compare(that.degrees, degrees) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(degrees);
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
