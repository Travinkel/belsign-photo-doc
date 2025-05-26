package com.belman.cleancode;

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

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

/**
 * Tests for design pattern implementations.
 * This class verifies that common design patterns are implemented correctly in the codebase.
 */
public class DesignPatternTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
        System.out.println("[DEBUG_LOG] Imported " + importedClasses.size() + " classes for design pattern analysis");
    }

    /**
     * Tests that Singleton pattern is implemented correctly.
     * A proper Singleton should:
     * 1. Have a private constructor
     * 2. Have a static instance field
     * 3. Have a static getInstance method
     */
    @Test
    public void singletonPatternShouldBeImplementedCorrectly() {
        System.out.println("[DEBUG_LOG] Running Singleton pattern test");

        ArchCondition<JavaClass> beProperSingleton = new ArchCondition<>("be a proper Singleton") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Check if class has private constructors
                boolean hasPrivateConstructor = javaClass.getConstructors().stream()
                    .anyMatch(constructor -> constructor.getModifiers().contains(Modifier.PRIVATE));

                // Check if class has static instance field
                boolean hasStaticInstanceField = javaClass.getFields().stream()
                    .anyMatch(field -> field.getModifiers().contains(Modifier.STATIC) && 
                                      field.getRawType().getName().equals(javaClass.getName()));

                // Check if class has getInstance method
                boolean hasGetInstanceMethod = javaClass.getMethods().stream()
                    .anyMatch(method -> method.getName().equals("getInstance") && 
                                       method.getModifiers().contains(Modifier.STATIC) &&
                                       method.getRawReturnType().getName().equals(javaClass.getName()));

                // If class name suggests it's a Singleton but doesn't follow the pattern
                if (javaClass.getSimpleName().contains("Singleton") || 
                    javaClass.getSimpleName().endsWith("Registry") ||
                    javaClass.getSimpleName().endsWith("Manager") ||
                    javaClass.getSimpleName().endsWith("Factory")) {

                    if (!hasPrivateConstructor || !hasStaticInstanceField || !hasGetInstanceMethod) {
                        String message = String.format(
                            "Class %s appears to be a Singleton but doesn't follow the pattern correctly: " +
                            "private constructor: %b, static instance: %b, getInstance method: %b",
                            javaClass.getName(), hasPrivateConstructor, hasStaticInstanceField, hasGetInstanceMethod);
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                        System.out.println("[DEBUG_LOG] " + message);
                    }
                }
            }
        };

        ArchRule rule = classes()
            .that().haveNameMatching(".*Singleton.*")
            .or().haveNameMatching(".*Registry")
            .or().haveNameMatching(".*Manager")
            .or().haveNameMatching(".*Factory")
            .should(beProperSingleton);

        rule.check(importedClasses);
    }

    /**
     * Tests that Observer pattern is implemented correctly.
     * A proper Observer implementation should:
     * 1. Have Subject classes with methods to add/remove observers
     * 2. Have Observer interfaces or classes that can be notified
     */
    @Test
    public void observerPatternShouldBeImplementedCorrectly() {
        System.out.println("[DEBUG_LOG] Running Observer pattern test");

        ArchCondition<JavaClass> beProperSubject = new ArchCondition<>("be a proper Subject") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Check if class has methods to add/remove observers
                boolean hasAddObserverMethod = javaClass.getMethods().stream()
                    .anyMatch(method -> method.getName().contains("addObserver") || 
                                       method.getName().contains("addEventListener") ||
                                       method.getName().contains("addListener") ||
                                       method.getName().contains("subscribe"));

                boolean hasRemoveObserverMethod = javaClass.getMethods().stream()
                    .anyMatch(method -> method.getName().contains("removeObserver") || 
                                       method.getName().contains("removeEventListener") ||
                                       method.getName().contains("removeListener") ||
                                       method.getName().contains("unsubscribe"));

                boolean hasNotifyMethod = javaClass.getMethods().stream()
                    .anyMatch(method -> method.getName().contains("notify") || 
                                       method.getName().contains("fire") ||
                                       method.getName().contains("trigger") ||
                                       method.getName().contains("publish"));

                // If class name suggests it's a Subject but doesn't follow the pattern
                if (javaClass.getSimpleName().contains("Subject") || 
                    javaClass.getSimpleName().contains("Observable") ||
                    javaClass.getSimpleName().contains("Publisher")) {

                    if (!hasAddObserverMethod || !hasRemoveObserverMethod || !hasNotifyMethod) {
                        String message = String.format(
                            "Class %s appears to be a Subject but doesn't follow the Observer pattern correctly: " +
                            "add observer: %b, remove observer: %b, notify method: %b",
                            javaClass.getName(), hasAddObserverMethod, hasRemoveObserverMethod, hasNotifyMethod);
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                        System.out.println("[DEBUG_LOG] " + message);
                    }
                }
            }
        };

        ArchRule rule = classes()
            .that().haveNameMatching(".*Subject.*")
            .or().haveNameMatching(".*Observable.*")
            .or().haveNameMatching(".*Publisher.*")
            .should(beProperSubject);

        rule.check(importedClasses);
    }

    /**
     * Tests that Repository pattern is implemented correctly.
     * A proper Repository should:
     * 1. Have methods for CRUD operations
     * 2. Be named with "Repository" suffix
     * 3. Ideally implement a Repository interface
     */
    @Test
    public void repositoryPatternShouldBeImplementedCorrectly() {
        System.out.println("[DEBUG_LOG] Running Repository pattern test");

        ArchCondition<JavaClass> beProperRepository = new ArchCondition<>("be a proper Repository") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Skip interfaces - we're checking implementations
                if (javaClass.isInterface()) {
                    return;
                }

                // Check if class has CRUD methods
                boolean hasFindMethod = javaClass.getMethods().stream()
                    .anyMatch(method -> method.getName().startsWith("find") || 
                                       method.getName().startsWith("get") ||
                                       method.getName().equals("findAll") ||
                                       method.getName().equals("findById"));

                boolean hasSaveMethod = javaClass.getMethods().stream()
                    .anyMatch(method -> method.getName().equals("save") || 
                                       method.getName().equals("create") ||
                                       method.getName().equals("update") ||
                                       method.getName().equals("insert"));

                boolean hasDeleteMethod = javaClass.getMethods().stream()
                    .anyMatch(method -> method.getName().startsWith("delete") || 
                                       method.getName().startsWith("remove"));

                // Check if class implements a Repository interface
                boolean implementsRepositoryInterface = javaClass.getInterfaces().stream()
                    .anyMatch(iface -> iface.getName().contains("Repository"));

                // If class is a Repository but doesn't follow the pattern
                if (!hasFindMethod || !hasSaveMethod || !hasDeleteMethod) {
                    String message = String.format(
                        "Class %s is a Repository but doesn't have all expected CRUD methods: " +
                        "find: %b, save: %b, delete: %b, implements interface: %b",
                        javaClass.getName(), hasFindMethod, hasSaveMethod, hasDeleteMethod, implementsRepositoryInterface);
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                    System.out.println("[DEBUG_LOG] " + message);
                }
            }
        };

        ArchRule rule = classes()
            .that().haveNameMatching(".*Repository")
            .and().areNotInterfaces()
            .should(beProperRepository);

        rule.check(importedClasses);
    }

    /**
     * Tests that Factory pattern is implemented correctly.
     * A proper Factory should:
     * 1. Have methods that create objects
     * 2. Be named with "Factory" suffix
     */
    @Test
    public void factoryPatternShouldBeImplementedCorrectly() {
        System.out.println("[DEBUG_LOG] Running Factory pattern test");

        ArchCondition<JavaClass> beProperFactory = new ArchCondition<>("be a proper Factory") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                // Get all methods that return a non-primitive type and are not getters
                List<JavaMethod> factoryMethods = javaClass.getMethods().stream()
                    .filter(method -> !method.getRawReturnType().isPrimitive())
                    .filter(method -> !method.getName().startsWith("get"))
                    .filter(method -> !method.getName().equals("toString"))
                    .filter(method -> !method.getName().equals("hashCode"))
                    .filter(method -> !method.getName().equals("equals"))
                    .collect(Collectors.toList());

                // Check if any of these methods create objects
                boolean hasFactoryMethods = !factoryMethods.isEmpty();

                // If class is a Factory but doesn't have factory methods
                if (!hasFactoryMethods) {
                    String message = String.format(
                        "Class %s is a Factory but doesn't have methods that create objects",
                        javaClass.getName());
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                    System.out.println("[DEBUG_LOG] " + message);
                }
            }
        };

        ArchRule rule = classes()
            .that().haveNameMatching(".*Factory")
            .should(beProperFactory);

        rule.check(importedClasses);
    }
}
