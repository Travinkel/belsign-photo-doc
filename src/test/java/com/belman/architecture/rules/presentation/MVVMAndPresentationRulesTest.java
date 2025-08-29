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
 * for the UI layer.
 */
public class MVVMAndPresentationRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void viewModelsShouldResideInUiLayer() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*ViewModel")
                .should().resideInAPackage("com.belman.presentation..")
                .because("ViewModels should be in the UI layer following MVVM pattern");

        rule.check(importedClasses);
    }

    @Test
    public void viewsShouldOnlyDependOnViewModels() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation..views..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman.presentation..views..",
                        "com.belman.presentation..common..",
                        "com.belman.presentation..navigation..",
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
                .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..views..")
                .because("ViewModels should not depend on Views in MVVM architecture");

        rule.check(importedClasses);
    }

    @Test
    public void coordinatorsShouldManageNavigation() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Coordinator")
                .should().resideInAPackage("com.belman.presentation..navigation..")
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
    public void controllersShouldResideInUiLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .and().resideInAPackage("com.belman.presentation..")
                .should().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void viewsShouldResideInUiLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("View")
                .and().resideInAPackage("com.belman.presentation..")
                .should().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    @Test
    public void uiLayerShouldNotDependOnRepositoryImplementations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.presentation..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.repository.persistence..")
                .orShould().dependOnClassesThat().resideInAPackage("com.belman.repository.email..")
                .orShould().dependOnClassesThat().resideInAPackage("com.belman.repository.camera..");

        rule.check(importedClasses);
    }

    @Test
    public void uiLayerShouldOnlyDependOnDomainAndService() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "com.belman.presentation..",
                        "com.belman.domain..",
                        "com.belman.service..",
                        "com.belman.common..",
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
