package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests to verify that logging is consistently implemented throughout the application.
 * Ensures that proper logging frameworks are used, loggers are properly initialized,
 * and appropriate log levels are used.
 */
public class LoggingArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void loggersShouldBePrivateStaticFinal() {
        ArchRule rule = fields()
                .that().haveName("logger").or().haveName("LOG")
                .and().haveRawType("org.slf4j.Logger")
                .should().bePrivate()
                .andShould().beStatic()
                .andShould().beFinal()
                .because("Logger fields should be private, static, and final following best practices");

        rule.check(importedClasses);
    }

    @Test
    public void shouldUseSlf4jForLogging() {
        ArchRule rule = fields()
                .that().haveName("logger").or().haveName("LOG")
                .should().haveRawType("org.slf4j.Logger")
                .because("SLF4J should be used as the logging facade for consistency");

        rule.check(importedClasses);
    }

    @Test
    public void shouldNotUseSystemOutForLogging() {
        ArchRule rule = noClasses()
                .should().accessClassesThat().haveFullyQualifiedName("java.lang.System")
                .because("System.out should not be used for logging");

        rule.check(importedClasses);
    }

    @Test
    public void loggerImplementationShouldBeInInfrastructureLayer() {
        ArchRule rule = classes()
                .that().implement("org.slf4j.Logger")
                .or().haveNameMatching(".*Logger(Impl)?")
                .should().resideInAPackage("com.belman.infrastructure.logging..")
                .because("Logger implementations should be in the infrastructure layer");

        rule.check(importedClasses);
    }

    @Test
    public void domainLayerShouldUseLoggerInterfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .should().onlyAccessClassesThat().haveFullyQualifiedName("com.belman.domain.services.Logger")
                .orShould().haveFullyQualifiedName("org.slf4j.Logger")
                .because("Domain layer should use logger interfaces, not concrete implementations");

        rule.check(importedClasses);
    }

    @Test
    public void loggerFactoryShouldBeUsedForLoggerCreation() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Logger")
                .should().resideInAPackage("com.belman.infrastructure.logging..")
                .because("Loggers should be created through proper logger factory");

        rule.check(importedClasses);
    }
}