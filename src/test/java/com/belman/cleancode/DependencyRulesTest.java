package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class DependencyRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void domainLayerShouldNotDependOnApplicationLayer() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .should().onlyDependOnClassesThat().resideOutsideOfPackage("com.belman.application..")
                .because("The domain layer should not depend on the application layer");

        rule.check(importedClasses);
    }

    @Test
    public void infrastructureLayerShouldNotDependOnPresentationLayer() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.infrastructure..")
                .should().onlyDependOnClassesThat().resideOutsideOfPackage("com.belman.presentation..")
                .because("The infrastructure layer should not depend on the presentation layer");

        rule.check(importedClasses);
    }

    @Test
    public void applicationLayerShouldOnlyDependOnDomainAndSharedLayers() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.application..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "com.belman.domain..",
                        "com.belman.shared..",
                        "java.."
                )
                .because("The application layer should only depend on the domain and shared layers");

        rule.check(importedClasses);
    }
}
