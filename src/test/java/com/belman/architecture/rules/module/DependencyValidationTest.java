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
 * 
 * Note: Only Presentation, Business, and Data are considered layers.
 * Domain, Common, and Bootstrap are shared packages that can be used by all layers.
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

                // Layers
                .layer("Presentation").definedBy("com.belman.presentation..")
                .layer("Business").definedBy("com.belman.business..")
                .layer("Data").definedBy("com.belman.data..")

                // Shared packages (not layers, just used by all)
                .optionalLayer("Domain").definedBy("com.belman.domain..")
                .optionalLayer("Common").definedBy("com.belman.common..")
                .optionalLayer("Bootstrap").definedBy("com.belman.bootstrap..")

                // Rules
                .whereLayer("Presentation").mayOnlyAccessLayers("Business", "Domain", "Common", "Bootstrap")
                .whereLayer("Business").mayOnlyAccessLayers("Presentation", "Data", "Domain", "Common", "Bootstrap")
                .whereLayer("Data").mayOnlyAccessLayers("Business", "Domain", "Common", "Bootstrap");

        rule.check(importedClasses);
    }


}
