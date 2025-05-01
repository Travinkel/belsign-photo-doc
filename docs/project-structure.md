# ExamProjectBelman Project Structure

## Overview

This document outlines the project structure for ExamProjectBelman, explaining the organization of modules, dependencies, and best practices for future development.

## Project Organization

The project is organized as a multi-module Maven project with the following modules:

1. **athomefx**: A framework module containing the AtHomeFX framework code
2. **athomefx-cli**: A command-line interface for generating components using the AtHomeFX framework
3. **belsign**: The main application module containing the BelSign application code

### Module Structure

```
ExamProjectBelman/
├── athomefx/                  # AtHomeFX framework module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/belman/athomefx/
│   │   │   │       ├── aop/
│   │   │   │       ├── core/
│   │   │   │       ├── di/
│   │   │   │       ├── events/
│   │   │   │       ├── exceptions/
│   │   │   │       ├── lifecycle/
│   │   │   │       ├── logging/
│   │   │   │       ├── navigation/
│   │   │   │       ├── state/
│   │   │   │       └── util/
│   │   ├── test/
│   └── pom.xml
├── athomefx-cli/              # AtHomeFX CLI module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/belman/athomefx/cli/
│   │   ├── test/
│   └── pom.xml
├── belsign/                   # com.belman.Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/belman/belsign/
│   │   │   │       ├── application/
│   │   │   │       ├── domain/
│   │   │   │       ├── framework/
│   │   │   │       │   └── nidhugg/
│   │   │   │       ├── infrastructure/
│   │   │   │       ├── presentation/
│   │   │   │       └── com.belman.Main.java
│   │   │   ├── resources/
│   │   ├── test/
│   └── pom.xml
├── docs/                      # Documentation
│   └── project/
├── pom.xml                    # Parent POM
└── README.md
```

## Dependencies

The dependencies between modules are as follows:

- **athomefx**: Depends on JavaFX and testing libraries
- **athomefx-cli**: Depends on the athomefx module
- **belsign**: Depends on the athomefx module, JavaFX, Gluon, and other libraries

## Best Practices

### Module Organization

1. **Separate Framework from Application**: The AtHomeFX framework is now in its own module, separate from the application code. This makes it easier to maintain, test, and reuse.

2. **CLI as a Separate Module**: The command-line interface is in its own module, making it easier to distribute and use independently of the application.

3. **com.belman.Main Application in Its Own Module**: The BelSign application is in its own module, making it easier to focus on application-specific code.

### Dependency Management

1. **Use dependencyManagement**: The parent POM uses `dependencyManagement` to define versions of dependencies that child modules can use without specifying versions.

2. **Minimize Direct Dependencies**: Each module should only depend on what it needs. For example, athomefx-cli only depends on the athomefx module, not on JavaFX directly.

3. **Explicit Dependencies**: Dependencies should be explicitly declared in each module's POM, even if they're inherited from the parent.

### Framework Development

1. **Framework as a Library**: The AtHomeFX framework is now a proper library that can be used by multiple applications.

2. **Framework Versioning**: The framework has its own version number, making it easier to track changes and updates.

3. **Framework Testing**: The framework should have its own tests, separate from application tests.

### ORM Framework (Nidhugg)

The Nidhugg ORM framework is currently part of the belsign module. In the future, it could be moved to its own module, similar to the AtHomeFX framework, to make it more reusable and maintainable.

## Future Improvements

1. **Move Nidhugg to Its Own Module**: The Nidhugg ORM framework could be moved to its own module to make it more reusable.

2. **Add More Tests**: Each module should have comprehensive tests to ensure quality and prevent regressions.

3. **Improve Documentation**: Each module should have its own documentation explaining its purpose, usage, and API.

4. **Add CI/CD**: Set up continuous integration and deployment to automate testing and deployment.

5. **Add Code Quality Tools**: Add tools like Checkstyle, PMD, and SpotBugs to ensure code quality.

## Conclusion

The new project structure follows best practices for Maven multi-module projects, making it easier to maintain, test, and extend the codebase. By separating the framework from the application code, we've made it easier to reuse the framework in other projects and to focus on application-specific code in the BelSign module.