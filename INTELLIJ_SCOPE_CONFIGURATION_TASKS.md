# IntelliJ IDEA Scope Configuration Tasks

## Overview

This document outlines the tasks required to configure IntelliJ IDEA Ultimate to enforce the dependency rules between different layers of the application. The rules ensure that:

1. UI, service, and repository layers can depend on common, bootstrap, and domain packages
2. Common, bootstrap, and domain packages must not depend on UI, repository, or service layers

## Configuration Tasks

### 1. Enable Dependency Validation

1. Open IntelliJ IDEA Ultimate
2. Go to File > Settings (or Ctrl+Alt+S)
3. Navigate to Editor > Inspections
4. Find "Java > Architecture > Dependency validation" and enable it
5. Click "Apply" and "OK"

### 2. Verify Scope Definitions

Ensure that the following scope files exist in the `.idea/scopes` directory:

- `ui_scope.xml`: Defines the UI layer scope (com.belman.ui.*)
- `service_scope.xml`: Defines the service layer scope (com.belman.service.*)
- `repository_scope.xml`: Defines the repository layer scope (com.belman.repository.*)
- `domain_scope.xml`: Defines the domain layer scope (com.belman.domain.*)
- `common_scope.xml`: Defines the common package scope (com.belman.common.*)
- `bootstrap_scope.xml`: Defines the bootstrap package scope (com.belman.bootstrap.*)
- `scope_settings.xml`: Defines the dependency rules between scopes

### 3. Verify Dependency Rules

Ensure that the `scope_settings.xml` file contains the following rules:

1. Default deny rules:
   - UI cannot depend on service or repository
   - Service cannot depend on repository
   - Repository cannot depend on service or UI
   - Domain cannot depend on UI, service, or repository
   - Common cannot depend on UI, service, or repository

2. Allowed dependencies:
   - UI can depend on service, domain, and common
   - Service can depend on UI, repository, domain, and common
   - Repository can depend on service, domain, and common
   - Domain can depend on common
   - Bootstrap can depend on all layers

### 4. Run Dependency Validation

1. Open the project in IntelliJ IDEA Ultimate
2. Go to Analyze > Run Inspection by Name... (or Ctrl+Alt+Shift+I)
3. Type "Dependency validation" and select it
4. Choose the scope "Whole project" and click "OK"
5. Review the results and fix any dependency violations

### 5. Run Architecture Tests

1. Open the project in IntelliJ IDEA Ultimate
2. Navigate to `src/test/java/com/belman/architecture/rules/module/DependencyValidationTest.java`
3. Right-click on the file and select "Run 'DependencyValidationTest'"
4. Fix any test failures related to dependency violations

## Troubleshooting

If you encounter issues with dependency validation:

1. Make sure you're using IntelliJ IDEA Ultimate (not Community Edition)
2. Verify that the scope files are correctly defined
3. Restart IntelliJ IDEA after making changes to scope files
4. Run "File > Invalidate Caches / Restart..." if changes don't take effect
5. Check for any conflicting rules in the scope settings

## Additional Resources

- [IntelliJ IDEA Scopes Documentation](https://www.jetbrains.com/help/idea/settings-scopes.html)
- [Dependency Validation Documentation](https://www.jetbrains.com/help/idea/dependencies-validation.html)
- [ArchUnit Documentation](https://www.archunit.org/userguide/html/000_Index.html)