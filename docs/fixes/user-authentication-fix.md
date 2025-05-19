# User Authentication Fix

## Issue
Users could not log in despite being active in the SQLite database. The error logs showed authentication failures with the message "Authentication failed: User production is not active".

## Root Cause
The issue was in the `SqlUserRepository` class, specifically in the `mapResultSetToUser` method. When a user was loaded from the database, their status was correctly set based on the "status" column in the database, but their approval state was not set based on this status.

The authentication service checks the user's approval state (not their status) to determine if they can log in. If a user's approval state is not explicitly set, it defaults to PENDING, which prevents them from logging in even if their status is ACTIVE in the database.

## Fix
The fix was to modify the `mapResultSetToUser` method in `SqlUserRepository` to set the user's approval state based on their status from the database:

```java
// Set approval state based on status
ApprovalState approvalState;
if (status == UserStatus.ACTIVE) {
    approvalState = ApprovalState.createApproved();
} else if (status == UserStatus.INACTIVE) {
    approvalState = ApprovalState.createRejected("User is inactive");
} else {
    approvalState = ApprovalState.createPendingState();
}

// Create a UserBusiness using the Builder pattern
UserBusiness.Builder builder = new UserBusiness.Builder()
        .id(id)
        .username(username)
        .password(password)
        .email(email)
        .approvalState(approvalState);
```

Now, when a user is loaded from the database:
- If their status is ACTIVE, their approval state will be set to APPROVED
- If their status is INACTIVE, their approval state will be set to REJECTED
- For any other status (like PENDING), their approval state will be set to PENDING

This ensures that users with ACTIVE status in the database will have an APPROVED approval state in the domain model, allowing them to log in.

## Testing
The fix was tested using the `UserAuthenticationTest` class, which verifies that users can log in with valid credentials and are denied access with invalid credentials. All tests passed, confirming that the fix resolves the authentication issue.

## Related Classes
- `DefaultAuthenticationService`: Checks the user's approval state to determine if they can log in
- `UserBusiness`: Contains the user's approval state and status
- `ApprovalState`: Interface for different approval states (PENDING, APPROVED, REJECTED)
- `SqlUserRepository`: Maps between the database and the domain model