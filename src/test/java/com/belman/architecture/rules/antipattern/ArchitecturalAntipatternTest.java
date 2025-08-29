package com.belman.architecture.rules.antipattern;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
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

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Tests to detect common architectural antipatterns in the codebase.
 * These tests combine scope rules with antipattern detection to ensure
 * the architecture remains clean and maintainable.
 */
public class ArchitecturalAntipatternTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
    }

    /**
     * Detects service classes that directly use repository implementations instead of interfaces.
     * This is an antipattern because it creates tight coupling between services and repository implementations.
     */
    @Test
    public void servicesShouldNotDependOnRepositoryImplementations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.service..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.repository.persistence..")
                .because("Services should depend on repository interfaces, not implementations");

        rule.check(importedClasses);
    }

    /**
     * Detects UI classes that directly access repositories, bypassing the service layer.
     * This is an antipattern because it violates the layered architecture.
     */
    @Test
    public void uiShouldNotDependOnRepositories() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.belman.presentation..")
                .should().dependOnClassesThat().resideInAPackage("com.belman.repository..")
                .because(
                        "UI classes should not directly access repositories, they should go through the service layer");

        rule.check(importedClasses);
    }

    /**
     * Detects circular dependencies between packages.
     * This is an antipattern because it creates tight coupling and makes the code harder to understand and maintain.
     */
    @Test
    public void noCyclicDependenciesBetweenPackages() {
        ArchRule rule = slices()
                .matching("com.belman.(*)..")
                .should().beFreeOfCycles()
                .because("Cyclic dependencies between packages make the code harder to understand and maintain");

        rule.check(importedClasses);
    }

    /**
     * Detects god classes (classes with too many responsibilities).
     * This is an antipattern because it violates the Single Responsibility Principle.
     */
    @Test
    public void noGodClasses() {
        // Define a condition to detect god classes
        ArchCondition<JavaClass> notBeGodClass = new ArchCondition<JavaClass>("not be a god class") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Count methods and fields
                int methodCount = javaClass.getMethods().size();
                int fieldCount = javaClass.getFields().size();

                // Define thresholds for god classes
                int methodThreshold = 20;
                int fieldThreshold = 15;

                boolean isGodClass = methodCount > methodThreshold || fieldCount > fieldThreshold;

                if (isGodClass) {
                    String message = String.format(
                            "Class %s is a god class with %d methods and %d fields",
                            javaClass.getName(), methodCount, fieldCount);
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };

        ArchRule rule = classes()
                .that().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotatedWith("org.junit.jupiter.api.Test")
                .and().haveNameNotMatching(".*Test")
                .should(notBeGodClass)
                .because("God classes violate the Single Responsibility Principle");

        rule.check(importedClasses);
    }

    /**
     * Detects feature envy (classes that use too many methods from other classes).
     * This is an antipattern because it indicates that functionality might be in the wrong place.
     */
    @Test
    public void noFeatureEnvy() {
        // Define a condition to detect feature envy
        ArchCondition<JavaClass> notHaveFeatureEnvy = new ArchCondition<JavaClass>("not have feature envy") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // For each method in the class
                for (JavaMethod method : javaClass.getMethods()) {
                    // Count calls to methods of other classes
                    long callsToOtherClasses = method.getCallsFromSelf().stream()
                            .filter(call -> !call.getTargetOwner().equals(javaClass))
                            .count();

                    // Define threshold for feature envy
                    int threshold = 5;

                    if (callsToOtherClasses > threshold) {
                        String message = String.format(
                                "Method %s in class %s has feature envy with %d calls to other classes",
                                method.getName(), javaClass.getName(), callsToOtherClasses);
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
            }
        };

        ArchRule rule = classes()
                .that().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotatedWith("org.junit.jupiter.api.Test")
                .and().haveNameNotMatching(".*Test")
                .should(notHaveFeatureEnvy)
                .because("Feature envy indicates that functionality might be in the wrong place");

        rule.check(importedClasses);
    }

    /**
     * Detects anemic domain models (domain classes without behavior).
     * This is an antipattern because domain classes should encapsulate both data and behavior.
     */
    @Test
    public void noDomainClassesWithoutBehavior() {
        // Define a condition to detect domain classes without behavior
        ArchCondition<JavaClass> haveBehavior = new ArchCondition<JavaClass>("have behavior") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Get all methods defined in the class (not inherited)
                Set<JavaMethod> methods = javaClass.getMethods().stream()
                        .filter(method -> method.getOwner().equals(javaClass))
                        .collect(Collectors.toSet());

                // Check if there's at least one method that is not a getter, setter, or standard Object method
                boolean hasBehavior = methods.stream().anyMatch(method -> {
                    String methodName = method.getName();
                    return !methodName.equals("equals") &&
                           !methodName.equals("hashCode") &&
                           !methodName.equals("toString") &&
                           !methodName.startsWith("get") &&
                           !methodName.startsWith("set") &&
                           !methodName.startsWith("is");
                });

                if (!hasBehavior) {
                    String message = String.format(
                            "Domain class %s is anemic (has no behavior methods)",
                            javaClass.getName());
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };

        ArchRule rule = classes()
                .that().resideInAPackage("com.belman.domain..")
                .and().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotatedWith("org.junit.jupiter.api.Test")
                .and().haveNameNotMatching(".*Test")
                .and().haveNameNotMatching(".*Event")
                .and().haveNameNotMatching(".*Exception")
                .and().haveNameNotMatching(".*Factory")
                .should(haveBehavior)
                .because("Domain classes should encapsulate both data and behavior");

        rule.check(importedClasses);
    }

    /**
     * Detects inappropriate intimacy (classes that know too much about each other).
     * This is an antipattern because it creates tight coupling between classes.
     */
    @Test
    public void noInappropriateIntimacy() {
        // Define a condition to detect inappropriate intimacy
        ArchCondition<JavaClass> notHaveInappropriateIntimacy = new ArchCondition<JavaClass>(
                "not have inappropriate intimacy") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // For each field in the class
                for (JavaField field : javaClass.getFields()) {
                    // Skip primitive fields and fields from standard libraries
                    if (field.getRawType().isPrimitive() ||
                        field.getRawType().getPackageName().startsWith("java.") ||
                        field.getRawType().getPackageName().startsWith("javafx.")) {
                        continue;
                    }

                    // Check if the field is of a type from a different package
                    if (!field.getRawType().getPackageName().equals(javaClass.getPackageName())) {
                        // Count accesses to the field's methods
                        long accessesToFieldMethods = javaClass.getMethods().stream()
                                .flatMap(method -> method.getCallsFromSelf().stream())
                                .filter(call -> call.getTargetOwner().equals(field.getRawType()))
                                .count();

                        // Define threshold for inappropriate intimacy
                        int threshold = 5;

                        if (accessesToFieldMethods > threshold) {
                            String message = String.format(
                                    "Class %s has inappropriate intimacy with %s (%d accesses)",
                                    javaClass.getName(), field.getRawType().getName(), accessesToFieldMethods);
                            events.add(SimpleConditionEvent.violated(javaClass, message));
                        }
                    }
                }
            }
        };

        ArchRule rule = classes()
                .that().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotatedWith("org.junit.jupiter.api.Test")
                .and().haveNameNotMatching(".*Test")
                .should(notHaveInappropriateIntimacy)
                .because("Inappropriate intimacy creates tight coupling between classes");

        rule.check(importedClasses);
    }

    /**
     * Detects service classes that depend on concrete implementations instead of interfaces.
     * This is an antipattern because it creates tight coupling and makes testing harder.
     */
    @Test
    public void servicesShouldDependOnInterfaces() {
        // Define a predicate to identify service classes
        DescribedPredicate<JavaClass> isServiceClass = new DescribedPredicate<JavaClass>("is a service class") {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.getSimpleName().endsWith("Service") &&
                       !javaClass.isInterface() &&
                       javaClass.getPackageName().contains("service");
            }
        };

        // Define a condition to check if a class depends on interfaces for its dependencies
        ArchCondition<JavaClass> dependOnInterfaces = new ArchCondition<JavaClass>("depend on interfaces") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // For each field in the class
                for (JavaField field : javaClass.getFields()) {
                    // Skip primitive fields and fields from standard libraries
                    if (field.getRawType().isPrimitive() ||
                        field.getRawType().getPackageName().startsWith("java.") ||
                        field.getRawType().getPackageName().startsWith("javafx.")) {
                        continue;
                    }

                    // Check if the field is of a concrete type that should be an interface
                    if (!field.getRawType().isInterface() &&
                        (field.getRawType().getSimpleName().endsWith("Repository") ||
                         field.getRawType().getSimpleName().endsWith("Service"))) {
                        String message = String.format(
                                "Service class %s depends on concrete implementation %s instead of an interface",
                                javaClass.getName(), field.getRawType().getName());
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
            }
        };

        ArchRule rule = classes()
                .that(isServiceClass)
                .should(dependOnInterfaces)
                .because("Services should depend on interfaces, not concrete implementations");

        rule.check(importedClasses);
    }

    /**
     * Detects classes that violate the Law of Demeter (principle of least knowledge).
     * This is an antipattern because it creates tight coupling and makes the code harder to maintain.
     * <p>
     * This simplified implementation looks for methods that make calls to methods of objects
     * that are not directly related to the class (not parameters, not fields, not created within the method).
     */
    @Test
    public void noLawOfDemeterViolations() {
        // Define a condition to detect potential Law of Demeter violations
        ArchCondition<JavaClass> notViolateLawOfDemeter = new ArchCondition<JavaClass>("not violate Law of Demeter") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // For each method in the class
                for (JavaMethod method : javaClass.getMethods()) {
                    // Count method chains (calls to methods on objects returned from other methods)
                    long methodChainCount = method.getCallsFromSelf().stream()
                            .filter(call -> call.getTarget().getName().startsWith("get") ||
                                            call.getTarget().getName().startsWith("find"))
                            .count();

                    // If there are too many method chains, it might violate Law of Demeter
                    if (methodChainCount > 3) {
                        String message = String.format(
                                "Method %s in class %s might violate Law of Demeter with %d getter/finder calls",
                                method.getName(), javaClass.getName(), methodChainCount);
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
            }
        };

        ArchRule rule = classes()
                .that().areNotInterfaces()
                .and().areNotEnums()
                .and().areNotAnnotatedWith("org.junit.jupiter.api.Test")
                .and().haveNameNotMatching(".*Test")
                .should(notViolateLawOfDemeter)
                .because("Law of Demeter violations create tight coupling and make code harder to maintain");

        rule.check(importedClasses);
    }
}
