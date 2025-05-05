package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

/**
 * Tests to verify that Domain-Driven Design principles are followed in the project.
 * These tests ensure that DDD concepts like aggregates, entities, value objects,
 * domain events, and repositories are implemented correctly.
 */
public class DddArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void aggregateRootsShouldBeInAggregatesPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Aggregate")
                .or().haveSimpleNameEndingWith("Root")
                .should().resideInAPackage("com.belman.domain.aggregates..")
                .because("Aggregate roots should reside in the aggregates package to maintain proper organization.")
                .allowEmptyShould(true);

        rule.check(importedClasses);
    }

    @Test
    public void domainEventsShouldBeImmutable() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.events..")
                .and().doNotHaveSimpleName("DomainEventPublisher")
                .and().doNotHaveSimpleName("DomainEvents")
                .should().haveOnlyFinalFields()
                .because("Domain events should be immutable to ensure consistency and thread safety.");

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectsShouldBeImmutable() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.valueobjects..")
                .should().haveOnlyFinalFields()
                .because("Value objects should be immutable to ensure their integrity.");

        rule.check(importedClasses);
    }
}
