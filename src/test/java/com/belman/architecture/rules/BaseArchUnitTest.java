package com.belman.architecture.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseArchUnitTest {

    protected static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
        if (importedClasses == null) {
            throw new IllegalStateException("Failed to import classes for testing.");
        }
    }
}
