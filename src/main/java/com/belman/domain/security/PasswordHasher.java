package com.belman.domain.security;

/**
 * Interface for password hashing services.
 * This abstraction allows the domain layer to hash and verify passwords
 * without depending on specific hashing implementations.
 */
public interface PasswordHasher {

    /**
     * Hashes a plain text password.
     *
     * @param plainTextPassword the plain text password to hash
     * @return the hashed password
     * @throws IllegalArgumentException if the plain text password is null or blank
     */
    String hash(String plainTextPassword);

    /**
     * Verifies if a plain text password matches a hashed password.
     *
     * @param plainTextPassword the plain text password to check
     * @param hashedPassword    the hashed password to compare against
     * @return true if the password matches, false otherwise
     */
    boolean verify(String plainTextPassword, String hashedPassword);
}
