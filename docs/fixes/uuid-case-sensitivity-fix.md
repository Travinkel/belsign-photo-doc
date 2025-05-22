# UUID Case Sensitivity Fix

## Issue Description

We encountered a case-sensitivity bug in our SQLite mode where assigned orders weren't showing for production users. The root cause was that the `assigned_to` field in the `ORDERS` table contains lowercase UUIDs, but the Java code was comparing them against `UserId` and `OrderId` strings that might be uppercase.

This broke assigned order filtering in production mode when using SQLite, as the string comparison was case-sensitive.

## Investigation

1. **Domain Model Classes**: We had already normalized UUIDs to lowercase in all ID classes (`UserId`, `OrderId`, `PhotoId`, `CustomerId`, `ReportId`), which was a good first step.

2. **Database Storage**: In SQLite, UUIDs are stored as plain `TEXT` fields. The production user IDs are generated using `hex(randomblob(16))`, which produces lowercase hexadecimal strings.

3. **Comparison Issue**: Despite normalizing the IDs in the domain classes, the comparison in the service layer was still case-sensitive because it was comparing the `UserId` objects directly instead of their string values.

## Fix Implementation

We fixed the issue by modifying the comparison in two key places:

1. **DefaultOrderProgressService.getAssignedOrder**:
   ```java
   // Before
   order.getAssignedTo().id().equals(worker.getId())
   
   // After
   order.getAssignedTo().id().id().equals(worker.getId().id())
   ```

2. **AssignedOrderViewModel.loadAssignedOrder**:
   ```java
   // Before
   o.getAssignedTo().id().equals(user.getId())
   
   // After
   o.getAssignedTo().id().id().equals(user.getId().id())
   ```

The key difference is that we're now comparing the string values of the IDs (`id().id()`) rather than the ID objects themselves (`id()`). Since the string values are normalized to lowercase in the domain classes, this ensures a case-insensitive comparison.

## Verification

We created a new test class `AssignedOrderFilteringTest` that verifies the fix works correctly. The test:

1. Creates a worker user with a mixed-case UUID
2. Creates an order assigned to the worker with the same UUID in lowercase
3. Calls the `getAssignedOrder` method
4. Verifies that it correctly returns the assigned order, despite the case difference in the UUIDs

The test passes, confirming that our fix works correctly.

## Lessons Learned

1. **Consistent Comparison**: When comparing IDs, always compare the normalized string values rather than the objects themselves.
2. **Case Sensitivity**: Be aware of case sensitivity issues when working with UUIDs, especially when they're stored as strings in the database.
3. **Testing**: Create tests that specifically verify case-insensitive comparison works correctly.

## Related Files

- `DefaultOrderProgressService.java`
- `AssignedOrderViewModel.java`
- `AssignedOrderFilteringTest.java`
- Domain model ID classes: `UserId.java`, `OrderId.java`, `PhotoId.java`, `CustomerId.java`, `ReportId.java`