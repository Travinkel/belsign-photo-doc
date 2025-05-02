# Frameworks and Libraries

## Core Technologies

### JavaFX 21
The project uses Java 21 for maximum compatibility with Gluon Mobile and GraalVM Native Image builds.
- Modern UI components
- CSS styling
- FXML for declarative UI definition
- Property binding for reactive UI updates
- Animation framework

### Maven
Maven is used for project management and build automation:
- Dependency management
- Build lifecycle
- Resource filtering
- Test execution
- Packaging and deployment

## Custom Frameworks

### Backbone Framework

Backbone is a Gluon Mobile-friendly, opinionated JavaFX micro-framework created in-house. It follows Clean Architecture and MVVM principles and is optimized for both desktop and mobile deployment.

#### Key Features
- Convention-over-configuration wiring between View, Controller, and ViewModel
- SPA-style navigation using a centralized `Router`
- Declarative lifecycle support (`onShow()`, `onHide()`)
- Domain event publishing with `DomainEventPublisher`
- State sharing with `StateStore`
- Dependency injection using a service locator (constructor preferred)

#### Goals
- Remove boilerplate in FXML/ViewModel setup
- Ensure consistent architecture across views
- Support both desktop JavaFX and Gluon Mobile targets (using Attach services when needed)


## Mobile Development

### Gluon Mobile
Gluon Mobile enables cross-platform mobile development with JavaFX:

#### Components
- **Glisten**: UI toolkit for mobile applications
- **Attach**: Access to native mobile features
- **Connect**: Cloud services integration
- **Maps**: Mapping and location services
- **CloudLink**: Backend connectivity

#### Benefits
- Single codebase for desktop and mobile
- Native look and feel on each platform
- Access to device features (camera, storage, etc.)
- Optimized for touch interfaces

## External Libraries

### Database Access
- **JDBC**: Java Database Connectivity for SQL database access
- **HikariCP**: High-performance JDBC connection pool

### PDF Generation
- **Apache PDFBox**: Library for creating and manipulating PDF documents
- **iText**: Advanced PDF generation and manipulation

### Email
- **JavaMail API**: Sending and receiving email
- **Jakarta Mail**: Modern implementation of the JavaMail API

### Utilities
- **Apache Commons IO**: File and stream utilities
- **Apache Commons Lang**: String manipulation, reflection, and other utilities
- **SLF4J**: Simple Logging Facade for Java
- **Logback**: Logging implementation

### Testing
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for unit tests
- **TestFX**: Testing framework for JavaFX applications
- **Awaitility**: DSL for asynchronous testing

## Dependency Management

### Maven Dependencies
The project's dependencies are managed in the `pom.xml` file:

```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.1</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.1</version>
    </dependency>

    <!-- Gluon Mobile -->
    <dependency>
        <groupId>com.gluonhq</groupId>
        <artifactId>charm-glisten</artifactId>
        <version>6.2.3</version>
    </dependency>
    <dependency>
        <groupId>com.gluonhq.attach</groupId>
        <artifactId>storage</artifactId>
        <version>4.0.18</version>
    </dependency>
    <dependency>
        <groupId>com.gluonhq.attach</groupId>
        <artifactId>util</artifactId>
        <version>4.0.18</version>
    </dependency>

    <!-- PDF Generation -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>2.0.29</version>
    </dependency>

    <!-- Email -->
    <dependency>
        <groupId>com.sun.mail</groupId>
        <artifactId>jakarta.mail</artifactId>
        <version>2.0.1</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.6.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Version Compatibility

| Component | Version | Compatibility Notes |
|-----------|---------|---------------------|
| Java | 21      | Required for Gluon Substrate and GraalVM compatibility |
| JavaFX | 21.0.1  | Compatible with Java 23 |
| Gluon Mobile | 6.2.3   | Compatible with JavaFX 21 |
| Maven | 3.8+    | Required for build process |
| Apache PDFBox | 2.0.29  | Latest stable version |
| Jakarta Mail | 2.0.1   | Compatible with Java 23 |

## Future Considerations

- **Modularization**: Moving towards Java Platform Module System (JPMS)
- **GraalVM Native Image**: Exploring native compilation for improved startup time
- **Jakarta EE Integration**: For potential server-side components
- **Reactive Extensions**: Considering RxJava for more complex async operations
