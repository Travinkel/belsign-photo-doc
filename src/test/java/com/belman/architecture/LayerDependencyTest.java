package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Tests to verify that the project follows clean architecture principles.
 * These tests ensure that dependencies between layers flow in the correct direction:
 * Presentation -> Usecase -> Domain
 * Infrastructure -> Domain
 */
public class LayerDependencyTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void servicesShouldNotAccessControllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.application..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldNotDependOnServices() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.infrastructure.persistence..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.application..");

        rule.check(importedClasses);
    }

    @Test
    public void domainShouldNotDependOnOtherLayers() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman.domain..",
                        "java..",
                        "javafx..",
                        "javax..",
                        "org.slf4j.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void applicationShouldNotDependOnPresentationOrInfrastructure() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.usecase..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman.usecase..",
                        "com.belman.domain..",
                        "java..",
                        "javafx..",
                        "javax..",
                        "org.slf4j.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void infrastructureShouldNotDependOnPresentation() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.infrastructure..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman.infrastructure..",
                        "com.belman.domain..",
                        "com.belman.usecase..",
                        "java..",
                        "javafx..",
                        "javax..",
                        "org.slf4j..",
                        "com.zaxxer..",
                        "com.microsoft..",
                        "com.sun.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Domain").definedBy("com.belman.domain..")
                .layer("Application").definedBy("com.belman.usecase..")
                .layer("Infrastructure").definedBy("com.belman.infrastructure..")
                .layer("Presentation").definedBy("com.belman.presentation..")

                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Presentation")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Presentation")
                .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Presentation")
                .whereLayer("Presentation").mayNotBeAccessedByAnyLayer();

        rule.check(importedClasses);
    }
}
