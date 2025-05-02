package com.belman.domain.aggregates;

import com.belman.domain.enums.UserStatus;
import com.belman.domain.valueobjects.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Entity representing a system user.
 * Includes credentials (username, email) and assigned roles (e.g., QA, Admin).
 */
public class User {
    private final UserId id;
    private final Username username;
    private final HashedPassword password;
    private PersonName name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private UserStatus status;
    private final Set<Role> roles = new HashSet<>();

    /**
     * Creates a new User with the specified username and email.
     * This constructor is maintained for backward compatibility with existing tests.
     * 
     * @param username the user's username
     * @param email the user's email address
     * @throws NullPointerException if username or email is null
     * @deprecated Use User(UserId, Username, EmailAddress) instead
     */
    @Deprecated
    public User(Username username, HashedPassword password, EmailAddress email) {
        this.password = password;
        this.id = UserId.newId();
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.status = UserStatus.ACTIVE; // Default status
    }

    /**
     * Creates a new User with the specified ID, username, and email.
     * 
     * @param id the unique identifier for this user
     * @param username the user's username
     * @param email the user's email address
     * @throws NullPointerException if any parameter is null
     */
    public User(UserId id, Username username, HashedPassword hashedPassword, EmailAddress email) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(hashedPassword, "hashedPassword must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.status = UserStatus.ACTIVE; // Default status
    }

    /**
     * Creates a new User with the specified ID, username, name, and email.
     * 
     * @param id the unique identifier for this user
     * @param username the user's username
     * @param name the user's name
     * @param email the user's email address
     * @throws NullPointerException if any parameter is null
     */
    public User(UserId id, Username username, HashedPassword hashedPassword, PersonName name, EmailAddress email) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(hashedPassword, "hashedPassword must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.status = UserStatus.ACTIVE; // Default status
    }

    /**
     * Creates a new User with all details.
     * 
     * @param id the unique identifier for this user
     * @param username the user's username
     * @param name the user's name
     * @param email the user's email address
     * @param phoneNumber the user's phone number
     * @param status the user's status
     * @throws NullPointerException if any required parameter is null
     */
    public User(UserId id, Username username, HashedPassword hashedPassword, PersonName name, EmailAddress email,
               PhoneNumber phoneNumber, UserStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(hashedPassword, "hashedPassword must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.phoneNumber = phoneNumber; // Can be null
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * @return the user's unique identifier
     */
    public UserId getId() {
        return id;
    }

    /**
     * @return the user's username
     */
    public Username getUsername() {
        return username;
    }

    /**
     * @return the user's hashed password
     */
    public HashedPassword getPassword() {
        return password;
    }

    /**
     * @return the user's name, or null if not set
     */
    public PersonName getName() {
        return name;
    }

    /**
     * Sets the user's name.
     * 
     * @param name the new name
     * @throws NullPointerException if name is null
     */
    public void setName(PersonName name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
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
     * @return the user's phone number, or null if not set
     */
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     * 
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the user's status
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Sets the user's status.
     * 
     * @param status the new status
     * @throws NullPointerException if status is null
     */
    public void setStatus(UserStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
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
     * @return true if the user is active
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * @return true if the user is inactive
     */
    public boolean isInactive() {
        return status == UserStatus.INACTIVE;
    }

    /**
     * @return true if the user is locked
     */
    public boolean isLocked() {
        return status == UserStatus.LOCKED;
    }

    /**
     * @return true if the user is pending activation
     */
    public boolean isPending() {
        return status == UserStatus.PENDING;
    }

    /**
     * Activates this user.
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * Deactivates this user.
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * Locks this user.
     */
    public void lock() {
        this.status = UserStatus.LOCKED;
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
