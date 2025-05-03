package com.belman.unit.backbone.api;

import com.belman.application.api.CoreAPI;
import com.belman.application.core.SessionManager;
import com.belman.domain.aggregates.User;
import com.belman.domain.services.AuthenticationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the session management functionality in the CoreAPI class.
 */
public class CoreAPISessionTest {

    @Mock
    private AuthenticationService authenticationService;
    
    @Mock
    private User mockUser;
    
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Close the mocks
        closeable.close();
        
        // Reset the SessionManager singleton
        // This is a bit of a hack, but necessary for testing singletons
        try {
            java.lang.reflect.Field instance = SessionManager.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void initializeSessionManager_shouldReturnSessionManager() {
        // Act
        SessionManager sessionManager = CoreAPI.initializeSessionManager(authenticationService);
        
        // Assert
        assertNotNull(sessionManager);
    }
    
    @Test
    void getSessionManager_afterInitialization_shouldReturnSessionManager() {
        // Arrange
        CoreAPI.initializeSessionManager(authenticationService);
        
        // Act
        SessionManager sessionManager = CoreAPI.getSessionManager();
        
        // Assert
        assertNotNull(sessionManager);
    }
    
    @Test
    void getCurrentUser_whenUserLoggedIn_shouldReturnUser() {
        // Arrange
        when(authenticationService.getCurrentUser()).thenReturn(Optional.of(mockUser));
        CoreAPI.initializeSessionManager(authenticationService);
        
        // Act
        Optional<User> result = CoreAPI.getCurrentUser();
        
        // Assert
        assertTrue(result.isPresent());
        assertSame(mockUser, result.get());
    }
    
    @Test
    void getCurrentUser_whenNoUserLoggedIn_shouldReturnEmpty() {
        // Arrange
        when(authenticationService.getCurrentUser()).thenReturn(Optional.empty());
        CoreAPI.initializeSessionManager(authenticationService);
        
        // Act
        Optional<User> result = CoreAPI.getCurrentUser();
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void getCurrentUser_whenSessionManagerNotInitialized_shouldThrowException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            CoreAPI.getCurrentUser();
        });
        
        assertTrue(exception.getMessage().contains("not been initialized"));
    }
    
    @Test
    void isLoggedIn_whenUserLoggedIn_shouldReturnTrue() {
        // Arrange
        when(authenticationService.isLoggedIn()).thenReturn(true);
        CoreAPI.initializeSessionManager(authenticationService);
        
        // Act
        boolean result = CoreAPI.isLoggedIn();
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void isLoggedIn_whenNoUserLoggedIn_shouldReturnFalse() {
        // Arrange
        when(authenticationService.isLoggedIn()).thenReturn(false);
        CoreAPI.initializeSessionManager(authenticationService);
        
        // Act
        boolean result = CoreAPI.isLoggedIn();
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void login_withValidCredentials_shouldReturnUser() {
        // Arrange
        when(authenticationService.authenticate("testuser", "password"))
            .thenReturn(Optional.of(mockUser));
        CoreAPI.initializeSessionManager(authenticationService);
        
        // Act
        Optional<User> result = CoreAPI.login("testuser", "password");
        
        // Assert
        assertTrue(result.isPresent());
        assertSame(mockUser, result.get());
    }
    
    @Test
    void login_withInvalidCredentials_shouldReturnEmpty() {
        // Arrange
        when(authenticationService.authenticate("testuser", "wrongpassword"))
            .thenReturn(Optional.empty());
        CoreAPI.initializeSessionManager(authenticationService);
        
        // Act
        Optional<User> result = CoreAPI.login("testuser", "wrongpassword");
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void logout_shouldCallAuthenticationService() {
        // Arrange
        CoreAPI.initializeSessionManager(authenticationService);
        
        // Act
        CoreAPI.logout();
        
        // Assert
        verify(authenticationService).logout();
    }
}