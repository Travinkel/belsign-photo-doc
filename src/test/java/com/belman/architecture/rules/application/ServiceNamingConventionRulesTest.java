package com.belman.architecture.rules.application;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ServiceNamingConventionRulesTest {

    private static JavaClasses importedClasses;

    private static final String SERVICE_PACKAGE = "com.belman.application.service..";

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman.application");
    }

    @Test
    public void serviceClassesShouldFollowNamingConvention() {
        ArchRule rule = classes()
                .that().resideInAPackage(SERVICE_PACKAGE)
                .should().haveSimpleNameEndingWith("Service")
                .because("Service classes should follow the naming convention of ending with 'Service'");

        rule.check(importedClasses);
    }
}
