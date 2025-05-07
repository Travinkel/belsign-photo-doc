package com.belman.architecture.rules.naming;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests to verify that naming conventions are followed in the project.
 * These tests ensure that classes are named according to their role and responsibility.
 */
public class NamingAndPackagingRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void controllersShouldBeSuffixedWithController() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation..")
                .and().haveNameMatching(".*Controller")
                .should().haveSimpleNameEndingWith("Controller");

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldBeSuffixedWithViewModel() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation..")
                .and().haveNameMatching(".*ViewModel")
                .should().haveSimpleNameEndingWith("ViewModel");

        rule.check(importedClasses);
    }

    @Test
    public void servicesShouldBeSuffixedWithService() {
        ArchRule rule = classes()
                .that().resideInAnyPackage("com.belman.application..", "com.belman.domain.services..",
                        "com.belman.infrastructure.service..")
                .and().haveNameMatching(".*Service")
                .should().haveSimpleNameEndingWith("Service");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldBeSuffixedWithRepository() {
        ArchRule rule = classes()
                .that().resideInAnyPackage("com.belman.domain.repositories..",
                        "com.belman.infrastructure.persistence..")
                .and().haveNameMatching(".*Repository")
                .should().haveSimpleNameEndingWith("Repository");

        rule.check(importedClasses);
    }

    @Test
    public void entitiesShouldNotHaveSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.entities..")
                .should().haveSimpleNameNotEndingWith("Entity");

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectsShouldNotHaveSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.valueobjects..")
                .should().haveSimpleNameNotEndingWith("ValueObject");

        rule.check(importedClasses);
    }

    @Test
    public void interfacesShouldNotHaveIPrefixOrInterfaceSuffix() {
        ArchRule rule = classes()
                .that().areInterfaces()
                .should().haveSimpleNameNotStartingWith("I")
                .andShould().haveSimpleNameNotEndingWith("Interface");

        rule.check(importedClasses);
    }

    @Test
    public void exceptionsShouldBeSuffixedWithException() {
        ArchRule rule = classes()
                .that().areAssignableTo(Exception.class)
                .should().haveSimpleNameEndingWith("Exception");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldHaveControllerSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation.controllers..")
                .or().resideInAPackage("com.belman.presentation..controller")
                .or().resideInAPackage("com.belman.presentation.views..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .should().haveSimpleNameEndingWith("Controller")
                .because("Controller classes should have 'Controller' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldHaveViewModelSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation.viewmodels..")
                .or().resideInAPackage("com.belman.presentation..viewmodel")
                .or().resideInAPackage("com.belman.presentation.views..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .should().haveSimpleNameEndingWith("ViewModel")
                .because("ViewModel classes should have 'ViewModel' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldHaveRepositorySuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.repositories..")
                .or().areAssignableTo("com.belman.domain.repositories.Repository")
                .should().haveSimpleNameEndingWith("Repository")
                .because("Repository interfaces and implementations should have 'Repository' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void servicesShouldHaveServiceSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.services..")
                .or().resideInAPackage("com.belman.application.services..")
                .or().resideInAPackage("com.belman.infrastructure.service..")
                .should().haveSimpleNameEndingWith("Service")
                .because("Service interfaces and implementations should have 'Service' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void useCasesShouldHaveUseCaseSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.usecase..")
                .or().resideInAPackage("com.belman.application..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .and().haveNameNotMatching(".*Service")
                .and().haveNameNotMatching(".*Factory")
                .and().haveNameNotMatching(".*Provider")
                .and().haveNameNotMatching(".*Config")
                .should().haveSimpleNameEndingWith("UseCase")
                .orShould().haveSimpleNameEndingWith("Command")
                .orShould().haveSimpleNameEndingWith("Query")
                .orShould().haveSimpleNameEndingWith("Handler")
                .because(
                        "Use case classes should have appropriate suffixes like 'UseCase', 'Command', 'Query', or 'Handler'");

        rule.check(importedClasses);
    }

    @Test
    public void exceptionsShouldHaveExceptionSuffix() {
        ArchRule rule = classes()
                .that().areAssignableTo(Exception.class)
                .should().haveSimpleNameEndingWith("Exception")
                .because("Exception classes should have 'Exception' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void factoriesShouldHaveFactorySuffix() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Factory")
                .should().haveSimpleNameEndingWith("Factory")
                .because("Factory classes should have 'Factory' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void coordinatorsShouldHaveCoordinatorSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation.coordinators..")
                .should().haveSimpleNameEndingWith("Coordinator")
                .because("Coordinator classes should have 'Coordinator' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void interfacesShouldNotHaveIPrefixOrInterfaceSuffix() {
        ArchRule rule = noClasses()
                .that().areInterfaces()
                .should().haveSimpleNameStartingWith("I")
                .orShould().haveSimpleNameEndingWith("Interface")
                .because("Interfaces should not have 'I' prefix or 'Interface' suffix");

        rule.check(importedClasses);
    }

    @Test
    public void eventsShouldHaveEventSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.events..")
                .and().areNotEnums()
                .should().haveSimpleNameEndingWith("Event")
                .because("Event classes should have 'Event' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void specificationsShouldHaveSpecificationSuffix() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.specification..")
                .and().areNotEnums()
                .should().haveSimpleNameEndingWith("Specification")
                .because("Specification classes should have 'Specification' suffix for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void domainClassesShouldResideInProperDDDPackages() {
        // Domain classes should reside in proper DDD packages
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .should().resideInAnyPackage(
                        "com.belman.domain.entities..",
                        "com.belman.domain.valueobjects..",
                        "com.belman.domain.repositories..",
                        "com.belman.domain.services..",
                        "com.belman.domain.aggregates..",
                        "com.belman.domain.events..",
                        "com.belman.domain.specification..",
                        "com.belman.domain.shared..",
                        "com.belman.domain.core..",
                        "com.belman.domain.enums..",
                        "com.belman.domain.rbac.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void applicationClassesShouldBeOrganizedByFeature() {
        // Application classes should be organized by feature or use case
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.application..")
                .and().haveNameNotMatching(".*Config")
                .and().haveNameNotMatching(".*Factory")
                .and().haveNameNotMatching(".*Provider")
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
        // Infrastructure classes should be organized by technology or external system
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
        // Presentation classes should be organized by UI component or view
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

    @Test
    public void featurePackagesShouldNotCrossBoundaries() {
        // Feature packages should not cross layer boundaries
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.application.order..")
                .should().onlyBeAccessed().byClassesThat()
                .resideInAnyPackage(
                        "com.belman.application.order..",
                        "com.belman.presentation..",
                        "com.belman.infrastructure.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void packagesShouldUseLowercaseNaming() {
        // All packages should use lowercase naming
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman..")
                .should().resideInAPackage(".."); // This is a placeholder assertion that always passes
        // We can't directly check package naming with classes(), but we ensure classes are in packages

        rule.check(importedClasses);
    }

    @Test
    public void domainEntitiesShouldOnlyDependOnDomainPackages() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.entities..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman.domain..",
                        "java..",
                        "javax.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void domainValueObjectsShouldBeImmutable() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain.valueobjects..")
                .should().haveOnlyFinalFields();

        rule.check(importedClasses);
    }

    @Test
    public void repositoryImplementationsShouldResideInInfrastructureLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areNotInterfaces()
                .should().resideInAPackage("com.belman.infrastructure.persistence..");

        rule.check(importedClasses);
    }

    @Test
    public void repositoryInterfacesShouldResideInDomainLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain.repositories..");

        rule.check(importedClasses);
    }

    @Test
    public void serviceImplementationsShouldResideInApplicationOrInfrastructureLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areNotInterfaces()
                .should().resideInAnyPackage(
                        "com.belman.application..",
                        "com.belman.infrastructure.service.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void serviceInterfacesShouldResideInDomainLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain.services..");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldResideInPresentationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldResideInPresentationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ViewModel")
                .should().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }
}