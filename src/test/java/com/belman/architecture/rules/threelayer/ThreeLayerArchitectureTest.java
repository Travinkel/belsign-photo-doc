package com.belman.architecture.rules.threelayer;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ThreeLayerArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Presentation").definedBy("com.belman.presentation..")
                .layer("Business").definedBy("com.belman.business..")
                .layer("Data").definedBy("com.belman.data..")
                .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
                .whereLayer("Business").mayOnlyBeAccessedByLayers("Presentation")
                .whereLayer("Data").mayOnlyBeAccessedByLayers("Business");

        rule.check(importedClasses);
    }

    @Test
    public void presentationLayerShouldOnlyDependOnBusinessLayer() {
        ArchRule rule = noClasses().that().resideInAPackage("com.belman.presentation..")
            .should().dependOnClassesThat().resideInAnyPackage("com.belman.data..");

        rule.check(importedClasses);
    }

    @Test
    public void businessLayerShouldNotDependOnPresentationLayer() {
        ArchRule rule = noClasses().that().resideInAPackage("com.belman.business..")
            .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void dataLayerShouldNotDependOnBusinessOrPresentationLayers() {
        ArchRule rule = noClasses().that().resideInAPackage("com.belman.data..")
            .should().dependOnClassesThat().resideInAnyPackage("com.belman.business..", "com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldResideInPresentationLayer() {
        ArchRule rule = classes().that().haveNameMatching(".*ViewModel")
            .should().resideInAPackage("com.belman.presentation.views..");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldResideInPresentationLayer() {
        ArchRule rule = classes().that().haveNameMatching(".*Controller")
            .should().resideInAPackage("com.belman.presentation.views..");

        rule.check(importedClasses);
    }

    @Test
    public void servicesAndDomainClassesShouldResideInBusinessLayer() {
        ArchRule rule = classes().that().haveNameMatching(".*Service")
            .or().haveSimpleNameEndingWith("Aggregate")
            .or().haveSimpleNameEndingWith("Entity")
            .or().haveSimpleNameEndingWith("ValueObject")
            .should().resideInAPackage("com.belman.business..");

        rule.check(importedClasses);
    }

    @Test
    public void repositoriesShouldResideInDataLayer() {
        ArchRule rule = classes().that().haveNameMatching(".*Repository")
            .should().resideInAPackage("com.belman.data.persistence");

        rule.check(importedClasses);
    }

    @Test
    public void useCasesShouldResideInBusinessLayer() {
        ArchRule rule = classes().that().haveNameMatching(".*UseCase")
            .should().resideInAPackage("com.belman.business.usecases..");

        rule.check(importedClasses);
    }

    @Test
    public void viewsShouldResideInPresentationLayer() {
        ArchRule rule = classes().that().haveNameMatching(".*View")
            .and().areNotInterfaces()
            .should().resideInAPackage("com.belman.presentation.views..");

        rule.check(importedClasses);
    }
}
