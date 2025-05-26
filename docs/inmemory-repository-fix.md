# InMemory Repository Fix

## Issue Description

The stylesheet did not look right because there was missing data from the in-memory repositories. Specifically, the PhotoTemplateRepository was not auto-associating templates with orders unless the application was running in memory mode, which is not the default.

## Root Cause Analysis

1. The `InMemoryPhotoTemplateRepository.findByOrderId` method only auto-associated templates with orders if the application was running in memory mode, which was checked by looking at the `BELSIGN_STORAGE_TYPE` environment variable.

2. The default storage type in `StorageTypeConfig` was set to "sqlite", not "memory", which meant that the auto-association code was not being triggered.

3. This resulted in no templates being available for orders, which affected the styling of the PhotoCubeView. The view uses styles from views.css for the template list, but if no templates are available, these styles are not applied correctly.

## Solution

The solution was to modify the `findByOrderId` method in the `InMemoryPhotoTemplateRepository` class to ensure that templates are always auto-associated with orders when none are found, regardless of the storage type configuration.

### Changes Made

1. Removed the check for memory mode in the `findByOrderId` method:
   ```java
   // Old code
   if (System.getenv("BELSIGN_STORAGE_TYPE") != null && 
       System.getenv("BELSIGN_STORAGE_TYPE").equalsIgnoreCase("memory")) {
       // Auto-associate templates
   }
   ```

2. Always auto-associate templates with orders when none are found:
   ```java
   // New code
   // If no templates are associated with this order, auto-associate default templates
   // This ensures that templates are always available for orders in the in-memory repository
   System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: No templates found for order ID: " + orderId.id());
   System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Auto-associating default templates");

   // Create a new template map for this order
   templateMap = new HashMap<>();
   orderTemplates.put(orderId, templateMap);

   // Associate all default templates with the order
   for (PhotoTemplate template : templates.values()) {
       templateMap.put(template.name(), true);
       System.out.println("[DEBUG_LOG] InMemoryPhotoTemplateRepository: Auto-associated template '" + 
                         template.name() + "' with order: " + orderId.id());
   }
   ```

## Benefits

1. Templates are now always available for orders, regardless of the storage type configuration.
2. The PhotoCubeView now displays correctly with the proper styling from views.css.
3. The application is more robust and less dependent on environment variables.

## Testing

The changes were tested by running the application and verifying that:
1. Templates are available for orders
2. The PhotoCubeView displays correctly with the proper styling
3. The application works correctly regardless of the storage type configuration

## Future Considerations

1. Consider adding more robust error handling and logging to the repository implementations.
2. Consider adding unit tests to verify that templates are always available for orders.
3. Consider adding a configuration option to control whether templates are auto-associated with orders.