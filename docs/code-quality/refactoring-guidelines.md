# Belsign Photo Documentation - Refactoring Guidelines

This document provides guidelines for refactoring code in the Belsign Photo Documentation project. It covers when to refactor, how to refactor safely, common refactoring patterns, and best practices.

## Table of Contents

1. [What is Refactoring?](#what-is-refactoring)
2. [When to Refactor](#when-to-refactor)
3. [Refactoring Process](#refactoring-process)
4. [Common Refactoring Patterns](#common-refactoring-patterns)
5. [Refactoring Specific Areas](#refactoring-specific-areas)
6. [Testing During Refactoring](#testing-during-refactoring)
7. [Tools and Resources](#tools-and-resources)

## What is Refactoring?

Refactoring is the process of restructuring existing code without changing its external behavior. The goal of refactoring is to improve the code's internal structure, making it more maintainable, readable, and efficient.

### Benefits of Refactoring

- **Improved Readability**: Makes code easier to understand
- **Enhanced Maintainability**: Makes future changes easier
- **Reduced Technical Debt**: Addresses accumulated design issues
- **Better Performance**: Can lead to more efficient code
- **Bug Reduction**: Often reveals hidden bugs

### Refactoring vs. Rewriting

Refactoring is not the same as rewriting:

- **Refactoring**: Incremental improvements while preserving behavior
- **Rewriting**: Creating new code from scratch to replace existing functionality

Always prefer refactoring over rewriting when possible, as it's generally less risky and more efficient.

## When to Refactor

### Signs That Code Needs Refactoring

1. **Code Smells**:
   - Duplicate code
   - Long methods (> 50 lines)
   - Large classes (> 500 lines)
   - Long parameter lists (> 5 parameters)
   - Excessive comments explaining complex code
   - Deeply nested conditionals
   - Switch statements that appear in multiple places

2. **Technical Indicators**:
   - High cyclomatic complexity (> 15)
   - Low cohesion (classes doing too many unrelated things)
   - High coupling (too many dependencies)
   - Poor test coverage (< 70%)
   - Frequent bugs in a specific area

3. **Development Friction**:
   - Changes require modifications in many places
   - Developers avoid working on certain parts of the code
   - New features take longer than expected to implement
   - Bug fixes often introduce new bugs

### The Boy Scout Rule

Follow the "Boy Scout Rule": Leave the code better than you found it. This means making small refactoring improvements whenever you work on a piece of code, even if the primary task is a feature or bug fix.

### Prioritizing Refactoring

Not all code needs refactoring with the same urgency. Prioritize based on:

1. **Impact**: Focus on code that's frequently changed or central to the application
2. **Risk**: Consider the risk of breaking functionality
3. **Effort**: Start with "low-hanging fruit" that provides good value for minimal effort
4. **Technical Debt**: Address areas with the highest technical debt first

## Refactoring Process

### Before You Start

1. **Understand the code**: Make sure you understand what the code does before refactoring it
2. **Ensure test coverage**: Verify that tests exist for the code you're refactoring
3. **Create a baseline**: Run tests to ensure they pass before you start
4. **Work in a separate branch**: Create a dedicated branch for refactoring

### Step-by-Step Process

1. **Make a plan**: Identify what you want to improve and how
2. **Take small steps**: Refactor in small, incremental changes
3. **Run tests after each change**: Ensure behavior remains the same
4. **Commit frequently**: Make small, focused commits with clear messages
5. **Document changes**: Update documentation if necessary

### After Refactoring

1. **Review the changes**: Self-review or request a code review
2. **Verify test coverage**: Ensure tests still cover all code paths
3. **Measure improvements**: Quantify improvements in metrics if possible
4. **Share knowledge**: Communicate changes to the team

## Common Refactoring Patterns

### Method-Level Refactoring

#### Extract Method

**When to use**: When a section of code can be grouped together and given a name.

**Before**:
```java
public void printInvoice() {
    // Print header
    System.out.println("INVOICE");
    System.out.println("Customer: " + customer.getName());
    System.out.println("Date: " + new Date());
    
    // Print items
    for (Item item : items) {
        System.out.println(item.getName() + ": $" + item.getPrice());
    }
    
    // Print footer
    System.out.println("Total: $" + calculateTotal());
    System.out.println("Thank you for your business!");
}
```

**After**:
```java
public void printInvoice() {
    printHeader();
    printItems();
    printFooter();
}

private void printHeader() {
    System.out.println("INVOICE");
    System.out.println("Customer: " + customer.getName());
    System.out.println("Date: " + new Date());
}

private void printItems() {
    for (Item item : items) {
        System.out.println(item.getName() + ": $" + item.getPrice());
    }
}

private void printFooter() {
    System.out.println("Total: $" + calculateTotal());
    System.out.println("Thank you for your business!");
}
```

#### Inline Method

**When to use**: When a method body is as clear as its name.

**Before**:
```java
public double getTotal() {
    return calculateTotal();
}

private double calculateTotal() {
    return price * quantity;
}
```

**After**:
```java
public double getTotal() {
    return price * quantity;
}
```

#### Replace Temp with Query

**When to use**: When a temporary variable holds the result of an expression.

**Before**:
```java
public double calculateTotal() {
    double basePrice = quantity * itemPrice;
    double discount = Math.max(0, quantity - 500) * itemPrice * 0.05;
    double shipping = Math.min(basePrice * 0.1, 100.0);
    return basePrice - discount + shipping;
}
```

**After**:
```java
public double calculateTotal() {
    return getBasePrice() - getDiscount() + getShipping();
}

private double getBasePrice() {
    return quantity * itemPrice;
}

private double getDiscount() {
    return Math.max(0, quantity - 500) * itemPrice * 0.05;
}

private double getShipping() {
    return Math.min(getBasePrice() * 0.1, 100.0);
}
```

### Class-Level Refactoring

#### Extract Class

**When to use**: When a class has too many responsibilities.

**Before**:
```java
public class Order {
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    // Order details...
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String name) { this.customerName = name; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String email) { this.customerEmail = email; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String phone) { this.customerPhone = phone; }
    // Order methods...
}
```

**After**:
```java
public class Order {
    private Customer customer;
    // Order details...
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    // Order methods...
}

public class Customer {
    private String name;
    private String email;
    private String phone;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
```

#### Move Method

**When to use**: When a method is used more by another class than the class it's in.

**Before**:
```java
public class Order {
    private Customer customer;
    
    public String getCustomerName() {
        return customer.getName();
    }
}

public class Customer {
    private String name;
    
    public String getName() {
        return name;
    }
}
```

**After**:
```java
public class Order {
    private Customer customer;
    
    public Customer getCustomer() {
        return customer;
    }
}

public class Customer {
    private String name;
    
    public String getName() {
        return name;
    }
}

// Usage: order.getCustomer().getName() instead of order.getCustomerName()
```

### Architecture-Level Refactoring

#### Replace Inheritance with Composition

**When to use**: When inheritance creates unnecessary coupling.

**Before**:
```java
public class Employee extends Person {
    private double salary;
    
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
```

**After**:
```java
public class Employee {
    private Person person;
    private double salary;
    
    public Person getPerson() { return person; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}
```

#### Introduce Design Pattern

**When to use**: When you recognize a common problem that a design pattern can solve.

Example: Introducing the Observer pattern for event handling.

## Refactoring Specific Areas

### UI Layer Refactoring

1. **Separate UI logic from business logic**:
   - Move business logic from controllers to view models
   - Use the MVVM pattern consistently

2. **Improve UI component reusability**:
   - Extract reusable components
   - Use composition for complex UI elements

3. **Enhance UI responsiveness**:
   - Move long-running operations to background threads
   - Use JavaFX properties and bindings correctly

### Service Layer Refactoring

1. **Apply Single Responsibility Principle**:
   - Split large services into smaller, focused ones
   - Extract cross-cutting concerns into separate services

2. **Improve error handling**:
   - Standardize exception handling
   - Use custom exceptions for domain-specific errors

3. **Enhance testability**:
   - Reduce dependencies through dependency injection
   - Make services more modular and focused

### Data Access Layer Refactoring

1. **Optimize database access**:
   - Reduce database round-trips
   - Use batch operations for bulk updates
   - Implement caching where appropriate

2. **Standardize repository implementations**:
   - Apply the Repository pattern consistently
   - Create base repository classes for common operations

3. **Improve transaction management**:
   - Ensure proper resource cleanup
   - Use appropriate transaction boundaries

## Testing During Refactoring

### Test-Driven Refactoring

1. **Write tests first**: If tests don't exist, write them before refactoring
2. **Run tests frequently**: After each small change
3. **Maintain test coverage**: Ensure refactored code is still well-covered

### Types of Tests for Refactoring

1. **Unit tests**: Test individual components in isolation
2. **Integration tests**: Test interactions between components
3. **Characterization tests**: Document existing behavior before refactoring
4. **Performance tests**: Ensure refactoring doesn't degrade performance

### Test Refactoring

Don't forget to refactor tests too:

1. **Remove duplication**: Extract common setup and assertion code
2. **Improve readability**: Use descriptive test names and assertions
3. **Enhance maintainability**: Make tests less brittle and more focused

## Tools and Resources

### Refactoring Tools

1. **IDE Refactoring Support**:
   - IntelliJ IDEA: Extensive refactoring capabilities
   - Eclipse: Built-in refactoring tools

2. **Static Analysis Tools**:
   - SonarQube: Identifies code smells and suggests improvements
   - PMD: Detects potential problems and suboptimal code

3. **Metrics Tools**:
   - JaCoCo: Measures test coverage
   - Metrics Reloaded (IntelliJ plugin): Calculates code metrics

### Learning Resources

1. **Books**:
   - "Refactoring: Improving the Design of Existing Code" by Martin Fowler
   - "Working Effectively with Legacy Code" by Michael Feathers
   - "Clean Code" by Robert C. Martin

2. **Online Resources**:
   - [Refactoring Guru](https://refactoring.guru/): Catalog of refactoring techniques
   - [SourceMaking](https://sourcemaking.com/): Code smells and refactoring patterns
   - [IntelliJ IDEA Refactoring Tutorial](https://www.jetbrains.com/help/idea/refactoring-source-code.html)

## Conclusion

Refactoring is an essential practice for maintaining code quality and managing technical debt in the Belsign Photo Documentation project. By following these guidelines, you can improve the codebase incrementally while minimizing the risk of introducing bugs.

Remember that refactoring is not a one-time activity but an ongoing process. Regular, small refactorings are generally safer and more effective than large, infrequent ones.

Always communicate with your team about significant refactorings, especially those that might affect interfaces or behavior that other components depend on.