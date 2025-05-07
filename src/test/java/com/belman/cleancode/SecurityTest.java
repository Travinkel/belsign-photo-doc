package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class SecurityTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void sensitiveDataShouldNotBeLoggedDirectly() {
        ArchRule rule = noClasses()
                .should().callMethodWhere(method -> method.getOwner().getSimpleName().equals("Logger")
                                                    && method.getName().startsWith("log")
                                                    && method.getParameterTypes().stream()
                                                            .anyMatch(param -> param.getName().contains("password")))
                .because("Sensitive data should not be logged directly");

        rule.check(importedClasses);
    }

    // Add more security-related tests
}