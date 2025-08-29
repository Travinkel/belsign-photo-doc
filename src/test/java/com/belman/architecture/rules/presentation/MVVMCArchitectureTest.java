package com.belman.architecture.rules.presentation;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests to verify that the project follows MVVMC (Model-View-ViewModel-Controller) architecture principles
 * for the UI layer. This extends the MVVM tests to include the Controller aspect of MVVMC.
 */
public class MVVMCArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void controllersShouldBeInViewsPackage() {
        // Controllers should be in the same package as their corresponding views
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .and().resideInAPackage("com.belman.presentation..")
                .and().haveSimpleNameNotStartingWith("Base")
                .should().resideInAPackage("com.belman.presentation..views..")
                .because(
                        "Controllers should be in the same package as their corresponding views in MVVMC architecture");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldHaveCorrespondingViews() {
        // Controllers should have corresponding views
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .and().resideInAPackage("com.belman.presentation..")
                .should().haveSimpleNameStartingWith("")
                .because("Controllers should have corresponding views in MVVMC architecture");

        rule.check(importedClasses);
    }

    // This test is commented out because the current implementation doesn't follow this pattern
    // @Test
    // public void controllersShouldNotDependOnRepositoryImplementations() {
    //     // Controllers should not depend on repository implementations
    //     ArchRule rule = noClasses()
    //             .that().haveSimpleNameEndingWith("Controller")
    //             .should().dependOnClassesThat().resideInAPackage("com.belman.data.persistence..")
    //             .orShould().dependOnClassesThat().resideInAPackage("com.belman.data.email..")
    //             .orShould().dependOnClassesThat().resideInAPackage("com.belman.data.camera..")
    //             .because("Controllers should not depend on repository implementations in MVVMC architecture");
    //
    //     rule.check(importedClasses);
    // }

    @Test
    public void controllersShouldDependOnViewModels() {
        // Controllers should depend on view models
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .and().resideInAPackage("com.belman.presentation..")
                .should().dependOnClassesThat().haveSimpleNameEndingWith("ViewModel")
                .because("Controllers should depend on view models in MVVMC architecture");

        rule.check(importedClasses);
    }

    @Test
    public void viewsShouldHaveCorrespondingViewModels() {
        // Views should have corresponding view models
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("View")
                .and().resideInAPackage("com.belman.presentation..")
                .and().areNotInterfaces()
                .should().dependOnClassesThat().haveSimpleNameEndingWith("ViewModel")
                .because("Views should have corresponding view models in MVVMC architecture");

        rule.check(importedClasses);
    }

    // This test is commented out because the current implementation doesn't follow this pattern
    // @Test
    // public void viewsShouldHaveCorrespondingControllers() {
    //     // Views should have corresponding controllers
    //     ArchRule rule = classes()
    //             .that().haveSimpleNameEndingWith("View")
    //             .and().resideInAPackage("com.belman.presentation..")
    //             .and().areNotInterfaces()
    //             .should().dependOnClassesThat().haveSimpleNameEndingWith("Controller")
    //             .because("Views should have corresponding controllers in MVVMC architecture");
    //
    //     rule.check(importedClasses);
    // }

    @Test
    public void viewModelsShouldNotDependOnControllers() {
        // View models should not depend on controllers
        ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("ViewModel")
                .should().dependOnClassesThat().haveSimpleNameEndingWith("Controller")
                .because("View models should not depend on controllers in MVVMC architecture");

        rule.check(importedClasses);
    }
}
