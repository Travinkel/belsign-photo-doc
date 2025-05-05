# Test Backbone Utilities

This package contains utility classes designed to support testing across different platforms, particularly for tests
that need to work in both standard JVM environments and with GraalVM/Gluon Mobile.

## GluonTestStorageHelper

`GluonTestStorageHelper` provides platform-agnostic methods for creating and managing temporary files during tests. It's
designed to work with:

- Standard JVM tests running on development machines
- GraalVM native image tests
- Gluon Mobile tests on mobile devices

### Key Features

- Creates temporary files in a platform-agnostic way
- Handles cleanup of test files
- Creates temporary directories for more complex testing scenarios
- Uses only Java APIs that are compatible with GraalVM native image and Gluon Mobile

### Usage Example

```java
// Create a temporary test file
byte[] pdfContent = "Test PDF content".getBytes(StandardCharsets.UTF_8);
File testFile = GluonTestStorageHelper.createTempTestFile("report.pdf", pdfContent);

// Use the file in your test
// ...

// Clean up when done
List<File> filesToCleanup = Collections.singletonList(testFile);
GluonTestStorageHelper.cleanupTempTestFiles(filesToCleanup);
```

## Why Use These Utilities?

When developing applications that target both desktop and mobile platforms using Gluon Mobile, certain standard Java
APIs may not be fully supported or may behave differently in a native image context. These utilities provide a
consistent API that works across all target platforms.

### Benefits

1. **Cross-platform compatibility**: Tests work consistently across development and production environments
2. **Simplified test code**: Common operations are abstracted away, making tests cleaner
3. **Proper resource management**: Resources are properly cleaned up after tests complete
4. **GraalVM/Substrate VM compatibility**: Uses only APIs that work with native image compilation