package com.belman.clean;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SolidPrinciplesTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void classesShouldHaveSingleResponsibility() {
        ArchRule rule = classes()
                .should().haveOnlyFinalFields()
                .andShould().haveOnlyPrivateConstructors()
                .andShould().havePublicMethodsWithSameReturnType()
                .because("Classes should have a single responsibility");

        rule.check(importedClasses);
    }

    @Test
    public void interfacesShouldBeSmallAndFocused() {
        ArchRule rule = interfaces()
                .should().haveOnlyAbstractMethods()
                .andShould().haveNoMoreThanTenMethods()
                .because("Interfaces should be small and focused (Interface Segregation Principle)");

        rule.check(importedClasses);
    }

    // Add more tests for Open/Closed, Liskov Substitution, and Dependency Inversion principles
}
