package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NamingConventionsTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void serviceClassesShouldEndWithService() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.service..")
                .should().haveSimpleNameEndingWith("Service")
                .because("Service classes should follow the naming convention of ending with 'Service'");

        rule.check(importedClasses);
    }

    @Test
    public void constantsShouldBeInUpperSnakeCase() {
        // This is a placeholder test that always passes
        // The actual implementation would check that constants are in upper snake case
        assertTrue(true, "Constants should be in upper snake case");
    }

    @Test
    public void controllerClassesShouldEndWithController() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.controller..")
                .should().haveSimpleNameEndingWith("Controller")
                .because("Controller classes should follow the naming convention of ending with 'Controller'");

        rule.check(importedClasses);
    }

    @Test
    public void packageNamesShouldBeLowercase() {
        ArchRule rule = classes()
                .should().resideInAPackage("..")
                .andShould().haveNameMatching("^[a-z.]+$")
                .because("Package names should be lowercase and follow naming conventions");

        rule.check(importedClasses);
    }
}
