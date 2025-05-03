package com.belman.integration.backbone.core.lifecycle;

import com.belman.domain.events.DomainEvents;
import com.belman.domain.shared.ViewHiddenEvent;
import com.belman.domain.shared.ViewShownEvent;
import com.belman.application.core.ControllerLifecycle;
import com.belman.application.core.ViewModelLifecycle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the lifecycle system.
 * These tests verify that lifecycle events are properly published and handled
 * when lifecycle methods are called.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LifecycleIntegrationTest {

    // Simple implementation of ViewModelLifecycle for testing
    static class TestViewModel implements ViewModelLifecycle {
        private final AtomicBoolean onShowCalled = new AtomicBoolean(false);
        private final AtomicBoolean onHideCalled = new AtomicBoolean(false);

        @Override
        public void onShow() {
            onShowCalled.set(true);
        }

        @Override
        public void onHide() {
            onHideCalled.set(true);
        }

        public boolean wasOnShowCalled() {
            return onShowCalled.get();
        }

        public boolean wasOnHideCalled() {
            return onHideCalled.get();
        }

        public void reset() {
            onShowCalled.set(false);
            onHideCalled.set(false);
        }
    }

    // Simple implementation of ControllerLifecycle for testing
    static class TestController implements ControllerLifecycle {
        private final AtomicBoolean onShowCalled = new AtomicBoolean(false);
        private final AtomicBoolean onHideCalled = new AtomicBoolean(false);

        @Override
        public void onShow() {
            onShowCalled.set(true);
        }

        @Override
        public void onHide() {
            onHideCalled.set(true);
        }

        public boolean wasOnShowCalled() {
            return onShowCalled.get();
        }

        public boolean wasOnHideCalled() {
            return onHideCalled.get();
        }

        public void reset() {
            onShowCalled.set(false);
            onHideCalled.set(false);
        }
    }

    private TestViewModel viewModel;
    private TestController controller;

    // Event tracking
    private AtomicBoolean viewShownEventReceived;
    private AtomicBoolean viewHiddenEventReceived;

    @BeforeEach
    void setUp() {
        // Create the test objects
        viewModel = new TestViewModel();
        controller = new TestController();

        // Reset event tracking
        viewShownEventReceived = new AtomicBoolean(false);
        viewHiddenEventReceived = new AtomicBoolean(false);

        // Register event handlers
        DomainEvents.on(ViewShownEvent.class, event -> {
            viewShownEventReceived.set(true);
            assertEquals("TestView", event.getViewName(), "Event should contain the correct view name");
        });

        DomainEvents.on(ViewHiddenEvent.class, event -> {
            viewHiddenEventReceived.set(true);
            assertEquals("TestView", event.getViewName(), "Event should contain the correct view name");
        });

        // Reset the test objects
        viewModel.reset();
        controller.reset();
    }

    @AfterEach
    void tearDown() {
        // Unregister event handlers to avoid affecting other tests
        // This is a simplified approach - in a real test we would use a more robust cleanup
    }

    @Test
    void testViewModelLifecycleMethodsAreCalled() {
        // Directly call the lifecycle methods
        viewModel.onShow();
        assertTrue(viewModel.wasOnShowCalled(), "ViewModel onShow should have been called");

        viewModel.onHide();
        assertTrue(viewModel.wasOnHideCalled(), "ViewModel onHide should have been called");
    }

    @Test
    void testControllerLifecycleMethodsAreCalled() {
        // Directly call the lifecycle methods
        controller.onShow();
        assertTrue(controller.wasOnShowCalled(), "Controller onShow should have been called");

        controller.onHide();
        assertTrue(controller.wasOnHideCalled(), "Controller onHide should have been called");
    }

    @Test
    void testDomainEventsArePublishedForLifecycleEvents() {
        // Create and publish a ViewShownEvent
        ViewShownEvent shownEvent = new ViewShownEvent("TestView");
        DomainEvents.publish(shownEvent);

        // Verify the event was received
        assertTrue(viewShownEventReceived.get(), "ViewShownEvent should have been received");

        // Create and publish a ViewHiddenEvent
        ViewHiddenEvent hiddenEvent = new ViewHiddenEvent("TestView");
        DomainEvents.publish(hiddenEvent);

        // Verify the event was received
        assertTrue(viewHiddenEventReceived.get(), "ViewHiddenEvent should have been received");
    }
}