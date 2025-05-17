package com.belman.domain.admin;

/**
 * Record representing admin data.
 * This record is used to store admin information such as username, password, and role.
 */
public record AdminData(String username, String password, String role) {
    /**
     * Creates a new AdminData instance with the specified username, password, and role.
     *
     * @param username the admin username
     * @param password the admin password
     * @param role the admin role
     * @throws IllegalArgumentException if any of the parameters are null or blank
     */
    public AdminData {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be null or blank");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role must not be null or blank");
        }
    }
}