package com.belman.unit.backbone.core.base;

import com.belman.backbone.core.base.BaseController;
import com.belman.backbone.core.base.BaseViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the BaseController class.
 * These tests verify that the BaseController correctly implements the ControllerLifecycle
 * interface and handles view model binding.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseControllerTest {

    // Test implementation of BaseViewModel
    private static class TestViewModel extends BaseViewModel<Object> {
    }

    // Test implementation of BaseController
    private static class TestController extends BaseController<TestViewModel> {
        private final AtomicBoolean onShowCalled = new AtomicBoolean(false);
        private final AtomicBoolean onHideCalled = new AtomicBoolean(false);
        private final AtomicBoolean initializeBindingCalled = new AtomicBoolean(false);

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
        public void initializeBinding() {
            super.initializeBinding();
            initializeBindingCalled.set(true);
        }

        public boolean wasOnShowCalled() {
            return onShowCalled.get();
        }

        public boolean wasOnHideCalled() {
            return onHideCalled.get();
        }

        public boolean wasInitializeBindingCalled() {
            return initializeBindingCalled.get();
        }

        public void reset() {
            onShowCalled.set(false);
            onHideCalled.set(false);
            initializeBindingCalled.set(false);
        }
    }

    @Mock
    private TestViewModel mockViewModel;

    private TestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new TestController();
        controller.reset();
    }

    @Test
    void setViewModel_shouldStoreViewModel() {
        // Act
        controller.setViewModel(mockViewModel);

        // Assert
        assertSame(mockViewModel, controller.getViewModel(), "ViewModel should be stored");
    }

    @Test
    void onShow_shouldSetFlag() {
        // Act
        controller.onShow();

        // Assert
        assertTrue(controller.wasOnShowCalled(), "onShow should have been called");
    }

    @Test
    void onHide_shouldSetFlag() {
        // Act
        controller.onHide();

        // Assert
        assertTrue(controller.wasOnHideCalled(), "onHide should have been called");
    }

    @Test
    void initializeBinding_shouldSetFlag() {
        // Act
        controller.initializeBinding();

        // Assert
        assertTrue(controller.wasInitializeBindingCalled(), "initializeBinding should have been called");
    }

    @Test
    void baseController_shouldImplementControllerLifecycle() {
        // Assert
        assertTrue(controller instanceof com.belman.backbone.core.lifecycle.ControllerLifecycle,
                "BaseController should implement ControllerLifecycle");
    }

    @Test
    void getViewModel_withNoViewModelSet_shouldReturnNull() {
        // Act & Assert
        assertNull(controller.getViewModel(), "getViewModel should return null when no ViewModel is set");
    }

    @Test
    void getViewModel_withViewModelSet_shouldReturnViewModel() {
        // Arrange
        controller.setViewModel(mockViewModel);

        // Act & Assert
        assertSame(mockViewModel, controller.getViewModel(), "getViewModel should return the set ViewModel");
    }
}