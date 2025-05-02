package com.belman.domain.valueobjects;

import org.mindrot.jbcrypt.BCrypt;
/**
 * Represents a securely hashed password.
 * Cannot be null or empty.
 */
public record HashedPassword(String value) {
    /**
     * Creates a new HashedPassword instance.
     *
     * @param value the hashed password value
     * @throws IllegalArgumentException if the value is null or empty
     */
    public HashedPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hashed password must not be null or blank");
        }
    }

    public static HashedPassword fromPlainText(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            throw new IllegalArgumentException("Plain text password must not be null or blank");
        }
        String hashed = BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
        return new HashedPassword(hashed);
    }

    /**
     * Checks if this hashed password matches a plain text password.
     *
     * @param plainTextPassword the plain text password to check
     * @return true if the password matches, false otherwise
     */
    public boolean matches(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, value);
    }

    /**
     * Checks if this hashed password matches another hashed password.
     *
     * @param other the other hashed password to compare with
     * @return true if the passwords match, false otherwise
     */
    public boolean matches(HashedPassword other) {
        if (other == null) {
            return false;
        }
        return this.value.equals(other.value);
    }
}
