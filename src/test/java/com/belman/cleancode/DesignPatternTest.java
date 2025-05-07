package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;

public class DesignPatternTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void singletonPatternShouldBeImplementedCorrectly() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Singleton")
                .should().haveOnlyPrivateConstructors()
                .andShould().haveStaticMethod("getInstance")
                .because("Singleton pattern should be implemented correctly");

        rule.check(importedClasses);
    }

    // Add more tests for other design patterns as needed
}