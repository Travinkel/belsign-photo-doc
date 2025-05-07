package com.belman.domain.user;

import com.belman.domain.events.UserApprovedEvent;
import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;
import com.belman.domain.core.AggregateRoot;
import com.belman.domain.events.DomainEvent;
import com.belman.domain.security.HashedPassword;

import java.time.Instant;
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
    private final UserId id;
    private final Username username;
    private HashedPassword password;
    private PersonName name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private ApprovalState approvalState;
    private final Set<UserRole> roles;
    private final Instant creationTimestamp;

    private UserAggregate(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.username = Objects.requireNonNull(builder.username, "username must not be null");
        this.password = Objects.requireNonNull(builder.password, "password must not be null");
        this.email = Objects.requireNonNull(builder.email, "email must not be null");
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.approvalState = builder.approvalState != null ? builder.approvalState : ApprovalState.createPendingState();
        this.roles = new HashSet<>(builder.roles);
        this.creationTimestamp = Instant.now();
    }

    // Factory methods
    public static UserAggregate createNewUser(Username username, HashedPassword password, EmailAddress email) {
        return new Builder()
                .id(UserId.newId())
                .username(username)
                .password(password)
                .email(email)
                .build();
    }

    public static UserAggregate reconstitute(UserId id, Username username, HashedPassword password, PersonName name,
                                             EmailAddress email, PhoneNumber phoneNumber, ApprovalState approvalState,
                                             Set<UserRole> roles) {
        Builder builder = new Builder()
                .id(id)
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .approvalState(approvalState);
        roles.forEach(builder::addRole);
        return builder.build();
    }

    // Builder
    public static class Builder {
        private UserId id;
        private Username username;
        private HashedPassword password;
        private PersonName name;
        private EmailAddress email;
        private PhoneNumber phoneNumber;
        private ApprovalState approvalState;
        private Set<UserRole> roles = new HashSet<>();

        public Builder id(UserId id) {
            this.id = id;
            return this;
        }

        public Builder username(Username username) {
            this.username = username;
            return this;
        }

        public Builder password(HashedPassword password) {
            this.password = password;
            return this;
        }

        public Builder name(PersonName name) {
            this.name = name;
            return this;
        }

        public Builder email(EmailAddress email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(PhoneNumber phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }

        public Builder addRole(UserRole role) {
            this.roles.add(role);
            return this;
        }

        public UserAggregate build() {
            return new UserAggregate(this);
        }
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
    public ApprovalState getApprovalState() {
        return approvalState;
    }

    /**
     * Approves the user with the given reviewer and timestamp.
     */
    public void approve(UserAggregate reviewer, Instant reviewedAt) {
        approvalState.approve(this, reviewer, reviewedAt);
        registerDomainEvent(new UserApprovedEvent(ApprovalStatus.APPROVED));
    }

    /**
     * Rejects the user with the given reviewer, timestamp, and reason.
     */
    public void reject(UserAggregate reviewer, Instant reviewedAt, String reason) {
        approvalState.reject(this, reviewer, reviewedAt, reason);
        registerDomainEvent(new UserApprovedEvent(ApprovalStatus.REJECTED));
    }

    /**
     * Returns an unmodifiable list of domain events emitted by this aggregate.
     */
    public List<DomainEvent> getDomainEvents() {
        return getRegisteredDomainEvents();
    }

    /**
     * Clears the list of domain events emitted by this aggregate.
     */
    public void clearDomainEvents() {
        pullDomainEvents();
    }

    public void addRole(com.belman.domain.user.UserRole role) {
        Objects.requireNonNull(role, "role must not be null");
        if (roles.contains(com.belman.domain.user.UserRole.ADMIN) && role == com.belman.domain.user.UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot add duplicate ADMIN role");
        }
        this.roles.add(role);
    }

    public void removeRole(com.belman.domain.user.UserRole role) {
        if (role == com.belman.domain.user.UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot remove ADMIN role");
        }
        if (!roles.contains(role)) {
            throw new IllegalArgumentException("Role does not exist: " + role);
        }
        this.roles.remove(role);
    }

    public Set<com.belman.domain.user.UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public UserId getId() {
        return new UserId(username.value());
    }

    public void setApprovalState(ApprovalState newState) {
        this.approvalState = Objects.requireNonNull(newState, "Approval state cannot be null");
    }

}


