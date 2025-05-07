package com.belman.architecture.rules.ddd;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class DddArchitectureRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void domainClassesShouldFollowDDDStructure() {
        // Consolidated rules for domain classes
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .should().resideInAnyPackage(
                        "com.belman.domain.entities..",
                        "com.belman.domain.valueobjects..",
                        "com.belman.domain.aggregates..",
                        "com.belman.domain.events..",
                        "com.belman.domain.repositories..",
                        "com.belman.domain.services..",
                        "com.belman.domain.specification..",
                        "com.belman.domain.shared.."
                )
                .because("Domain classes should follow proper DDD structure.");

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectsAndDomainEventsShouldBeImmutable() {
        // Consolidated immutability rules for value objects and domain events
        ArchRule rule = classes()
                .that().resideInAnyPackage("com.belman.domain.valueobjects..", "com.belman.domain.events..")
                .and().doNotHaveSimpleName("DomainEventPublisher")
                .and().doNotHaveSimpleName("DomainEvents")
                .should().haveOnlyFinalFields()
                .because("Value objects and domain events should be immutable.");

        rule.check(importedClasses);
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
                .because("Domain layer should be independent from outside layers according to Clean Architecture.");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldFollowNamingAndPlacementConventions() {
        // Consolidated rules for repository interfaces and implementations
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideInAnyPackage(
                        "com.belman.domain.repositories..",
                        "com.belman.infrastructure.persistence.."
                )
                .andShould().beInterfaces()
                .because("Repositories should follow naming conventions and be placed in appropriate packages.");

        rule.check(importedClasses);
    }
}
