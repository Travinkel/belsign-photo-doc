package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class LayerDependencyTests {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void servicesShouldNotAccessControllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.application..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldNotDependOnServices() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.infrastructure.persistence..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.application..");

        rule.check(importedClasses);
    }
}
