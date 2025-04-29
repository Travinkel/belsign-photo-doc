package unit.athomefx.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import util.NamingConventions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NamingConventionsTest {

    @ParameterizedTest
    @ValueSource(strings = {"LoginView", "UserProfileView", "DashboardView", "View1"})
    void isValidViewName_shouldReturnTrueForValidNames(String viewName) {
        assertTrue(NamingConventions.isValidViewName(viewName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"loginView", "UserProfile", "Dashboard-View", "1View", ""})
    void isValidViewName_shouldReturnFalseForInvalidNames(String viewName) {
        assertFalse(NamingConventions.isValidViewName(viewName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"LoginController", "UserProfileController", "DashboardController", "Controller1"})
    void isValidControllerName_shouldReturnTrueForValidNames(String controllerName) {
        assertTrue(NamingConventions.isValidControllerName(controllerName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"loginController", "UserProfile", "Dashboard-Controller", "1Controller", ""})
    void isValidControllerName_shouldReturnFalseForInvalidNames(String controllerName) {
        assertFalse(NamingConventions.isValidControllerName(controllerName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"LoginViewModel", "UserProfileModel", "DashboardViewModel", "ViewModel1", "Model1"})
    void isValidViewModelName_shouldReturnTrueForValidNames(String viewModelName) {
        assertTrue(NamingConventions.isValidViewModelName(viewModelName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"loginViewModel", "UserProfile", "Dashboard-ViewModel", "1ViewModel", ""})
    void isValidViewModelName_shouldReturnFalseForInvalidNames(String viewModelName) {
        assertFalse(NamingConventions.isValidViewModelName(viewModelName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"LoginService", "UserProfileService", "DashboardService", "Service1"})
    void isValidServiceName_shouldReturnTrueForValidNames(String serviceName) {
        assertTrue(NamingConventions.isValidServiceName(serviceName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"loginService", "UserProfile", "Dashboard-Service", "1Service", ""})
    void isValidServiceName_shouldReturnFalseForInvalidNames(String serviceName) {
        assertFalse(NamingConventions.isValidServiceName(serviceName));
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
    void getBaseName_shouldReturnCorrectBaseName(String componentName, String expectedBaseName) {
        assertEquals(expectedBaseName, NamingConventions.getBaseName(componentName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Login", "UserProfile", "Dashboard", "Product"})
    void getViewName_shouldAppendViewSuffix(String baseName) {
        assertEquals(baseName + "View", NamingConventions.getViewName(baseName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Login", "UserProfile", "Dashboard", "Product"})
    void getControllerName_shouldAppendControllerSuffix(String baseName) {
        assertEquals(baseName + "Controller", NamingConventions.getControllerName(baseName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Login", "UserProfile", "Dashboard", "Product"})
    void getViewModelName_shouldAppendViewModelSuffix(String baseName) {
        assertEquals(baseName + "ViewModel", NamingConventions.getViewModelName(baseName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Login", "UserProfile", "Dashboard", "Product"})
    void getModelName_shouldAppendModelSuffix(String baseName) {
        assertEquals(baseName + "Model", NamingConventions.getModelName(baseName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Login", "UserProfile", "Dashboard", "Product"})
    void getServiceName_shouldAppendServiceSuffix(String baseName) {
        assertEquals(baseName + "Service", NamingConventions.getServiceName(baseName));
    }

    @Test
    void getPossibleViewModelNames_shouldReturnAllPossibilities() {
        List<String> names = NamingConventions.getPossibleViewModelNames("LoginView");
        assertEquals(2, names.size());
        assertTrue(names.contains("LoginViewModel"));
        assertTrue(names.contains("LoginModel"));
    }

    @Test
    void getPossibleViewModelClassNames_shouldReturnAllPossibilities() {
        List<String> names = NamingConventions.getPossibleViewModelClassNames(DummyView.class);
        assertEquals(4, names.size());
        assertTrue(names.contains("unit.athomefx.util.DummyViewModel"));
        assertTrue(names.contains("unit.athomefx.util.DummyModel"));
        assertTrue(names.contains("unit.athomefx.util.viewmodel.DummyViewModel"));
        assertTrue(names.contains("unit.athomefx.util.viewmodel.DummyModel"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"LoginView", "UserProfileView", "DashboardView"})
    void getFxmlFileName_shouldAppendFxmlExtension(String viewName) {
        assertEquals(viewName + ".fxml", NamingConventions.getFxmlFileName(viewName));
    }

    // Dummy class for testing
    private static class DummyView {}
}