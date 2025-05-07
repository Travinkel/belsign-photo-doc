package com.belman.domain.user;

import java.sql.Timestamp;

public class PendingApprovalState implements ApprovalState {

    @Override
    public String getStatus() {
        return "PENDING";
    }

    @Override
    public void approve(UserAggregate user, UserAggregate reviewer, Timestamp reviewedAt) {
        user.setApprovalState(new ApprovedState());
    }

    @Override
    public void reject(UserAggregate user, UserAggregate reviewer, Timestamp reviewedAt, String reason) {
        user.setApprovalState(new RejectedState(reason));
    }
}
