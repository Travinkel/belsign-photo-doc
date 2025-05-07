package com.belman.architecture.rules.ddd;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class DomainContextsTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman.domain");
    }

    @Test
    public void domainClassesShouldBeInCorrectContexts() {
        // Consolidated rules for context-specific placement
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .should().resideInAnyPackage(
                        "com.belman.domain.user..",
                        "com.belman.domain.order..",
                        "com.belman.domain.photo..",
                        "com.belman.domain.report..",
                        "com.belman.domain.customer..",
                        "com.belman.domain.common.."
                )
                .because("Domain classes should be organized by business contexts.");

        rule.check(importedClasses);
    }
}
