# Design Guidelines

## Code Style and Conventions

### Naming Conventions

#### Packages
- Use lowercase letters
- Use reverse domain name notation (e.g., `com.belman.domain.model`)
- Use singular nouns for packages containing classes of the same type (e.g., `model`, not `models`)
- Examples:
  - `com.belman.domain.model`
  - `com.belman.domain.service`
  - `com.belman.infrastructure.repository`

#### Classes
- Use PascalCase (UpperCamelCase)
- Use nouns or noun phrases
- Be descriptive and avoid abbreviations
- Examples:
  - `OrderRepository`
  - `PhotoDocumentService`
  - `EmailAddress`

#### Interfaces
- Use PascalCase
- Do not use "I" prefix
- Examples:
  - `Repository` (not `IRepository`)
  - `EmailService` (not `IEmailService`)

#### Methods
- Use camelCase (lowerCamelCase)
- Use verbs or verb phrases
- Be descriptive about what the method does
- Examples:
  - `findById`
  - `uploadPhoto`
  - `generateReport`

#### Variables
- Use camelCase
- Use meaningful names that describe the purpose
- Avoid single-letter names except for loop counters
- Examples:
  - `orderNumber`
  - `customerEmail`
  - `isApproved`

#### Constants
- Use UPPER_SNAKE_CASE
- Examples:
  - `MAX_UPLOAD_SIZE`
  - `DEFAULT_TIMEOUT_SECONDS`

#### Null Handling
- Never return `null` from methods — use `Optional<T>` instead.
- Validate method parameters early and fail fast.
- Use `Objects.requireNonNull()` when a value is required.

#### FXML Files and IDs
- FXML files: Use PascalCase with "View" suffix (e.g., `LoginView.fxml`)
- FXML IDs: Use camelCase (e.g., `loginButton`, `usernameField`)

### Code Organization

#### Class Structure
- Order of elements in a class:
  1. Static fields
  2. Instance fields
  3. Constructors
  4. Public methods
  5. Protected methods
  6. Private methods
  7. Inner classes/interfaces

#### Method Length
- Keep methods short and focused on a single responsibility
- Aim for methods under 30 lines
- Extract helper methods for complex operations

#### Comments
- Use JavaDoc for public APIs
- Comment complex algorithms or business rules
- Avoid obvious comments that repeat what the code already says
- Use TODO comments for temporary solutions or future improvements

## Domain-Driven Design Guidelines

### Value Objects
- Should be immutable
- Should validate their state in the constructor
- Should override equals() and hashCode()
- Should have a meaningful toString() method
- Examples:
  ```java
  public final class EmailAddress {
      private final String value;
      
      public EmailAddress(String email) {
          if (email == null || !isValidEmail(email)) {
              throw new IllegalArgumentException("Invalid email address: " + email);
          }
          this.value = email;
      }
      
      public String getValue() {
          return value;
      }
      
      private boolean isValidEmail(String email) {
          // Email validation logic
          return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
      }
      
      @Override
      public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          EmailAddress that = (EmailAddress) o;
          return value.equalsIgnoreCase(that.value);
      }
      
      @Override
      public int hashCode() {
          return value.toLowerCase().hashCode();
      }
      
      @Override
      public String toString() {
          return value;
      }
  }
  ```

### Entities
- Should have a clear identity concept
- Should encapsulate business rules
- Should validate state changes
- Example:
  ```java
  public class PhotoDocument {
      private final PhotoId id;
      private OrderId orderId;
      private ImagePath imagePath;
      private PhotoAngle angle;
      private ApprovalStatus approvalStatus;
      private String comment;
      private Timestamp createdAt;
      
      // Constructor and methods
      
      public void approve(User approver) {
          if (!approver.hasRole(Role.QA)) {
              throw new UnauthorizedOperationException("Only QA can approve photos");
          }
          this.approvalStatus = ApprovalStatus.APPROVED;
      }
      
      public void reject(User rejector, String reason) {
          if (!rejector.hasRole(Role.QA)) {
              throw new UnauthorizedOperationException("Only QA can reject photos");
          }
          if (reason == null || reason.trim().isEmpty()) {
              throw new IllegalArgumentException("Rejection reason is required");
          }
          this.approvalStatus = ApprovalStatus.REJECTED;
          this.comment = reason;
      }
  }
  ```

### Aggregates
- Identify clear aggregate roots
- Keep aggregates small
- Reference other aggregates by identity
- Example:
  ```java
  public class Order {
      private final OrderId id;
      private final CustomerId customerId;
      private final OrderNumber orderNumber;
      private final Set<PhotoDocument> photoDocuments;
      private OrderStatus status;
      
      // Constructor and methods
      
      public void addPhotoDocument(PhotoDocument document) {
          // Business rules for adding photos
          photoDocuments.add(document);
      }
      
      public boolean canGenerateReport() {
          // Business rule: need at least one approved photo
          return photoDocuments.stream()
              .anyMatch(doc -> doc.getApprovalStatus() == ApprovalStatus.APPROVED);
      }
  }
  ```

### Repositories
- Use interfaces in the domain layer
- Implement in the infrastructure layer
- Return domain objects, not DTOs
- Example:
  ```java
  // Domain layer
  public interface OrderRepository {
      Optional<Order> findById(OrderId id);
      Optional<Order> findByOrderNumber(OrderNumber orderNumber);
      List<Order> findBySpecification(Specification<Order> spec);
      void save(Order orderAggregate);
  }
  
  // Infrastructure layer
  public class SqlOrderRepository implements OrderRepository {
      // Implementation
  }
  ```

### Services
- Use domain services for operations that don't belong to entities
- Keep services focused on a single responsibility
- Example:
  ```java
  public class ReportGenerationService {
      private final OrderRepository orderRepository;
      private final PDFGenerator pdfGenerator;
      
      // Constructor
      
      public Report generateQCReport(OrderId orderId) {
          Order orderAggregate = orderRepository.findById(orderId)
              .orElseThrow(() -> new EntityNotFoundException("OrderAggregate not found"));
              
          if (!orderAggregate.canGenerateReport()) {
              throw new BusinessRuleViolationException("Cannot generate reportAggregate without approved photos");
          }
          
          // Generate reportAggregate logic
      }
  }
  ```

## UI Design Guidelines

### JavaFX/FXML Best Practices
- Separate UI structure (FXML) from behavior (Controller)
- Use CSS for styling
- Use binding for reactive UI updates
- Keep controllers thin, delegate to ViewModels

### MVVM Pattern
- Model: Domain entities and business logic
- View: FXML and minimal controller code
- ViewModel: UI state and operations

### Responsive Design
- Use layout containers that resize appropriately (VBox, HBox, GridPane)
- Use percentage-based sizing where appropriate
- Test on different screen sizes

### Mobile-Friendly Design
- Use touch-friendly controls (larger buttons, etc.)
- Consider gesture support
- Ensure adequate spacing between interactive elements

## Error Handling Guidelines

### Exception Types
- Use checked exceptions for recoverable errors
- Use unchecked exceptions for programming errors
- Create custom exceptions for domain-specific errors

### Exception Handling Strategy
- Handle exceptions at the appropriate level
- Log exceptions with context information
- Provide user-friendly error messages
- Don't catch exceptions you can't handle properly

### Validation
- Validate input at the domain level
- Use defensive programming
- Fail fast and provide clear error messages

#### Immutability
- Prefer immutability for value objects and DTOs.
- Use `final` fields and avoid setters.
- Ensure constructors fully initialize objects into a valid state.

### Interface-Based Design
- Define interfaces in the domain or application layer for:
  - Repositories
  - Services
  - External integrations
- Implement these interfaces in the infrastructure layer.
- Inject interfaces via constructor to improve testability and decoupling.

```java
// Domain Layer
public interface QCReportService {
    void generateAndSendReport(OrderId orderId);
}

// Application Layer
public class DefaultQCReportService implements QCReportService {
    private final PDFGenerator generator;
    private final MailService mailService;

    public DefaultQCReportService(PDFGenerator generator, MailService mailService) {
        this.generator = generator;
        this.mailService = mailService;
    }

    public void generateAndSendReport(OrderId orderId) {
        // Orchestration logic
    }
}
