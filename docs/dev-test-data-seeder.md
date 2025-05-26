# DevTestDataSeeder Documentation

## Overview

The `DevTestDataSeeder` is a utility class that ensures in-memory repositories are populated with test data when the application boots in development or test mode. This makes it easier to test and develop the application without having to manually create test data each time.

## Problem Solved

When running the application with in-memory repositories (using `BELSIGN_STORAGE_TYPE=memory`), we need to ensure that test data is available for all use cases. Previously, test data was only seeded during repository initialization, but this didn't guarantee that all required data was available for all use cases.

The `DevTestDataSeeder` addresses this by:

1. Providing a centralized way to seed test data for all repositories
2. Ensuring test data is seeded when the application boots
3. Making it easy to force-seed test data at any time, regardless of storage mode

## Implementation Details

### DevTestDataSeeder Class

The `DevTestDataSeeder` class provides two main methods:

1. `seedData()` - Seeds test data for all repositories, but only if the application is running in memory mode.
2. `forceSeedData()` - Seeds test data for all repositories, regardless of storage mode. This is useful for testing.

Both methods get all repositories from ServiceLocator and call the existing `seedTestData` method in RepositoryInitializer.

```java
public static void seedData() {
    // Only seed data if we're in memory mode
    if (!StorageTypeConfig.isMemoryMode()) {
        logger.info("Not in memory mode, skipping test data seeding");
        return;
    }

    logger.info("🌱 Seeding test data for in-memory repositories");

    try {
        // Get all repositories from ServiceLocator
        UserRepository userRepository = ServiceLocator.getService(UserRepository.class);
        OrderRepository orderRepository = ServiceLocator.getService(OrderRepository.class);
        PhotoRepository photoRepository = ServiceLocator.getService(PhotoRepository.class);
        PhotoTemplateRepository photoTemplateRepository = ServiceLocator.getService(PhotoTemplateRepository.class);
        ReportRepository reportRepository = ServiceLocator.getService(ReportRepository.class);

        // Call the existing seedTestData method in RepositoryInitializer
        RepositoryInitializer.seedTestData(
            userRepository,
            orderRepository,
            photoRepository,
            photoTemplateRepository,
            reportRepository
        );

        logger.success("✅ Test data seeded successfully");
    } catch (Exception e) {
        logger.error("❌ Error seeding test data", e);
    }
}
```

### Integration with Application Startup

The `DevTestDataSeeder.seedData()` method is called in the `start` method of the `Main` class, after the application is bootstrapped but before the splash view is shown:

```java
// Seed test data for in-memory repositories
logger.info("Seeding test data for in-memory repositories");
DevTestDataSeeder.seedData();

// Show the splash view (GUI)
logger.info("Showing splash view");
ViewStackManager.getInstance().navigateTo("SplashView");
```

This ensures that test data is available as soon as the application starts.

## Test Data Seeded

The test data seeded includes:

1. **Users**:
   - Admin user with username "admin"
   - Production user with username "production"
   - QA user with username "qa_user"

2. **Orders**:
   - Multiple test orders with different order numbers and descriptions
   - Orders assigned to production workers

3. **Photo Templates**:
   - All default photo templates associated with each order

4. **Photos**:
   - Test photos for some orders with different approval statuses (PENDING, APPROVED, REJECTED)

## How to Use

### Running the Application in Memory Mode

To run the application with in-memory repositories and automatically seed test data:

1. Set the `BELSIGN_STORAGE_TYPE` environment variable to `memory`:
   ```
   set BELSIGN_STORAGE_TYPE=memory
   ```

2. Start the application normally. The `DevTestDataSeeder` will automatically seed test data.

### Forcing Test Data Seeding

If you need to force-seed test data at any time, you can call the `forceSeedData()` method:

```java
DevTestDataSeeder.forceSeedData();
```

This will seed test data regardless of the current storage mode. Use this with caution in non-development environments.

## Benefits

1. **Consistent Test Environment**: Ensures a consistent set of test data is available for all use cases.
2. **Improved Developer Experience**: Developers don't need to manually create test data each time they run the application.
3. **Easier Testing**: Makes it easier to test the application with realistic data.
4. **Centralized Data Seeding**: Provides a single place to manage test data seeding.

## Future Improvements

Potential future improvements to the `DevTestDataSeeder` include:

1. Adding more test data for specific use cases
2. Adding configuration options to control which data is seeded
3. Adding support for seeding data from external files or resources
4. Adding support for cleaning up test data when the application shuts down