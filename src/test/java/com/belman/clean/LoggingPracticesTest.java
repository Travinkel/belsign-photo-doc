package com.belman.clean;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoggingPracticesTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void classesShouldUseProperLogging() {
        ArchRule rule = classes()
                .should().containAFieldOfType(Logger.class)
                .andShould().callMethod(Logger.class, "log").atLeastOnce()
                .because("Classes should use proper logging practices");

        rule.check(importedClasses);
    }

    // Add more tests for specific logging practices
}