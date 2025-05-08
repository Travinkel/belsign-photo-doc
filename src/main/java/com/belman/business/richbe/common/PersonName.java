package com.belman.business.richbe.common;

/**
 * Value object representing a person's name.
 * This is in the common package as it's used across multiple bounded contexts.
 */
public record PersonName(String firstName, String lastName) {

    /**
     * Creates a new PersonName with the specified first and last name.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @throws IllegalArgumentException if either name is null or empty
     */
    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be null or blank");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be null or blank");
        }
    }

    /**
     * Returns the full name as a combination of first and last name.
     *
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}