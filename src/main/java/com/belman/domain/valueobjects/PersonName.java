package com.belman.domain.valueobjects;

/**
 * Value object representing a person's name.
 */
public record PersonName(String firstName, String lastName) {
    /**
     * Creates a PersonName with the specified first and last names.
     * 
     * @param firstName the person's first name
     * @param lastName the person's last name
     * @throws IllegalArgumentException if either name is blank
     */
    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
    }
    
    /**
     * @return the full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return getFullName();
    }
}