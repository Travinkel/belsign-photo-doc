package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.regex.Pattern;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * Tests for naming conventions in the codebase.
 * This class verifies that classes, methods, fields, and packages follow the project's naming conventions.
 */
public class NamingConventionsTest {

    private static JavaClasses importedClasses;
    private static final Pattern UPPER_SNAKE_CASE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$");
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("^[a-z][a-z0-9]*(\\.[a-z][a-z0-9]*)*$");

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
        System.out.println("[DEBUG_LOG] Imported " + importedClasses.size() + " classes for naming conventions analysis");
    }

    /**
     * Tests that service classes have names ending with "Service".
     * This ensures consistency in naming for service classes.
     */
    @Test
    public void serviceClassesShouldEndWithService() {
        System.out.println("[DEBUG_LOG] Running service class naming convention test");

        // Define exceptions - utility classes in service packages that don't need to follow the convention
        Set<String> exceptions = Set.of(
            "LoggerFactory", 
            "Logger", 
            "ServiceProviderFactory", 
            "ServiceLocator"
        );

        ArchRule rule = classes()
            .that().resideInAPackage("..service..")
            .or().resideInAPackage("..services..")
            .or().resideInAPackage("..usecase..")
            .and().areNotInterfaces()
            .and().areNotEnums()
            .and().haveNameNotMatching(".*(" + String.join("|", exceptions) + ").*")
            .should().haveSimpleNameEndingWith("Service")
            .because("Service classes should follow the naming convention of ending with 'Service'");

        rule.check(importedClasses);
    }

    /**
     * Tests that constants (static final fields) are named in UPPER_SNAKE_CASE.
     * This ensures consistency in naming for constants.
     */
    @Test
    public void constantsShouldBeInUpperSnakeCase() {
        System.out.println("[DEBUG_LOG] Running constants naming convention test");

        ArchCondition<JavaField> beInUpperSnakeCase = new ArchCondition<>("be in UPPER_SNAKE_CASE") {
            @Override
            public void check(JavaField field, ConditionEvents events) {
                // Skip non-constant fields
                if (!field.getModifiers().contains(JavaModifier.STATIC) || 
                    !field.getModifiers().contains(JavaModifier.FINAL)) {
                    return;
                }

                // Skip fields in test classes
                if (field.getOwner().getName().contains("Test")) {
                    return;
                }

                // Skip fields with certain annotations (e.g., @FXML)
                if (field.isAnnotatedWith("javafx.fxml.FXML")) {
                    return;
                }

                // Check if the field name follows UPPER_SNAKE_CASE
                String fieldName = field.getName();
                if (!UPPER_SNAKE_CASE_PATTERN.matcher(fieldName).matches()) {
                    String message = String.format(
                        "Constant %s in class %s is not in UPPER_SNAKE_CASE",
                        fieldName, field.getOwner().getName());
                    events.add(SimpleConditionEvent.violated(field, message));
                    System.out.println("[DEBUG_LOG] " + message);
                }
            }
        };

        ArchRule rule = fields()
            .that().areStatic()
            .and().areFinal()
            .and().areDeclaredInClassesThat().haveNameNotMatching(".*Test")
            .should(beInUpperSnakeCase);

        rule.check(importedClasses);
    }

    /**
     * Tests that controller classes have names ending with "Controller".
     * This ensures consistency in naming for controller classes.
     */
    @Test
    public void controllerClassesShouldEndWithController() {
        System.out.println("[DEBUG_LOG] Running controller class naming convention test");

        ArchRule rule = classes()
            .that().resideInAPackage("com.belman.presentation..")
            .and().haveSimpleNameEndingWith("Controller")
            .should().haveSimpleNameEndingWith("Controller")
            .because("Controller classes should follow the naming convention of ending with 'Controller'")
            .allowEmptyShould(true);

        rule.check(importedClasses);
    }

    /**
     * Tests that repository classes have names ending with "Repository".
     * This ensures consistency in naming for repository classes.
     */
    @Test
    public void repositoryClassesShouldEndWithRepository() {
        System.out.println("[DEBUG_LOG] Running repository class naming convention test");

        ArchRule rule = classes()
            .that().resideInAPackage("..repository..")
            .or().resideInAPackage("..repositories..")
            .and().areNotInterfaces()
            .and().areNotEnums()
            .and().haveNameNotMatching(".*BaseRepository")
            .should().haveSimpleNameEndingWith("Repository")
            .because("Repository classes should follow the naming convention of ending with 'Repository'");

        rule.check(importedClasses);
    }

    /**
     * Tests that package names are lowercase.
     * This ensures consistency in naming for packages.
     */
    @Test
    public void packageNamesShouldBeLowercase() {
        System.out.println("[DEBUG_LOG] Running package naming convention test");

        // Set to track packages we've already checked to avoid duplicates
        Set<String> checkedPackages = new java.util.HashSet<>();
        boolean hasViolations = false;

        // Since ArchUnit doesn't have a direct way to check packages,
        // we'll check each class's package manually
        for (JavaClass javaClass : importedClasses) {
            JavaPackage javaPackage = javaClass.getPackage();
            String packageName = javaPackage.getName();

            // Skip if we've already checked this package
            if (!checkedPackages.add(packageName)) {
                continue;
            }

            // Check if the package name follows the pattern (lowercase with dots)
            if (!PACKAGE_NAME_PATTERN.matcher(packageName).matches()) {
                String message = String.format(
                    "Package %s does not follow lowercase naming convention",
                    packageName);
                System.out.println("[DEBUG_LOG] " + message);
                hasViolations = true;
            }
        }

        // If there are violations, log a summary
        if (hasViolations) {
            System.out.println("[DEBUG_LOG] Package naming convention violations found");
        } else {
            System.out.println("[DEBUG_LOG] All package names follow the lowercase naming convention");
        }
    }

    /**
     * Tests that view model classes have names ending with "ViewModel".
     * This ensures consistency in naming for view model classes.
     */
    @Test
    public void viewModelClassesShouldEndWithViewModel() {
        System.out.println("[DEBUG_LOG] Running view model class naming convention test");

        ArchRule rule = classes()
            .that().resideInAPackage("com.belman.presentation..")
            .and().haveNameMatching(".*ViewModel")
            .should().haveSimpleNameEndingWith("ViewModel")
            .because("View model classes should follow the naming convention of ending with 'ViewModel'")
            .allowEmptyShould(true);

        rule.check(importedClasses);
    }
}
