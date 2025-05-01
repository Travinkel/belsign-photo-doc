package com.belman.integration.gluon;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.belman.backbone.core.base.BaseViewModel;
import dev.stefan.athomefx.core.di.ServiceLocator;
import com.belman.backbone.core.events.DomainEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Integration test for GluonView.
 * Tests the interaction between GluonView, Router, and GluonAPI.
 */
@ExtendWith(MockitoExtension.class)
public class GluonViewIntegrationTest {

    @Mock
    private MobileApplication mockMobileApplication;

    @Mock
    private DomainEventPublisher mockEventPublisher;

    @Mock
    private TestService mockService;

    @BeforeEach
    void setUp() {
        // Register the service
        ServiceLocator.registerService(TestService.class, mockService);
    }

    @AfterEach
    void tearDown() {
        // Clear the service locator
        ServiceLocator.clear();
    }

    @Test
    void shouldInjectServicesIntoViewModel() {
        // Arrange
        TestViewModel viewModel = new TestViewModel();
        
        // Act
        ServiceLocator.injectServices(viewModel);
        
        // Assert
        assertSame(mockService, viewModel.getService());
    }

    @Test
    void shouldCallLifecycleMethods() {
        // Arrange
        TestViewModel viewModel = new TestViewModel();
        ServiceLocator.injectServices(viewModel);
        
        // Act
        viewModel.onShow();
        
        // Assert
        assertTrue(viewModel.onShowCalled);
        
        // Act
        viewModel.onHide();
        
        // Assert
        assertTrue(viewModel.onHideCalled);
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
     * Test implementation of BaseViewModel for testing purposes.
     */
    public static class TestViewModel extends BaseViewModel<TestViewModel> {
        private TestService service;
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
        
        public TestService getService() {
            return service;
        }
        
        public void setService(TestService service) {
            this.service = service;
        }
    }
}