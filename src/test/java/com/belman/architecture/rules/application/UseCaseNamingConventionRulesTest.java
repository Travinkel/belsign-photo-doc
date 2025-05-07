package com.belman.architecture.rules.application;

import com.belman.architecture.rules.BaseArchUnitTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class UseCaseNamingConventionRulesTest extends BaseArchUnitTest {

    private static final String USECASE_PACKAGE = "com.belman.application.usecase..";

    @Test
    public void useCaseClassesShouldFollowNamingConvention() {
        ArchRule rule = classes()
                .that().resideInAPackage(USECASE_PACKAGE)
                .should().haveSimpleNameEndingWith("UseCase")
                .because("Use case classes should follow the naming convention of ending with 'UseCase'");

        rule.check(importedClasses);
    }

    @Test
    public void useCaseClassesShouldNotDependOnServiceOrControllerLayers() {
        ArchRule rule = classes()
                .that().resideInAPackage(USECASE_PACKAGE)
                .should().onlyDependOnClassesThat().resideOutsideOfPackage("com.belman.application.service..")
                .andShould().onlyDependOnClassesThat().resideOutsideOfPackage("com.belman.controller..")
                .because("Use case classes should not depend on service or controller layers");

        rule.check(importedClasses);
    }
}
