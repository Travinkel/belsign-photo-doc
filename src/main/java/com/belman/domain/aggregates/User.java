package com.belman.domain.aggregates;

import com.belman.domain.enums.UserStatus;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.valueobjects.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

/**
 * Entity representing a system user in the BelSign application.
 * 
 * The User aggregate is a core domain entity that represents a user of the system.
 * It encapsulates user identity, authentication credentials, contact information,
 * and authorization roles. Users can have different roles (PRODUCTION, QA, ADMIN)
 * which determine their permissions within the system.
 * 
 * Users go through a lifecycle represented by their status:
 * - PENDING: Newly created users awaiting activation
 * - ACTIVE: Users who can log in and use the system
 * - INACTIVE: Users who have been deactivated but not deleted
 * - LOCKED: Users who have been locked out due to security concerns
 * 
 * This aggregate is responsible for:
 * - Maintaining user identity and profile information
 * - Managing user roles and permissions
 * - Tracking user status and lifecycle
 * 
 * Users are referenced by other aggregates like Order and PhotoDocument to track
 * who created or modified domain objects.
 */
public class User {
    private final UserId id;
    private final Username username;
    private HashedPassword password;
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
     * @param password the user's password
     * @param email the user's email address
     * @throws NullPointerException if username or email is null
     * @deprecated Use User(UserId, Username, EmailAddress) instead
     */
    @Deprecated
    public User(Username username, HashedPassword password, EmailAddress email) {
        this.id = UserId.newId();
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.status = UserStatus.ACTIVE; // Default status
    }

    /**
     * Creates a new User with the specified ID, username, and email.
     * 
     * @param id the unique identifier for this user
     * @param username the user's username
     * @param password the user's password
     * @param email the user's email address
     * @throws NullPointerException if any parameter is null
     */
    public User(UserId id, Username username, HashedPassword password, EmailAddress email) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.status = UserStatus.ACTIVE; // Default status
    }

    /**
     * Creates a new User with the specified ID, username, name, and email.
     * 
     * @param id the unique identifier for this user
     * @param username the user's username
     * @param password the user's password
     * @param name the user's name
     * @param email the user's email address
     * @throws NullPointerException if any parameter is null
     */
    public User(UserId id, Username username, HashedPassword password, PersonName name, EmailAddress email) {
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
     * @param id the unique identifier for this user
     * @param username the user's username
     * @param password the user's password
     * @param name the user's name
     * @param email the user's email address
     * @param phoneNumber the user's phone number
     * @param status the user's status
     * @throws NullPointerException if any required parameter is null
     */
    public User(UserId id, Username username, HashedPassword password, PersonName name, EmailAddress email,
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
     * This ID is immutable and serves as the primary identifier for the user in the system.
     * 
     * @return the user's unique identifier
     */
    public UserId getId() {
        return id;
    }

    /**
     * Returns the username of this user.
     * The username is immutable and used for authentication and identification purposes.
     * 
     * @return the user's username
     */
    public Username getUsername() {
        return username;
    }

    /**
     * Returns the hashed password of this user.
     * The password is stored in a secure, hashed format and is used for authentication.
     * 
     * @return the user's hashed password
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
     * 
     * @return the user's name, or null if not set
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
     * The email address is used for communication and notifications.
     * 
     * @return the user's email address
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
     * 
     * @return the user's phone number, or null if not set
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
     * The status determines whether the user can log in and use the system.
     * 
     * @return the user's status (ACTIVE, INACTIVE, LOCKED, or PENDING)
     * @see UserStatus
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Sets or updates the status of this user.
     * Changing a user's status affects their ability to log in and use the system.
     * Consider using the specialized methods {@link #activate()}, {@link #deactivate()},
     * and {@link #lock()} instead of this method for better semantic clarity.
     * 
     * @param status the new status to set
     * @throws NullPointerException if status is null
     * @see #activate()
     * @see #deactivate()
     * @see #lock()
     */
    public void setStatus(UserStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Returns an unmodifiable view of the roles assigned to this user.
     * The returned set cannot be modified, providing protection against
     * accidental modification of the user's roles.
     * 
     * @return an unmodifiable view of the user's roles
     * @see Collections#unmodifiableSet(Set)
     */
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Adds a role to this user's set of roles.
     * If the user already has the specified role, this method has no effect.
     * Roles determine what actions a user is authorized to perform in the system.
     * 
     * @param role the role to add (PRODUCTION, QA, or ADMIN)
     * @throws NullPointerException if role is null
     * @see Role
     */
    public void addRole(Role role) {
        this.roles.add(Objects.requireNonNull(role, "role must not be null"));
    }

    /**
     * Removes a role from this user's set of roles.
     * If the user does not have the specified role, this method has no effect.
     * Removing a role may restrict the actions a user can perform in the system.
     * 
     * @param role the role to remove
     * @throws NullPointerException if role is null
     * @see Role
     */
    public void removeRole(Role role) {
        this.roles.remove(Objects.requireNonNull(role, "role must not be null"));
    }

    /**
     * Checks if the user is in the ACTIVE status.
     * Only active users can log in and use the system.
     * 
     * @return true if the user is active, false otherwise
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Checks if the user is in the INACTIVE status.
     * Inactive users cannot log in but their accounts still exist in the system.
     * This status is typically used for temporary suspension or for users who have left the organization.
     * 
     * @return true if the user is inactive, false otherwise
     */
    public boolean isInactive() {
        return status == UserStatus.INACTIVE;
    }

    /**
     * Checks if the user is in the LOCKED status.
     * Locked users cannot log in due to security concerns (e.g., too many failed login attempts).
     * This status requires administrator intervention to resolve.
     * 
     * @return true if the user is locked, false otherwise
     */
    public boolean isLocked() {
        return status == UserStatus.LOCKED;
    }

    /**
     * Checks if the user is in the PENDING status.
     * Pending users are newly created accounts that have not yet been activated.
     * They cannot log in until their account is activated.
     * 
     * @return true if the user is pending activation, false otherwise
     */
    public boolean isPending() {
        return status == UserStatus.PENDING;
    }

    /**
     * Activates this user by setting their status to ACTIVE.
     * This allows the user to log in and use the system.
     * This operation is typically performed by an administrator or through an activation process.
     */
    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    /**
     * Deactivates this user by setting their status to INACTIVE.
     * This prevents the user from logging in but preserves their account information.
     * This operation is typically performed when a user leaves the organization or needs to be temporarily suspended.
     */
    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    /**
     * Locks this user by setting their status to LOCKED.
     * This prevents the user from logging in due to security concerns.
     * This operation may be performed automatically (e.g., after too many failed login attempts)
     * or manually by an administrator.
     */
    public void lock() {
        this.status = UserStatus.LOCKED;
    }

    /**
     * Defines the possible roles a user can have in the system.
     * Roles determine what actions a user is authorized to perform.
     * 
     * <ul>
     *   <li>PRODUCTION: Users who can upload photos and create orders</li>
     *   <li>QA: Quality Assurance users who can review and approve/reject photos</li>
     *   <li>ADMIN: Administrators who can manage users and system settings</li>
     * </ul>
     * 
     * Users can have multiple roles simultaneously, allowing for flexible permission management.
     */
    public enum Role {
        /**
         * Production workers who can upload photos and create orders.
         * This role is typically assigned to users working in the production area.
         */
        PRODUCTION,

        /**
         * Quality Assurance personnel who can review and approve/reject photos.
         * This role is responsible for ensuring the quality of photo documentation.
         */
        QA,

        /**
         * Administrators who can manage users and system settings.
         * This role has the highest level of access and can perform all operations.
         */
        ADMIN
    }
}
