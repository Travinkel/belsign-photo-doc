package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Tests to verify that Gluon Mobile specific architectural rules are followed.
 * These tests ensure that Gluon components are used correctly in the appropriate layers.
 */
public class GluonArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void glistenShouldOnlyBeUsedInPresentationLayer() {
        ArchRule rule = classes()
                .that().resideOutsideOfPackage("..presentation..")
                .should().onlyDependOnClassesThat().resideOutsideOfPackage("com.gluonhq.charm.glisten..")
                .because("Glisten UI components should only be used in the presentation layer");

        rule.check(importedClasses);
    }

    @Test
    public void attachShouldNotBeUsedInDomainLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("com.gluonhq.attach..")
                .because("Attach services should not be accessed from the domain layer");

        rule.check(importedClasses);
    }

    @Test
    public void javafxShouldNotBeUsedInDomainLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("javafx..")
                .because("Domain should be independent of UI frameworks");

        rule.check(importedClasses);
    }

    @Test
    public void lifecycleAnnotationsShouldOnlyBeInOuterLayers() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("javax.annotation.PostConstruct")
                .or().areAnnotatedWith("javax.annotation.PreDestroy")
                .should().resideInAnyPackage("..infrastructure..", "..presentation..")
                .because("Lifecycle methods should only exist in outer layers");

        rule.check(importedClasses);
    }
}
