# Build and Run Instructions

## Prerequisites

Before building and running the BelSign Photo Documentation Module, ensure you have the following prerequisites installed:

### Required Software
- **Java Development Kit (JDK) 21**
- **Maven 3.8** or higher
- **Git** for version control

### Optional Software
- **IntelliJ IDEA** or **Eclipse** IDE (recommended for development)
- **Scene Builder** for FXML editing
- **Docker** for containerized deployment (optional)
- **GraalVM** for native image generation (optional)
- **Gluon Mobile** for mobile builds (optional)

### Mobile Requirements (Optional)
To build and run the app on mobile devices, ensure:

- **GraalVM Gluon Edition** is installed and configured
- **Gluon Substrate CLI** is available (optional for manual native image control)
- **WSL2 (Windows only)** for cross-compilation via Linux


## Getting the Source Code

Clone the repository using Git:

```bash
git clone https://github.com/belman/belsign.git
cd belsign
```

## Building the Project

### Using Maven Command Line

1. Navigate to the project root directory
2. Run the Maven build command:

```bash
mvn clean install
```

This command will:
- Clean the target directory
- Compile the source code
- Run the tests
- Package the application

### Using an IDE

#### IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select "Open" or "Import Project"
3. Navigate to the project directory and select the `pom.xml` file
4. Choose "Open as Project"
5. Wait for the IDE to import and index the project
6. Build the project using the Build menu or by pressing Ctrl+F9 (Windows/Linux) or Cmd+F9 (macOS)

#### Eclipse
1. Open Eclipse
2. Select "File" > "Import"
3. Choose "Maven" > "Existing Maven Projects"
4. Navigate to the project directory and select the `pom.xml` file
5. Click "Finish"
6. Wait for Eclipse to import and build the project
7. Right-click on the project and select "Run As" > "Maven build..."
8. Enter "clean install" in the Goals field and click "Run"

## Running the Application

### Desktop Mode

To run the application in desktop mode:

```bash
mvn javafx:run
```

Or using the full class path:

```bash
mvn exec:java -Dexec.mainClass="com.belman.infrastructure.bootstrap.Main"
```

### Development Mode

For development with hot reloading:

```bash
mvn javafx:run -Djavafx.run.options="--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED"
```

### Running Specific Profiles

The application supports different profiles for various environments:

#### Development Profile
```bash
mvn javafx:run -Pdev
```

#### Testing Profile
```bash
mvn javafx:run -Ptest
```

#### Production Profile
```bash
mvn javafx:run -Pprod
```

## Mobile Builds

### Prerequisites for Mobile Development
- **Gluon Mobile** license (for commercial use)
- **Android SDK** (for Android builds)
- **Xcode** and **iOS SDK** (for iOS builds, macOS only)

### Building for Android

1. Ensure Android SDK is properly configured in your environment
2. Run the Gluon build command:

```bash
mvn gluonfx:build -Pandroid
```

3. The APK file will be generated in the `target/gluonfx/aarch64-android/gvm` directory

### Building for iOS (macOS only)

1. Ensure Xcode and iOS SDK are properly installed
2. Run the Gluon build command:

```bash
mvn gluonfx:build -Pios
```

3. The iOS app will be generated in the `target/gluonfx/aarch64-ios/gvm` directory

## Configuration

### Application Properties

The application uses property files for configuration:

- `application.properties`: Default configuration
- `application-dev.properties`: Development configuration
- `application-test.properties`: Testing configuration
- `application-prod.properties`: Production configuration

These files are located in the `src/main/resources` directory.

### Database Configuration

To configure the database connection:

1. Open the appropriate properties file
2. Modify the database properties:

```properties
# Database Configuration
db.url=jdbc:sqlserver://localhost:1433;databaseName=BelSign
db.username=sa
db.password=YourPassword
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### Logging Configuration

Logging is configured in the `logback.xml` file in the `src/main/resources` directory:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/belsign.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### Gluon Mobile Configuration

When targeting Android/iOS, ensure the correct profiles and attach modules are configured in `pom.xml`. Gluon requires additional flags for GraalVM native image compilation.


## Troubleshooting

### Common Build Issues

#### Maven Dependencies Not Found
If Maven cannot download dependencies, check your internet connection and Maven settings in `~/.m2/settings.xml`. You may need to configure a proxy or mirror.

#### JavaFX Not Found
Ensure that JavaFX modules are included in your dependencies. Check the `pom.xml` file for JavaFX dependencies.

#### Compilation Errors
- Ensure you're using JDK 21 (GraalVM Gluon edition for mobile builds)
- Check that all required dependencies are available
- Verify that your source code is compatible with Java 23

### Runtime Issues

#### Application Won't Start
- Check the logs for error messages
- Verify that all required resources are available
- Ensure the database is running and accessible (if applicable)

#### UI Rendering Issues
- Ensure JavaFX is properly configured
- Check for CSS errors in the logs
- Verify that FXML files are correctly formatted

#### Database Connection Issues
- Verify database credentials
- Check that the database server is running
- Ensure firewall rules allow the connection

## Deployment

### JAR Packaging

To create an executable JAR file:

```bash
mvn clean package
```

The JAR file will be created in the `target` directory.

### Native Packaging

To create a native executable:

```bash
mvn javafx:jlink
```

This will create a custom runtime image in the `target/app` directory.

### Docker Deployment

A Dockerfile is provided for containerized deployment:

```bash
# Build the Docker image
docker build -t belsign .

# Run the container
docker run -p 8080:8080 belsign
```

## Continuous Integration
> Note: Native mobile builds (`mvn gluonfx:build`) are not executed in CI by default. These are done manually on supported OS environments (Linux/macOS).
The project uses GitHub Actions for continuous integration. The workflow is defined in `.github/workflows/ci.yml`.

To run the CI pipeline locally:

```bash
# Install act (https://github.com/nektos/act)
act -j build
```

## Performance Tuning

### JVM Options

For better performance, consider the following JVM options:

```bash
mvn javafx:run -Djavafx.run.options="--add-modules=javafx.controls,javafx.fxml -Xms512m -Xmx1g"
```

### Database Connection Pool

Adjust the connection pool settings in the properties file:

```properties
# Connection Pool Configuration
db.pool.initialSize=5
db.pool.maxSize=20
db.pool.minIdle=5
db.pool.maxIdle=10
```

## Additional Resources

- [JavaFX Documentation](https://openjfx.io/javadoc/21/)
- [Gluon Mobile Documentation](https://docs.gluonhq.com/)
- [Maven Documentation](https://maven.apache.org/guides/index.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)