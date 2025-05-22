# Implementation: Order Status Workflow

## Task Description
Implement order status workflow (pending, in progress, completed, approved) as specified in the implementation task list.

## Implementation Details

### 1. OrderStatus Enum
The OrderStatus enum already includes all the necessary statuses for the workflow:
- `PENDING`: Initial state when an order is created but not yet started
- `IN_PROGRESS`: The order is currently being processed
- `COMPLETED`: The order has been completed but not yet approved
- `APPROVED`: The order has been approved by QA
- `REJECTED`: The order has been rejected by QA and needs rework
- `DELIVERED`: The order has been delivered to the customer
- `CANCELLED`: The order has been cancelled

### 2. Workflow Methods in OrderBusiness
The OrderBusiness class includes methods for all the required status transitions:

1. **Start Processing**: Transitions from PENDING to IN_PROGRESS
   ```java
   public void startProcessing() {
       if (status != OrderStatus.PENDING) {
           throw new IllegalStateException("Cannot start processing an order with status: " + status);
       }
       status = OrderStatus.IN_PROGRESS;
       updateLastModifiedAt();
   }
   ```

2. **Complete Processing**: Transitions from IN_PROGRESS to COMPLETED
   ```java
   public void completeProcessing() {
       if (status != OrderStatus.IN_PROGRESS) {
           throw new IllegalStateException("Cannot complete an order with status: " + status);
       }
       status = OrderStatus.COMPLETED;
       this.completedAt = new Timestamp(Instant.now());
       updateLastModifiedAt();
   }
   ```

3. **Approve**: Transitions from COMPLETED to APPROVED
   ```java
   public void approve() {
       if (status != OrderStatus.COMPLETED) {
           throw new IllegalStateException("Cannot approve an order with status: " + status);
       }
       status = OrderStatus.APPROVED;
       updateLastModifiedAt();
   }
   ```

4. **Reject**: Transitions from COMPLETED to REJECTED
   ```java
   public void reject() {
       if (status != OrderStatus.COMPLETED) {
           throw new IllegalStateException("Cannot reject an order with status: " + status);
       }
       status = OrderStatus.REJECTED;
       updateLastModifiedAt();
   }
   ```

5. **Deliver**: Transitions from APPROVED to DELIVERED
   ```java
   public void deliver() {
       if (status != OrderStatus.APPROVED) {
           throw new IllegalStateException("Cannot deliver an order with status: " + status);
       }
       status = OrderStatus.DELIVERED;
       updateLastModifiedAt();
   }
   ```

6. **Cancel**: Transitions to CANCELLED from any status except DELIVERED
   ```java
   public void cancel() {
       if (status == OrderStatus.DELIVERED) {
           throw new IllegalStateException("Cannot cancel an order that has already been delivered");
       }
       status = OrderStatus.CANCELLED;
       updateLastModifiedAt();
   }
   ```

### 3. Additional Helper Methods

1. **isReadyForQaReview**: Checks if an order is ready for QA review
   ```java
   public boolean isReadyForQaReview() {
       return status == OrderStatus.COMPLETED && !photoIds.isEmpty();
   }
   ```

## Benefits
This implementation provides several benefits:
1. **Clear Workflow**: Establishes a clear workflow for orders with well-defined states
2. **Validation**: Ensures that orders can only transition between valid states
3. **Traceability**: Tracks when orders are completed with the completedAt timestamp
4. **Flexibility**: Supports all the required status transitions plus additional ones for a complete workflow
5. **Error Prevention**: Prevents invalid status transitions with clear error messages

## Relation to Requirements
This implementation satisfies the second task in the Order Management section of the implementation task list:
> Implement order status workflow (pending, in progress, completed, approved)

The implementation provides a complete solution for managing the order workflow, which is essential for tracking the progress of orders in the BelSign system.