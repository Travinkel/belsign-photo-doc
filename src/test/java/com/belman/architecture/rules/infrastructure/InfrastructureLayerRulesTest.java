package com.belman.architecture.rules.infrastructure;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class InfrastructureLayerRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void onlyInfrastructureShouldAccessExternalLibraries() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.infrastructure..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman..",
                        "java..",
                        "javax..",
                        "org.slf4j..",
                        "com.zaxxer..",
                        "com.microsoft..",
                        "com.fasterxml.jackson.."
                );

        rule.check(importedClasses);
    }
}
