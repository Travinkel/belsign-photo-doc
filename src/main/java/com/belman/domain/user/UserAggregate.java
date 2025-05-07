package com.belman.domain.user;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;
import com.belman.domain.core.AggregateRoot;
import com.belman.domain.events.DomainEvent;
import com.belman.domain.security.HashedPassword;

import java.sql.Timestamp;
import java.util.*;

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
 * Users are referenced by other aggregates like OrderAggregate and PhotoDocument to track
 * who created or modified domain objects.
 */
public class UserAggregate extends AggregateRoot<UserId> {
    private final Username username;
    private HashedPassword password;
    private PersonName name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private ApprovalState approvalState;
    private final Set<UserRole> roles = new HashSet<>();
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Creates a new User with the specified username, password and email.
     *
     * @param username the user's username
     * @param password the user's password
     * @param email    the user's email address
     * @throws NullPointerException if username, password or email is null
     */
    public UserAggregate(Username username, HashedPassword password, EmailAddress email) {
        super(UserId.newId());
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.approvalState = new PendingApprovalState(); // Default state
    }

    // Constructor for additional properties (e.g., PersonName)
    public UserAggregate(Username username, HashedPassword password, EmailAddress email, PersonName name) {
        this(username, password, email);
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    // Constructor for all properties
    public UserAggregate(UserId id, Username username, HashedPassword password, PersonName name,
                         EmailAddress email, PhoneNumber phoneNumber, UserStatus status) {
        super(id);
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.phoneNumber = phoneNumber; // Can be null
        this.approvalState = Objects.requireNonNull(status, "status must not be null").toApprovalState();
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
     */
    public void setPassword(HashedPassword password) {
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    /**
     * Returns the current approval status of this user.
     */
    public String getStatus() {
        return approvalState.getStatus();
    }

    /**
     * Approves the user with the given reviewer and timestamp.
     */
    public void approve(UserAggregate reviewer, Timestamp reviewedAt) {
        approvalState.approve(reviewer, reviewedAt);
        domainEvents.add(new UserApprovedEvent(this.getId(), reviewer.getId(), reviewedAt));
    }

    /**
     * Rejects the user with the given reviewer, timestamp, and reason.
     */
    public void reject(UserAggregate reviewer, Timestamp reviewedAt, String reason) {
        approvalState.reject(reviewer, reviewedAt, reason);
        domainEvents.add(new UserRejectedEvent(this.getId(), reviewer.getId(), reviewedAt, reason));
    }

    /**
     * Returns an unmodifiable list of domain events emitted by this aggregate.
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clears the list of domain events emitted by this aggregate.
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public void addRole(UserRole role) {
        Objects.requireNonNull(role, "role must not be null");
        this.roles.add(role);
    }

    public void removeRole(UserRole role) {
        if (!roles.contains(role)) {
            throw new IllegalArgumentException("Role does not exist: " + role);
        }
        this.roles.remove(role);
    }

    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public UserId getId() {
        return super.getId();
    }
}