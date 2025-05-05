package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Tests to verify that package structure follows Domain-Driven Design principles.
 * These tests ensure that classes are organized in the appropriate packages
 * according to their DDD role.
 */
public class DddPackageStructureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void domainClassesShouldResideInProperDDDPackages() {
        // Domain classes should reside in proper DDD packages
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Entity")
                .should().resideInAPackage("com.belman.domain.entities..")
                .allowEmptyShould(true); // Allow test to pass if no classes match the pattern

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectClassesShouldResideInValueObjectsPackage() {
        // Value object classes should reside in valueobjects package
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Value")
                .or().haveSimpleNameEndingWith("Id")
                .or().haveSimpleNameEndingWith("Email")
                .or().haveSimpleNameEndingWith("Name")
                .or().haveSimpleNameEndingWith("Number")
                .or().haveSimpleNameEndingWith("Path")
                .should().resideInAPackage("com.belman.domain.valueobjects..");

        rule.check(importedClasses);
    }

    @Test
    public void aggregateRootsShouldResideInAggregatesPackage() {
        // Aggregate roots should reside in aggregates package
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Aggregate")
                .or().haveSimpleNameEndingWith("Root")
                .should().resideInAPackage("com.belman.domain.aggregates..")
                .allowEmptyShould(true); // Allow test to pass if no classes match the pattern

        rule.check(importedClasses);
    }

    @Test
    public void domainEventsShouldResideInEventsPackage() {
        // Domain events should reside in events or shared package
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Event")
                .and().resideInAPackage("com.belman.domain..")
                .should().resideInAnyPackage(
                        "com.belman.domain.events..",
                        "com.belman.domain.shared.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void specificationsShouldResideInSpecificationPackage() {
        // Specifications should reside in specification package
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Specification")
                .should().resideInAPackage("com.belman.domain.specification..");

        rule.check(importedClasses);
    }

    @Test
    public void repositoryInterfacesShouldFollowPortsAndAdaptersPattern() {
        // Repository interfaces should follow ports and adapters pattern
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .should().resideInAnyPackage(
                        "com.belman.domain.repositories..",
                        "com.belman.application.*.port.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void serviceInterfacesShouldFollowPortsAndAdaptersPattern() {
        // Service interfaces should follow ports and adapters pattern
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areInterfaces()
                .and().resideOutsideOfPackage("com.belman.unit..")
                .and().resideOutsideOfPackage("com.belman.integration..")
                .and().resideOutsideOfPackage("com.belman.acceptance..")
                .should().resideInAnyPackage(
                        "com.belman.domain.services..",
                        "com.belman.application.*.port.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void factoriesShouldFollowLayerConventions() {
        // Factories should follow layer conventions
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Factory")
                .should().resideInAnyPackage(
                        "com.belman.domain..",
                        "com.belman.infrastructure..",
                        "com.belman.presentation.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void applicationFeatureClassesShouldBeInFeaturePackages() {
        // Application classes should be organized by feature
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.application..")
                .and().haveNameNotMatching(".*Config")
                .and().haveNameNotMatching(".*Factory")
                .and().haveNameNotMatching(".*Provider")
                .and().haveNameNotMatching(".*Module")
                .should().resideInAnyPackage(
                        "com.belman.application.core..",
                        "com.belman.application.commands..",
                        "com.belman.application.api..",
                        "com.belman.application.admin..",
                        "com.belman.application.auth..",
                        "com.belman.application.mobile..",
                        "com.belman.application.order..",
                        "com.belman.application.photo..",
                        "com.belman.application.qa..",
                        "com.belman.application.reporting..",
                        "com.belman.application.support.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void infrastructureClassesShouldBeOrganizedByTechnology() {
        // Infrastructure classes should be organized by technology
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.infrastructure..")
                .should().resideInAnyPackage(
                        "com.belman.infrastructure.persistence..",
                        "com.belman.infrastructure.service..",
                        "com.belman.infrastructure.email..",
                        "com.belman.infrastructure.storage..",
                        "com.belman.infrastructure.security..",
                        "com.belman.infrastructure.logging..",
                        "com.belman.infrastructure.config..",
                        "com.belman.infrastructure.camera..",
                        "com.belman.infrastructure.platform..",
                        "com.belman.infrastructure.core.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void presentationClassesShouldBeOrganizedByUIComponent() {
        // Presentation classes should be organized by UI component
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation..")
                .should().resideInAnyPackage(
                        "com.belman.presentation.views..",
                        "com.belman.presentation.components..",
                        "com.belman.presentation.core..",
                        "com.belman.presentation.navigation..",
                        "com.belman.presentation.binding.."
                );

        rule.check(importedClasses);
    }
}
