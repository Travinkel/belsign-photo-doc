package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Tests to verify that naming conventions are followed in the project.
 * These tests ensure that classes are named according to their role and responsibility.
 */
public class NamingConventionTest {

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
                .that().resideInAnyPackage("com.belman.application..", "com.belman.domain.services..", "com.belman.infrastructure.service..")
                .and().haveNameMatching(".*Service")
                .should().haveSimpleNameEndingWith("Service");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldBeSuffixedWithRepository() {
        ArchRule rule = classes()
                .that().resideInAnyPackage("com.belman.domain.repositories..", "com.belman.infrastructure.persistence..")
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
}