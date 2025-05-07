package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

public class MethodCohesionTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void methodsShouldBeHighlyCohesive() {
        ArchRule rule = methods()
                .should().haveRawLinesOfCodeLessThanOrEqualTo(20)
                .andShould().haveNumberOfAccessedFieldsLessThanOrEqualTo(5)
                .because("Methods should be highly cohesive");

        rule.check(importedClasses);
    }

    // Add more tests for method cohesion
}