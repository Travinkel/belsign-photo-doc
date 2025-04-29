package com.belman.belsign.domain.model.user;

/**
 * Value object for a user's login name.
 */
public record Username(String value) {
    /**
     * Creates a Username with the specified value.
     * 
     * @param value the username string
     * @throws IllegalArgumentException if the username is empty or null
     */
    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }

    /**
     * @return the username string
     * @deprecated Use value() instead
     */
    @Deprecated
    public String getValue() {
        return value;
    }
}
