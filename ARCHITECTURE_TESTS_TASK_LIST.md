# Architecture Tests Task List

This document outlines the tasks needed to improve code quality, enforce clean code practices, and update the architecture tests to ensure the codebase follows the defined architectural rules.

## Architecture Tests

### Dependency Validation

- [x] Create a new architecture test (`DependencyValidationTest.java`) that implements the dependency validation rules from `.idea/scopes/scope_settings.xml`
- [ ] Fix violations in the codebase to make the dependency validation test pass
- [ ] Add more specific tests for critical architectural boundaries
- [ ] Add tests to verify that bootstrap code only exists in the bootstrap package
- [ ] Add tests to verify that UI code doesn't directly access repository implementations

### Rich Business Entities

- [x] Update `RichBusinessEntitiesTest.java` to use the new package structure:
  - [x] Update package references from `com.belman.business.domain..` to `com.belman.domain..`
  - [x] Update class name patterns to match the new naming conventions (e.g., `BusinessObject` instead of `Entity`)
  - [x] Add tests for new business object types

### Three-Layer Architecture

- [x] Update `ThreeLayerArchitectureTest.java` to use the new package structure:
  - [x] Update package references to match the new structure
  - [x] Update layer definitions to match the new scopes
  - [x] Uncomment and fix the `layeredArchitectureShouldBeRespected()` test

### MVVM Architecture

- [x] Update `MVVMAndPresentationRulesTest.java` to use the new package structure:
  - [x] Update package references from `com.belman.presentation..` to `com.belman.ui..`
  - [x] Update tests to verify MVVM pattern compliance in the new structure

## Clean Code Practices

### Naming Conventions

- [ ] Ensure consistent naming across the codebase:
  - [ ] Use `BusinessObject` instead of `Entity` or `Aggregate`
  - [ ] Use `DataAccess` instead of `Repository`
  - [ ] Use consistent naming for interfaces and implementations

### Code Organization

- [ ] Ensure classes are in the correct packages according to their responsibilities:
  - [ ] UI components in `com.belman.ui`
  - [ ] Business logic in `com.belman.service`
  - [ ] Data access in `com.belman.repository`
  - [ ] Domain objects in `com.belman.domain`
  - [ ] Common utilities in `com.belman.common`
  - [ ] Bootstrap code in `com.belman.repository.bootstrap`

### Code Quality

- [ ] Add tests for code quality rules:
  - [ ] No unused imports
  - [ ] No unused variables
  - [ ] No empty catch blocks
  - [ ] No magic numbers
  - [ ] No duplicate code
  - [ ] Methods should not be too long
  - [ ] Classes should not be too large
  - [ ] Cyclomatic complexity should not be too high

### Documentation

- [ ] Ensure all public APIs are documented with JavaDoc
- [ ] Add package-info.java files to describe package purposes
- [ ] Update README.md with architecture overview

## Implementation Plan

1. Start by updating the architecture tests to match the new package structure
2. Run the tests to identify violations
3. Fix the most critical violations first (e.g., incorrect package dependencies)
4. Gradually fix remaining violations
5. Add more specific tests for code quality
6. Document the architecture and clean code practices
