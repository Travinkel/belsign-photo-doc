# Order Number Format Fix

## Issue
The application was failing to load orders from the database with the error:
```
java.lang.IllegalArgumentException: Invalid order number format: ORD-01-230701-WLD-0001. Expected format: MM/YY-CUSTOMER-SEQUENCE
```

This error occurred in the `OrderNumber` constructor, which was validating that order numbers follow the format `MM/YY-CUSTOMER-SEQUENCE` (e.g., "01/23-123456-12345678"). However, the order numbers in the database were in a different format: `ORD-XX-YYMMDD-ABC-NNNN` (e.g., "ORD-01-230701-WLD-0001").

## Root Cause
The `OrderNumber` class was designed to enforce a specific format for order numbers, but the actual order numbers in the database were in a different format. This mismatch caused the validation in the `OrderNumber` constructor to fail when loading orders from the database.

The issue occurred in the `SqlOrderRepository.mapResultSetToOrder` method, which retrieves the order number from the database and creates a new `OrderNumber` object with it:

```java
String orderNumberStr = rs.getString("order_number");
if (orderNumberStr != null) {
    orderBusiness.setOrderNumber(new OrderNumber(orderNumberStr));
}
```

## Solution
The solution was to update the `OrderNumber` class to accept both the legacy format (`ORD-XX-YYMMDD-ABC-NNNN`) and the new format (`MM/YY-CUSTOMER-SEQUENCE`). This was done by:

1. Modifying the validation in the `OrderNumber` constructor to accept both formats:
```java
// Accept both the legacy format (ORD-XX-YYMMDD-ABC-NNNN) and the new format (MM/YY-CUSTOMER-SEQUENCE)
if (!value.matches("\\d{2}/\\d{2}-\\d{6}-\\d{8}") && !value.matches("ORD-\\d{2}-\\d{6}-[A-Z]{3}-\\d{4}")) {
    throw new IllegalArgumentException("Invalid order number format: " + value +
                                       ". Expected format: MM/YY-CUSTOMER-SEQUENCE or ORD-XX-YYMMDD-ABC-NNNN");
}
```

2. Adding a private `isLegacyFormat` method to check if the order number is in the legacy format:
```java
private boolean isLegacyFormat() {
    return value.startsWith("ORD-");
}
```

3. Updating the `getMonthYear`, `getCustomerIdentifier`, and `getSequenceNumber` methods to handle both formats:
```java
public String getMonthYear() {
    if (isLegacyFormat()) {
        // For legacy format, extract date part (YYMMDD) and convert to MM/YY
        String datePart = value.split("-")[2];
        if (datePart.length() >= 6) {
            String yy = datePart.substring(0, 2);
            String mm = datePart.substring(2, 4);
            return mm + "/" + yy;
        }
        return "";
    }
    return value.substring(0, 5);
}
```

## Benefits
This solution allows the application to work with both the legacy format and the new format, providing backward compatibility while still enforcing validation for new order numbers. It also ensures that the `getMonthYear`, `getCustomerIdentifier`, and `getSequenceNumber` methods work correctly regardless of the format.

## Future Considerations
In the future, it may be desirable to standardize on a single format for order numbers. This could be done by:

1. Creating a migration script to update all existing order numbers in the database to the new format
2. Updating the `OrderNumber` class to only accept the new format
3. Ensuring that all code that creates new order numbers uses the new format

However, for now, supporting both formats provides the most seamless experience for users and minimizes the risk of data migration issues.