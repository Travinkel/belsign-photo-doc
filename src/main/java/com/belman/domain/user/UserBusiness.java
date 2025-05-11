package com.belman.domain.user;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;
import com.belman.domain.core.BusinessObject;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.events.UserApprovedAuditEvent;
import com.belman.domain.user.events.UserRejectedAuditEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a user in the system.
 * This is a primary business object in the business model.
 */
public class UserBusiness extends BusinessObject<UserId> {
    private final UserId id;
    private final Username username;
    private final Set<UserRole> roles;
    private final Instant creationTimestamp;
    private HashedPassword password;
    private PersonName name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private ApprovalState approvalState;

    private UserBusiness(Builder builder) {
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
    public static UserBusiness createNewUser(Username username, HashedPassword password, EmailAddress email) {
        return new Builder()
                .id(UserId.newId())
                .username(username)
                .password(password)
                .email(email)
                .build();
    }

    public static UserBusiness reconstitute(UserId id, Username username, HashedPassword password, PersonName name,
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
        updateLastModifiedAt();
    }

    /**
     * Returns the current approval state of this user.
     */
    public ApprovalState getApprovalState() {
        return approvalState;
    }

    /**
     * Sets the approval state of this user.
     * <p>
     * This method is primarily used by the approval state objects themselves
     * during state transitions. It should not be called directly except in
     * special circumstances.
     * <p>
     * Changing the approval state affects the user's status in the system,
     * which determines whether they can access the system and what actions
     * they can perform.
     *
     * @param newState the new approval state
     * @throws NullPointerException if newState is null
     */
    public void setApprovalState(ApprovalState newState) {
        this.approvalState = Objects.requireNonNull(newState, "Approval state cannot be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the email address of this user.
     *
     * @return the email address
     */
    public EmailAddress getEmail() {
        return email;
    }

    /**
     * Sets or updates the email address of this user.
     *
     * @param email the new email address
     * @throws NullPointerException if email is null
     */
    public void setEmail(EmailAddress email) {
        this.email = Objects.requireNonNull(email, "email must not be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the name of this user.
     *
     * @return the name, may be null if not set
     */
    public PersonName getName() {
        return name;
    }

    /**
     * Sets or updates the name of this user.
     *
     * @param name the new name
     */
    public void setName(PersonName name) {
        this.name = name; // Name can be null
        updateLastModifiedAt();
    }

    /**
     * Approves the user with the given reviewer and timestamp.
     * <p>
     * This method changes the user's approval state to APPROVED, which allows
     * the user to access the system. It also registers an audit event to track
     * who approved the user and when.
     * <p>
     * The approval state transition is handled by the current approval state object,
     * which may throw exceptions if the transition is not allowed (e.g., if the user
     * is already approved or rejected).
     *
     * @param reviewer   the user who is approving this user
     * @param reviewedAt the timestamp when the approval occurred
     * @throws IllegalStateException if the user is already approved or rejected
     * @throws NullPointerException  if reviewer or reviewedAt is null
     */
    public void approve(UserBusiness reviewer, Instant reviewedAt) {
        Objects.requireNonNull(reviewer, "reviewer must not be null");
        Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
        approvalState.approve(this, reviewer, reviewedAt);
        registerAuditEvent(new UserApprovedAuditEvent(this.getId(), reviewer.getId(), ApprovalStatus.APPROVED));
    }

    /**
     * Returns the unique identifier of this user.
     *
     * @return the user ID
     */
    @Override
    public UserId getId() {
        return id;
    }

    /**
     * Rejects the user with the given reviewer, timestamp, and reason.
     * <p>
     * This method changes the user's approval state to REJECTED, which prevents
     * the user from accessing the system. It also registers an audit event to track
     * who rejected the user, when, and why.
     * <p>
     * The approval state transition is handled by the current approval state object,
     * which may throw exceptions if the transition is not allowed (e.g., if the user
     * is already approved or rejected).
     *
     * @param reviewer   the user who is rejecting this user
     * @param reviewedAt the timestamp when the rejection occurred
     * @param reason     the reason for rejection
     * @throws IllegalStateException if the user is already approved or rejected
     * @throws NullPointerException  if reviewer or reviewedAt is null
     */
    public void reject(UserBusiness reviewer, Instant reviewedAt, String reason) {
        Objects.requireNonNull(reviewer, "reviewer must not be null");
        Objects.requireNonNull(reviewedAt, "reviewedAt must not be null");
        approvalState.reject(this, reviewer, reviewedAt, reason);
        registerAuditEvent(new UserRejectedAuditEvent(this.getId(), reviewer.getId(), reason));
    }

    /**
     * Adds a role to this user.
     * <p>
     * This method adds the specified role to the user's set of roles.
     * If the user already has the ADMIN role, adding another ADMIN role
     * will throw an exception to prevent duplicate ADMIN roles.
     *
     * @param role the role to add
     * @throws NullPointerException     if role is null
     * @throws IllegalArgumentException if trying to add a duplicate ADMIN role
     */
    public void addRole(UserRole role) {
        Objects.requireNonNull(role, "role must not be null");
        if (roles.contains(UserRole.ADMIN) && role == UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot add duplicate ADMIN role");
        }
        this.roles.add(role);
        updateLastModifiedAt();
    }

    /**
     * Removes a role from this user.
     * <p>
     * This method removes the specified role from the user's set of roles.
     * The ADMIN role cannot be removed to prevent accidental removal of
     * administrative privileges. If the user doesn't have the specified role,
     * an exception will be thrown.
     *
     * @param role the role to remove
     * @throws IllegalArgumentException if trying to remove the ADMIN role or if the user doesn't have the specified role
     */
    public void removeRole(UserRole role) {
        if (role == UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot remove ADMIN role");
        }
        if (!roles.contains(role)) {
            throw new IllegalArgumentException("Role does not exist: " + role);
        }
        this.roles.remove(role);
        updateLastModifiedAt();
    }

    /**
     * Alias for getRoles() to maintain backward compatibility.
     * <p>
     * This method returns the same result as {@link #getRoles()}.
     *
     * @return an unmodifiable set of roles
     * @see #getRoles()
     */
    public Set<UserRole> roles() {
        return getRoles();
    }

    /**
     * Returns an unmodifiable view of the roles assigned to this user.
     *
     * @return an unmodifiable set of roles
     */
    public Set<UserRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Returns the phone number of this user.
     *
     * @return the phone number, may be null if not set
     */
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets or updates the phone number of this user.
     *
     * @param phoneNumber the new phone number, can be null to remove the phone number
     */
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber; // Phone number can be null
        updateLastModifiedAt();
    }

    /**
     * Returns the status of this user based on the approval state.
     * <p>
     * The status is derived from the approval state as follows:
     * - PENDING approval state maps to PENDING status
     * - APPROVED approval state maps to ACTIVE status
     * - REJECTED approval state maps to INACTIVE status
     *
     * @return the current status of this user
     */
    public UserStatus getStatus() {
        ApprovalStatus approvalStatus = approvalState.getStatusEnum();
        return switch (approvalStatus) {
            case PENDING -> UserStatus.PENDING;
            case APPROVED -> UserStatus.ACTIVE;
            case REJECTED -> UserStatus.INACTIVE;
        };
    }

    // Builder
    public static class Builder {
        private final Set<UserRole> roles = new HashSet<>();
        private UserId id;
        private Username username;
        private HashedPassword password;
        private PersonName name;
        private EmailAddress email;
        private PhoneNumber phoneNumber;
        private ApprovalState approvalState;

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

        public UserBusiness build() {
            return new UserBusiness(this);
        }
    }
}
