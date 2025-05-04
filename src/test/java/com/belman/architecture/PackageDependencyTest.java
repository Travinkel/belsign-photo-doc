package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests to verify package dependencies within the clean architecture.
 * These tests ensure that specific package dependencies are respected.
 */
public class PackageDependencyTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
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