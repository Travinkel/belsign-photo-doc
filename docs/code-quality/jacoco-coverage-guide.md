# JaCoCo Code Coverage Guide for Belsign Photo Documentation

This guide explains how to generate and interpret JaCoCo code coverage reports for the Belsign Photo Documentation project.

## Overview

JaCoCo (Java Code Coverage) is a code coverage library for Java that helps measure how much of your code is covered by tests. The Belsign Photo Documentation project uses JaCoCo to ensure adequate test coverage of the codebase.

## Coverage Thresholds

The project has defined the following coverage thresholds:

| Metric | Threshold |
|--------|-----------|
| Instruction Coverage | 70% |
| Branch Coverage | 60% |
| Line Coverage | 70% |
| Method Coverage | 75% |
| Class Coverage | 80% |

These thresholds are enforced during the build process. If the coverage falls below these thresholds, the build will fail.

## Generating Coverage Reports

### Using Maven

To generate a JaCoCo coverage report, run the following Maven command:

```bash
mvn clean verify
```

This command will:
1. Compile the code
2. Run the tests
3. Generate a JaCoCo coverage report
4. Verify that the coverage meets the defined thresholds

### Report Location

The JaCoCo coverage report is generated in the following location:

```
target/site/jacoco/index.html
```

Open this file in a web browser to view the coverage report.

## Interpreting the Coverage Report

The JaCoCo report provides detailed information about code coverage at different levels:

### Project Overview

The main page of the report shows an overview of the coverage for the entire project, including:

- Overall coverage percentages for each metric
- List of packages with their coverage statistics
- Color-coded indicators (red, yellow, green) for quick visual assessment

### Package Level

Clicking on a package name shows coverage details for that package, including:

- Coverage statistics for each class in the package
- Color-coded indicators for each class

### Class Level

Clicking on a class name shows coverage details for that class, including:

- Line-by-line coverage information
- Color-coded source code:
  - **Green**: Covered code
  - **Yellow**: Partially covered code (e.g., branches)
  - **Red**: Uncovered code

## Understanding Coverage Metrics

JaCoCo provides several coverage metrics:

1. **Instruction Coverage**: Measures the percentage of Java bytecode instructions that have been executed.
2. **Branch Coverage**: Measures the percentage of branches that have been executed (e.g., if/else statements).
3. **Line Coverage**: Measures the percentage of lines of code that have been executed.
4. **Method Coverage**: Measures the percentage of methods that have been executed.
5. **Class Coverage**: Measures the percentage of classes that have been executed.

## Improving Coverage

If the coverage falls below the defined thresholds, you need to add or improve tests. Here are some tips:

1. Focus on uncovered (red) areas in the report
2. Prioritize critical business logic
3. Use parameterized tests for edge cases
4. Test both success and failure paths
5. Mock external dependencies to isolate the code being tested

## Exclusions

Some parts of the codebase are excluded from coverage requirements:

- Bootstrap code (`**/bootstrap/**/*`)
- Generated code (`**/generated/**/*`)

These exclusions are defined in the JaCoCo configuration in the pom.xml file.

## Continuous Integration

The JaCoCo coverage check is integrated into the CI/CD pipeline. If the coverage falls below the defined thresholds, the pipeline will fail.

## Troubleshooting

### Common Issues

1. **Build fails due to insufficient coverage**:
   - Review the JaCoCo report to identify uncovered areas
   - Add tests to increase coverage
   - If necessary, adjust the thresholds temporarily (not recommended for long-term)

2. **Tests run but coverage is not generated**:
   - Ensure the JaCoCo agent is properly configured
   - Check for test execution errors

3. **Coverage report shows 0% coverage**:
   - Verify that tests are actually running
   - Check for configuration issues in the pom.xml file

## Additional Resources

- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [JaCoCo Maven Plugin Documentation](https://www.eclemma.org/jacoco/trunk/doc/maven.html)
- [Code Coverage Best Practices](https://www.atlassian.com/continuous-delivery/software-testing/code-coverage)