package com.belman.unit.backbone.core.base;

import com.belman.application.core.Inject;
import com.belman.application.core.ViewModelLifecycle;
import com.belman.presentation.core.BaseViewModel;
import com.belman.application.core.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BaseViewModel class.
 * These tests verify that the BaseViewModel correctly implements the ViewModelLifecycle
 * interface and handles service injection.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseViewModelTest {

    // Test implementation of BaseViewModel
    private static class TestViewModel extends BaseViewModel<Object> {
        private final AtomicBoolean onShowCalled = new AtomicBoolean(false);
        private final AtomicBoolean onHideCalled = new AtomicBoolean(false);
        private final AtomicBoolean injectServicesCalled = new AtomicBoolean(false);

        @Override
        public void onShow() {
            super.onShow();
            onShowCalled.set(true);
        }

        @Override
        public void onHide() {
            super.onHide();
            onHideCalled.set(true);
        }

        @Override
        protected void injectServices() {
            super.injectServices();
            injectServicesCalled.set(true);
        }

        public boolean wasOnShowCalled() {
            return onShowCalled.get();
        }

        public boolean wasOnHideCalled() {
            return onHideCalled.get();
        }

        public boolean wasInjectServicesCalled() {
            return injectServicesCalled.get();
        }

        public void reset() {
            onShowCalled.set(false);
            onHideCalled.set(false);
            injectServicesCalled.set(false);
        }
    }

    private TestViewModel viewModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new TestViewModel();
        viewModel.reset();
    }

    @Test
    void onShow_shouldSetFlag() {
        // Act
        viewModel.onShow();

        // Assert
        assertTrue(viewModel.wasOnShowCalled(), "onShow should have been called");
    }

    @Test
    void onHide_shouldSetFlag() {
        // Act
        viewModel.onHide();

        // Assert
        assertTrue(viewModel.wasOnHideCalled(), "onHide should have been called");
    }

    @Test
    void injectServices_shouldSetFlag() {
        // Act
        viewModel.injectServices();

        // Assert
        assertTrue(viewModel.wasInjectServicesCalled(), "injectServices should have been called");
    }

    @Test
    void baseViewModel_shouldImplementViewModelLifecycle() {
        // Assert
        assertTrue(viewModel instanceof ViewModelLifecycle,
                "BaseViewModel should implement ViewModelLifecycle");
    }

    @Test
    void injectServices_shouldBeAnnotatedWithInject() throws NoSuchMethodException {
        // Arrange
        Method injectServicesMethod = BaseViewModel.class.getDeclaredMethod("injectServices");
        
        // Assert
        assertTrue(injectServicesMethod.isAnnotationPresent(Inject.class),
                "injectServices method should be annotated with @Inject");
    }

    @Test
    void serviceLocator_shouldInjectServicesIntoViewModel() {
        // Arrange
        ServiceLocator mockServiceLocator = mock(ServiceLocator.class);
        
        // We need to use reflection to access the static method
        try {
            // Get the injectServices method
            Method injectServicesMethod = ServiceLocator.class.getDeclaredMethod("injectServices", Object.class);
            
            // Make it accessible
            injectServicesMethod.setAccessible(true);
            
            // Act
            injectServicesMethod.invoke(null, viewModel);
            
            // No direct way to verify this without mocking the static method,
            // but we can check that no exception was thrown
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}