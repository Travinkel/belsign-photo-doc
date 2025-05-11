# Dependency Violations Task List

## Overview

This document outlines the dependency violations found in the codebase and provides a task list for fixing them. The violations were identified when running the DependencyValidationTest, which enforces the dependency rules between different layers of the application.

## Dependency Rules

The application follows a layered architecture with the following rules:
- UI layer can access Service, Domain, Common, and Bootstrap layers
- Service layer can access UI, Repository, Domain, Common, and Bootstrap layers
- Repository layer can access Service, Domain, Common, and Bootstrap layers
- Domain layer can only access Common layer
- Common layer cannot access any other layer
- Bootstrap layer can access all layers

## Violations

The build failed with numerous dependency violations, including:

1. Classes in the Bootstrap layer importing from Repository layer:
   - `StorageServiceFactory` importing from `com.belman.repository.platform`

2. Classes in the UI layer importing from Repository layer:
   - `TransitionPresets` importing from `com.belman.repository.platform`
   - `ViewFactory` importing `EmojiLogger` from Repository layer
   - `ViewLoader` importing from `com.belman.repository.platform`
   - `Router` importing `EmojiLogger` from Repository layer

3. Classes in the Common layer importing from Repository layer:
   - `SecureConfigStorage` importing from `com.belman.repository.platform`

4. Classes in the UI layer importing from Service.infrastructure layer:
   - `ViewLoader` importing from `com.belman.service.infrastructure.service`
   - `RouteGuardImpl` importing from `com.belman.service.infrastructure.routing`

## Task List

### 1. Fix Bootstrap Layer Violations

- [ ] Move `StorageServiceFactory` to the Bootstrap layer or create an interface in the Bootstrap layer
- [ ] Update imports in `StorageServiceFactory` to use classes from appropriate layers

### 2. Fix UI Layer Violations

- [ ] Move `EmojiLogger` to the Common layer or create an interface in the Common layer
- [ ] Update imports in `ViewFactory`, `Router`, and other UI classes to use the new location
- [ ] Move platform utilities from Repository layer to Common layer
- [ ] Update imports in `TransitionPresets`, `ViewLoader`, and other UI classes to use the new location
- [ ] Create interfaces in the Service layer for classes from `service.infrastructure` package
- [ ] Update imports in `ViewLoader`, `RouteGuardImpl`, and other UI classes to use the new interfaces

### 3. Fix Common Layer Violations

- [ ] Move platform utilities from Repository layer to Common layer
- [ ] Update imports in `SecureConfigStorage` and other Common classes to use the new location

### 4. Fix Service Layer Violations

- [ ] Identify and fix any Service layer classes importing from Repository layer
- [ ] Create interfaces for Repository layer classes that need to be accessed by Service layer

### 5. Fix Repository Layer Violations

- [ ] Identify and fix any Repository layer classes importing from UI or Service layers
- [ ] Create interfaces for UI or Service layer classes that need to be accessed by Repository layer

### 6. Fix Domain Layer Violations

- [ ] Identify and fix any Domain layer classes importing from UI, Service, or Repository layers
- [ ] Ensure Domain layer only depends on Common layer

## Implementation Strategy

1. Start with the Common layer violations, as fixing these will help resolve violations in other layers
2. Then fix the Domain layer violations
3. Next, fix the Repository layer violations
4. Then fix the Service layer violations
5. Finally, fix the UI and Bootstrap layer violations

This bottom-up approach ensures that lower layers are fixed before higher layers that depend on them.

## Testing

After fixing each set of violations:
1. Run the build to check for compilation errors
2. Run the DependencyValidationTest to verify that the dependency rules are enforced
3. Run other tests to ensure that the functionality still works as expected

## Conclusion

Fixing these dependency violations will improve the architecture of the application and make it more maintainable. It will also ensure that the codebase follows the dependency rules defined in the DependencyValidationTest.