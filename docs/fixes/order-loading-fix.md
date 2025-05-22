# Order Loading Fix

## Issue
The application was failing to load orders from the database with the following error:
```
java.sql.SQLException: no such column: 'delivery_information'
```

This error occurred in the `SqlOrderRepository.mapResultSetToOrder` method, which was trying to access a column called "delivery_information", but the column in the database is called "delivery_address".

Additionally, the `fetchCustomer` method was using a hardcoded CustomerId "dwd" instead of the actual customerId from the database.

## Root Cause
1. The `SqlOrderRepository.mapResultSetToOrder` method was trying to access a column called "delivery_information", but the column in the database is called "delivery_address".
2. The `fetchCustomer` method was using a hardcoded CustomerId "dwd" instead of the actual customerId from the database.

## Solution
1. Modified the `SqlOrderRepository.mapResultSetToOrder` method to use the correct column name "delivery_address" instead of "delivery_information".
2. Added a try-catch block to handle the case where the column doesn't exist or there's an error accessing it.
3. Fixed the `fetchCustomer` method to use the actual customerId from the database instead of a hardcoded value.
4. Added a try-catch block to handle the case where the customer can't be fetched, and in that case, it sets the customer ID directly from the database.

## Benefits
These changes ensure that:
1. The application can load orders from the database without throwing an SQLException.
2. The application uses the actual customerId from the database instead of a hardcoded value.
3. The application is more robust and can handle cases where columns don't exist or there are errors accessing them.

## Future Improvements
In the future, the following improvements could be made:
1. Update the database schema to match the domain model more closely.
2. Add more robust error handling and logging.
3. Add more comprehensive tests to ensure that the application can handle various edge cases.