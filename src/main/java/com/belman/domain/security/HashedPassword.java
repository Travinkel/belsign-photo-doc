package com.belman.domain.security;

import com.belman.domain.common.base.ValueObject;

/**
 * Represents a securely hashed password.
 * Cannot be null or empty.
 */
public record HashedPassword(String value) implements ValueObject {
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

    /**
     * Creates a new HashedPassword from a plain text password.
     *
     * @param plainTextPassword the plain text password to hash
     * @param passwordHasher    the password hasher to use
     * @return a new HashedPassword instance
     * @throws IllegalArgumentException if the plain text password is null or blank
     */
    public static HashedPassword fromPlainText(String plainTextPassword, PasswordHasher passwordHasher) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            throw new IllegalArgumentException("Plain text password must not be null or blank");
        }
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher must not be null");
        }
        String hashed = passwordHasher.hash(plainTextPassword);
        return new HashedPassword(hashed);
    }

    /**
     * Checks if this hashed password matches a plain text password.
     *
     * @param plainTextPassword the plain text password to check
     * @param passwordHasher    the password hasher to use
     * @return true if the password matches, false otherwise
     */
    public boolean matches(String plainTextPassword, PasswordHasher passwordHasher) {
        if (plainTextPassword == null || plainTextPassword.isBlank() || passwordHasher == null) {
            return false;
        }
        return passwordHasher.verify(plainTextPassword, value);
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
