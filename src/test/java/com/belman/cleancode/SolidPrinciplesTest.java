package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests for adherence to SOLID principles.
 * This class analyzes the codebase for violations of the SOLID principles:
 * - Single Responsibility Principle
 * - Open/Closed Principle
 * - Liskov Substitution Principle
 * - Interface Segregation Principle
 * - Dependency Inversion Principle
 */
public class SolidPrinciplesTest {
    private static JavaClasses importedClasses;
    private static final int MAX_METHODS_PER_CLASS = 20;
    private static final int MAX_FIELDS_PER_CLASS = 15;
    private static final int MAX_METHODS_PER_INTERFACE = 10;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
        System.out.println("[DEBUG_LOG] Imported " + importedClasses.size() + " classes for SOLID principles analysis");
    }

    /**
     * Tests that classes adhere to the Single Responsibility Principle.
     * A class should have only one reason to change, meaning it should have only one responsibility.
     * This test checks for classes with too many methods or fields, which might indicate multiple responsibilities.
     */
    @Test
    public void classesShouldHaveSingleResponsibility() {
        System.out.println("[DEBUG_LOG] Running Single Responsibility Principle test");

        ArchCondition<JavaClass> haveSingleResponsibility = new ArchCondition<>("have a single responsibility") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Skip test classes, interfaces, enums, and anonymous classes
                if (javaClass.getName().contains("Test") || 
                    javaClass.isInterface() || 
                    javaClass.isEnum() || 
                    javaClass.isAnonymousClass()) {
                    return;
                }

                int methodCount = javaClass.getMethods().size();
                int fieldCount = javaClass.getFields().size();

                // Check if the class has too many methods or fields
                if (methodCount > MAX_METHODS_PER_CLASS || fieldCount > MAX_FIELDS_PER_CLASS) {
                    String message = String.format(
                        "Class %s might violate the Single Responsibility Principle: %d methods (max %d), %d fields (max %d)",
                        javaClass.getName(), methodCount, MAX_METHODS_PER_CLASS, fieldCount, MAX_FIELDS_PER_CLASS);
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                    System.out.println("[DEBUG_LOG] " + message);
                }

                // Check for diverse method names that might indicate multiple responsibilities
                Set<String> methodPrefixes = javaClass.getMethods().stream()
                    .map(method -> {
                        String name = method.getName();
                        if (name.startsWith("get") || name.startsWith("set") || name.startsWith("is")) {
                            return name.substring(0, 3);
                        }
                        return name.length() > 3 ? name.substring(0, 3) : name;
                    })
                    .collect(Collectors.toSet());

                if (methodPrefixes.size() > 5 && methodCount > 10) {
                    String message = String.format(
                        "Class %s might have diverse responsibilities: %d different method name prefixes",
                        javaClass.getName(), methodPrefixes.size());
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                    System.out.println("[DEBUG_LOG] " + message);
                }
            }
        };

        ArchRule rule = classes()
            .that().areNotAnnotatedWith("org.junit.jupiter.api.Test")
            .and().areNotAnnotatedWith("org.junit.Test")
            .and().areNotInterfaces()
            .and().areNotEnums()
            .and().areNotAnonymousClasses()
            .and().haveNameNotMatching(".*Test")
            .should(haveSingleResponsibility);

        rule.check(importedClasses);
    }

    /**
     * Tests that classes adhere to the Open/Closed Principle.
     * Software entities should be open for extension but closed for modification.
     * This test checks for classes that might be difficult to extend without modification.
     */
    @Test
    public void classesShouldBeOpenForExtensionClosedForModification() {
        System.out.println("[DEBUG_LOG] Running Open/Closed Principle test");

        ArchCondition<JavaClass> beOpenForExtensionClosedForModification = 
            new ArchCondition<>("be open for extension but closed for modification") {
                @Override
                public void check(JavaClass javaClass, ConditionEvents events) {
                    // Skip test classes, interfaces, enums, and anonymous classes
                    if (javaClass.getName().contains("Test") || 
                        javaClass.isInterface() || 
                        javaClass.isEnum() || 
                        javaClass.isAnonymousClass()) {
                        return;
                    }

                    // Check if the class has final methods that might prevent extension
                    List<JavaMethod> finalMethods = javaClass.getMethods().stream()
                        .filter(method -> method.getModifiers().contains(JavaModifier.FINAL))
                        .filter(method -> !method.getModifiers().contains(JavaModifier.PRIVATE))
                        .collect(Collectors.toList());

                    if (!finalMethods.isEmpty() && !javaClass.getModifiers().contains(JavaModifier.FINAL)) {
                        String message = String.format(
                            "Class %s has %d final methods but is not final itself, which might violate the Open/Closed Principle",
                            javaClass.getName(), finalMethods.size());
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                        System.out.println("[DEBUG_LOG] " + message);
                    }

                    // Check if the class has public fields that might be difficult to extend
                    List<JavaField> publicFields = javaClass.getFields().stream()
                        .filter(field -> field.getModifiers().contains(JavaModifier.PUBLIC))
                        .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))
                        .filter(field -> !field.getModifiers().contains(JavaModifier.FINAL))
                        .collect(Collectors.toList());

                    if (!publicFields.isEmpty()) {
                        String message = String.format(
                            "Class %s has %d public non-final instance fields, which might violate the Open/Closed Principle",
                            javaClass.getName(), publicFields.size());
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                        System.out.println("[DEBUG_LOG] " + message);
                    }
                }
            };

        ArchRule rule = classes()
            .that().areNotAnnotatedWith("org.junit.jupiter.api.Test")
            .and().areNotAnnotatedWith("org.junit.Test")
            .and().areNotInterfaces()
            .and().areNotEnums()
            .and().areNotAnonymousClasses()
            .and().haveNameNotMatching(".*Test")
            .should(beOpenForExtensionClosedForModification);

        rule.check(importedClasses);
    }

    /**
     * Tests that classes adhere to the Liskov Substitution Principle.
     * Objects of a superclass should be replaceable with objects of a subclass without affecting the correctness of the program.
     * This test checks for potential violations of the Liskov Substitution Principle.
     * 
     * Note: This is a simplified implementation that checks for common LSP violations that can be detected statically.
     */
    @Test
    public void classesShouldFollowLiskovSubstitutionPrinciple() {
        System.out.println("[DEBUG_LOG] Running Liskov Substitution Principle test");

        ArchCondition<JavaClass> followLiskovSubstitutionPrinciple = 
            new ArchCondition<>("follow the Liskov Substitution Principle") {
                @Override
                public void check(JavaClass javaClass, ConditionEvents events) {
                    // Skip classes that don't extend other classes or are test/utility classes
                    if (javaClass.getName().contains("Test") ||
                        javaClass.isInterface() ||
                        javaClass.isEnum() ||
                        javaClass.isAnonymousClass()) {
                        return;
                    }

                    // Check for final methods in non-final classes
                    // This can violate LSP by preventing proper overriding in subclasses
                    List<JavaMethod> finalMethods = javaClass.getMethods().stream()
                        .filter(method -> method.getModifiers().contains(JavaModifier.FINAL))
                        .filter(method -> !method.getModifiers().contains(JavaModifier.PRIVATE))
                        .filter(method -> !method.isConstructor())
                        .collect(Collectors.toList());

                    if (!finalMethods.isEmpty() && !javaClass.getModifiers().contains(JavaModifier.FINAL)) {
                        String message = String.format(
                            "Class %s has %d final methods but is not final itself, which might violate the Liskov Substitution Principle",
                            javaClass.getName(), finalMethods.size());
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                        System.out.println("[DEBUG_LOG] " + message);

                        // Log the first few final methods for debugging
                        int logLimit = Math.min(finalMethods.size(), 3);
                        for (int i = 0; i < logLimit; i++) {
                            System.out.println("[DEBUG_LOG] - Final method: " + finalMethods.get(i).getName());
                        }
                    }

                    // Check for methods that override Object methods but change return type
                    // This is a common LSP violation
                    for (JavaMethod method : javaClass.getMethods()) {
                        if (method.getName().equals("equals") && 
                            method.getRawParameterTypes().size() == 1 &&
                            !method.getRawReturnType().getName().equals("boolean")) {

                            String message = String.format(
                                "Method equals in class %s violates LSP: return type is not boolean",
                                javaClass.getName());
                            events.add(SimpleConditionEvent.violated(javaClass, message));
                            System.out.println("[DEBUG_LOG] " + message);
                        }

                        if (method.getName().equals("hashCode") && 
                            method.getRawParameterTypes().isEmpty() &&
                            !method.getRawReturnType().getName().equals("int")) {

                            String message = String.format(
                                "Method hashCode in class %s violates LSP: return type is not int",
                                javaClass.getName());
                            events.add(SimpleConditionEvent.violated(javaClass, message));
                            System.out.println("[DEBUG_LOG] " + message);
                        }

                        if (method.getName().equals("toString") && 
                            method.getRawParameterTypes().isEmpty() &&
                            !method.getRawReturnType().getName().equals("java.lang.String")) {

                            String message = String.format(
                                "Method toString in class %s violates LSP: return type is not String",
                                javaClass.getName());
                            events.add(SimpleConditionEvent.violated(javaClass, message));
                            System.out.println("[DEBUG_LOG] " + message);
                        }
                    }
                }
            };

        ArchRule rule = classes()
            .that().areNotAnnotatedWith("org.junit.jupiter.api.Test")
            .and().areNotAnnotatedWith("org.junit.Test")
            .and().areNotInterfaces()
            .and().areNotEnums()
            .and().areNotAnonymousClasses()
            .and().haveNameNotMatching(".*Test")
            .should(followLiskovSubstitutionPrinciple);

        rule.check(importedClasses);
    }

    /**
     * Tests that interfaces adhere to the Interface Segregation Principle.
     * Clients should not be forced to depend on interfaces they do not use.
     * This test checks for interfaces with too many methods, which might indicate a violation of ISP.
     */
    @Test
    public void interfacesShouldBeSmallAndFocused() {
        System.out.println("[DEBUG_LOG] Running Interface Segregation Principle test");

        ArchCondition<JavaClass> beSmallAndFocused = new ArchCondition<>("be small and focused") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Skip non-interfaces
                if (!javaClass.isInterface()) {
                    return;
                }

                int methodCount = javaClass.getMethods().size();

                // Check if the interface has too many methods
                if (methodCount > MAX_METHODS_PER_INTERFACE) {
                    String message = String.format(
                        "Interface %s might violate the Interface Segregation Principle: %d methods (max %d)",
                        javaClass.getName(), methodCount, MAX_METHODS_PER_INTERFACE);
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                    System.out.println("[DEBUG_LOG] " + message);
                }

                // Check for diverse method names that might indicate multiple responsibilities
                Set<String> methodPrefixes = javaClass.getMethods().stream()
                    .map(method -> {
                        String name = method.getName();
                        if (name.startsWith("get") || name.startsWith("set") || name.startsWith("is")) {
                            return name.substring(0, 3);
                        }
                        return name.length() > 3 ? name.substring(0, 3) : name;
                    })
                    .collect(Collectors.toSet());

                if (methodPrefixes.size() > 3 && methodCount > 5) {
                    String message = String.format(
                        "Interface %s might have diverse responsibilities: %d different method name prefixes",
                        javaClass.getName(), methodPrefixes.size());
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                    System.out.println("[DEBUG_LOG] " + message);
                }
            }
        };

        ArchRule rule = classes()
            .that().areInterfaces()
            .and().haveNameNotMatching(".*Test")
            .should(beSmallAndFocused);

        rule.check(importedClasses);
    }

    /**
     * Tests that classes adhere to the Dependency Inversion Principle.
     * High-level modules should not depend on low-level modules. Both should depend on abstractions.
     * Abstractions should not depend on details. Details should depend on abstractions.
     * This test checks for potential violations of the Dependency Inversion Principle.
     */
    @Test
    public void classesShouldFollowDependencyInversionPrinciple() {
        System.out.println("[DEBUG_LOG] Running Dependency Inversion Principle test");

        // Check that application layer doesn't depend on infrastructure implementations
        ArchRule applicationShouldNotDependOnInfrastructure = noClasses()
            .that().resideInAPackage("com.belman.application..")
            .should().dependOnClassesThat().resideInAPackage("com.belman.dataaccess.persistence..")
            .because("Application layer should depend on abstractions, not infrastructure implementations");

        applicationShouldNotDependOnInfrastructure.check(importedClasses);

        // Check that domain layer doesn't depend on application or infrastructure
        ArchRule domainShouldNotDependOnApplicationOrInfrastructure = noClasses()
            .that().resideInAPackage("com.belman.domain..")
            .should().dependOnClassesThat().resideInAnyPackage("com.belman.application..", "com.belman.dataaccess..")
            .because("Domain layer should not depend on application or infrastructure layers");

        domainShouldNotDependOnApplicationOrInfrastructure.check(importedClasses);

        // Check that interfaces are used for dependencies between layers
        ArchCondition<JavaClass> useDependencyInjectionOrInterfaces = 
            new ArchCondition<>("use dependency injection or interfaces for dependencies") {
                @Override
                public void check(JavaClass javaClass, ConditionEvents events) {
                    // Skip test classes, interfaces, enums, and anonymous classes
                    if (javaClass.getName().contains("Test") || 
                        javaClass.isInterface() || 
                        javaClass.isEnum() || 
                        javaClass.isAnonymousClass()) {
                        return;
                    }

                    // Check fields for concrete implementation dependencies
                    for (JavaField field : javaClass.getFields()) {
                        JavaClass fieldType = field.getRawType();

                        // Skip primitive types, enums, and standard library classes
                        if (fieldType.isPrimitive() || 
                            fieldType.isEnum() || 
                            !fieldType.getPackageName().startsWith("com.belman")) {
                            continue;
                        }

                        // Check if the field type is a concrete class (not an interface or abstract class)
                        if (!fieldType.isInterface() && 
                            !fieldType.getModifiers().contains(JavaModifier.ABSTRACT) &&
                            fieldType.getPackageName().startsWith("com.belman.dataaccess")) {

                            String message = String.format(
                                "Class %s has a field of concrete implementation type %s, which might violate the Dependency Inversion Principle",
                                javaClass.getName(), fieldType.getName());
                            events.add(SimpleConditionEvent.violated(javaClass, message));
                            System.out.println("[DEBUG_LOG] " + message);
                        }
                    }
                }
            };

        ArchRule rule = classes()
            .that().areNotAnnotatedWith("org.junit.jupiter.api.Test")
            .and().areNotAnnotatedWith("org.junit.Test")
            .and().areNotInterfaces()
            .and().areNotEnums()
            .and().areNotAnonymousClasses()
            .and().haveNameNotMatching(".*Test")
            .and().resideInAnyPackage("com.belman.application..", "com.belman.domain..")
            .should(useDependencyInjectionOrInterfaces);

        rule.check(importedClasses);
    }
}
