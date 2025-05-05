package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Consolidated tests for Clean Architecture structure.
 * This class combines rules from CleanArchitectureTest, CleanArchitectureLayerTest,
 * and CleanArchitectureBootstrapTest to provide a comprehensive set of rules for
 * clean architecture layer dependencies and package structure.
 */
public class ConsolidatedCleanArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void enforceLayeredArchitecture() {
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Domain").definedBy("com.belman.domain..")
                .layer("Application").definedBy("com.belman.application..", "com.belman.usecase..")
                .layer("Infrastructure").definedBy("com.belman.infrastructure..")
                .layer("Presentation").definedBy("com.belman.presentation..")

                // Define the allowed dependencies between layers
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Presentation")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Presentation")
                .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Presentation")

                .because("The project should follow Clean Architecture principles with unidirectional dependencies");

        rule.check(importedClasses);
    }

    @Test
    public void domainLayerShouldNotDependOnOtherLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.application..",
                        "com.belman.usecase..",
                        "com.belman.infrastructure..",
                        "com.belman.presentation.."
                )
                .because("Domain layer should be independent of other layers");

        rule.check(importedClasses);
    }

    @Test
    public void domainLayerShouldNotHaveFrameworkDependencies() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "javafx..",
                        "com.gluonhq..",
                        "com.microsoft.sqlserver..",
                        "com.zaxxer.hikari..",
                        "com.sun.mail..",
                        "org.springframework..",
                        "javax.persistence..",
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "com.fasterxml.jackson.."
                )
                .because("Domain layer should not depend on external frameworks or infrastructure");

        rule.check(importedClasses);
    }

    @Test
    public void applicationLayerShouldNotDependOnInfrastructureOrPresentation() {
        ArchRule rule = noClasses()
                .that().resideInAnyPackage("com.belman.application..", "com.belman.usecase..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.infrastructure..",
                        "com.belman.presentation.."
                )
                .because("Application layer should not depend on infrastructure or presentation layers");

        rule.check(importedClasses);
    }

    @Test
    public void infrastructureLayerShouldNotDependOnPresentation() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.infrastructure..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..")
                .because("Infrastructure layer should not depend on presentation layer");

        rule.check(importedClasses);
    }

    @Test
    public void controllersShouldBeInPresentationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .and().areNotInterfaces()
                .should().resideInAPackage("com.belman.presentation..")
                .because("Controllers should be in the presentation layer");

        rule.check(importedClasses);
    }

    @Test
    public void viewModelsShouldBeInPresentationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ViewModel")
                .should().resideInAPackage("com.belman.presentation..")
                .because("ViewModels should be in the presentation layer");

        rule.check(importedClasses);
    }

    @Test
    public void useCasesShouldBeInApplicationLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("UseCase")
                .or().haveSimpleNameEndingWith("Command")
                .or().haveSimpleNameEndingWith("Query")
                .or().haveSimpleNameEndingWith("Handler")
                .should().resideInAnyPackage("com.belman.application..", "com.belman.usecase..")
                .because("Use cases should be in the application layer");

        rule.check(importedClasses);
    }

    @Test
    public void entitiesShouldBeInDomainLayer() {
        ArchRule rule = classes()
                .that().haveSimpleName("*Entity")
                .or().haveSimpleNameEndingWith("Aggregate")
                .or().haveSimpleNameEndingWith("Root")
                .or().resideInAPackage("com.belman.domain.entities..")
                .or().resideInAPackage("com.belman.domain.aggregates..")
                .should().resideInAPackage("com.belman.domain..")
                .because("Entities and aggregates should be in the domain layer");

        rule.check(importedClasses);
    }

    @Test
    public void repositoryInterfacesShouldBeInDomainLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain..")
                .because("Repository interfaces should be in the domain layer");

        rule.check(importedClasses);
    }

    @Test
    public void serviceInterfacesShouldBeInDomainLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain..")
                .because("Service interfaces should be in the domain layer");

        rule.check(importedClasses);
    }

    @Test
    public void repositoryImplementationsShouldBeInInfrastructureLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areNotInterfaces()
                .should().resideInAPackage("com.belman.infrastructure..")
                .because("Repository implementations should be in the infrastructure layer");

        rule.check(importedClasses);
    }

    @Test
    public void mainClassShouldBeInBootstrap() {
        ArchRule rule = classes()
                .that().haveSimpleName("Main")
                .or().haveSimpleName("Application")
                .should().resideInAPackage("com.belman.bootstrap..")
                .because("Main application class should be in the bootstrap package");

        rule.check(importedClasses);
    }
}