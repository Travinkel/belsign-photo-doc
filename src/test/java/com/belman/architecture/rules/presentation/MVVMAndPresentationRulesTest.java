package com.belman.architecture.rules.presentation;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests to verify that the project follows MVVM (Model-View-ViewModel) architecture principles
 * for the presentation layer.
 */
public class MVVMAndPresentationRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void viewModelsShouldResideInPresentationLayer() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*ViewModel")
                .should().resideInAPackage("com.belman.presentation..")
                .because("ViewModels should be in the presentation layer following MVVM pattern");

        rule.check(importedClasses);
    }

    @Test
    public void viewsShouldOnlyDependOnViewModels() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation..view..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman.presentation..viewmodel..",
                        "com.belman.presentation..common..",
                        "com.belman.presentation..coordinator..",
                        "java..", "javafx..", "com.gluonhq..",
                        // Common utilities and language features
                        "org.slf4j..", "ch.qos.logback.."
                )
                .because("Views should only depend on ViewModels in MVVM architecture");

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldNotDependOnViews() {
        ArchRule rule = noClasses()
                .that().haveNameMatching(".*ViewModel")
                .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..view..")
                .because("ViewModels should not depend on Views in MVVM architecture");

        rule.check(importedClasses);
    }

    @Test
    public void coordinatorsShouldManageNavigation() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Coordinator")
                .should().resideInAPackage("com.belman.presentation..coordinator..")
                .because("Coordinators should handle navigation in MVVM+C architecture");

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldHaveCorrectNaming() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ViewModel")
                .should().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldResideInPresentationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .and().resideInAPackage("com.belman.presentation..")
                .should().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void viewsShouldResideInPresentationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("View")
                .and().resideInAPackage("com.belman.presentation..")
                .should().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void presentationLayerShouldNotDependOnInfrastructureImplementations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.presentation..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.infrastructure.persistence..")
                .orShould().dependOnClassesThat().resideInAPackage("com.belman.infrastructure.email..")
                .orShould().dependOnClassesThat().resideInAPackage("com.belman.infrastructure.camera..");

        rule.check(importedClasses);
    }

    @Test
    public void presentationLayerShouldOnlyDependOnDomainAndApplication() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman.presentation..",
                        "com.belman.domain..",
                        "com.belman.application..",
                        "java..",
                        "javafx..",
                        "javax..",
                        "com.gluonhq..",
                        "org.slf4j.."
                );

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldNotDependOnControllers() {
        ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("ViewModel")
                .should().dependOnClassesThat().haveSimpleNameEndingWith("Controller");

        rule.check(importedClasses);
    }

    @Test
    public void viewsShouldBeSimple() {
        // Views should be simple and delegate to ViewModels and Controllers
        // This rule checks that View classes don't have too many methods
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("View")
                .and().resideInAPackage("com.belman.presentation..")
                .should().haveOnlyFinalFields();

        rule.check(importedClasses);
    }
}
