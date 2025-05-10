package com.belman.architecture.rules.module;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Tests to verify that the bootstrap layer follows the correct patterns.
 * The bootstrap layer is responsible for initializing the application and
 * should be in the repository.bootstrap package.
 */
public class BootstrapLayerTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void bootstrapCodeShouldOnlyBeInBootstrapPackage() {
        // Bootstrap code should only be in the repository.bootstrap package
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Bootstrap")
                .or().haveSimpleNameEndingWith("Bootstrapper")
                .or().haveSimpleNameEndingWith("Main")
                .and().haveSimpleNameNotEndingWith("Test")
                .and().resideInAnyPackage("com.belman.repository..", "com.belman.bootstrap..")
                .should().resideInAnyPackage("com.belman.repository.bootstrap..", "com.belman.bootstrap..")
                .because("Bootstrap code should only be in the repository.bootstrap or bootstrap package");

        rule.check(importedClasses);
    }

    @Test
    public void bootstrapPackageShouldOnlyContainBootstrapCode() {
        // The bootstrap package should only contain bootstrap-related classes
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.repository.bootstrap..")
                .should().haveSimpleNameContaining("Bootstrap")
                .orShould().haveSimpleNameEndingWith("Bootstrapper")
                .orShould().haveSimpleNameEndingWith("Main")
                .orShould().haveSimpleNameEndingWith("Initializer")
                .because("The bootstrap package should only contain bootstrap-related classes");

        rule.check(importedClasses);
    }

    @Test
    public void bootstrapCodeShouldBeStateless() {
        // Bootstrap code should be stateless (use static methods)
        ArchRule rule = methods()
                .that().areDeclaredInClassesThat().haveSimpleNameContaining("Bootstrap")
                .or().areDeclaredInClassesThat().haveSimpleNameEndingWith("Bootstrapper")
                .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Test")
                .should().beStatic()
                .because("Bootstrap code should be stateless and use static methods");

        rule.check(importedClasses);
    }

    @Test
    public void bootstrapCodeShouldHaveInitializeMethod() {
        // Bootstrap code should have an initialize method
        ArchRule rule = methods()
                .that().haveName("initialize")
                .and().areDeclaredInClassesThat().haveSimpleNameContaining("Bootstrap")
                .or().areDeclaredInClassesThat().haveSimpleNameEndingWith("Bootstrapper")
                .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Test")
                .should().bePublic()
                .because("Bootstrap code should have a public initialize method");

        rule.check(importedClasses);
    }

    @Test
    public void bootstrapCodeShouldHaveShutdownMethod() {
        // Bootstrap code should have a shutdown method
        ArchRule rule = methods()
                .that().haveName("shutdown")
                .and().areDeclaredInClassesThat().haveSimpleNameContaining("Bootstrap")
                .or().areDeclaredInClassesThat().haveSimpleNameEndingWith("Bootstrapper")
                .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Test")
                .should().bePublic()
                .because("Bootstrap code should have a public shutdown method");

        rule.check(importedClasses);
    }

    @Test
    public void bootstrapCodeShouldNotHaveInstanceFields() {
        // Bootstrap code should not have instance fields
        ArchRule rule = fields()
                .that().areDeclaredInClassesThat().haveSimpleNameContaining("Bootstrap")
                .or().areDeclaredInClassesThat().haveSimpleNameEndingWith("Bootstrapper")
                .and().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Test")
                .should().beStatic()
                .andShould().beFinal()
                .because("Bootstrap code should not have instance fields");

        rule.check(importedClasses);
    }
}
