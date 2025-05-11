package com.belman.architecture.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Tests to enforce the organization of classes in the business layer by responsibility or feature.
 * This test ensures that classes are properly organized in the business.module package.
 */
public class BusinessLayerPackageStructureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void businessObjectsShouldBeInModulePackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Business")
                .should().resideInAPackage("com.belman.business.module..")
                .because("Business objects should be in the module package");

        rule.check(importedClasses);
    }

    @Test
    public void businessComponentsShouldBeInModulePackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Component")
                .should().resideInAPackage("com.belman.business.module..")
                .because("Business components should be in the module package");

        rule.check(importedClasses);
    }

    @Test
    public void dataAccessInterfacesShouldBeInModulePackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("DataAccess")
                .or().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.business.module..")
                .because("Data access interfaces should be in the module package");

        rule.check(importedClasses);
    }

    @Test
    public void businessServicesShouldBeInModulePackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("BusinessService")
                .or().haveSimpleNameEndingWith("DomainService")
                .should().resideInAPackage("com.belman.business.module..")
                .because("Business services should be in the module package");

        rule.check(importedClasses);
    }

    @Test
    public void dataObjectsShouldBeInModulePackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("DataObject")
                .or().haveSimpleNameEndingWith("ValueObject")
                .should().resideInAPackage("com.belman.business.module..")
                .because("Data objects should be in the module package");

        rule.check(importedClasses);
    }

    @Test
    public void auditEventsShouldBeInModuleEventsPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Event")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .should().resideInAPackage("com.belman.business.module.events..")
                .orShould().resideInAPackage("com.belman.business.module.*.events..")
                .because("Audit events should be in the module.events package or a feature-specific events package");

        rule.check(importedClasses);
    }

    @Test
    public void orderRelatedClassesShouldBeInOrderPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Order")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.order..")
                .because("Order-related classes should be in the module.order package");

        rule.check(importedClasses);
    }

    @Test
    public void userRelatedClassesShouldBeInUserPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("User")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.user..")
                .because("User-related classes should be in the module.user package");

        rule.check(importedClasses);
    }

    @Test
    public void customerRelatedClassesShouldBeInCustomerPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Customer")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.customer..")
                .because("Customer-related classes should be in the module.customer package");

        rule.check(importedClasses);
    }

    @Test
    public void reportRelatedClassesShouldBeInReportPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Report")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.report..")
                .because("Report-related classes should be in the module.report package");

        rule.check(importedClasses);
    }

    @Test
    public void photoRelatedClassesShouldBeInOrderPhotoPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Photo")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.order.photo..")
                .because("Photo-related classes should be in the module.order.photo package");

        rule.check(importedClasses);
    }

    @Test
    public void securityRelatedClassesShouldBeInSecurityPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Security")
                .or().haveSimpleNameContaining("Authentication")
                .or().haveSimpleNameContaining("Authorization")
                .or().haveSimpleNameContaining("Password")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.security..")
                .because("Security-related classes should be in the module.security package");

        rule.check(importedClasses);
    }

    @Test
    public void commonValueObjectsShouldBeInCommonPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Address")
                .or().haveSimpleNameEndingWith("Name")
                .or().haveSimpleNameEndingWith("Email")
                .or().haveSimpleNameEndingWith("Phone")
                .or().haveSimpleNameEndingWith("Money")
                .or().haveSimpleNameEndingWith("Timestamp")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.common..")
                .because("Common value objects should be in the module.common package");

        rule.check(importedClasses);
    }

    @Test
    public void specificationsShouldBeInSpecificationPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Specification")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.specification..")
                .orShould().resideInAPackage("com.belman.business.module.*.specification..")
                .because(
                        "Specifications should be in the module.specification package or a feature-specific specification package");

        rule.check(importedClasses);
    }

    @Test
    public void exceptionsShouldBeInExceptionsPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Exception")
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.exceptions..")
                .because("Exceptions should be in the module.exceptions package");

        rule.check(importedClasses);
    }

    @Test
    public void servicesShouldBeInServicesPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areInterfaces()
                .and().resideInAPackage("com.belman.business.module..")
                .should().resideInAPackage("com.belman.business.module.services..")
                .orShould().resideInAPackage("com.belman.business.module.*.services..")
                .because(
                        "Service interfaces should be in the module.services package or a feature-specific services package");

        rule.check(importedClasses);
    }
}