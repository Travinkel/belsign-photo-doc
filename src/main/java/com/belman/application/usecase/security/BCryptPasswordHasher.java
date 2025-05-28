package com.belman.application.usecase.security;

import com.belman.domain.security.PasswordHasher;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Implementation of the PasswordHasher interface using BCrypt.
 * This class encapsulates the BCrypt library to provide password hashing services.
 */
public class BCryptPasswordHasher implements PasswordHasher {

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param plainTextPassword the plain text password to hash
     * @return the hashed password
     * @throws IllegalArgumentException if the plain text password is null or blank
     */
    @Override
    public String hash(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank()) {
            throw new IllegalArgumentException("Plain text password must not be null or blank");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Verifies if a plain text password matches a hashed password using BCrypt.
     *
     * @param plainTextPassword the plain text password to check
     * @param hashedPassword    the hashed password to compare against
     * @return true if the password matches, false otherwise
     */
    @Override
    public boolean verify(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || plainTextPassword.isBlank() || hashedPassword == null ||
            hashedPassword.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
