package com.belman.domain.user;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;
import com.belman.domain.security.HashedPassword;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a system user in the BelSign application.
 * <p>
 * The User aggregate is a core domain entity that represents a user of the system.
 * It encapsulates user identity, authentication credentials, contact information,
 * and authorization roles. Users can have different roles (PRODUCTION, QA, ADMIN)
 * which determine their permissions within the system.
 * <p>
 * Users go through a lifecycle represented by their status:
 * - PENDING: Newly created users awaiting activation
 * - ACTIVE: Users who can log in and use the system
 * - INACTIVE: Users who have been deactivated but not deleted
 * - LOCKED: Users who have been locked out due to security concerns
 * <p>
 * This aggregate is responsible for:
 * - Maintaining user identity and profile information
 * - Managing user roles and permissions
 * - Tracking user status and lifecycle
 * <p>
 * Users are referenced by other aggregates like Order and PhotoDocument to track
 * who created or modified domain objects.
 */
public class UserAggregate {
    private final UserId id;
    private final Username username;
    private HashedPassword password;
    private PersonName name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private UserStatus status;
    private final Set<UserRole> roles = new HashSet<>();

    /**
     * Creates a new User with the specified username, password and email.
     *
     * @param username the user's username
     * @param password the user's password
     * @param email    the user's email address
     * @throws NullPointerException if username, password or email is null
     */
    public UserAggregate(Username username, HashedPassword password, EmailAddress email) {
        this.id = UserId.newId();
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.status = UserStatus.ACTIVE; // Default status
    }

    /**
     * Creates a new User with the specified ID, username, password and email.
     *
     * @param id       the unique identifier for this user
     * @param username the user's username
     * @param password the user's password
     * @param email    the user's email address
     * @throws NullPointerException if any parameter is null
     */
    public UserAggregate(UserId id, Username username, HashedPassword password, EmailAddress email) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.status = UserStatus.ACTIVE; // Default status
    }

    /**
     * Creates a new User with the specified ID, username, password, name, and email.
     *
     * @param id       the unique identifier for this user
     * @param username the user's username
     * @param password the user's password
     * @param name     the user's name
     * @param email    the user's email address
     * @throws NullPointerException if any parameter is null
     */
    public UserAggregate(UserId id, Username username, HashedPassword password, PersonName name, EmailAddress email) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.status = UserStatus.ACTIVE; // Default status
    }

    /**
     * Creates a new User with all details.
     *
     * @param id          the unique identifier for this user
     * @param username    the user's username
     * @param password    the user's password
     * @param name        the user's name
     * @param email       the user's email address
     * @param phoneNumber the user's phone number
     * @param status      the user's status
     * @throws NullPointerException if any required parameter is null
     */
    public UserAggregate(UserId id, Username username, HashedPassword password, PersonName name, EmailAddress email,
                         PhoneNumber phoneNumber, UserStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.phoneNumber = phoneNumber; // Can be null
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Returns the unique identifier for this user.
     */
    public UserId getId() {
        return id;
    }

    /**
     * Returns the username of this user.
     */
    public Username getUsername() {
        return username;
    }

    /**
     * Returns the hashed password of this user.
     */
    public HashedPassword getPassword() {
        return password;
    }

    /**
     * Sets or updates the password of this user.
     *
     * @param password the new password to set
     * @throws NullPointerException if password is null
     */
    public void setPassword(HashedPassword password) {
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    /**
     * Returns the full name of this user.
     * The name is optional and may be null if not set.
     */
    public PersonName getName() {
        return name;
    }

    /**
     * Sets or updates the full name of this user.
     *
     * @param name the new name to set
     * @throws NullPointerException if name is null
     */
    public void setName(PersonName name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    /**
     * Returns the email address of this user.
     */
    public EmailAddress getEmail() {
        return email;
    }

    /**
     * Sets or updates the email address of this user.
     *
     * @param email the new email address to set
     * @throws NullPointerException if email is null
     */
    public void setEmail(EmailAddress email) {
        this.email = Objects.requireNonNull(email, "email must not be null");
    }

    /**
     * Returns the phone number of this user.
     * The phone number is optional and may be null if not set.
     */
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets or updates the phone number of this user.
     * Unlike other properties, the phone number can be set to null to indicate
     * that the user does not have a phone number.
     *
     * @param phoneNumber the new phone number to set, or null to clear the phone number
     */
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the current status of this user.
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Sets or updates the status of this user.
     * Changing a user's status affects their ability to log in and use the system.
     * Consider using the specialized methods activate(), deactivate(),
     * and lock() instead of this method for better semantic clarity.
     *
     * @param status the new status to set
     * @throws NullPointerException if status is null
     */
    public void setStatus(UserStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Returns an unmodifiable view of the roles assigned to this user.
     */
    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Adds a role to this user's set of roles.
     *
     * @param role the role to add
     * @throws NullPointerException if role is null
     */
    public void addRole(UserRole role) {
        this.roles.add(Objects.requireNonNull(role, "role must not be null"));
    }

    /**
     * Removes a role from this user's set of roles.
     *
     * @param role the role to remove
     * @throws NullPointerException if role is null
     */
    public void removeRole(UserRole role) {
        this.roles.remove(Objects.requireNonNull(role, "role must not be null"));
    }

    /**
     * Checks if the user is in the ACTIVE status.
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Checks if the user is in the INACTIVE status.
     */
    public boolean isInactive() {
        return status == UserStatus.INACTIVE;
    }

    /**
     * Checks if the user is in the LOCKED status.
     */
    public boolean isLocked() {
        return status == UserStatus.LOCKED;
    }

    /**
     * Checks if the user is in the PENDING status.
     */
    public boolean isPending() {
        return status == UserStatus.PENDING;
    }

    /**
     * Activates this user by setting their status to ACTIVE.
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * Deactivates this user by setting their status to INACTIVE.
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * Locks this user by setting their status to LOCKED.
     */
    public void lock() {
        this.status = UserStatus.LOCKED;
    }
}