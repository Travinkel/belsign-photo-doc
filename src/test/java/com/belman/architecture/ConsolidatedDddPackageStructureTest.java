package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * Consolidated tests for Domain-Driven Design package structure.
 * This class combines rules from DddArchitectureTest, DddPackageStructureTest, and DddConceptsTest
 * to provide a comprehensive set of rules for DDD package structure and class placement.
 */
public class ConsolidatedDddPackageStructureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void entitiesShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Entity")
                .should().resideInAPackage("com.belman.domain.entities..")
                .because("Entities should be organized in the domain.entities package");

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectsShouldBeInCorrectPackageAndImmutable() {
        // First check package structure
        ArchRule packageRule = classes()
                .that().haveSimpleNameEndingWith("Value")
                .or().haveSimpleNameEndingWith("Id")
                .or().haveSimpleNameEndingWith("Email")
                .or().haveSimpleNameEndingWith("Name")
                .or().haveSimpleNameEndingWith("Number")
                .or().haveSimpleNameEndingWith("Path")
                .should().resideInAPackage("com.belman.domain.valueobjects..")
                .because("Value objects should be organized in the domain.valueobjects package");

        packageRule.check(importedClasses);

        // Then check immutability
        ArchRule immutabilityRule = classes()
                .that().resideInAPackage("com.belman.domain.valueobjects..")
                .should().haveOnlyFinalFields()
                .because("Value objects should be immutable to ensure their integrity");

        immutabilityRule.check(importedClasses);
    }

    @Test
    public void aggregateRootsShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Aggregate")
                .or().haveSimpleNameEndingWith("Root")
                .should().resideInAPackage("com.belman.domain.aggregates..")
                .because("Aggregate roots should be organized in the domain.aggregates package");

        rule.check(importedClasses);
    }

    @Test
    public void domainEventsShouldBeInCorrectPackageAndImmutable() {
        // First check package structure
        ArchRule packageRule = classes()
                .that().haveSimpleNameEndingWith("Event")
                .and().resideInAPackage("com.belman.domain..")
                .should().resideInAPackage("com.belman.domain.events..")
                .because("Domain events should be organized in the domain.events package");

        packageRule.check(importedClasses);

        // Then check immutability
        ArchRule immutabilityRule = classes()
                .that().resideInAPackage("com.belman.domain.events..")
                .and().doNotHaveSimpleName("DomainEventPublisher")
                .and().doNotHaveSimpleName("DomainEvents")
                .should().haveOnlyFinalFields()
                .because("Domain events should be immutable for consistency and thread safety");

        immutabilityRule.check(importedClasses);
    }

    @Test
    public void repositoryInterfacesShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain.repositories..")
                .because("Repository interfaces should be organized in the domain.repositories package");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldFollowNamingConventions() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.repositories..")
                .should().haveSimpleNameEndingWith("Repository")
                .andShould().beInterfaces()
                .because("Repository interfaces should follow naming conventions and be interfaces");

        rule.check(importedClasses);
    }

    @Test
    public void repositoryImplementationsShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areNotInterfaces()
                .should().resideInAPackage("com.belman.infrastructure.persistence..")
                .because("Repository implementations should be organized in the infrastructure.persistence package");

        rule.check(importedClasses);
    }

    @Test
    public void serviceInterfacesShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain.services..")
                .because("Service interfaces should be organized in the domain.services package");

        rule.check(importedClasses);
    }

    @Test
    public void serviceImplementationsShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areNotInterfaces()
                .should().resideInAnyPackage("com.belman.application..", "com.belman.infrastructure..")
                .because("Service implementations should be organized in application or infrastructure layers");

        rule.check(importedClasses);
    }

    @Test
    public void specificationsShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Specification")
                .should().resideInAPackage("com.belman.domain.specification..")
                .because("Specifications should be organized in the domain.specification package");

        rule.check(importedClasses);
    }

    @Test
    public void factoriesShouldBeInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Factory")
                .should().resideInAnyPackage(
                        "com.belman.domain.factories..",
                        "com.belman.application.factories..",
                        "com.belman.infrastructure.factories.."
                )
                .because("Factories should be organized in the appropriate layer's factories package");

        rule.check(importedClasses);
    }

    @Test
    public void bootstrapShouldBePartOfInfrastructure() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.bootstrap..")
                .should().dependOnClassesThat().resideInAnyPackage("com.belman.infrastructure..")
                .because("Bootstrap should be part of or depend on the infrastructure layer")
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }

    @Test
    public void sharedShouldBePartOfDomain() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.shared..")
                .should().resideInAPackage("com.belman.domain.shared..")
                .because("Shared should be part of the domain layer");

        rule.check(importedClasses);
    }
}