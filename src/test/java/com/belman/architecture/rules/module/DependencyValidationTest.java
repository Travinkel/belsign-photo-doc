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
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()

                .layer("UI").definedBy("com.belman.ui..")
                .layer("Service").definedBy("com.belman.service..")
                .layer("Repository").definedBy("com.belman.repository..")
                .layer("Domain").definedBy("com.belman.domain..")
                .layer("Common").definedBy("com.belman.common..")
                .layer("Bootstrap").definedBy("com.belman.bootstrap..")

                // Allow forward and shared layer flow
                .whereLayer("UI").mayOnlyAccessLayers("Service", "Domain", "Common", "Bootstrap")
                .whereLayer("Service").mayOnlyAccessLayers("UI", "Repository", "Domain", "Common", "Bootstrap")
                .whereLayer("Repository").mayOnlyAccessLayers("Service", "Domain", "Common", "Bootstrap")
                .whereLayer("Domain").mayOnlyAccessLayers("Common")
                .whereLayer("Common").mayOnlyAccessLayers()
                .whereLayer("Bootstrap").mayOnlyAccessLayers("UI", "Service", "Repository", "Domain", "Common");

        rule.check(importedClasses);
    }

}
