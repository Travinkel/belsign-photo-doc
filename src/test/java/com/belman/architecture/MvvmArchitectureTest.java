package com.belman.architecture;

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
public class MvvmArchitectureTest {

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
}