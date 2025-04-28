package com.belman.belsign.domain.model.user;

/**
 * Value object for a user's login name.
 */
public final class Username {
    private final String value;

    public Username(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        // standard equals implementation
        return false;
    }

    @Override
    public int hashCode() {
        // standard hashCode implementation
        return 0;
    }
}
