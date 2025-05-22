# Photo Templates Implementation Plan

## Issue
The application currently shows "No Photo Templates Available" in the AssignedOrderView because the DefaultWorkerService.getAvailableTemplates method returns a hardcoded list of templates that aren't associated with any specific order. The database has been properly set up with PHOTO_TEMPLATES and ORDER_PHOTO_TEMPLATES tables, but the application code doesn't query these tables yet.

## Current State
1. The database schema includes:
   - PHOTO_TEMPLATES table to store template definitions
   - ORDER_PHOTO_TEMPLATES join table to associate orders with templates
   - Migration script V15__Add_Photo_Templates_Table.sql that creates these tables and populates them with data

2. The DefaultWorkerService.getAvailableTemplates method currently:
   - Returns a hardcoded list of PhotoTemplate objects
   - Doesn't query the database for templates associated with a specific order
   - Has a TODO comment explaining how it should be implemented in the future

## Implementation Plan

### Short-term Solution (Implemented)
Added a detailed TODO comment to DefaultWorkerService.getAvailableTemplates explaining:
- The SQL query that should be used to retrieve templates from the database
- The components needed to implement this properly
- The steps required to integrate with the existing architecture

This allows the application to continue functioning with the hardcoded templates while providing clear guidance for a proper implementation.

### Long-term Solution (Future Work)
1. Create a PhotoTemplateRepository interface with methods like:
   ```java
   public interface PhotoTemplateRepository {
       List<PhotoTemplate> findByOrderId(OrderId orderId);
       Optional<PhotoTemplate> findById(String templateId);
       List<PhotoTemplate> findAll();
       // Other methods as needed
   }
   ```

2. Create a SqlPhotoTemplateRepository implementation that:
   - Takes a DataSource or SqlConnectionManager in its constructor
   - Implements the methods to query the PHOTO_TEMPLATES and ORDER_PHOTO_TEMPLATES tables
   - Maps the results to PhotoTemplate objects

3. Update RepositoryInitializer to:
   - Create and register the SqlPhotoTemplateRepository
   - Add it to the repositories array returned by initializeRepositories and initializeDevModeRepositories

4. Update DefaultWorkerService to:
   - Take a PhotoTemplateRepository in its constructor
   - Use it in getAvailableTemplates to retrieve templates for the specified order

## Benefits
Implementing this plan will:
1. Properly integrate the photo templates feature with the database
2. Allow for order-specific templates rather than a one-size-fits-all approach
3. Make the system more flexible and maintainable
4. Eliminate the "No Photo Templates Available" message in the UI

## Related Files
- src/main/java/com/belman/application/usecase/worker/DefaultWorkerService.java
- src/main/resources/sqlitedb/migration/V15__Add_Photo_Templates_Table.sql
- src/main/java/com/belman/domain/photo/PhotoTemplate.java
- src/main/java/com/belman/presentation/usecases/worker/assignedorder/AssignedOrderViewModel.java