package com.belman.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BaseViewModel class.
 */
public class BaseViewModelTest {

    private TestViewModel viewModel;

    @BeforeEach
    void setUp() {
        viewModel = new TestViewModel();
    }

    @Test
    @DisplayName("Should call onShow method")
    void shouldCallOnShowMethod() {
        // Act
        viewModel.onShow();

        // Assert
        assertTrue(viewModel.onShowCalled, "onShow method should be called");
    }

    @Test
    @DisplayName("Should call onHide method")
    void shouldCallOnHideMethod() {
        // Act
        viewModel.onHide();

        // Assert
        assertTrue(viewModel.onHideCalled, "onHide method should be called");
    }

    /**
     * Test implementation of BaseViewModel for testing purposes.
     */
    private static class TestViewModel extends BaseViewModel<ViewLifecycle> {
        boolean onShowCalled = false;
        boolean onHideCalled = false;

        @Override
        public void onShow() {
            super.onShow();
            onShowCalled = true;
        }

        @Override
        public void onHide() {
            super.onHide();
            onHideCalled = true;
        }
    }
}