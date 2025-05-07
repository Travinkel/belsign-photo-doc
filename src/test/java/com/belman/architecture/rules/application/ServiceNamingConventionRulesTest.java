package com.belman.architecture.rules.application;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ServiceNamingConventionRulesTest extends BaseArchUnitTest {

    private static final String SERVICE_PACKAGE = "com.belman.application.service..";

    @Test
    public void serviceClassesShouldFollowNamingConvention() {
        ArchRule rule = classes()
                .that().resideInAPackage(SERVICE_PACKAGE)
                .should().haveSimpleNameEndingWith("Service")
                .because("Service classes should follow the naming convention of ending with 'Service'");

        rule.check(importedClasses);
    }

    @Test
    public void serviceClassesShouldNotDependOnControllerOrUiLayers() {
        ArchRule rule = classes()
                .that().resideInAPackage(SERVICE_PACKAGE)
                .should().onlyDependOnClassesThat().resideOutsideOfPackage("com.belman.controller..")
                .andShould().onlyDependOnClassesThat().resideOutsideOfPackage("com.belman.ui..")
                .because("Service classes should not depend on controller or UI layers");

        rule.check(importedClasses);
    }
}
