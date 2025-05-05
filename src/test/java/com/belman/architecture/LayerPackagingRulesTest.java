package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Tests to verify the organization of classes within each layer's packages.
 * This ensures that classes in each layer are properly grouped by responsibility.
 */
public class LayerPackagingRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void applicationClassesShouldBeOrganizedByFeature() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.application..")
                .and().haveNameNotMatching(".*Config")
                .and().haveNameNotMatching(".*Factory")
                .and().haveNameNotMatching(".*Provider")
                .and().haveNameNotMatching(".*Module")
                .should().resideInAnyPackage(
                        "com.belman.application.core..",
                        "com.belman.application.commands..",
                        "com.belman.application.queries..",
                        "com.belman.application.services..",
                        "com.belman.application.admin..",
                        "com.belman.application.auth..",
                        "com.belman.application.api..",
                        "com.belman.application.mobile..",
                        "com.belman.application.photo..",
                        "com.belman.application.qa..",
                        "com.belman.application.reporting.."
                )
                .because("Application classes should be organized by feature for better maintainability");

        rule.check(importedClasses);
    }

    @Test
    public void useCasesClassesShouldBeOrganizedByFeature() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.usecase..")
                .should().resideInAnyPackage(
                        "com.belman.usecase.core..",
                        "com.belman.usecase.admin..",
                        "com.belman.usecase.auth..",
                        "com.belman.usecase.photo..",
                        "com.belman.usecase.qa..",
                        "com.belman.usecase.reporting.."
                )
                .because("Use case classes should be organized by feature for better maintainability");

        rule.check(importedClasses);
    }

    @Test
    public void infrastructureClassesShouldBeOrganizedByTechnology() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.infrastructure..")
                .should().resideInAnyPackage(
                        "com.belman.infrastructure.persistence..",
                        "com.belman.infrastructure.service..",
                        "com.belman.infrastructure.email..",
                        "com.belman.infrastructure.storage..",
                        "com.belman.infrastructure.security..",
                        "com.belman.infrastructure.logging..",
                        "com.belman.infrastructure.config..",
                        "com.belman.infrastructure.camera..",
                        "com.belman.infrastructure.platform..",
                        "com.belman.infrastructure.bootstrap..",
                        "com.belman.infrastructure.core.."
                )
                .because("Infrastructure classes should be organized by technology or responsibility");

        rule.check(importedClasses);
    }

    @Test
    public void presentationClassesShouldBeOrganizedByComponent() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.presentation..")
                .should().resideInAnyPackage(
                        "com.belman.presentation.views..",
                        "com.belman.presentation.viewmodels..",
                        "com.belman.presentation.controllers..",
                        "com.belman.presentation.components..",
                        "com.belman.presentation.navigation..",
                        "com.belman.presentation.coordinators..",
                        "com.belman.presentation.core.."
                )
                .because("Presentation classes should be organized by UI component or responsibility");

        rule.check(importedClasses);
    }

    @Test
    public void persistenceClassesShouldBeOrganizedByStorage() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.infrastructure.persistence..")
                .should().resideInAnyPackage(
                        "com.belman.infrastructure.persistence.database..",
                        "com.belman.infrastructure.persistence.file..",
                        "com.belman.infrastructure.persistence.memory..",
                        "com.belman.infrastructure.persistence.remote.."
                )
                .because("Persistence classes should be organized by storage mechanism");

        rule.check(importedClasses);
    }

    @Test
    public void domainClassesShouldBeOrganizedByDddConcept() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .should().resideInAnyPackage(
                        "com.belman.domain.entities..",
                        "com.belman.domain.valueobjects..",
                        "com.belman.domain.aggregates..",
                        "com.belman.domain.events..",
                        "com.belman.domain.repositories..",
                        "com.belman.domain.services..",
                        "com.belman.domain.factories..",
                        "com.belman.domain.specification..",
                        "com.belman.domain.shared..",
                        "com.belman.domain.exceptions.."
                )
                .because("Domain classes should be organized by DDD concept for better understanding");

        rule.check(importedClasses);
    }
}