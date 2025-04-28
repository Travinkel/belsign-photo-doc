package com.belman.belsign.domain.model.user;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Entity representing a system user.
 * Includes credentials (username, email) and assigned roles (e.g., QA, Admin).
 */
public class User {
    private final Username username;
    private EmailAddress email;
    private final Set<Role> roles = new HashSet<>();

    /**
     * Creates a new User with the specified username and email.
     * 
     * @param username the user's username
     * @param email the user's email address
     * @throws NullPointerException if username or email is null
     */
    public User(Username username, EmailAddress email) {
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
    }

    /**
     * @return the user's username
     */
    public Username getUsername() {
        return username;
    }

    /**
     * @return the user's email address
     */
    public EmailAddress getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * 
     * @param email the new email address
     * @throws NullPointerException if email is null
     */
    public void setEmail(EmailAddress email) {
        this.email = Objects.requireNonNull(email, "email must not be null");
    }

    /**
     * @return an unmodifiable view of the user's roles
     */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Adds a role to this user (e.g., PRODUCTION, QA, ADMIN).
     * 
     * @param role the role to add
     * @throws NullPointerException if role is null
     */
    public void addRole(Role role) {
        this.roles.add(Objects.requireNonNull(role, "role must not be null"));
    }

    /**
     * Removes a role from this user.
     * 
     * @param role the role to remove
     * @throws NullPointerException if role is null
     */
    public void removeRole(Role role) {
        this.roles.remove(Objects.requireNonNull(role, "role must not be null"));
    }

    /**
     * Possible user roles for access control.
     */
    public enum Role {
        PRODUCTION,
        QA,
        ADMIN
    }
}
