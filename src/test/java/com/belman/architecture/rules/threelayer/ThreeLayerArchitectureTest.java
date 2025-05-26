package com.belman.architecture.rules.threelayer;

import com.belman.architecture.rules.BaseArchUnitTest;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Tests for three-layer architecture compliance.
 * This class verifies that the codebase follows the industry-standard three-layer architecture pattern:
 * - Presentation Layer (UI): Handles user interaction and display
 * - Application Layer (Business Logic): Contains business rules and workflows
 * - Data Access Layer (Persistence): Manages data storage and retrieval
 * 
 * The Domain layer is shared across all layers and contains the core business entities and value objects.
 */
public class ThreeLayerArchitectureTest extends BaseArchUnitTest {

    /**
     * Tests that the codebase follows the industry-standard three-layer architecture.
     * This test verifies that dependencies between layers follow the correct direction:
     * - Presentation Layer can access Application Layer and Domain Layer
     * - Application Layer can access Data Access Layer and Domain Layer
     * - Data Access Layer can only access Domain Layer
     * - Domain Layer should not depend on any other layer
     * - Common utilities can be used by all layers
     * - Bootstrap layer can access all other layers for initialization
     */
    @Test
    public void layeredArchitectureShouldBeRespected() {
        // This test checks that the production code follows the layered architecture
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Presentation").definedBy("com.belman.presentation..")
                .layer("Application").definedBy("com.belman.application..")
                .layer("DataAccess").definedBy("com.belman.dataaccess..")
                .layer("Domain").definedBy("com.belman.domain..")
                .layer("Common").definedBy("com.belman.common..")
                .layer("Bootstrap").definedBy("com.belman.bootstrap..")

                // Presentation layer can only access Application, Domain, and Common
                .whereLayer("Presentation").mayOnlyAccessLayers("Application", "Domain", "Common")

                // Application layer can only access DataAccess, Domain, and Common
                .whereLayer("Application").mayOnlyAccessLayers("DataAccess", "Domain", "Common")

                // DataAccess layer can only access Domain and Common
                .whereLayer("DataAccess").mayOnlyAccessLayers("Domain", "Common")

                // Domain layer can only access Common
                .whereLayer("Domain").mayOnlyAccessLayers("Common")

                // Bootstrap layer can access all other layers
                .whereLayer("Bootstrap").mayOnlyAccessLayers("Presentation", "Application", "DataAccess", "Domain", "Common");

        rule.check(importedClasses);
    }

    /**
     * Tests that the Presentation layer does not directly depend on internal Data Access implementations.
     * This enforces the principle that the Presentation layer should only interact with the Application layer,
     * not with the implementation details of the Data Access layer.
     */
    @Test
    public void presentationLayerShouldNotDependOnInternalDataAccessImplementations() {
        // Presentation layer should not depend on internal data access implementations
        ArchRule rule = noClasses().that().resideInAPackage("com.belman.presentation..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.dataaccess.persistence..",
                        "com.belman.dataaccess.implementation..");

        rule.check(importedClasses);
    }

    /**
     * Tests that the Application layer does not depend on the Presentation layer.
     * This enforces the principle that the Application layer should be independent of UI concerns.
     */
    @Test
    public void applicationLayerShouldNotDependOnPresentationLayer() {
        // Application layer should not depend on Presentation layer
        ArchRule rule = noClasses().that().resideInAPackage("com.belman.application..")
                .and().areNotInterfaces()
                .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    /**
     * Tests that the Data Access layer does not depend on the Presentation layer.
     * This enforces the principle that the Data Access layer should be independent of UI concerns.
     */
    @Test
    public void dataAccessLayerShouldNotDependOnPresentationLayer() {
        // Data Access layer should not depend on Presentation layer, except for bootstrap classes
        ArchRule rule = noClasses().that().resideInAPackage("com.belman.dataaccess..")
                .and(new DescribedPredicate<JavaClass>("are not bootstrap classes") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("Main") &&
                               !javaClass.getPackageName().contains("bootstrap");
                    }
                })
                .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..");

        rule.check(importedClasses);
    }

    /**
     * Tests that concrete ViewModels are in the correct package structure.
     * This enforces consistent organization of presentation layer components.
     */
    @Test
    public void concreteViewModelsInUsecasesPackage() {
        // Allow base view models in the presentation.base package,
        // but require concrete view models to be in the presentation.usecases package
        ArchRule rule = classes().that().haveNameMatching(".*ViewModel")
                .and(new DescribedPredicate<JavaClass>("are not base view models") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("BaseViewModel");
                    }
                })
                .should().resideInAPackage("com.belman.presentation.usecases..");

        rule.check(importedClasses);
    }

    /**
     * Tests that view controllers are in the correct package structure.
     * This enforces consistent organization of presentation layer components.
     */
    @Test
    public void viewControllersInUsecasesPackage() {
        // Require view controllers to be in the presentation.usecases package
        ArchRule rule = classes().that().haveNameMatching(".*Controller")
                .and().resideInAPackage("com.belman.presentation..")
                .and(new DescribedPredicate<JavaClass>("are not base controllers") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("BaseController");
                    }
                })
                .should().resideInAPackage("com.belman.presentation.usecases..");

        rule.check(importedClasses);
    }

    /**
     * Tests that domain classes reside in the domain layer.
     * This enforces the principle that domain entities should be in the domain layer.
     */
    @Test
    public void domainClassesShouldResideInDomainLayer() {
        // Domain business classes and components should be in the domain layer
        ArchRule rule = classes().that().haveSimpleNameEndingWith("Business")
                .or().haveSimpleNameEndingWith("Component")
                .should().resideInAPackage("com.belman.domain..");

        rule.check(importedClasses);
    }

    /**
     * Tests that data access implementations reside in the data access layer.
     * This enforces the principle that repository implementations should be in the data access layer.
     */
    @Test
    public void dataAccessImplementationsShouldResideInDataAccessLayer() {
        // Repository implementations should be in the dataaccess.persistence package
        ArchRule rule = classes().that().haveNameMatching(".*Repository")
                .and().areNotInterfaces()
                .should().resideInAPackage("com.belman.dataaccess.persistence..");

        rule.check(importedClasses);
    }

    /**
     * Tests that use cases reside in the application layer.
     * This enforces the principle that business logic should be in the application layer.
     */
    @Test
    public void useCasesInApplicationLayer() {
        // Use cases should be in the application.usecase package
        ArchRule rule = classes().that().haveNameMatching(".*UseCase")
                .and(new DescribedPredicate<JavaClass>("are not base use case classes") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("UseCase");
                    }
                })
                .should().resideInAPackage("com.belman.application.usecase..");

        rule.check(importedClasses);
    }

    /**
     * Tests that concrete views are in the correct package structure.
     * This enforces consistent organization of presentation layer components.
     */
    @Test
    public void concreteViewsInUsecasesPackage() {
        // Concrete views should be in the presentation.usecases package
        ArchRule rule = classes().that().haveNameMatching(".*View")
                .and().areNotInterfaces()
                .and(new DescribedPredicate<JavaClass>("are not base views") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("BaseView");
                    }
                })
                .should().resideInAPackage("com.belman.presentation.usecases..");

        rule.check(importedClasses);
    }

    /**
     * Tests that the Data Access layer does not depend on the Application layer.
     * This enforces the principle that the Data Access layer should be independent of business logic.
     */
    @Test
    public void dataAccessLayerShouldNotDependOnApplicationLayer() {
        // Data Access layer should not depend on Application layer
        ArchRule rule = noClasses().that().resideInAPackage("com.belman.dataaccess..")
                .and(new DescribedPredicate<JavaClass>("are not bootstrap classes") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("Main") &&
                               !javaClass.getPackageName().contains("bootstrap");
                    }
                })
                .should().dependOnClassesThat().resideInAPackage("com.belman.application..");

        rule.check(importedClasses);
    }
}
