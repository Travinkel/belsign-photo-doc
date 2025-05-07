package com.belman.architecture.rules.application;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class UseCaseNamingConventionRulesTest {

    private static JavaClasses importedClasses;

    private static final String USECASE_PACKAGE = "com.belman.application.usecase..";

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman.application");
    }

    @Test
    public void useCaseClassesShouldFollowNamingConvention() {
        ArchRule rule = classes()
                .that().resideInAPackage(USECASE_PACKAGE)
                .should().haveSimpleNameEndingWith("UseCase")
                .because("Use case classes should follow the naming convention of ending with 'UseCase'");

        rule.check(importedClasses);
    }
}
