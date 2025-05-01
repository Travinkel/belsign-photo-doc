package com.belman.integration.gluon;

import dev.stefan.athomefx.core.di.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the GluonViewLoader class.
 * Tests the complete flow from loading a view to lifecycle method triggering.
 */
@ExtendWith(MockitoExtension.class)
public class GluonViewLoaderIntegrationTest {

    @Mock
    private TestService mockService;

    private LoginView loginView;

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
    void shouldLoadViewAndTriggerLifecycle() {
        // Arrange - Create a mock view setup
        loginView = new LoginView();

        // Act - Simulate view showing
        loginView.show();

        // Assert
        // 1. Verify the view was loaded
        assertNotNull(loginView);

        // 2. Verify the controller was instantiated
        assertNotNull(loginView.getController());

        // 3. Verify the view model was instantiated
        assertNotNull(loginView.getViewModel());

        // 4. Verify service injection
        assertNotNull(loginView.getViewModel().getService());
        assertSame(mockService, loginView.getViewModel().getService());

        // 5. Verify lifecycle methods were called
        assertTrue(loginView.getViewModel().isOnShowCalled());

        // 6. Verify binding was initialized
        assertTrue(((TestController)loginView.getController()).isBindingInitialized());

        // Now hide the view
        loginView.hide();

        // 7. Verify onHide was called
        assertTrue(loginView.getViewModel().isOnHideCalled());
    }

    @Test
    void shouldVerifyCorrectInstancesAreInjected() {
        // Arrange
        loginView = new LoginView();

        // Act
        loginView.show();

        // Assert - Verify that the injected service is the exact registered instance
        assertSame(mockService, loginView.getViewModel().getService());
    }

}
