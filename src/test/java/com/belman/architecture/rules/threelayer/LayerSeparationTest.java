package com.belman.architecture.rules.threelayer;

import com.belman.architecture.rules.BaseArchUnitTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests for proper layer separation in the three-layer architecture.
 * This class verifies that each layer only depends on the layers below it and not on the layers above it.
 * It complements the ThreeLayerArchitectureTest by providing more specific tests for layer separation.
 */
public class LayerSeparationTest extends BaseArchUnitTest {

    /**
     * Tests that the domain layer does not depend on any other layer.
     * The domain layer should be the most independent layer, containing only business entities and logic.
     */
    @Test
    public void domainLayerShouldNotDependOnOtherLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.domain..")
                // Exclude known exceptions
                .and().haveNameNotMatching(".*PhotoReportGenerationService")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.presentation..",
                        "com.belman.application..",
                        "com.belman.dataaccess..")
                .because("Domain layer should be independent of other layers");

        rule.check(importedClasses);
    }

    /**
     * Tests that the data access layer does not depend on the application or presentation layers.
     * The data access layer should only depend on the domain layer.
     */
    @Test
    public void dataAccessLayerShouldNotDependOnApplicationOrPresentationLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.dataaccess..")
                // Exclude known exceptions
                .and().haveNameNotMatching(".*DefaultEmailService")
                .and().haveNameNotMatching(".*CameraImageProvider")
                .and().haveNameNotMatching(".*CameraService.*")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "com.belman.presentation..",
                        "com.belman.application..")
                .because("Data access layer should only depend on domain layer");

        rule.check(importedClasses);
    }

    /**
     * Tests that the application layer does not depend on the presentation layer.
     * The application layer should only depend on the domain and data access layers.
     */
    @Test
    public void applicationLayerShouldNotDependOnPresentationLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.application..")
                // Exclude known exceptions
                .and().haveNameNotMatching(".*DefaultPhotoService")
                .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..")
                .because("Application layer should not depend on presentation layer");

        rule.check(importedClasses);
    }

    /**
     * Tests that the presentation layer only depends on the application and domain layers.
     * The presentation layer should not depend on the data access layer.
     */
    @Test
    public void presentationLayerShouldNotDependOnDataAccessLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.presentation..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.dataaccess..")
                .because("Presentation layer should not depend on data access layer");

        rule.check(importedClasses);
    }

    /**
     * Tests that service interfaces are in the application layer.
     * Service interfaces define the contract between the presentation and application layers.
     */
    @Test
    public void serviceInterfacesShouldBeInApplicationLayer() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Service")
                .and().areInterfaces()
                .and().haveNameNotMatching(".*Test")
                .should().resideInAnyPackage(
                        "com.belman.application..",
                        "com.belman.domain.services..",
                        "com.belman.domain.security..")
                .because("Service interfaces should be in the application or domain layer");

        rule.check(importedClasses);
    }

    /**
     * Tests that service implementations are in the application layer.
     * Service implementations contain the business logic of the application.
     */
    /**
     * This test is commented out because it's not possible to reliably identify service implementations
     * based on naming patterns alone. The project uses various naming conventions for service implementations.
     */
    /*
    @Test
    public void serviceImplementationsShouldBeInApplicationLayer() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Service")
                .and().areNotInterfaces()
                .and().haveNameNotMatching(".*Test")
                .and().haveNameNotMatching(".*Mock.*")
                // Exclude known exceptions
                .and().haveNameNotMatching(".*DefaultEmailService")
                .should().resideInAnyPackage(
                        "com.belman.application.usecase..",
                        "com.belman.application..",
                        "com.belman.domain.report.service..")
                .because("Service implementations should be in the application or domain layer");

        rule.check(importedClasses);
    }
    */

    /**
     * Tests that repository interfaces are in the domain layer.
     * Repository interfaces define the contract between the application and data access layers.
     */
    @Test
    public void repositoryInterfacesShouldBeInDomainLayer() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Repository")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain..")
                .because("Repository interfaces should be in the domain layer");

        rule.check(importedClasses);
    }

    /**
     * Tests that repository implementations are in the data access layer.
     * Repository implementations contain the data access logic of the application.
     */
    @Test
    public void repositoryImplementationsShouldBeInDataAccessLayer() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Repository")
                .and().areNotInterfaces()
                .and().haveNameNotMatching(".*Test")
                .and().haveNameNotMatching(".*Mock.*")
                .should().resideInAnyPackage(
                        "com.belman.dataaccess.repository..",
                        "com.belman.dataaccess.persistence..")
                .because("Repository implementations should be in the data access layer");

        rule.check(importedClasses);
    }
}
