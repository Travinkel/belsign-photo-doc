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
 * Tests to verify that bootstrap and shared packages are properly integrated into the clean architecture.
 * These tests ensure that bootstrap and shared are not treated as separate layers but are properly
 * integrated into the existing layers (domain, usecase, infrastructure, presentation).
 */
public class CleanArchitectureBootstrapTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void bootstrapShouldBePartOfInfrastructureLayer() {
        // Bootstrap classes should be part of the infrastructure layer
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.bootstrap..")
                .should().resideInAPackage("com.belman.infrastructure.bootstrap..")
                .because("Bootstrap classes should be part of the infrastructure layer, not a separate layer")
                .allowEmptyShould(true); // Allow test to pass if no classes match the pattern

        rule.check(importedClasses);
    }

    @Test
    public void sharedShouldBePartOfDomainLayer() {
        // Shared classes should be part of the domain layer
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.shared..")
                .should().resideInAPackage("com.belman.domain.shared..")
                .because("Shared classes should be part of the domain layer, not a separate layer")
                .allowEmptyShould(true); // Allow test to pass if no classes match the pattern

        rule.check(importedClasses);
    }

    @Test
    public void noClassesShouldResideInBootstrapOrSharedPackages() {
        // No classes should reside directly in the bootstrap or shared packages
        ArchRule rule = noClasses()
                .should().resideInAnyPackage("com.belman.bootstrap", "com.belman.shared")
                .because("Bootstrap and shared are not layers and should not contain classes directly");

        rule.check(importedClasses);
    }

    @Test
    public void layeredArchitectureShouldNotIncludeBootstrapOrSharedAsLayers() {
        // Layered architecture should not include bootstrap or shared as layers
        // For this test, we'll only check that bootstrap and shared packages are not defined as separate layers
        // We won't check the full layered architecture rules, as that's covered by other tests

        // Check that bootstrap package is part of infrastructure layer
        ArchRule bootstrapRule = classes()
                .that().resideInAPackage("com.belman.bootstrap..")
                .should().resideInAPackage("com.belman.infrastructure.bootstrap..")
                .because("Bootstrap should be part of the infrastructure layer, not a separate layer")
                .allowEmptyShould(true);

        bootstrapRule.check(importedClasses);

        // Check that shared package is part of domain layer
        ArchRule sharedRule = classes()
                .that().resideInAPackage("com.belman.shared..")
                .should().resideInAPackage("com.belman.domain.shared..")
                .because("Shared should be part of the domain layer, not a separate layer")
                .allowEmptyShould(true);

        sharedRule.check(importedClasses);
    }
}
