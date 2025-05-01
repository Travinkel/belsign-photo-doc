package dev.stefan.athomefx.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the NamingConventions class.
 */
public class NamingConventionsTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "LoginView", 
        "DashboardView", 
        "UserProfileView",
        "View1" // Special case for test data
    })
    @DisplayName("Should validate valid view names")
    void shouldValidateValidViewNames(String viewName) {
        assertTrue(NamingConventions.isValidViewName(viewName), 
                viewName + " should be a valid view name");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "loginView", // Doesn't start with uppercase
        "Dashboard", // Missing View suffix
        "View", // Just the suffix
        "123View", // Doesn't start with letter
        "" // Empty string
    })
    @DisplayName("Should invalidate invalid view names")
    void shouldInvalidateInvalidViewNames(String viewName) {
        assertFalse(NamingConventions.isValidViewName(viewName), 
                viewName + " should be an invalid view name");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "LoginController", 
        "DashboardController", 
        "UserProfileController",
        "Controller1" // Special case for test data
    })
    @DisplayName("Should validate valid controller names")
    void shouldValidateValidControllerNames(String controllerName) {
        assertTrue(NamingConventions.isValidControllerName(controllerName), 
                controllerName + " should be a valid controller name");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "loginController", // Doesn't start with uppercase
        "Dashboard", // Missing Controller suffix
        "Controller", // Just the suffix
        "123Controller", // Doesn't start with letter
        "" // Empty string
    })
    @DisplayName("Should invalidate invalid controller names")
    void shouldInvalidateInvalidControllerNames(String controllerName) {
        assertFalse(NamingConventions.isValidControllerName(controllerName), 
                controllerName + " should be an invalid controller name");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "LoginViewModel", 
        "DashboardViewModel", 
        "UserProfileViewModel",
        "LoginModel", // Model suffix is also valid
        "DashboardModel",
        "ViewModel1", // Special case for test data
        "Model1" // Special case for test data
    })
    @DisplayName("Should validate valid view model names")
    void shouldValidateValidViewModelNames(String viewModelName) {
        assertTrue(NamingConventions.isValidViewModelName(viewModelName), 
                viewModelName + " should be a valid view model name");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "loginViewModel", // Doesn't start with uppercase
        "Dashboard", // Missing ViewModel suffix
        "ViewModel", // Just the suffix
        "Model", // Just the suffix
        "123ViewModel", // Doesn't start with letter
        "" // Empty string
    })
    @DisplayName("Should invalidate invalid view model names")
    void shouldInvalidateInvalidViewModelNames(String viewModelName) {
        assertFalse(NamingConventions.isValidViewModelName(viewModelName), 
                viewModelName + " should be an invalid view model name");
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void shouldHandleNullInputGracefully() {
        assertFalse(NamingConventions.isValidViewName(null), 
                "Null should be an invalid view name");
        assertFalse(NamingConventions.isValidControllerName(null), 
                "Null should be an invalid controller name");
        assertFalse(NamingConventions.isValidViewModelName(null), 
                "Null should be an invalid view model name");
    }
}