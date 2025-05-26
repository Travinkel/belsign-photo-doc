# SonarQube Quality Gates and Rules for Belsign Photo Documentation

This document defines the quality gates and rules for the Belsign Photo Documentation project.

## Quality Gates

Quality Gates are a set of conditions that determine whether a project passes or fails the quality check. The following quality gate is defined for the Belsign Photo Documentation project:

### Default Quality Gate

| Metric | Operator | Value |
|--------|----------|-------|
| Coverage on new code | is less than | 80% |
| Duplicated lines on new code | is greater than | 3% |
| Maintainability rating on new code | is worse than | A |
| Reliability rating on new code | is worse than | A |
| Security rating on new code | is worse than | A |
| Security hotspots reviewed on new code | is less than | 100% |

## Custom Rules

In addition to the default rules provided by SonarQube, the following custom rules are defined for the Belsign Photo Documentation project:

### Java Rules

#### Code Smells

| Rule | Severity | Description |
|------|----------|-------------|
| Method length | MAJOR | Methods should not have more than 50 lines of code |
| Class complexity | CRITICAL | Classes should not have a cyclomatic complexity greater than 80 |
| Method complexity | MAJOR | Methods should not have a cyclomatic complexity greater than 15 |
| Too many parameters | MAJOR | Methods should not have more than 7 parameters |
| Cognitive complexity | MAJOR | Methods should not have a cognitive complexity greater than 15 |
| File length | MAJOR | Files should not have more than 1000 lines of code |
| Comment density | INFO | Files should have a comment density of at least 20% |

#### Bugs

| Rule | Severity | Description |
|------|----------|-------------|
| Null pointer dereference | BLOCKER | Code should not dereference a null pointer |
| Resource leak | CRITICAL | Resources should be closed properly |
| Infinite recursion | BLOCKER | Methods should not call themselves infinitely |
| Division by zero | BLOCKER | Code should not divide by zero |
| Empty catch block | MAJOR | Catch blocks should not be empty |

#### Vulnerabilities

| Rule | Severity | Description |
|------|----------|-------------|
| SQL injection | BLOCKER | SQL queries should not be vulnerable to injection attacks |
| Path traversal | CRITICAL | File paths should not be vulnerable to path traversal attacks |
| Insecure random | CRITICAL | Secure random number generators should be used |
| Hard-coded credentials | BLOCKER | Credentials should not be hard-coded |
| Cross-site scripting | CRITICAL | Code should not be vulnerable to XSS attacks |

### JavaFX Rules

| Rule | Severity | Description |
|------|----------|-------------|
| FXML injection | CRITICAL | FXML loading should not be vulnerable to injection attacks |
| UI thread blocking | MAJOR | Long-running operations should not block the UI thread |
| Memory leak | CRITICAL | UI components should be properly disposed |
| Event handler leak | MAJOR | Event handlers should be properly removed |

## Rule Exclusions

Some rules may be excluded for specific files or directories. The following exclusions are defined:

| Rule | Exclusion Pattern | Reason |
|------|-------------------|--------|
| Method complexity | **/bootstrap/** | Bootstrap code may have higher complexity |
| Class complexity | **/bootstrap/** | Bootstrap code may have higher complexity |
| File length | **/bootstrap/** | Bootstrap files may be longer |
| Comment density | **/generated/** | Generated code does not need comments |

## Setting Up Quality Gates in SonarQube

To set up the quality gates in SonarQube:

1. Log in to the SonarQube dashboard as an administrator
2. Go to Quality Gates
3. Create a new quality gate or edit the default one
4. Add the conditions defined above
5. Set the quality gate as default for the project

## Setting Up Custom Rules in SonarQube

To set up custom rules in SonarQube:

1. Log in to the SonarQube dashboard as an administrator
2. Go to Rules
3. Create a new rule or edit an existing one
4. Set the severity and parameters as defined above
5. Activate the rule for the project

## Monitoring Quality Gate Status

The quality gate status can be monitored in the following ways:

1. SonarQube dashboard
2. SonarQube API
3. CI/CD pipeline integration
4. IDE plugins (e.g., SonarLint)

## Handling Quality Gate Failures

When a quality gate fails, the following steps should be taken:

1. Review the issues reported by SonarQube
2. Fix the issues with the highest severity first
3. Run the analysis again to verify that the issues are fixed
4. If the quality gate still fails, repeat the process

## Continuous Improvement

The quality gates and rules should be reviewed and updated regularly to ensure they remain relevant and effective. The following schedule is recommended:

- Monthly review of quality gate conditions
- Quarterly review of custom rules
- Annual review of rule exclusions

## References

- [SonarQube Quality Gates Documentation](https://docs.sonarqube.org/latest/user-guide/quality-gates/)
- [SonarQube Rules Documentation](https://docs.sonarqube.org/latest/user-guide/rules/)
- [SonarQube Java Rules](https://rules.sonarsource.com/java/)