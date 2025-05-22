# Implementation: Photo Approval Workflow with Status Tracking

## Task Description
Implement photo approval workflow with status tracking as specified in the implementation task list.

## Implementation Details

### 1. Created ApprovalStatus Enum
Created an enum `ApprovalStatus` within the `PhotoDocument` class to represent the different states in the approval workflow:
- `PENDING`: Initial state when a photo is uploaded but not yet reviewed by QA
- `APPROVED`: The photo has been reviewed and approved by QA personnel
- `REJECTED`: The photo has been reviewed and rejected by QA personnel

### 2. Enhanced PhotoDocument Class
Implemented approval workflow functionality in the `PhotoDocument` class:
- Added an `status` field to track the current approval status (initialized to `PENDING`)
- Added `reviewedBy` field to store the user who reviewed the photo
- Added `reviewedAt` field to store the timestamp when the photo was reviewed
- Added `reviewComment` field to store optional comments, especially for rejected photos

### 3. Implemented Workflow Methods
Added methods to manage the approval workflow:
- `approve(UserReference reviewer, Timestamp reviewedAt)`: Marks the photo as approved
- `reject(UserReference reviewer, Timestamp reviewedAt, String reason)`: Marks the photo as rejected with an optional reason
- `isApproved()`: Checks if the photo has been approved
- `isPending()`: Checks if the photo is still pending review
- `getStatus()`: Returns the current approval status

### 4. Added Validation and State Tracking
Implemented proper validation and state tracking:
- Photos can only be approved or rejected if they are in the `PENDING` state
- Attempting to approve or reject a photo that is already approved or rejected throws an `IllegalStateException`
- The reviewer and review timestamp are required (null checks with appropriate error messages)
- The last modified timestamp is updated whenever the status changes

## Benefits
This implementation provides several benefits:
1. **Clear Workflow**: Establishes a clear workflow for photo approval with well-defined states
2. **Traceability**: Tracks who reviewed each photo and when
3. **Feedback Mechanism**: Allows QA personnel to provide feedback on rejected photos
4. **Data Integrity**: Ensures that photos cannot be approved or rejected multiple times
5. **Status Tracking**: Makes it easy to filter photos by their approval status

## Relation to Requirements
This implementation satisfies the fourth task in the Domain Layer section of the implementation task list:
> Implement photo approval workflow with status tracking

The implementation provides a complete solution for managing the approval workflow of photos, which is essential for the quality control process in the BelSign system.