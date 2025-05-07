package com.belman.architecture.rules.application;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ApplicationLayerDependencyRulesTest extends BaseArchUnitTest {

    private static final String APPLICATION_PACKAGE = "com.belman.application..";
    private static final String DOMAIN_PACKAGE = "com.belman.domain..";
    private static final String SHARED_PACKAGE = "com.belman.shared..";

    @Test
    public void applicationLayerShouldOnlyDependOnDomainAndSharedLayers() {
        ArchRule rule = classes()
                .that().resideInAPackage(APPLICATION_PACKAGE)
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        DOMAIN_PACKAGE,
                        SHARED_PACKAGE,
                        "java.."
                )
                .because("The application layer should only depend on the domain and shared layers");

        rule.check(importedClasses);
    }

    @Test
    public void applicationLayerShouldNotDependOnExternalLibraries() {
        ArchRule rule = classes()
                .that().resideInAPackage(APPLICATION_PACKAGE)
                .should().onlyDependOnClassesThat().resideOutsideOfPackage("com.external..")
                .because("The application layer should not depend on external libraries unless explicitly allowed");

        rule.check(importedClasses);
    }
}
