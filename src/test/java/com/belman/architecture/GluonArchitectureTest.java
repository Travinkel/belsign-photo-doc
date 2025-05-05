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
        ArchRule rule = noClasses()
                .that().resideOutsideOfPackage("..presentation..")
                .should().dependOnClassesThat().resideInAnyPackage("com.gluonhq.charm.glisten..")
                .because("Glisten UI components must only be used in the presentation layer to maintain separation of concerns");

        rule.check(importedClasses);
    }

    @Test
    public void attachShouldNotBeUsedInDomainLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("com.gluonhq.attach..")
                .because("Domain layer must remain independent of platform-specific services");

        rule.check(importedClasses);
    }

    @Test
    public void javafxShouldNotBeUsedInDomainLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("javafx..")
                .because("Domain layer must not depend on UI frameworks like JavaFX");

        rule.check(importedClasses);
    }

    @Test
    public void lifecycleAnnotationsShouldOnlyBeInInfrastructureOrPresentationLayers() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("javax.annotation.PostConstruct")
                .or().areAnnotatedWith("javax.annotation.PreDestroy")
                .should().resideInAnyPackage("..infrastructure..", "..presentation..")
                .because("Lifecycle annotations should only exist in outer layers to manage application lifecycle");

        rule.check(importedClasses);
    }
}
