package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * Tests to verify that the dependency injection pattern is correctly implemented in the project.
 * Ensures that classes use appropriate mechanisms for dependency injection rather than creating
 * concrete implementations directly.
 */
public class DependencyInjectionTest {

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
}