package com.belman.cleancode;

import com.belman.domain.common.base.ValueObject;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ImmutabilityTest {
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void valueObjectsShouldBeImmutable() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(ValueObject.class)
                .should().beImmutable()
                .because("Value objects should be immutable");

        rule.check(importedClasses);
    }
}