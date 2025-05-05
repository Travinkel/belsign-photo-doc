package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ApplicationLayerStructureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman.application");
    }

    @Test
    public void applicationLayerShouldOnlyDependOnDomainAndSharedLayers() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.application..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "com.belman.domain..",
                        "com.belman.shared..",
                        "java.."
                )
                .because("The application layer should only depend on the domain and shared layers");

        rule.check(importedClasses);
    }

    @Test
    public void useCaseClassesShouldBeSuffixedWithUseCase() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.application.usecase..")
                .should().haveSimpleNameEndingWith("UseCase")
                .because("Use case classes should follow the naming convention of ending with 'UseCase'");

        rule.check(importedClasses);
    }

    @Test
    public void serviceClassesShouldBeSuffixedWithService() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.application.service..")
                .should().haveSimpleNameEndingWith("Service")
                .because("Service classes should follow the naming convention of ending with 'Service'");

        rule.check(importedClasses);
    }
}