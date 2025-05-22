package com.belman.domain.report;

import com.belman.domain.common.base.ValueObject;

import java.util.Objects;

/**
 * Value object representing a unique identifier for a report.
 */
public final class ReportId implements ValueObject {

    private final String value;

    /**
     * Creates a new ReportId with the specified value.
     * The value is normalized to lowercase to ensure consistent comparison across all storage backends.
     *
     * @param value the string value of the report ID
     * @throws NullPointerException     if value is null
     * @throws IllegalArgumentException if value is empty
     */
    public ReportId(String value) {
        Objects.requireNonNull(value, "Report ID must not be null");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Report ID must not be empty");
        }
        // Normalize UUID to lowercase for consistent comparison
        this.value = value.toLowerCase();
    }

    /**
     * Creates a new ReportId with a random UUID.
     *
     * @return a new ReportId with a random UUID
     */
    public static ReportId newId() {
        return new ReportId(java.util.UUID.randomUUID().toString());
    }

    /**
     * Returns the string value of this report ID.
     *
     * @return the string value
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportId reportId = (ReportId) o;
        return value.equals(reportId.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
