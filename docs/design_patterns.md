# Design Patterns in ExamProjectBelman

This document provides an overview of the design patterns used in the ExamProjectBelman project.

## Builder Pattern

### Implementation in Report Class

The Builder pattern is implemented in the `Report` class to simplify the creation of complex Report objects. The pattern provides a clear and fluent API for constructing Report instances with many optional parameters.

```java
// Example usage of the Builder pattern
Report report = Report.builder()
    .id(reportId)
    .orderId(orderId)
    .approvedPhotos(approvedPhotos)
    .generatedBy(user)
    .generatedAt(timestamp)
    .recipient(recipient)
    .format(ReportFormat.PDF)
    .status(ReportStatus.DRAFT)
    .comments("Test comments")
    .version(1)
    .build();
```

### Benefits

1. **Improved Readability**: The Builder pattern makes the code more readable by clearly indicating which parameter is being set.
2. **Flexibility**: It allows for the creation of objects with different combinations of parameters without having to create numerous constructors.
3. **Immutability**: It supports the creation of immutable objects, which are thread-safe and easier to reason about.
4. **Default Values**: It provides a way to set default values for optional parameters.
5. **Parameter Validation**: It allows for validation of parameters before the object is created.

## Factory Method Pattern

### Implementation in Customer Class

The Factory Method pattern is implemented in the `Customer` class to create different types of customers (individual or company) while encapsulating the creation logic.

```java
// Example usage of the Factory Method pattern
Customer individual = Customer.individual(
    CustomerId.newId(),
    new PersonName("John", "Doe"),
    new EmailAddress("john.doe@example.com")
);

Customer company = Customer.company(
    CustomerId.newId(),
    new Company("Acme Inc.", "12345", "123 Main St"),
    new EmailAddress("info@acme.com")
);
```

### Benefits

1. **Encapsulation**: The Factory Method pattern encapsulates the creation logic, making it easier to change how objects are created without affecting the rest of the code.
2. **Type Safety**: It ensures that the correct type of object is created based on the factory method used.
3. **Naming**: It provides meaningful names for different ways of creating objects, making the code more readable.
4. **Centralization**: It centralizes the creation logic, making it easier to maintain and update.

## Recommendations for Further Improvements

1. **Implement Builder Pattern for Order and User Classes**: The Order and User classes also have multiple constructors with many parameters, making them good candidates for the Builder pattern.
2. **Consider Strategy Pattern for Report Generation**: If there are different ways to generate reports (e.g., PDF, HTML), consider using the Strategy pattern to encapsulate these algorithms.
3. **Consider Observer Pattern for Report Status Changes**: If other parts of the system need to be notified when a report's status changes, consider using the Observer pattern.
4. **Consider Command Pattern for Report Operations**: If there are complex operations that can be performed on reports (e.g., finalizing, archiving), consider using the Command pattern to encapsulate these operations.