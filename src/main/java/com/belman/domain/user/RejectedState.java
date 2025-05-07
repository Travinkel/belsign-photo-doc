package com.belman.domain.user;

import com.belman.domain.photo.ApprovalStatus;
import java.time.Instant;
import java.util.Objects;

public class RejectedState implements ApprovalState {
    private final String reason;

    public RejectedState(String reason) {
        this.reason = Objects.requireNonNull(reason, "Reason must not be null");
    }

    @Override
    public String getStatus() {
        return "REJECTED";
    }

    @Override
    public ApprovalStatus getStatusEnum() {
        return ApprovalStatus.REJECTED;
    }

    @Override
    public void approve(UserAggregate user, UserAggregate reviewer, Instant reviewedAt) {
        throw new UnsupportedOperationException("Cannot approve a rejected state");
    }

    @Override
    public void reject(UserAggregate user, UserAggregate reviewer, Instant reviewedAt, String reason) {
        throw new UnsupportedOperationException("Already rejected.");
    }
}