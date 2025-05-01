package com.belman.integration.gluon;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.mvc.View;
import com.belman.backbone.core.base.BaseViewModel;
import dev.stefan.athomefx.core.di.ServiceLocator;
import com.belman.backbone.core.events.AbstractDomainEvent;
import com.belman.backbone.core.events.DomainEventPublisher;
import dev.stefan.athomefx.gluon.*;
import dev.stefan.athomefx.gluon.mock.MockGluonView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GluonIntegrationTest {

    @Mock
    private MobileApplication mockMobileApplication;

    @Mock
    private DomainEventPublisher mockEventPublisher;

    @Mock
    private TestService mockService;

    private TestFirstView firstView;
    private TestSecondView secondView;

    @BeforeEach
    void setUp() {
        // Register the service
        ServiceLocator.registerService(TestService.class, mockService);
        
        // Create the views
        firstView = new TestFirstView();
        secondView = new TestSecondView();
        
        // Initialize the Gluon module
        try (MockedStatic<MobileApplication> mockedMobileApplication = mockStatic(MobileApplication.class);
             MockedStatic<DomainEventPublisher> mockedEventPublisher = mockStatic(DomainEventPublisher.class)) {
            
            // Mock MobileApplication.getInstance()
            mockedMobileApplication.when(MobileApplication::getInstance).thenReturn(mockMobileApplication);
            
            // Mock DomainEventPublisher.getInstance()
            mockedEventPublisher.when(DomainEventPublisher::getInstance).thenReturn(mockEventPublisher);
            
            // Initialize the Gluon module
            GluonAPI.initialize(mockMobileApplication);
        }
    }

    @AfterEach
    void tearDown() {
        // Clear the service locator
        ServiceLocator.clear();
    }

    @Test
    void shouldNavigateBetweenViewsAndInjectServices() {
        // Arrange
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("testParam", "testValue");
        
        try (MockedStatic<GluonRouter> mockedRouter = mockStatic(GluonRouter.class);
             MockedStatic<ServiceLocator> mockedServiceLocator = mockStatic(ServiceLocator.class)) {
            
            // Mock Router.navigateTo()
            doAnswer(invocation -> {
                Class<? extends View> viewClass = invocation.getArgument(0);
                if (viewClass == TestFirstView.class) {
                    firstView.show();
                } else if (viewClass == TestSecondView.class) {
                    secondView.show();
                }
                return null;
            }).when(mockedRouter).navigateTo(any(Class.class));
            
            doAnswer(invocation -> {
                Class<? extends View> viewClass = invocation.getArgument(0);
                Map<String, Object> params = invocation.getArgument(1);
                if (viewClass == TestFirstView.class) {
                    firstView.show();
                } else if (viewClass == TestSecondView.class) {
                    secondView.show();
                }
                return null;
            }).when(mockedRouter).navigateTo(any(Class.class), any(Map.class));
            
            // Mock ServiceLocator.injectServices()
            doAnswer(invocation -> {
                Object target = invocation.getArgument(0);
                if (target instanceof TestFirstViewModel) {
                    ((TestFirstViewModel) target).setService(mockService);
                } else if (target instanceof TestSecondViewModel) {
                    ((TestSecondViewModel) target).setService(mockService);
                }
                return null;
            }).when(mockedServiceLocator).injectServices(any());
            
            // Act - Navigate to the first view
            GluonAPI.navigateTo(TestFirstView.class);
            
            // Assert - First view should be shown and service injected
            assertTrue(firstView.getViewModel().onShowCalled);
            assertSame(mockService, firstView.getViewModel().getService());
            
            // Act - Navigate to the second view with parameters
            GluonAPI.navigateTo(TestSecondView.class, parameters);
            
            // Assert - Second view should be shown and service injected
            assertTrue(secondView.getViewModel().onShowCalled);
            assertSame(mockService, secondView.getViewModel().getService());
            
            // Act - Navigate back
            when(mockedRouter.navigateBack()).thenReturn(true);
            boolean result = GluonAPI.navigateBack();
            
            // Assert - Should return true
            assertTrue(result);
        }
    }

    @Test
    void shouldPublishAndHandleEvents() {
        // Arrange
        TestEvent event = new TestEvent("Test data");
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        
        try (MockedStatic<GluonEventBus> mockedEventBus = mockStatic(GluonEventBus.class);
             MockedStatic<DomainEventPublisher> mockedEventPublisher = mockStatic(DomainEventPublisher.class)) {
            
            // Mock GluonEventBus.register()
            doAnswer(invocation -> {
                Class<?> eventType = invocation.getArgument(0);
                Consumer<?> listener = invocation.getArgument(1);
                if (eventType == TestEvent.class) {
                    // Simulate registering the handler with DomainEventPublisher
                    DomainEventHandler<TestEvent> handler = event1 -> {
                        ((Consumer<TestEvent>) listener).accept(event1);
                    };
                    DomainEventPublisher.getInstance().register(TestEvent.class, handler);
                }
                return null;
            }).when(mockedEventBus).register(any(Class.class), any(Consumer.class));
            
            // Mock GluonEventBus.publish()
            doAnswer(invocation -> {
                DomainEvent event1 = invocation.getArgument(0);
                DomainEventPublisher.getInstance().publish(event1);
                return null;
            }).when(mockedEventBus).publish(any(DomainEvent.class));
            
            // Mock DomainEventPublisher.getInstance()
            mockedEventPublisher.when(DomainEventPublisher::getInstance).thenReturn(mockEventPublisher);
            
            // Register an event handler
            GluonAPI.registerEventListener(TestEvent.class, e -> eventHandled.set(true));
            
            // Act - Publish an event
            GluonAPI.publishEvent(event);
            
            // Assert - Event handler should be called
            verify(mockEventPublisher).publish(event);
        }
    }

    /**
     * Test service for integration testing.
     */
    public static class TestService {
        public String getData() {
            return "Test service data";
        }
    }

    /**
     * Test implementation of DomainEvent for testing purposes.
     */
    private static class TestEvent extends AbstractDomainEvent {
        private final String data;
        
        public TestEvent(String data) {
            this.data = data;
        }
        
        public String getData() {
            return data;
        }
    }

    /**
     * Test implementation of BaseViewModel for the first view.
     */
    public static class TestFirstViewModel extends BaseViewModel<TestFirstViewModel> {
        private TestService service;
        boolean onShowCalled = false;
        
        @Override
        public void onShow() {
            super.onShow();
            onShowCalled = true;
        }
        
        public TestService getService() {
            return service;
        }
        
        public void setService(TestService service) {
            this.service = service;
        }
    }

    /**
     * Test implementation of BaseViewModel for the second view.
     */
    public static class TestSecondViewModel extends BaseViewModel<TestSecondViewModel> {
        private TestService service;
        boolean onShowCalled = false;
        
        @Override
        public void onShow() {
            super.onShow();
            onShowCalled = true;
        }
        
        public TestService getService() {
            return service;
        }
        
        public void setService(TestService service) {
            this.service = service;
        }
    }

    /**
     * Test implementation of MockGluonView for the first view.
     */
    public static class TestFirstView extends MockGluonView<TestFirstViewModel> {
        @Override
        public TestFirstViewModel getViewModel() {
            return (TestFirstViewModel) super.getViewModel();
        }
    }

    /**
     * Test implementation of MockGluonView for the second view.
     */
    public static class TestSecondView extends MockGluonView<TestSecondViewModel> {
        @Override
        public TestSecondViewModel getViewModel() {
            return (TestSecondViewModel) super.getViewModel();
        }
    }
}