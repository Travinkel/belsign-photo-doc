package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Architecture tests for the intention-based domain layer structure.
 * These tests ensure that the domain layer is organized by business contexts
 * following Domain-Driven Design principles.
 */
public class DomainContextsTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman.domain");
    }

    @Test
    public void userRelatedClassesShouldBeInUserContext() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("User")
                .or().haveSimpleNameContaining("Auth")
                .or().haveSimpleNameContaining("Permission")
                .or().haveSimpleNameContaining("Role")
                .or().haveSimpleNameContaining("Password")
                .or().haveSimpleNameContaining("Credential")
                .and().resideInAPackage("com.belman.domain..")
                .and().resideOutsideOfPackage("..common..")
                .and().resideOutsideOfPackage("..security..")  // Allow security-specific classes to remain in security
                .should().resideInAPackage("com.belman.domain.user..")
                .because("User-related classes should be in the user context");

        rule.check(importedClasses);
    }

    @Test
    public void orderRelatedClassesShouldBeInOrderContext() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Order")
                .and().resideInAPackage("com.belman.domain..")
                .and().resideOutsideOfPackage("..common..")
                .should().resideInAPackage("com.belman.domain.order..")
                .because("Order-related classes should be in the order context");

        rule.check(importedClasses);
    }

    @Test
    public void photoRelatedClassesShouldBeInPhotoContext() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Photo")
                .and().resideInAPackage("com.belman.domain..")
                .and().resideOutsideOfPackage("..common..")
                .should().resideInAPackage("com.belman.domain.photo..")
                .because("Photo-related classes should be in the photo context");

        rule.check(importedClasses);
    }

    @Test
    public void reportRelatedClassesShouldBeInReportContext() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Report")
                .and().resideInAPackage("com.belman.domain..")
                .and().resideOutsideOfPackage("..common..")
                .should().resideInAPackage("com.belman.domain.report..")
                .because("Report-related classes should be in the report context");

        rule.check(importedClasses);
    }

    @Test
    public void customerRelatedClassesShouldBeInCustomerContext() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Customer")
                .and().resideInAPackage("com.belman.domain..")
                .and().resideOutsideOfPackage("..common..")
                .should().resideInAPackage("com.belman.domain.customer..")
                .because("Customer-related classes should be in the customer context");

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectsShouldBeInContextOrCommon() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Value.*")
                .or().haveSimpleNameEndingWith("Id")
                .or().haveSimpleNameEndingWith("Name")
                .or().haveSimpleNameEndingWith("Email")
                .or().haveSimpleNameEndingWith("Number")
                .and().resideInAPackage("com.belman.domain..")
                .should().resideInAnyPackage(
                        "com.belman.domain.common..",
                        "com.belman.domain.user..",
                        "com.belman.domain.order..",
                        "com.belman.domain.photo..",
                        "com.belman.domain.report..",
                        "com.belman.domain.customer..",
                        "com.belman.domain.security.."
                )
                .because("Value objects should be in their context or in common");

        rule.check(importedClasses);
    }

    @Test
    public void contextsShouldNotDependOnOtherContextsExceptCommon() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain.user..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.domain.order..",
                        "com.belman.domain.photo..",
                        "com.belman.domain.report..",
                        "com.belman.domain.customer.."
                );

        rule.check(importedClasses);
    }
}