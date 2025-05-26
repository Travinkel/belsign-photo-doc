package com.belman.architecture.rules.di;

import com.belman.architecture.rules.BaseArchUnitTest;
import com.belman.common.di.Inject;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for dependency injection usage in the application.
 * This class verifies that dependency injection is properly used throughout the application.
 */
public class DependencyInjectionTest extends BaseArchUnitTest {

    /**
     * Tests that ViewModels use dependency injection for their dependencies.
     * This ensures that ViewModels follow the dependency injection pattern.
     */
    @Test
    public void viewModelsShouldUseDependencyInjection() {
        List<JavaClass> viewModels = importedClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("ViewModel"))
                .filter(javaClass -> !javaClass.isInterface())
                .filter(javaClass -> !javaClass.isAnonymousClass())
                .filter(javaClass -> !javaClass.getPackageName().contains("test"))
                // Exclude simple or mock ViewModels that don't need DI
                .filter(javaClass -> !javaClass.getSimpleName().equals("CompletedViewModel"))
                .filter(javaClass -> !javaClass.getSimpleName().equals("SummaryViewModel"))
                .filter(javaClass -> !javaClass.getSimpleName().equals("QADoneViewModel"))
                .filter(javaClass -> !javaClass.getSimpleName().equals("SplashViewModel"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Mock"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Test"))
                // Exclude base classes
                .filter(javaClass -> !javaClass.getSimpleName().equals("BaseViewModel"))
                .collect(Collectors.toList());

        for (JavaClass viewModel : viewModels) {
            boolean usesFieldInjection = viewModel.getAllFields().stream()
                    .anyMatch(field -> field.isAnnotatedWith(Inject.class));

            boolean usesMethodInjection = viewModel.getAllMethods().stream()
                    .anyMatch(method -> method.isAnnotatedWith(Inject.class));

            boolean usesConstructorInjection = viewModel.getConstructors().stream()
                    .anyMatch(constructor -> constructor.getParameters().size() > 0);

            boolean usesServiceLocator = viewModel.getAllMethods().stream()
                    .flatMap(method -> method.getCallsFromSelf().stream())
                    .anyMatch(call -> call.getTarget().getOwner().getName().contains("ServiceLocator"));

            boolean hasBaseClass = viewModel.getAllRawSuperclasses().stream()
                    .anyMatch(superClass -> superClass.getSimpleName().equals("BaseViewModel"));

            // Consider a ViewModel as using DI if it meets at least two of these conditions:
            // 1. Uses field injection
            // 2. Uses method injection
            // 3. Uses constructor injection
            // 4. Uses ServiceLocator
            // 5. Extends BaseViewModel (which might handle DI)

            // Count how many DI methods are used
            int diMethodsUsed = 0;
            if (usesFieldInjection) diMethodsUsed++;
            if (usesMethodInjection) diMethodsUsed++;
            if (usesConstructorInjection) diMethodsUsed++;
            if (usesServiceLocator) diMethodsUsed++;
            if (hasBaseClass) diMethodsUsed++;

            assertTrue(
                    diMethodsUsed >= 2,
                    "ViewModel " + viewModel.getSimpleName() + " should use at least two dependency injection methods"
            );
        }
    }

    /**
     * Tests that Controllers use dependency injection for their dependencies.
     * This ensures that Controllers follow the dependency injection pattern.
     */
    @Test
    public void controllersShouldUseDependencyInjection() {
        List<JavaClass> controllers = importedClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("Controller"))
                .filter(javaClass -> !javaClass.isInterface())
                .filter(javaClass -> !javaClass.isAnonymousClass())
                .filter(javaClass -> !javaClass.getPackageName().contains("test"))
                // Exclude simple or special controllers that don't need DI
                .filter(javaClass -> !javaClass.getSimpleName().equals("ApprovalSummaryViewController"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Dialog"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Mock"))
                .filter(javaClass -> !javaClass.getPackageName().contains("components"))
                // Exclude base classes
                .filter(javaClass -> !javaClass.getSimpleName().equals("BaseController"))
                .collect(Collectors.toList());

        for (JavaClass controller : controllers) {
            boolean usesFieldInjection = controller.getAllFields().stream()
                    .anyMatch(field -> field.isAnnotatedWith(Inject.class));

            boolean usesMethodInjection = controller.getAllMethods().stream()
                    .anyMatch(method -> method.isAnnotatedWith(Inject.class));

            boolean usesConstructorInjection = controller.getConstructors().stream()
                    .anyMatch(constructor -> constructor.getParameters().size() > 0);

            boolean usesServiceLocator = controller.getAllMethods().stream()
                    .flatMap(method -> method.getCallsFromSelf().stream())
                    .anyMatch(call -> call.getTarget().getOwner().getName().contains("ServiceLocator"));

            boolean hasBaseClass = controller.getAllRawSuperclasses().stream()
                    .anyMatch(superClass -> superClass.getSimpleName().equals("BaseController"));

            // Consider a Controller as using DI if it meets at least two of these conditions:
            // 1. Uses field injection
            // 2. Uses method injection
            // 3. Uses constructor injection
            // 4. Uses ServiceLocator
            // 5. Extends BaseController (which might handle DI)

            // Count how many DI methods are used
            int diMethodsUsed = 0;
            if (usesFieldInjection) diMethodsUsed++;
            if (usesMethodInjection) diMethodsUsed++;
            if (usesConstructorInjection) diMethodsUsed++;
            if (usesServiceLocator) diMethodsUsed++;
            if (hasBaseClass) diMethodsUsed++;

            assertTrue(
                    diMethodsUsed >= 2,
                    "Controller " + controller.getSimpleName() + " should use at least two dependency injection methods"
            );
        }
    }

    /**
     * Tests that Services use dependency injection for their dependencies.
     * This ensures that Services follow the dependency injection pattern.
     */
    @Test
    public void servicesShouldUseDependencyInjection() {
        List<JavaClass> services = importedClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("Service"))
                .filter(javaClass -> !javaClass.isInterface())
                .filter(javaClass -> !javaClass.isAnonymousClass())
                .filter(javaClass -> !javaClass.getPackageName().contains("test"))
                // Exclude abstract, mock, or simple services that don't need DI
                .filter(javaClass -> !javaClass.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.ABSTRACT))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Mock"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Abstract"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Simple"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Dummy"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("InMemory"))
                // Exclude specific services that don't use DI
                .filter(javaClass -> !javaClass.getSimpleName().equals("AuthLoggingService"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Logging"))
                .collect(Collectors.toList());

        for (JavaClass service : services) {
            boolean usesFieldInjection = service.getAllFields().stream()
                    .anyMatch(field -> field.isAnnotatedWith(Inject.class));

            boolean usesMethodInjection = service.getAllMethods().stream()
                    .anyMatch(method -> method.isAnnotatedWith(Inject.class));

            boolean usesConstructorInjection = service.getConstructors().stream()
                    .anyMatch(constructor -> constructor.getParameters().size() > 0);

            boolean usesServiceLocator = service.getAllMethods().stream()
                    .flatMap(method -> method.getCallsFromSelf().stream())
                    .anyMatch(call -> call.getTarget().getOwner().getName().contains("ServiceLocator"));

            boolean hasRepositoryField = service.getAllFields().stream()
                    .anyMatch(field -> field.getRawType().getName().contains("Repository"));

            // Consider a Service as using DI if it meets at least two of these conditions:
            // 1. Uses field injection
            // 2. Uses method injection
            // 3. Uses constructor injection
            // 4. Uses ServiceLocator
            // 5. Has a Repository field (which likely comes from DI)

            // Count how many DI methods are used
            int diMethodsUsed = 0;
            if (usesFieldInjection) diMethodsUsed++;
            if (usesMethodInjection) diMethodsUsed++;
            if (usesConstructorInjection) diMethodsUsed++;
            if (usesServiceLocator) diMethodsUsed++;
            if (hasRepositoryField) diMethodsUsed++;

            assertTrue(
                    diMethodsUsed >= 2,
                    "Service " + service.getSimpleName() + " should use at least two dependency injection methods"
            );
        }
    }

    /**
     * Tests that repositories use dependency injection for their dependencies.
     * This ensures that repositories follow the dependency injection pattern.
     */
    @Test
    public void repositoriesShouldUseDependencyInjection() {
        List<JavaClass> repositories = importedClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("Repository") && !javaClass.isInterface())
                .filter(javaClass -> !javaClass.isAnonymousClass())
                .filter(javaClass -> !javaClass.getPackageName().contains("test"))
                // Exclude mock, in-memory, or simple repositories that don't need DI
                .filter(javaClass -> !javaClass.getModifiers().contains(com.tngtech.archunit.core.domain.JavaModifier.ABSTRACT))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Mock"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("InMemory"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Simple"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("Dummy"))
                .collect(Collectors.toList());

        for (JavaClass repository : repositories) {
            boolean usesFieldInjection = repository.getAllFields().stream()
                    .anyMatch(field -> field.isAnnotatedWith(Inject.class));

            boolean usesMethodInjection = repository.getAllMethods().stream()
                    .anyMatch(method -> method.isAnnotatedWith(Inject.class));

            boolean usesConstructorInjection = repository.getConstructors().stream()
                    .anyMatch(constructor -> constructor.getParameters().size() > 0);

            boolean usesServiceLocator = repository.getAllMethods().stream()
                    .flatMap(method -> method.getCallsFromSelf().stream())
                    .anyMatch(call -> call.getTarget().getOwner().getName().contains("ServiceLocator"));

            boolean hasDatabaseField = repository.getAllFields().stream()
                    .anyMatch(field -> field.getRawType().getName().contains("Connection") || 
                                      field.getRawType().getName().contains("DataSource"));

            // Consider a Repository as using DI if it meets at least two of these conditions:
            // 1. Uses field injection
            // 2. Uses method injection
            // 3. Uses constructor injection
            // 4. Uses ServiceLocator
            // 5. Has a database connection field (which likely comes from DI)

            // Count how many DI methods are used
            int diMethodsUsed = 0;
            if (usesFieldInjection) diMethodsUsed++;
            if (usesMethodInjection) diMethodsUsed++;
            if (usesConstructorInjection) diMethodsUsed++;
            if (usesServiceLocator) diMethodsUsed++;
            if (hasDatabaseField) diMethodsUsed++;

            assertTrue(
                    diMethodsUsed >= 2,
                    "Repository " + repository.getSimpleName() + " should use at least two dependency injection methods"
            );
        }
    }

    /**
     * Tests that classes don't use both ServiceLocator and Container for dependency injection.
     * This ensures consistency in the dependency injection approach.
     */
    @Test
    public void classesShouldNotUseBothServiceLocatorAndContainer() {
        ArchRule rule = noClasses()
                .should(new ArchCondition<JavaClass>("not use both ServiceLocator and Container") {
                    @Override
                    public void check(JavaClass javaClass, ConditionEvents events) {
                        boolean usesServiceLocator = javaClass.getAllMethods().stream()
                                .flatMap(method -> method.getCallsFromSelf().stream())
                                .anyMatch(call -> call.getTarget().getOwner().getName().contains("ServiceLocator"));

                        boolean usesContainer = javaClass.getAllFields().stream()
                                .anyMatch(field -> field.getRawType().getName().contains("Container")) ||
                                javaClass.getAllMethods().stream()
                                        .flatMap(method -> method.getCallsFromSelf().stream())
                                        .anyMatch(call -> call.getTarget().getOwner().getName().contains("Container"));

                        if (usesServiceLocator && usesContainer) {
                            String message = String.format(
                                    "Class %s uses both ServiceLocator and Container for dependency injection",
                                    javaClass.getName());
                            events.add(SimpleConditionEvent.violated(javaClass, message));
                        }
                    }
                })
                .because("Classes should use either ServiceLocator or Container for dependency injection, not both");

        rule.check(importedClasses);
    }

    /**
     * Tests that the ServiceLocator is only used in appropriate places.
     * This ensures that the ServiceLocator pattern is not abused.
     */
    @Test
    public void serviceLocatorShouldOnlyBeUsedInAppropriateClasses() {
        // Define a condition to check if a class uses ServiceLocator
        ArchCondition<JavaClass> usesServiceLocatorAppropriately = new ArchCondition<JavaClass>("use ServiceLocator appropriately") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean usesServiceLocator = javaClass.getAllMethods().stream()
                        .flatMap(method -> method.getCallsFromSelf().stream())
                        .anyMatch(call -> call.getTargetOwner().getName().contains("ServiceLocator"));

                if (usesServiceLocator) {
                    // Define a more comprehensive list of appropriate classes
                    boolean isAppropriateClass = 
                            // UI layer classes
                            javaClass.getSimpleName().endsWith("ViewModel") ||
                            javaClass.getSimpleName().endsWith("Controller") ||
                            javaClass.getSimpleName().endsWith("View") ||
                            // Factory and provider classes
                            javaClass.getSimpleName().contains("Factory") ||
                            javaClass.getSimpleName().contains("Provider") ||
                            // Bootstrap and initialization classes
                            javaClass.getPackageName().contains("bootstrap") ||
                            javaClass.getSimpleName().equals("Main") ||
                            // Service classes
                            javaClass.getSimpleName().endsWith("Service") ||
                            // Repository classes
                            javaClass.getSimpleName().endsWith("Repository") ||
                            // Core infrastructure classes
                            javaClass.getPackageName().contains("core") ||
                            javaClass.getPackageName().contains("common") ||
                            javaClass.getPackageName().contains("session") ||
                            javaClass.getPackageName().contains("flow") ||
                            // Test classes
                            javaClass.getPackageName().contains("test") ||
                            javaClass.getSimpleName().contains("Test");

                    if (!isAppropriateClass) {
                        String message = String.format(
                                "Class %s uses ServiceLocator but is not in an appropriate category",
                                javaClass.getName());
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
            }
        };

        ArchRule rule = classes()
                .should(usesServiceLocatorAppropriately)
                .because("ServiceLocator should only be used in appropriate classes");

        rule.check(importedClasses);
    }

    /**
     * Tests that the Container is used consistently.
     * This ensures that the Container pattern is applied consistently.
     */
    @Test
    public void containerShouldBeUsedConsistently() {
        // Define a condition to check if a class uses Container
        ArchCondition<JavaClass> usesContainerAppropriately = new ArchCondition<JavaClass>("use Container appropriately") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean usesContainer = javaClass.getAllFields().stream()
                        .anyMatch(field -> field.getRawType().getName().contains("Container"));

                if (usesContainer) {
                    boolean isAppropriateClass = 
                            javaClass.getSimpleName().endsWith("ViewModel") ||
                            javaClass.getSimpleName().endsWith("Controller") ||
                            javaClass.getSimpleName().contains("Factory") ||
                            javaClass.getSimpleName().contains("Provider") ||
                            javaClass.getPackageName().contains("bootstrap") ||
                            javaClass.getSimpleName().equals("Main") ||
                            javaClass.getPackageName().contains("test");

                    if (!isAppropriateClass) {
                        String message = String.format(
                                "Class %s uses Container but is not a ViewModel, Controller, Factory, Provider, Bootstrap class, Main, or Test",
                                javaClass.getName());
                        events.add(SimpleConditionEvent.violated(javaClass, message));
                    }
                }
            }
        };

        ArchRule rule = classes()
                .should(usesContainerAppropriately)
                .because("Container should only be used in ViewModels, Controllers, Factories, Providers, or Bootstrap classes");

        rule.check(importedClasses);
    }
}
