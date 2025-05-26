package com.belman.architecture.rules.presentation;

import com.belman.architecture.rules.BaseArchUnitTest;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for MVVM pattern compliance.
 * This class verifies that the UI layer follows the MVVM (Model-View-ViewModel) pattern.
 */
public class MVVMPatternTest extends BaseArchUnitTest {

    /**
     * Tests that all ViewModels extend BaseViewModel.
     * This ensures consistency in the ViewModel layer.
     */
    @Test
    public void viewModelsShouldExtendBaseViewModel() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("ViewModel")
                .and().areNotInterfaces()
                .and().areNotAnonymousClasses()
                .and(new DescribedPredicate<JavaClass>("are not special ViewModels") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("PhotoTemplateStatusViewModel");
                    }
                })
                .should().beAssignableTo("com.belman.presentation.base.BaseViewModel")
                .because("ViewModels should extend BaseViewModel for consistency");

        rule.check(importedClasses);
    }

    /**
     * Tests that all Controllers extend BaseController.
     * This ensures consistency in the Controller layer.
     */
    @Test
    public void controllersShouldExtendBaseController() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .and().areNotInterfaces()
                .and().areNotAnonymousClasses()
                .and(new DescribedPredicate<JavaClass>("are not test controllers or special controllers") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getPackageName().contains("test") &&
                               !javaClass.getPackageName().contains("components") &&
                               !javaClass.getSimpleName().contains("DialogController");
                    }
                })
                .should().beAssignableTo("com.belman.presentation.base.BaseController")
                .because("Controllers should extend BaseController for consistency");

        rule.check(importedClasses);
    }

    /**
     * Tests that all Views extend BaseView.
     * This ensures consistency in the View layer.
     */
    @Test
    public void viewsShouldExtendBaseView() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("View")
                .and().areNotInterfaces()
                .and().areNotAnonymousClasses()
                .and(new DescribedPredicate<JavaClass>("are not test views") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getPackageName().contains("test");
                    }
                })
                .and(new DescribedPredicate<JavaClass>("are not base views") {
                    @Override
                    public boolean test(JavaClass javaClass) {
                        return !javaClass.getSimpleName().equals("BaseView") &&
                               !javaClass.getSimpleName().equals("DashboardBaseView") &&
                               !javaClass.getSimpleName().equals("PhotoDocumentationBaseView") &&
                               !javaClass.getSimpleName().equals("AdminBaseView") &&
                               !javaClass.getSimpleName().equals("QABaseView") &&
                               !javaClass.getSimpleName().equals("WorkerBaseView");
                    }
                })
                .should().beAssignableTo("com.belman.presentation.base.BaseView")
                .because("Views should extend BaseView for consistency");

        rule.check(importedClasses);
    }

    /**
     * Tests that ViewModels don't directly access UI components.
     * This ensures proper separation of concerns in the MVVM pattern.
     */
    @Test
    public void viewModelsShouldNotDependOnUIComponents() {
        ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("ViewModel")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "javafx.scene.control..",
                        "javafx.scene.layout..",
                        "javafx.scene.shape..",
                        "javafx.scene.web..")
                .because("ViewModels should not directly access UI components");

        rule.check(importedClasses);
    }

    /**
     * Tests that ViewModels have observable properties.
     * This ensures that ViewModels follow the MVVM pattern by providing observable properties
     * that can be bound to the UI.
     */
    @Test
    public void viewModelsShouldHaveObservableProperties() {
        List<JavaClass> viewModels = importedClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("ViewModel"))
                .filter(javaClass -> !javaClass.isInterface())
                .filter(javaClass -> !javaClass.isAnonymousClass())
                .filter(javaClass -> !javaClass.getPackageName().contains("test"))
                .collect(Collectors.toList());

        for (JavaClass viewModel : viewModels) {
            List<JavaField> observableProperties = viewModel.getAllFields().stream()
                    .filter(field -> field.getRawType().getName().contains("javafx.beans.property") ||
                                    field.getRawType().getName().contains("javafx.collections"))
                    .collect(Collectors.toList());

            List<JavaMethod> propertyMethods = viewModel.getAllMethods().stream()
                    .filter(method -> method.getName().endsWith("Property"))
                    .collect(Collectors.toList());

            // Either the ViewModel should have observable fields or property methods
            assertTrue(
                    !observableProperties.isEmpty() || !propertyMethods.isEmpty(),
                    "ViewModel " + viewModel.getSimpleName() + " should have observable properties or property methods"
            );
        }
    }

    /**
     * Tests that Controllers have a reference to their ViewModel.
     * This ensures proper connection between Controllers and ViewModels in the MVVM pattern.
     */
    @Test
    public void controllersShouldHaveViewModelReference() {
        List<JavaClass> controllers = importedClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("Controller"))
                .filter(javaClass -> !javaClass.isInterface())
                .filter(javaClass -> !javaClass.isAnonymousClass())
                .filter(javaClass -> !javaClass.getPackageName().contains("test"))
                .filter(javaClass -> !javaClass.getPackageName().contains("components"))
                .filter(javaClass -> !javaClass.getSimpleName().contains("DialogController"))
                .collect(Collectors.toList());

        for (JavaClass controller : controllers) {
            List<JavaField> viewModelFields = controller.getAllFields().stream()
                    .filter(field -> field.getRawType().getName().endsWith("ViewModel"))
                    .collect(Collectors.toList());

            assertTrue(
                    !viewModelFields.isEmpty(),
                    "Controller " + controller.getSimpleName() + " should have a reference to a ViewModel"
            );
        }
    }

    /**
     * Tests that for each ViewModel there is a corresponding Controller.
     * This ensures that the MVVM pattern is consistently applied across the application.
     */
    @Test
    public void eachViewModelShouldHaveCorrespondingController() {
        List<String> viewModelNames = importedClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("ViewModel"))
                .filter(javaClass -> !javaClass.isInterface())
                .filter(javaClass -> !javaClass.isAnonymousClass())
                .filter(javaClass -> !javaClass.getPackageName().contains("test"))
                // Exclude only essential ViewModels that don't need controllers
                .filter(javaClass -> !javaClass.getSimpleName().equals("BaseViewModel"))
                .filter(javaClass -> !javaClass.getSimpleName().equals("TestablePhotoCubeViewModel"))
                .filter(javaClass -> !javaClass.getSimpleName().equals("PhotoTemplateStatusViewModel"))
                .map(javaClass -> javaClass.getSimpleName().replace("ViewModel", ""))
                .collect(Collectors.toList());

        List<String> controllerNames = importedClasses.stream()
                .filter(javaClass -> javaClass.getSimpleName().endsWith("Controller"))
                .filter(javaClass -> !javaClass.isInterface())
                .filter(javaClass -> !javaClass.isAnonymousClass())
                .filter(javaClass -> !javaClass.getPackageName().contains("test"))
                .map(javaClass -> javaClass.getSimpleName().replace("Controller", ""))
                .collect(Collectors.toList());

        for (String viewModelName : viewModelNames) {
            assertTrue(
                    controllerNames.contains(viewModelName),
                    "ViewModel " + viewModelName + "ViewModel should have a corresponding " + viewModelName + "Controller"
            );
        }
    }
}
