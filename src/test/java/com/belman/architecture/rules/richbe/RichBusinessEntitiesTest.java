package com.belman.architecture.rules.richbe;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Tests to enforce the rich business entities concept as specified in the guidelines.md.
 * Rich business entities should be in the domain package inside the business layer.
 */
public class RichBusinessEntitiesTest {

    private static JavaClasses importedClasses;

    // Custom condition to check if a class has business behavior methods (not just getters/setters)
    private static final ArchCondition<JavaClass> HAVE_BUSINESS_BEHAVIOR = new ArchCondition<JavaClass>("have business behavior") {
        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
            // Get all methods defined in the class (not inherited)
            Set<JavaMethod> methods = javaClass.getMethods().stream()
                    .filter(method -> method.getOwner().equals(javaClass))
                    .collect(Collectors.toSet());

            // Check if there's at least one method that is not a getter, setter, or standard Object method
            boolean hasBusinessBehavior = methods.stream().anyMatch(method -> {
                String methodName = method.getName();
                return !methodName.equals("equals") && 
                       !methodName.equals("hashCode") && 
                       !methodName.equals("toString") && 
                       !methodName.startsWith("get") && 
                       !methodName.startsWith("set") && 
                       !methodName.startsWith("is");
            });

            // Report the result
            boolean satisfied = hasBusinessBehavior;
            String message = String.format("Class %s %s business behavior methods", 
                    javaClass.getSimpleName(), 
                    satisfied ? "has" : "does not have");
            events.add(new SimpleConditionEvent(javaClass, satisfied, message));
        }
    };

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    @Test
    public void aggregatesShouldResideInDomainPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Aggregate")
                .should().resideInAPackage("com.belman.business.domain..")
                .because("Aggregates are rich business entities that should reside in the domain package");

        rule.check(importedClasses);
    }

    @Test
    public void entitiesShouldResideInDomainPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Entity")
                .should().resideInAPackage("com.belman.business.domain..")
                .because("Entities are rich business entities that should reside in the domain package");

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectsShouldResideInDomainPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ValueObject")
                .should().resideInAPackage("com.belman.business.domain..")
                .because("Value objects should reside in the domain package");

        rule.check(importedClasses);
    }

    @Test
    public void aggregatesShouldNotBeAnemic() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Aggregate")
                .should(HAVE_BUSINESS_BEHAVIOR)
                .because("Aggregates should have behavior methods, not just getters and setters");

        rule.check(importedClasses);
    }

    @Test
    public void entitiesShouldNotBeAnemic() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Entity")
                .and(new DescribedPredicate<JavaClass>("are not base or abstract entities") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("Entity");
                    }
                })
                .should(HAVE_BUSINESS_BEHAVIOR)
                .because("Entities should have behavior methods, not just getters and setters")
                .allowEmptyShould(true); // Allow the rule to pass if no classes match the criteria

        rule.check(importedClasses);
    }

    @Test
    public void valueObjectsShouldHaveOnlyFinalFields() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ValueObject")
                .or().resideInAPackage("com.belman.business.domain..values..")
                .should().haveOnlyFinalFields()
                .because("Value objects should be immutable");

        rule.check(importedClasses);
    }

    @Test
    public void domainClassesShouldNotBeAnemic() {
        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.business.domain..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .and().haveSimpleNameNotContaining("Test")
                .and().haveSimpleNameNotContaining("Mock")
                .and().areNotAnnotatedWith("org.junit.jupiter.api.Test")
                .and(new DescribedPredicate<JavaClass>("are not events, exceptions, or specifications") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        String name = javaClass.getSimpleName();
                        return !name.endsWith("Event") && 
                               !name.endsWith("Exception") && 
                               !name.endsWith("Specification") &&
                               !name.equals("Entity") &&
                               !name.equals("AbstractDomainEvent") &&
                               !name.equals("CommonStateKeys") &&
                               !name.contains("Specification");
                    }
                })
                .should(HAVE_BUSINESS_BEHAVIOR)
                .because("Domain classes should have behavior methods, not just getters and setters");

        rule.check(importedClasses);
    }
}
