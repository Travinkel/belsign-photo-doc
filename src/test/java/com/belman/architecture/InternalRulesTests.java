/**
 * @startuml
 * package com.belman.internal {
 *   class InternalClass1
 *   class InternalClass2
 *   ' Mark these as package-private (no public modifier)
 *   InternalClass1 -[hidden]-> InternalClass2
 * }
 *
 * note right of InternalClass1
 *   All classes in com.belman.internal..
 *   should NOT be public.
 * end note
 *
 * ' Example of a violation (should not exist):
 * ' class PublicInternalClass <<public>>
 * @enduml
 *
 * ArchUnit Rule: No public classes in internal packages.
 */

package com.belman.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests for enforcing proper encapsulation of internal implementation details.
 * In Clean Architecture and DDD, implementation details should be properly encapsulated,
 * with only the necessary abstractions exposed to clients.
 */
public class InternalRulesTests {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
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
}