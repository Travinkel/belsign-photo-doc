package com.belman.unit.backbone.util;

import com.belman.infrastructure.NamingConventions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the NamingConventions class.
 */
public class NamingConventionsTest {

    @ParameterizedTest
    @ValueSource(strings = {"LoginView", "UserProfileView", "DashboardView", "View1"})
    void isValidViewName_withValidNames_shouldReturnTrue(String viewName) {
        // Act & Assert
        assertTrue(NamingConventions.isValidViewName(viewName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"loginView", "UserProfile", "Dashboard", "View", "", "123View"})
    void isValidViewName_withInvalidNames_shouldReturnFalse(String viewName) {
        // Act & Assert
        assertFalse(NamingConventions.isValidViewName(viewName));
    }

    @Test
    void isValidViewName_withNullName_shouldReturnFalse() {
        // Act & Assert
        assertFalse(NamingConventions.isValidViewName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"LoginController", "UserProfileController", "DashboardController", "Controller1"})
    void isValidControllerName_withValidNames_shouldReturnTrue(String controllerName) {
        // Act & Assert
        assertTrue(NamingConventions.isValidControllerName(controllerName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"loginController", "UserProfile", "Dashboard", "Controller", "", "123Controller"})
    void isValidControllerName_withInvalidNames_shouldReturnFalse(String controllerName) {
        // Act & Assert
        assertFalse(NamingConventions.isValidControllerName(controllerName));
    }

    @Test
    void isValidControllerName_withNullName_shouldReturnFalse() {
        // Act & Assert
        assertFalse(NamingConventions.isValidControllerName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"LoginViewModel", "UserProfileViewModel", "DashboardModel", "ViewModel1", "Model1"})
    void isValidViewModelName_withValidNames_shouldReturnTrue(String viewModelName) {
        // Act & Assert
        assertTrue(NamingConventions.isValidViewModelName(viewModelName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"loginViewModel", "UserProfile", "Dashboard", "ViewModel", "Model", "", "123ViewModel"})
    void isValidViewModelName_withInvalidNames_shouldReturnFalse(String viewModelName) {
        // Act & Assert
        assertFalse(NamingConventions.isValidViewModelName(viewModelName));
    }

    @Test
    void isValidViewModelName_withNullName_shouldReturnFalse() {
        // Act & Assert
        assertFalse(NamingConventions.isValidViewModelName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"LoginService", "UserProfileService", "DashboardService", "Service1"})
    void isValidServiceName_withValidNames_shouldReturnTrue(String serviceName) {
        // Act & Assert
        assertTrue(NamingConventions.isValidServiceName(serviceName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"loginService", "UserProfile", "Dashboard", "Service", "", "123Service"})
    void isValidServiceName_withInvalidNames_shouldReturnFalse(String serviceName) {
        // Act & Assert
        assertFalse(NamingConventions.isValidServiceName(serviceName));
    }

    @Test
    void isValidServiceName_withNullName_shouldReturnFalse() {
        // Act & Assert
        assertFalse(NamingConventions.isValidServiceName(null));
    }

    @ParameterizedTest
    @CsvSource({
        "LoginView,Login",
        "UserProfileController,UserProfile",
        "DashboardViewModel,Dashboard",
        "ProductModel,Product",
        "AuthService,Auth",
        "Plain,Plain"
    })
    void getBaseName_shouldExtractBaseNameCorrectly(String componentName, String expectedBaseName) {
        // Act
        String baseName = NamingConventions.getBaseName(componentName);
        
        // Assert
        assertEquals(expectedBaseName, baseName);
    }

    @Test
    void getBaseName_withNullName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getBaseName(null));
    }

    @Test
    void getViewName_shouldAppendViewSuffix() {
        // Act
        String viewName = NamingConventions.getViewName("Login");
        
        // Assert
        assertEquals("LoginView", viewName);
    }

    @Test
    void getViewName_withNullBaseName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getViewName(null));
    }

    @Test
    void getControllerName_shouldAppendControllerSuffix() {
        // Act
        String controllerName = NamingConventions.getControllerName("Login");
        
        // Assert
        assertEquals("LoginController", controllerName);
    }

    @Test
    void getControllerName_withNullBaseName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getControllerName(null));
    }

    @Test
    void getViewModelName_shouldAppendViewModelSuffix() {
        // Act
        String viewModelName = NamingConventions.getViewModelName("Login");
        
        // Assert
        assertEquals("LoginViewModel", viewModelName);
    }

    @Test
    void getViewModelName_withNullBaseName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getViewModelName(null));
    }

    @Test
    void getModelName_shouldAppendModelSuffix() {
        // Act
        String modelName = NamingConventions.getModelName("Login");
        
        // Assert
        assertEquals("LoginModel", modelName);
    }

    @Test
    void getModelName_withNullBaseName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getModelName(null));
    }

    @Test
    void getServiceName_shouldAppendServiceSuffix() {
        // Act
        String serviceName = NamingConventions.getServiceName("Login");
        
        // Assert
        assertEquals("LoginService", serviceName);
    }

    @Test
    void getServiceName_withNullBaseName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getServiceName(null));
    }

    @Test
    void getPossibleViewModelNames_shouldReturnBothViewModelAndModelNames() {
        // Act
        List<String> viewModelNames = NamingConventions.getPossibleViewModelNames("LoginView");
        
        // Assert
        assertEquals(2, viewModelNames.size());
        assertTrue(viewModelNames.contains("LoginViewModel"));
        assertTrue(viewModelNames.contains("LoginModel"));
    }

    @Test
    void getPossibleViewModelNames_withNullViewName_shouldReturnEmptyList() {
        // Act
        List<String> viewModelNames = NamingConventions.getPossibleViewModelNames(null);
        
        // Assert
        assertTrue(viewModelNames.isEmpty());
    }

    @Test
    void getPossibleViewModelClassNames_shouldReturnAllPossibleClassNames() {
        // Act
        List<String> classNames = NamingConventions.getPossibleViewModelClassNames(TestView.class);
        
        // Assert
        assertEquals(4, classNames.size());
        assertTrue(classNames.contains(TestView.class.getPackageName() + ".TestViewModel"));
        assertTrue(classNames.contains(TestView.class.getPackageName() + ".TestModel"));
        assertTrue(classNames.contains(TestView.class.getPackageName() + ".viewmodel.TestViewModel"));
        assertTrue(classNames.contains(TestView.class.getPackageName() + ".viewmodel.TestModel"));
    }

    @Test
    void getPossibleViewModelClassNames_withNullViewClass_shouldReturnEmptyList() {
        // Act
        List<String> classNames = NamingConventions.getPossibleViewModelClassNames(null);
        
        // Assert
        assertTrue(classNames.isEmpty());
    }

    @Test
    void getTemplateFileName_shouldAppendExtension() {
        // Act
        String fileName = NamingConventions.getTemplateFileName("LoginView", "template");
        
        // Assert
        assertEquals("LoginView.template", fileName);
    }

    @Test
    void getTemplateFileName_withNullViewName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getTemplateFileName(null, "template"));
    }

    @Test
    void getTemplateFileName_withNullExtension_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getTemplateFileName("LoginView", null));
    }

    @Test
    void getFxmlFileName_shouldAppendFxmlExtension() {
        // Act
        String fileName = NamingConventions.getFxmlFileName("LoginView");
        
        // Assert
        assertEquals("LoginView.fxml", fileName);
    }

    @Test
    void getFxmlFileName_withNullViewName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getFxmlFileName(null));
    }

    @Test
    void getCssFileName_shouldAppendCssExtension() {
        // Act
        String fileName = NamingConventions.getCssFileName("LoginView");
        
        // Assert
        assertEquals("LoginView.css", fileName);
    }

    @Test
    void getCssFileName_withNullViewName_shouldReturnNull() {
        // Act & Assert
        assertNull(NamingConventions.getCssFileName(null));
    }

    // Test class for getPossibleViewModelClassNames test
    static class TestView {
    }
}