package com.belman.architecture.rules.module;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Tests to enforce the dependency validation rules between different scopes.
 * These rules match the configuration in .idea/scopes/scope_settings.xml.
 */
public class DependencyValidationTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void dependencyRulesShouldBeRespected() {
        // This test implements the dependency validation rules from .idea/scopes/scope_settings.xml
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                // Define the layers based on the scopes
                .layer("UI").definedBy("com.belman.ui..")
                .layer("Service").definedBy("com.belman.service..")
                .layer("Repository").definedBy("com.belman.repository..")
                .layer("Domain").definedBy("com.belman.domain..")
                .layer("Common").definedBy("com.belman.common..")
                .layer("Bootstrap").definedBy("com.belman.repository.bootstrap..")

                // 🔒 Deny all by default (implicitly done by layeredArchitecture)

                // 🔓 Now allow what you *explicitly* want

                // Normal forward flow
                .whereLayer("UI").mayOnlyAccessLayers("Service", "Domain", "Common")

                // Bidirectional flow between Service and Repository
                .whereLayer("Service").mayOnlyAccessLayers("UI", "Repository", "Domain", "Common")
                .whereLayer("Repository").mayOnlyAccessLayers("Service", "Domain", "Common")

                // Shared domain layer and common
                .whereLayer("Domain").mayOnlyAccessLayers("Common")

                // Bootstrap layer can access all other layers
                .whereLayer("Bootstrap").mayOnlyAccessLayers("UI", "Service", "Repository", "Domain", "Common");

        rule.check(importedClasses);
    }
}
