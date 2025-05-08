# BelSign Photo Documentation System - Development Guidelines

## Table of Contents
1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Architecture](#architecture)
4. [Coding Standards](#coding-standards)
5. [Testing Guidelines](#testing-guidelines)
6. [User Interface Guidelines](#user-interface-guidelines)
7. [Mobile Compatibility](#mobile-compatibility)
8. [Documentation](#documentation)
9. [Version Control](#version-control)
10. [Build and Deployment](#build-and-deployment)

## Introduction

This document provides guidelines for developers working on the BelSign Photo Documentation System. It covers architecture, coding standards, testing, and other important aspects of the development process. Following these guidelines will ensure consistency, maintainability, and quality of the codebase.

## Project Overview

### Purpose
The BelSign Photo Documentation System is designed to help Belman A/S with their quality control process by:
- Attaching images to order numbers and saving them to a database
- Autogenerating QC reports
- Enabling sending of emails with QC documentation directly to customers

### User Groups
The system serves three main user groups:
1. **Production Workers**: Taking pictures of products
2. **Quality Assurance Employees**: Approving the documentation
3. **Administrators**: Assigning roles to employees

### Key Requirements
- User-friendly interface for non-technical users
- Report preview generation
- Tablet-friendly design
- Java desktop application using JavaFX
- Three-layered architecture
- Design patterns implementation
- Automated testing

## Architecture

The BelSign Photo Documentation System follows a three-layer architecture combined with Clean Architecture principles, Domain-Driven Design (DDD), and MVVM+C patterns adapted for JavaFX.

### Three-Layer Architecture

The system is divided into three distinct layers:

1. **Presentation Layer** (`com.belman.presentation`)
   - User interface components
   - View models
   - Controllers
   - Navigation/routing

2. **Business Layer** (`com.belman.business`)
   - Domain model (entities, value objects, aggregates)
   - Business logic
   - Use cases
   - Application services
   - Domain events

3. **Data Layer** (`com.belman.data`)
   - Repository implementations
   - Data access
   - External services integration
   - Infrastructure concerns

### Dependency Rules

The following dependency rules must be followed:

1. The Presentation layer may depend on the Business layer but not on the Data layer
2. The Business layer may depend on its own components but not on the Presentation or Data layers
3. The Data layer may depend on the Business layer (to implement interfaces) but not on the Presentation layer

### Component Placement Guidelines

#### Presentation Layer
- **Views**: All UI components should be placed in `com.belman.presentation.views.[feature]`
- **ViewModels**: All view models should be placed in `com.belman.presentation.views.[feature]` and named with the suffix "ViewModel"
- **Controllers**: All controllers should be placed in `com.belman.presentation.views.[feature]` and named with the suffix "Controller"

#### Business Layer
- **Domain Model**: All domain entities, value objects, and aggregates should be placed in `com.belman.business.domain`
- **Use Cases**: All use cases should be placed in `com.belman.business.usecases.[feature]` and named with the suffix "UseCase"
- **Services**: All business services should be placed in `com.belman.business.services` or `com.belman.business.domain.[feature].services` and named with the suffix "Service"
- **Repository Interfaces**: All repository interfaces should be placed in `com.belman.business.domain.[feature]` and named with the suffix "Repository"

#### Data Layer
- **Repository Implementations**: All repository implementations should be placed in `com.belman.data.persistence` and named with a prefix indicating the storage mechanism (e.g., "InMemory", "Sql") and the suffix "Repository"
- **External Services**: All external service integrations should be placed in appropriate packages under `com.belman.data`

### MVVM+C Pattern for UI

For the presentation layer, we use an MVVM+C (Model-View-ViewModel + Controller) pattern adapted for JavaFX:

- **Model**: Domain entities and business logic
- **View**: FXML files and minimal JavaFX controller classes
- **ViewModel**: Classes that transform domain models into observable properties for views
- **Controller**: Coordinates between the view and the application layer

## Coding Standards

### General Guidelines
- Follow Java coding conventions
- Use meaningful names for classes, methods, and variables
- Keep methods short and focused on a single responsibility
- Document public APIs with JavaDoc comments
- Use appropriate design patterns where applicable
- Avoid code duplication
- Handle exceptions appropriately

### Naming Conventions
- **Classes**: PascalCase (e.g., `OrderRepository`)
- **Interfaces**: PascalCase, often with an "I" prefix (e.g., `IOrderRepository`) or describing a capability
- **Methods**: camelCase (e.g., `findOrderById`)
- **Variables**: camelCase (e.g., `orderList`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_ORDER_COUNT`)
- **Packages**: lowercase (e.g., `com.belman.business.domain`)

### Component-Specific Naming
- **ViewModels**: `[Feature]ViewModel`
- **Controllers**: `[Feature]Controller`
- **Views**: `[Feature]View`
- **Use Cases**: `[Action]UseCase`
- **Repository Interfaces**: `[Entity]Repository`
- **Repository Implementations**: `[Storage][Entity]Repository`

### Code Organization
- Group related functionality together
- Use packages to organize code by feature and layer
- Keep file sizes manageable
- Use consistent formatting and indentation

## Testing Guidelines

### Test Types
- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test interactions between components
- **Architecture Tests**: Verify architectural constraints
- **UI Tests**: Test user interface components

### Testing Best Practices
- Write tests before or alongside production code
- Keep tests independent and isolated
- Use descriptive test names that explain what is being tested
- Follow the Arrange-Act-Assert pattern
- Mock external dependencies
- Aim for high test coverage, especially for business logic
- Run tests regularly, ideally as part of the build process

### Architecture Tests
The architecture is enforced through automated tests in the `com.belman.architecture.rules.threelayer` package. These tests verify:

1. The layered architecture structure (Presentation, Business, Data)
2. Dependency rules between layers
3. Proper placement of components in their respective layers
4. Naming conventions for components

## User Interface Guidelines

### General Principles
- Design for simplicity and ease of use
- Provide clear feedback for user actions
- Use consistent UI patterns throughout the application
- Follow JavaFX best practices
- Implement responsive layouts that adapt to different screen sizes

### UI Components
- Use standard JavaFX controls where possible
- Create custom controls only when necessary
- Ensure all UI components are accessible
- Provide keyboard shortcuts for common actions

### User Experience
- Minimize the number of steps required to complete tasks
- Provide clear error messages and guidance
- Design for the specific needs of each user group
- Ensure the interface is intuitive for non-technical users

## Mobile Compatibility

The system must be designed to be tablet-friendly, with the following considerations:

### Responsive Design
- Use layouts that adapt to different screen sizes
- Ensure touch targets are large enough for finger interaction
- Test on different screen sizes and orientations

### Touch Optimization
- Implement touch-friendly UI components
- Provide appropriate feedback for touch interactions
- Avoid hover-dependent interactions

### Performance Considerations
- Optimize for mobile hardware
- Minimize resource usage
- Implement efficient data loading and caching strategies

### Offline Capabilities
- Allow for offline photo capture
- Implement data synchronization when connectivity is restored
- Provide clear indication of offline status

## Documentation

### Code Documentation
- Use JavaDoc comments for public APIs
- Document complex algorithms and business rules
- Keep documentation up-to-date with code changes

### Project Documentation
- Maintain up-to-date architecture documentation
- Document design decisions and rationales
- Provide user guides for different user groups
- Include setup and installation instructions

### Documentation Location
- Code documentation should be in the code itself
- Architecture documentation should be in the `docs/project` directory
- User guides should be in the `docs/user` directory

## Version Control

### Git Workflow
- Use feature branches for new development
- Create pull requests for code review
- Merge only after code review and tests pass
- Keep commits focused and with descriptive messages

### Commit Messages
- Use clear and descriptive commit messages
- Follow a consistent format (e.g., "Add feature X" or "Fix issue Y")
- Reference issue numbers where applicable

### Branch Naming
- Use descriptive branch names
- Follow a consistent format (e.g., `feature/add-photo-upload` or `bugfix/fix-report-generation`)

## Build and Deployment

### Build Process
- Use Maven for building the project
- Run tests as part of the build process
- Generate documentation during the build

### Continuous Integration
- Set up automated builds on commit
- Run all tests on each build
- Generate reports for test coverage and code quality

### Deployment
- Create release builds with proper versioning
- Include necessary documentation with releases
- Provide installation instructions

### Environment Configuration
- Use configuration files for environment-specific settings
- Avoid hardcoding configuration values
- Provide sensible defaults for configuration values