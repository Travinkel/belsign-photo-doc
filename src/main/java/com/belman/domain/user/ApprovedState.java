package com.belman.domain.user;

import com.belman.domain.photo.ApprovalStatus;
import java.time.Instant;

public class ApprovedState implements ApprovalState {

    @Override
    public ApprovalStatus getStatusEnum() {
        return ApprovalStatus.APPROVED;
    }

    @Override
    public void approve(UserAggregate user, UserAggregate reviewer, Instant reviewedAt) {
        throw new UnsupportedOperationException("Cannot approve an already approved state");
    }

    @Override
    public void reject(UserAggregate user, UserAggregate reviewer, Instant reviewedAt, String reason) {
        throw new UnsupportedOperationException("Cannot reject an approved state");
    }
}