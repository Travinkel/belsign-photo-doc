# Implementation Plan for Updating UserAggregate to UserBusiness

## Overview

This document outlines the implementation plan for updating the codebase to use UserBusiness instead of UserAggregate. The goal is to ensure that the module package doesn't depend on other layers and follows the three-layer architecture (GUI, DAL, BLL + BE).

## Current State

1. **UserBusiness** is already a complete replacement for UserAggregate, with all the necessary methods and properties.
2. The **state classes** (ApprovalState, PendingApprovalState, etc.) already use UserBusiness instead of UserAggregate.
3. There's already a **UserDataAccess** interface and a **UserDataAccessAdapter** class that adapt the InMemoryUserRepository to use UserBusiness instead of UserAggregate.
4. Many other classes still use UserAggregate, including:
   - RBAC classes (RoleBasedAccessController, AccessPolicy)
   - PhotoDocument
   - Events (UserLoggedInEvent, UserLoggedOutEvent)
   - Services (SessionManager, ServiceInjector)
   - View models and controllers

## Implementation Plan

### 1. Update RBAC Classes

Update the following RBAC classes to use UserBusiness instead of UserAggregate:

- RoleBasedAccessController.java
- AccessPolicy.java
- RoleBasedAccessManager.java

### 2. Update PhotoDocument Class

Update the PhotoDocument class to use UserBusiness instead of UserAggregate.

### 3. Update Event Classes

Update the following event classes to use UserBusiness instead of UserAggregate:

- UserLoggedInEvent.java
- UserLoggedOutEvent.java

### 4. Update Services

Update the following services to use UserBusiness instead of UserAggregate:

- SessionManager.java
- ServiceInjector.java

### 5. Update View Models

Update the following view models to use UserBusiness instead of UserAggregate:

- UserManagementViewModel.java
- AdminViewModel.java
- LoginViewModel.java
- MainViewModel.java
- OrderGalleryViewModel.java

### 6. Update Controllers

Update the following controllers to use UserBusiness instead of UserAggregate:

- UserManagementViewController.java
- AdminViewController.java
- OrderGalleryViewController.java
- PhotoReviewViewController.java
- PhotoUploadViewController.java

### 7. Run Tests

Run tests to verify that the changes don't break existing functionality.

### 8. Update Documentation

Update documentation to reflect the changes, including:

- MASTER_TASK_LIST.md
- architecture_overhaul_progress.md
- Any other relevant documentation

## Benefits

1. **Consistent Terminology**: By using a single set of user classes, we make it easier for developers to understand the purpose and responsibility of each class.
2. **Reduced Coupling**: The module package will depend on fewer external components, making it more self-contained and easier to maintain.
3. **Improved Testability**: Using a single approach to user management makes it more testable and reduces code duplication.
4. **Better Error Handling**: By using a consistent approach to user management, we create a more consistent error handling approach.