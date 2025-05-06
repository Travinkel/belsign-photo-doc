package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests that verify the integrity of bounded contexts in the domain layer.
 * These tests ensure that bounded contexts remain independent and properly encapsulated.
 */
public class BoundedContextsTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void userContextShouldNotDependOnOtherBoundedContexts() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain.user..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.domain.order..",
                        "com.belman.domain.photo..",
                        "com.belman.domain.customer..",
                        "com.belman.domain.report.."
                )
                .because("User bounded context should be independent from other bounded contexts");

        rule.check(importedClasses);
    }

    @Test
    public void orderContextShouldNotDependOnUserContextInternals() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain.order..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.domain.user.events..",
                        "com.belman.domain.user.services.."
                )
                .because("Order context should only depend on UserReference, not user internals");

        rule.check(importedClasses);
    }

    @Test
    public void photoContextShouldNotDependOnUserContextInternals() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain.photo..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.domain.user.events..",
                        "com.belman.domain.user.services.."
                )
                .because("Photo context should only depend on UserReference, not user internals");

        rule.check(importedClasses);
    }

    @Test
    public void boundedContextsShouldAccessOtherContextsOnlyViaPublicInterfaces() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage(
                        "..domain.order..",
                        "..domain.photo..",
                        "..domain.customer..",
                        "..domain.report.."
                )
                .and().doNotHaveSimpleName("UserReference")
                .should().accessClassesThat().haveSimpleNameStartingWith("UserAggregate")
                .because("Bounded contexts should only interact via explicit references");

        rule.check(importedClasses);
    }

    @Test
    public void boundedContextsShouldHaveTheirOwnRepositories() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .and().resideInAPackage("com.belman.domain..")
                .should().resideInAnyPackage(
                        "..domain.user..",
                        "..domain.order..",
                        "..domain.photo..",
                        "..domain.customer..",
                        "..domain.report..",
                        "..domain.repositories.."  // legacy location
                )
                .because("Each bounded context should have its own repository interfaces");

        rule.check(importedClasses);
    }

    @Test
    public void commonPackageShouldNotDependOnBoundedContexts() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain.common..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..domain.user..",
                        "..domain.order..",
                        "..domain.photo..",
                        "..domain.customer..",
                        "..domain.report.."
                )
                .because("Common package should be independent of any specific bounded context");

        rule.check(importedClasses);
    }
}