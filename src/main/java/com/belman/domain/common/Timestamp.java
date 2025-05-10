package com.belman.domain.common;

import com.belman.domain.common.base.DataObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Value object representing a point in time.
 * This is in the common package as it's used across multiple bounded contexts.
 */
public record Timestamp(Instant value) implements DataObject {

    /**
     * Creates a new Timestamp with the specified Instant.
     *
     * @param value the Instant value
     * @throws NullPointerException if the value is null
     */
    public Timestamp {
        Objects.requireNonNull(value, "Timestamp value must not be null");
    }

    /**
     * Creates a new Timestamp with the current time.
     *
     * @return a new Timestamp with the current time
     */
    public static Timestamp now() {
        return new Timestamp(Instant.now());
    }

    /**
     * Returns this timestamp as a formatted string using the default formatter.
     *
     * @return the formatted timestamp
     */
    public String formatted() {
        return DateTimeFormatter.ISO_INSTANT.format(value);
    }

    /**
     * Returns this timestamp as a formatted string using the specified formatter.
     *
     * @param formatter the formatter to use
     * @return the formatted timestamp
     */
    public String formatted(DateTimeFormatter formatter) {
        return formatter.format(LocalDateTime.ofInstant(value, ZoneId.systemDefault()));
    }

    public Instant toInstant() {
        return value;
    }
}
