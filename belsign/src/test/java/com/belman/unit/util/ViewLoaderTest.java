package dev.stefan.athomefx.core.util;

import com.belman.backbone.core.base.BaseController;
import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.lifecycle.ViewLifecycle;
import dev.stefan.athomefx.core.di.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockedStatic;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ViewLoader class.
 */
public class ViewLoaderTest {

    private TestController controller;

    @BeforeEach
    void setUp() {
        controller = new TestController();
    }

    @Test
    @DisplayName("Should set up view model and controller correctly")
    void shouldSetUpViewModelAndControllerCorrectly() throws Exception {
        // Arrange
        TestViewModel viewModel = new TestViewModel();
        
        // Use reflection to access the protected method
        Method setupViewModelMethod = ViewLoader.class.getDeclaredMethod("setupViewModel", 
                BaseController.class, BaseViewModel.class);
        setupViewModelMethod.setAccessible(true);
        
        // Mock the ServiceLocator.injectServices method
        try (MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {
            
            // Act
            setupViewModelMethod.invoke(null, controller, viewModel);
            
            // Assert
            mockedServiceLocator.verify(() -> ServiceLocator.injectServices(viewModel));
            assertSame(viewModel, controller.getViewModel(), "Controller should have the view model set");
            assertTrue(controller.initializeBindingCalled, "initializeBinding should be called");
        }
    }

    @Test
    @DisplayName("Should handle null controller gracefully")
    void shouldHandleNullControllerGracefully() throws Exception {
        // Arrange
        TestViewModel viewModel = new TestViewModel();
        
        // Use reflection to access the protected method
        Method setupViewModelMethod = ViewLoader.class.getDeclaredMethod("setupViewModel", 
                BaseController.class, BaseViewModel.class);
        setupViewModelMethod.setAccessible(true);
        
        // Mock the ServiceLocator.injectServices method
        try (MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {
            
            // Act & Assert - should not throw exception
            setupViewModelMethod.invoke(null, null, viewModel);
            
            // Verify
            mockedServiceLocator.verify(() -> ServiceLocator.injectServices(viewModel));
        }
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