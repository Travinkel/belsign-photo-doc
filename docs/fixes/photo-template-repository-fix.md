# Photo Template Repository Fix

## Issue
The application was failing to initialize with the following error:
```
java.lang.ClassCastException: class com.belman.dataaccess.repository.memory.InMemoryPhotoTemplateRepository cannot be cast to class com.belman.domain.report.ReportRepository
```

This error occurred in the ApplicationInitializer class when it tried to cast the 4th element of the repositories array to ReportRepository, but the 4th element was actually a PhotoTemplateRepository.

## Root Cause
The RepositoryInitializer class was updated to include a PhotoTemplateRepository in the repositories array, but the ApplicationInitializer class wasn't updated to handle this new element. The repositories array now contains 5 elements instead of 4:
1. UserRepository
2. OrderRepository
3. PhotoRepository
4. PhotoTemplateRepository (new)
5. ReportRepository

The ApplicationInitializer was still assuming that the 4th element was the ReportRepository, causing the ClassCastException.

## Solution
The solution involved several steps:

1. Created a PhotoTemplateRepository interface in the domain layer:
```java
public interface PhotoTemplateRepository extends Repository<PhotoTemplate, String> {
    List<PhotoTemplate> findByOrderId(OrderId orderId);
    boolean associateWithOrder(OrderId orderId, String templateId, boolean required);
    // ... other methods
}
```

2. Implemented SqlPhotoTemplateRepository and InMemoryPhotoTemplateRepository classes:
```java
public class SqlPhotoTemplateRepository extends BaseRepository<PhotoTemplate, String> implements PhotoTemplateRepository {
    // ... implementation
}

public class InMemoryPhotoTemplateRepository extends BaseRepository<PhotoTemplate, String> implements PhotoTemplateRepository {
    // ... implementation
}
```

3. Updated the ApplicationInitializer to correctly extract the PhotoTemplateRepository from the repositories array:
```java
userRepository = (UserRepository) repositories[0];
orderRepository = (OrderRepository) repositories[1];
PhotoRepository photoRepository = (PhotoRepository) repositories[2];
PhotoTemplateRepository photoTemplateRepository = (PhotoTemplateRepository) repositories[3];
ReportRepository reportRepository = (ReportRepository) repositories[4];
```

4. Updated the DefaultWorkerService to use the PhotoTemplateRepository:
```java
public class DefaultWorkerService implements WorkerService {
    private final OrderRepository orderRepository;
    private final PhotoRepository photoRepository;
    private final PhotoTemplateRepository photoTemplateRepository;

    public DefaultWorkerService(OrderRepository orderRepository, PhotoRepository photoRepository, PhotoTemplateRepository photoTemplateRepository) {
        this.orderRepository = orderRepository;
        this.photoRepository = photoRepository;
        this.photoTemplateRepository = photoTemplateRepository;
    }

    @Override
    public List<PhotoTemplate> getAvailableTemplates(OrderId orderId) {
        // Use the PhotoTemplateRepository to get templates for this order
        List<PhotoTemplate> templates = photoTemplateRepository.findByOrderId(orderId);
        
        // If no templates are found, return a default set of templates
        if (templates.isEmpty()) {
            templates = Arrays.asList(
                    PhotoTemplate.TOP_VIEW_OF_JOINT,
                    PhotoTemplate.SIDE_VIEW_OF_WELD,
                    // ... other templates
            );
        }
        return templates;
    }
    // ... other methods
}
```

5. Updated the ApplicationInitializer to pass the PhotoTemplateRepository to the DefaultWorkerService:
```java
WorkerService workerService = new DefaultWorkerService(orderRepository, photoRepository, photoTemplateRepository);
```

## Benefits
These changes ensure that:
1. The application initializes correctly without ClassCastException
2. The DefaultWorkerService can retrieve photo templates for orders from the database
3. The code is more maintainable and follows the same pattern as other repositories

## Future Improvements
In the future, the DefaultWorkerService could be enhanced to:
1. Add methods for managing photo templates (create, update, delete)
2. Implement more sophisticated template selection based on order properties
3. Add support for custom templates per customer or product type