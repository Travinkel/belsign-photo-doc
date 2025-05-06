package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Architecture tests for the domain layer structure.
 * These tests ensure that the domain layer is organized according to
 * Domain-Driven Design (DDD) principles and Clean Architecture.
 */
public class DomainLayerTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman.domain");
    }

    @Test
    public void domainLayerShouldNotDependOnOutsideLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.application..",
                        "com.belman.infrastructure..",
                        "com.belman.presentation.."
                )
                .because("Domain layer should be independent from outside layers according to Clean Architecture");

        rule.check(importedClasses);
    }

    @Test
    public void aggregatesMustHaveIdentityAndEncapsulateEntities() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.aggregates..")
                .should().accessClassesThat().resideInAnyPackage(
                        "com.belman.domain.aggregates..",
                        "com.belman.domain.entities..",
                        "com.belman.domain.valueobjects..",
                        "com.belman.domain.enums..",
                        "com.belman.domain.core..",
                        "com.belman.domain.shared.."
                )
                .because("Aggregates should encapsulate entities and value objects");

        rule.check(importedClasses);
    }

    @Test
    public void entitiesShouldBeEncapsulatedByAggregates() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.entities..")
                .should().onlyBeAccessed().byAnyPackage("com.belman.domain.aggregates..")
                .because("Entities should be encapsulated by aggregates");

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectsShouldBeImmutable() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.valueobjects..")
                .should().haveOnlyFinalFields()
                .because("Value objects should be immutable");

        rule.check(importedClasses);
    }

    @Test
    public void domainServicesShouldOperateOnMultipleAggregates() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.services..")
                .and().areNotInterfaces()
                .should().accessClassesThat().resideInAPackage("com.belman.domain.aggregates..")
                .because("Domain services should operate on aggregates");

        rule.check(importedClasses);
    }

    @Test
    public void domainServicesShouldNotHaveMutableState() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.services..")
                .and().areNotInterfaces()
                .and().doNotHaveSimpleName("DefaultDomainEventHandler") // Exclude specific exceptions
                .should().haveOnlyFinalFields()
                .because("Domain services should be stateless or have only immutable state");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldOnlyBeAccessedByServicesOrDomainLayer() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.repositories..")
                .should().onlyBeAccessed().byAnyPackage(
                        "com.belman.domain.services..",
                        "com.belman.domain.core..",
                        "com.belman.application..",
                        "com.belman.infrastructure.persistence.."
                )
                .because("Repositories should only be accessed by services or domain layer");

        rule.check(importedClasses);
    }

    @Test
    public void securityClassesShouldBeInSecurityPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Auth.*|.*Password.*|.*Security.*|.*Credential.*")
                .and().resideInAPackage("com.belman.domain..")
                .should().resideInAPackage("com.belman.domain.security..")
                .because("Security classes should be in the security package");

        rule.check(importedClasses);
    }

    @Test
    public void rbacClassesShouldBeInRbacPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Role.*|.*Permission.*|.*Access.*|.*RBAC.*")
                .and().resideInAPackage("com.belman.domain..")
                .should().resideInAPackage("com.belman.domain.rbac..")
                .because("Role-based access control classes should be in the rbac package");

        rule.check(importedClasses);
    }

    @Test
    public void specificationsClassesShouldBeInSpecificationPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Specification.*")
                .and().resideInAPackage("com.belman.domain..")
                .should().resideInAPackage("com.belman.domain.specification..")
                .because("Specifications should be in the specification package");

        rule.check(importedClasses);
    }

    @Test
    public void eventsClassesShouldBeInEventsPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Event.*")
                .and().resideInAPackage("com.belman.domain..")
                .and().resideOutsideOfPackage("com.belman.domain.shared..")
                .should().resideInAPackage("com.belman.domain.events..")
                .because("Domain events should be in the events package");

        rule.check(importedClasses);
    }

    @Test
    public void commandClassesShouldBeInCommandsPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Command.*")
                .and().resideInAPackage("com.belman.domain..")
                .and().resideOutsideOfPackage("com.belman.domain.shared..")
                .should().resideInAPackage("com.belman.domain.commands..")
                .because("Command classes should be in the commands package");

        rule.check(importedClasses);
    }

    @Test
    public void domainExceptionsShouldBeInExceptionsPackage() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Exception")
                .and().resideInAPackage("com.belman.domain..")
                .should().resideInAPackage("com.belman.domain.exceptions..")
                .because("Domain exceptions should be in the exceptions package");

        rule.check(importedClasses);
    }
}