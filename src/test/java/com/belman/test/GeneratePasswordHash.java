package com.belman.test;

import com.belman.application.usecase.security.BCryptPasswordHasher;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Utility class to generate password hashes for testing.
 */
class GeneratePasswordHash {

    @Test
    public void generatePasswordHash() {
        // Create a password hasher
        PasswordHasher passwordHasher = new BCryptPasswordHasher();

        // Generate hash for pass1234
        String plainTextPassword = "pass1234";
        String hashedPassword = passwordHasher.hash(plainTextPassword);

        System.out.println("[DEBUG_LOG] Password: " + plainTextPassword);
        System.out.println("[DEBUG_LOG] Hashed Password: " + hashedPassword);

        // Verify the hash
        boolean isValid = passwordHasher.verify(plainTextPassword, hashedPassword);
        System.out.println("[DEBUG_LOG] Is Valid: " + isValid);
        Assertions.assertTrue(isValid, "Generated hash should be valid for the password");

        // Verify the hash in the database
        String storedHash = "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS";
        boolean isStoredValid = passwordHasher.verify(plainTextPassword, storedHash);
        System.out.println("[DEBUG_LOG] Is Stored Valid: " + isStoredValid);

        // If the stored hash is not valid for pass1234, we need to update it
        if (!isStoredValid) {
            System.out.println("[DEBUG_LOG] The stored hash is not valid for pass1234. Need to update it to: " + hashedPassword);
            // This assertion will fail if the stored hash is not valid for pass1234
            Assertions.fail("The stored hash is not valid for pass1234. Need to update it to: " + hashedPassword);
        } else {
            System.out.println("[DEBUG_LOG] The stored hash is valid for pass1234.");
        }
    }

    @Test
    public void verifyStoredHash() {
        // Create a password hasher
        PasswordHasher passwordHasher = new BCryptPasswordHasher();

        // The password we want to verify
        String plainTextPassword = "pass1234";

        // The hash stored in the database
        String storedHash = "$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS";

        // Verify if the stored hash is valid for pass1234
        boolean isValid = passwordHasher.verify(plainTextPassword, storedHash);

        // This will fail if the stored hash is not valid for pass1234
        Assertions.assertTrue(isValid, "The stored hash should be valid for pass1234");
    }
}
