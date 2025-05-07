package com.belman.cleancode;

import com.tngtech.archunit.lang.ArchRule;

public class FunctionalProgrammingTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void shouldUseStreamAPIWhereAppropriate() {
        ArchRule rule = methods()
                .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Service")
                .should(useStreamAPI())
                .because("Service methods should use Stream API for collections processing where appropriate");

        rule.check(importedClasses);
    }
}