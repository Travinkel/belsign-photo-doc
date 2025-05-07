package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NullHandlingTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void methodsShouldNotReturnNull() {
        ArchRule rule = methods()
                .should(not(returnNull()))
                .because("Methods should return Optional or throw exceptions instead of returning null");

        rule.check(importedClasses);
    }

    @Test
    public void publicMethodsShouldValidateParameters() {
        ArchRule rule = methods()
                .that().arePublic()
                .should(validateParameters())
                .because("Public methods should validate their parameters");

        rule.check(importedClasses);
    }
}
