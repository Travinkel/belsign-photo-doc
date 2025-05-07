package com.belman.architecture.rules.clean;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Tests to verify that the dependency injection pattern is correctly implemented in the project.
 * Ensures that classes use appropriate mechanisms for dependency injection rather than creating
 * concrete implementations directly.
 */
public class GeneralArchitectureSupportTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void domainClassesShouldUseInterfacesNotImplementations() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .should().onlyDependOnClassesThat().areNotAssignableTo("com.belman.infrastructure..")
                .because(
                        "Domain classes should depend on interfaces, not concrete implementations from the infrastructure layer");

        rule.check(importedClasses);
    }

    @Test
    public void repositoryImplementationsShouldBeInInfrastructureLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("RepositoryImpl")
                .or().haveSimpleNameEndingWith("RepositoryImplementation")
                .should().resideInAPackage("com.belman.infrastructure.persistence..")
                .because("Repository implementations should be in the infrastructure layer");

        rule.check(importedClasses);
    }

    @Test
    public void repositoryInterfacesShouldBeInDomainLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain.repositories..")
                .because("Repository interfaces should be in the domain layer");

        rule.check(importedClasses);
    }

    @Test
    public void serviceImplementationsShouldImplementServiceInterfaces() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ServiceImpl")
                .or().haveSimpleNameEndingWith("ServiceImplementation")
                .should().resideInAnyPackage("com.belman.application..", "com.belman.infrastructure..")
                .because("Service implementations should be in application or infrastructure layers");

        rule.check(importedClasses);
    }

    @Test
    public void serviceInterfacesShouldBeInDomainLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().areInterfaces()
                .should().resideInAPackage("com.belman.domain.services..")
                .because("Service interfaces should be in the domain layer");

        rule.check(importedClasses);
    }

    @Test
    public void serviceFieldsShouldBePrivate() {
        ArchRule rule = fields()
                .that().haveNameMatching(".*[Ss]ervice")
                .or().haveNameMatching(".*[Rr]epository")
                .should().bePrivate()
                .because("Service and repository fields should be private and accessed through dependency injection");

        rule.check(importedClasses);
    }

    @Test
    public void constructorInjectionShouldBeUsed() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*Service.*")
                .or().haveNameMatching(".*Controller.*")
                .or().haveNameMatching(".*Repository.*")
                .should().haveOnlyFinalFields()
                .because("Classes should use constructor injection and have final fields");

        rule.check(importedClasses);
    }

    @Test
    public void daoClassesShouldBeInInfrastructureLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Dao")
                .should().resideInAPackage("com.belman.infrastructure.persistence..")
                .because("DAO classes should be in the infrastructure persistence package");

        rule.check(importedClasses);
    }

    @Test
    public void configClassesShouldBeInInfrastructureLayer() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Config")
                .or().haveSimpleNameEndingWith("Configuration")
                .should().resideInAPackage("com.belman.infrastructure..")
                .because("Configuration classes should be in the infrastructure layer");

        rule.check(importedClasses);
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

    @Test
    public void internalClassesShouldNotBePublic() {
        ArchRule rule = classes()
                .that().resideInAPackage("..internal..")
                .should().notBePublic()
                .because("Internal implementation classes should not be publicly accessible to enforce encapsulation");

        rule.check(importedClasses);
    }

    @Test
    public void internalClassesShouldOnlyBeAccessedWithinTheirModule() {
        ArchRule rule = classes()
                .that().resideInAPackage("..internal..")
                .should().onlyBeAccessed().byAnyPackage("..internal..", "..samemodule..")
                .because("Internal classes should only be accessed by classes within the same module");

        rule.check(importedClasses);
    }

    @Test
    public void internalClassesShouldImplementPublicInterfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage("..internal..")
                .and().areNotInterfaces()
                .should().implement(com.tngtech.archunit.base.DescribedPredicate.describe(
                        "interface from a public API package",
                        clazz -> !clazz.getInterfaces().isEmpty()))
                .because("Internal implementation classes should implement interfaces from public API packages");

        rule.check(importedClasses);
    }

    @Test
    public void internalFieldsShouldBePrivate() {
        ArchRule rule = fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..internal..")
                .should().bePrivate()
                .andShould().notBeStatic()
                .because("Fields in internal classes should be private to maintain encapsulation");

        rule.check(importedClasses);
    }

    @Test
    public void domainModelShouldNotDependOnInternalClasses() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain.model..")
                .should().dependOnClassesThat().resideInAPackage("..internal..")
                .because("Domain model should depend on abstractions, not internal implementations");

        rule.check(importedClasses);
    }

    @Test
    public void internalImplementationsShouldRespectHexagonalArchitecture() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.internal..")
                .should().dependOnClassesThat().resideOutsideOfPackage("..domain..")
                .because("Internal infrastructure implementations should not be referenced by domain layer");

        rule.check(importedClasses);
    }

    @Test
    public void internalClassesShouldFollowNamingConvention() {
        ArchRule rule = classes()
                .that().resideInAPackage("..internal..")
                .should().haveSimpleNameStartingWith("Default")
                .orShould().haveSimpleNameStartingWith("Basic")
                .orShould().haveSimpleNameEndingWith("Impl")
                .because("Internal implementations should follow naming conventions for better maintainability");

        rule.check(importedClasses);
    }

    @Test
    public void internalUtilitiesShouldBeFinal() {
        ArchRule rule = classes()
                .that().resideInAPackage("..internal.utils..")
                .should().haveModifier(JavaModifier.FINAL)
                .andShould().haveOnlyPrivateConstructors()
                .because(
                        "Internal utility classes should be final with private constructors to prevent instantiation and inheritance");

        rule.check(importedClasses);
    }

    @Test
    public void internalFactoriesShouldBeUsedForComplexObjectCreation() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Factory")
                .and().resideInAPackage("..internal..")
                .should().bePackagePrivate()
                .because("Internal factories should be package-private and responsible for complex object creation");

        rule.check(importedClasses);
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
    @Test
    public void noCyclicDependenciesBetweenPackages() {
        ArchRule rule = slices()
                .matching("com.belman.(*)..")
                .should().beFreeOfCycles();

        rule.check(importedClasses);
    }

}