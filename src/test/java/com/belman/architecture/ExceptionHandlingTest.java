package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests to verify that exception handling in the project follows best practices.
 * This includes proper exception hierarchies, custom exceptions extending appropriate base classes,
 * and proper exception propagation patterns.
 */
public class ExceptionHandlingTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void exceptionsShouldBeSuffixedWithException() {
        ArchRule rule = classes()
                .that().areAssignableTo(Exception.class)
                .and().areNotAssignableTo(RuntimeException.class)
                .should().haveNameMatching(".*Exception")
                .because("Checked exceptions should be suffixed with 'Exception' for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void runtimeExceptionsShouldBeSuffixedWithException() {
        ArchRule rule = classes()
                .that().areAssignableTo(RuntimeException.class)
                .should().haveNameMatching(".*Exception")
                .because("Runtime exceptions should be suffixed with 'Exception' for clarity");

        rule.check(importedClasses);
    }

    @Test
    public void domainExceptionsShouldResideInDomainExceptionsPackage() {
        ArchRule rule = classes()
                .that().areAssignableTo(Exception.class)
                .and().haveNameMatching(".*Domain.*Exception")
                .should().resideInAPackage("com.belman.domain.exceptions")
                .because("Domain exceptions should be organized in the domain.exceptions package");

        rule.check(importedClasses);
    }

    @Test
    public void applicationExceptionsShouldResideInApplicationExceptionsPackage() {
        ArchRule rule = classes()
                .that().areAssignableTo(Exception.class)
                .and().haveNameMatching(".*Application.*Exception")
                .should().resideInAPackage("com.belman.application.exceptions")
                .because("Application exceptions should be organized in the application.exceptions package");

        rule.check(importedClasses);
    }

    @Test
    public void infrastructureExceptionsShouldResideInInfrastructureExceptionsPackage() {
        ArchRule rule = classes()
                .that().areAssignableTo(Exception.class)
                .and().haveNameMatching(".*Infrastructure.*Exception")
                .should().resideInAPackage("com.belman.infrastructure.exceptions")
                .because("Infrastructure exceptions should be organized in the infrastructure.exceptions package");

        rule.check(importedClasses);
    }

    @Test
    public void presentationExceptionsShouldResideInPresentationExceptionsPackage() {
        ArchRule rule = classes()
                .that().areAssignableTo(Exception.class)
                .and().haveNameMatching(".*Presentation.*Exception")
                .should().resideInAPackage("com.belman.presentation.exceptions")
                .because("Presentation exceptions should be organized in the presentation.exceptions package");

        rule.check(importedClasses);
    }

    @Test
    public void dontThrowGenericExceptions() {
        ArchRule rule = noClasses()
                .should().callMethod(Exception.class, "printStackTrace")
                .orShould().callMethod(RuntimeException.class, "printStackTrace")
                .orShould().callMethod(Throwable.class, "printStackTrace")
                .because("Directly printing stack traces is discouraged. Use proper logging instead");

        rule.check(importedClasses);
    }

    @Test
    public void servicesAndRepositoriesShouldDeclareSpecificExceptions() {
        ArchRule rule = methods()
                .that().areDeclaredInClassesThat().haveNameMatching(".*Service|.*Repository")
                .and().arePublic()
                .should().notDeclareThrowableOfType(Exception.class)
                .because("Services and repositories should declare specific exceptions, not generic Exception");

        rule.check(importedClasses);
    }
}