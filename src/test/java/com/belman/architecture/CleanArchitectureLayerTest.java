package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Tests to verify that the project follows Clean Architecture layer principles.
 * These tests focus on the responsibilities and dependencies of each layer.
 */
public class CleanArchitectureLayerTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void domainLayerShouldNotHaveFrameworkDependencies() {
        // Domain layer should not have framework dependencies
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "javafx..", "com.gluonhq..", "org.springframework..",
                        "javax.persistence..", "jakarta.persistence..", "org.hibernate..", "com.fasterxml.jackson.."
                )
                .because("The domain layer should be independent of frameworks to maintain business logic purity.");

        rule.check(importedClasses);
    }

    @Test
    public void applicationLayerShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.usecase..")
                .and().resideOutsideOfPackage("com.belman.usecase.core..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.infrastructure..")
                .because("The application layer should not depend on the infrastructure layer.");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldOnlyDependOnAllowedLayers() {
        // Controllers should only depend on allowed layers
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .and().resideInAPackage("com.belman.presentation..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "com.belman.presentation..", "com.belman.usecase..", "com.belman.domain..",
                        "com.belman.infrastructure.service..", "com.belman.infrastructure.camera..",
                        "java..", "javafx..", "com.gluonhq.."
                )
                .because("Controllers should only depend on allowed layers to maintain separation of concerns.");

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldOnlyDependOnAllowedLayers() {
        // ViewModels should only depend on allowed layers
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ViewModel")
                .and().resideInAPackage("com.belman.presentation..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "com.belman.presentation..", "com.belman.usecase..", "com.belman.domain..",
                        "com.belman.infrastructure.logging..", "com.belman.infrastructure.service..",
                        "java..", "javafx..", "com.gluonhq.."
                )
                .because("ViewModels should only depend on allowed layers to maintain separation of concerns.");

        rule.check(importedClasses);
    }

    @Test
    public void infrastructureRepositoriesShouldFollowNamingConventions() {
        // Infrastructure repositories should follow naming conventions
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.infrastructure.persistence..")
                .and().areNotInterfaces()
                .should().haveSimpleNameEndingWith("Repository")
                .because("Infrastructure persistence classes should follow naming conventions for clarity.");

        rule.check(importedClasses);
    }
}
