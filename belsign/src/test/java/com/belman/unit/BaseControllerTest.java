package com.belman.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BaseController class.
 */
public class BaseControllerTest {

    private TestController controller;
    private TestViewModel viewModel;

    @BeforeEach
    void setUp() {
        controller = new TestController();
        viewModel = new TestViewModel();
    }

    @Test
    @DisplayName("Should set and get ViewModel correctly")
    void shouldSetAndGetViewModelCorrectly() {
        // Act
        controller.setViewModel(viewModel);
        
        // Assert
        assertSame(viewModel, controller.getViewModel(), "The retrieved ViewModel should be the same as the one set");
    }

    @Test
    @DisplayName("Should call initializeBinding method")
    void shouldCallInitializeBindingMethod() {
        // Act
        controller.initializeBinding();
        
        // Assert
        assertTrue(controller.initializeBindingCalled, "initializeBinding method should be called");
    }

    /**
     * Test implementation of BaseViewModel for testing purposes.
     */
    private static class TestViewModel extends BaseViewModel<ViewLifecycle> {
        // Simple test implementation
    }

    /**
     * Test implementation of BaseController for testing purposes.
     */
    private static class TestController extends BaseController<TestViewModel> {
        boolean initializeBindingCalled = false;

        @Override
        public void initializeBinding() {
            super.initializeBinding();
            initializeBindingCalled = true;
        }
    }
}