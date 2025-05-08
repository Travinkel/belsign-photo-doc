package com.belman.business.domain.user;

import com.belman.business.domain.events.UserApprovedEvent;
import com.belman.business.domain.common.EmailAddress;
import com.belman.business.domain.common.PersonName;
import com.belman.business.domain.common.PhoneNumber;
import com.belman.business.domain.core.AggregateRoot;
import com.belman.business.domain.events.DomainEvent;
import com.belman.business.domain.security.HashedPassword;

import java.time.Instant;
import java.util.*;

/**
 * Represents a user in the system.
 * This is an aggregate root entity in the domain model.
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
     * Returns the email address of this user.
     */
    public EmailAddress getEmail() {
        return email;
    }

    /**
     * Returns the name of this user.
     */
    public PersonName getName() {
        return name;
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

    public void addRole(UserRole role) {
        Objects.requireNonNull(role, "role must not be null");
        if (roles.contains(UserRole.ADMIN) && role == UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot add duplicate ADMIN role");
        }
        this.roles.add(role);
    }

    public void removeRole(UserRole role) {
        if (role == UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot remove ADMIN role");
        }
        if (!roles.contains(role)) {
            throw new IllegalArgumentException("Role does not exist: " + role);
        }
        this.roles.remove(role);
    }

    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Alias for getRoles() to maintain backward compatibility.
     */
    public Set<UserRole> roles() {
        return getRoles();
    }

    @Override
    public UserId getId() {
        return id;
    }

    /**
     * Returns the phone number of this user.
     */
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns the status of this user.
     */
    public UserStatus getStatus() {
        return UserStatus.ACTIVE; // Default to ACTIVE for now
    }

    public void setApprovalState(ApprovalState newState) {
        this.approvalState = Objects.requireNonNull(newState, "Approval state cannot be null");
    }
}
