package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests to verify naming conventions across all layers of the application.
 * This helps ensure consistency in naming patterns across the codebase.
 */
public class ConsolidatedNamingConventionsTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
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
}