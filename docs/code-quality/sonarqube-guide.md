# SonarQube Analysis Guide for Belsign Photo Documentation

This guide explains how to set up and run SonarQube analysis for the Belsign Photo Documentation project.

## Prerequisites

1. Java 17 or higher
2. Maven 3.8 or higher
3. SonarQube Server (local or remote)

## Setting Up SonarQube Server (Local)

1. Download SonarQube Community Edition from [SonarQube Downloads](https://www.sonarqube.org/downloads/)
2. Extract the downloaded file to a directory of your choice
3. Start the SonarQube server:
   ```
   cd <sonarqube_directory>/bin/<your_os>
   ./sonar.sh start  # For Linux/Mac
   StartSonar.bat    # For Windows
   ```
4. Access the SonarQube dashboard at http://localhost:9000
5. Default credentials: admin/admin (you'll be prompted to change the password on first login)

## Configuring the Project

The project is already configured with SonarQube properties in the pom.xml file. The configuration includes:

- Project key: `belman-photo-doc`
- Project name: `Belsign Photo Documentation`
- SonarQube server URL: `http://localhost:9000`
- Source and test directories
- Coverage report paths
- Exclusions

## Running SonarQube Analysis

### Using Maven Command Line

1. Generate JaCoCo coverage report:
   ```
   mvn clean verify
   ```

2. Run SonarQube analysis:
   ```
   mvn sonar:sonar -Dsonar.login=<your_sonar_token>
   ```

   Replace `<your_sonar_token>` with your SonarQube authentication token. You can generate a token in the SonarQube dashboard under Administration > Security > Users > Tokens.

### Using CI/CD Pipeline

For continuous integration, add the following steps to your CI/CD pipeline:

```yaml
# Example GitHub Actions workflow
steps:
  - name: Build and analyze
    env:
      GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    run: mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=belman-photo-doc
```

## Viewing Analysis Results

1. After the analysis completes, open the SonarQube dashboard at http://localhost:9000
2. Navigate to Projects > Belsign Photo Documentation
3. Review the analysis results, including:
   - Code quality issues
   - Code coverage
   - Duplications
   - Security vulnerabilities
   - Technical debt

## Quality Gates

SonarQube uses Quality Gates to determine if your project passes or fails the quality check. The default Quality Gate includes:

- Coverage on new code > 80%
- Duplicated lines on new code < 3%
- Maintainability rating on new code is A
- Reliability rating on new code is A
- Security rating on new code is A

You can customize the Quality Gate in the SonarQube dashboard under Quality Gates.

## Troubleshooting

### Common Issues

1. **Connection refused**: Ensure the SonarQube server is running and accessible at the configured URL.
2. **Authentication failed**: Verify your authentication token is correct and has not expired.
3. **Analysis fails**: Check the Maven logs for detailed error messages.

### Logs

SonarQube server logs are located at:
```
<sonarqube_directory>/logs/sonar.log
```

## Additional Resources

- [SonarQube Documentation](https://docs.sonarqube.org/latest/)
- [SonarQube Java Plugin](https://docs.sonarqube.org/latest/analysis/languages/java/)
- [SonarQube Maven Scanner](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/)