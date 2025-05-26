package com.belman.unit.application.usecase.photo;

import com.belman.application.usecase.photo.DefaultPhotoTemplateService;
import com.belman.dataaccess.repository.memory.InMemoryPhotoTemplateRepository;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the interaction between DefaultPhotoTemplateService and InMemoryPhotoTemplateRepository.
 * Specifically tests the fix for the issue where templates are not visible for orders in dev/test mode.
 */
public class DefaultPhotoTemplateServiceInMemoryTest {

    private DefaultPhotoTemplateService photoTemplateService;
    private InMemoryPhotoTemplateRepository photoTemplateRepository;
    private OrderRepository orderRepository;
    private PhotoRepository photoRepository;
    private UserRepository userRepository;
    private LoggerFactory loggerFactory;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        // Create mocks for everything except the PhotoTemplateRepository
        orderRepository = Mockito.mock(OrderRepository.class);
        photoRepository = Mockito.mock(PhotoRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        loggerFactory = Mockito.mock(LoggerFactory.class);
        logger = Mockito.mock(Logger.class);

        // Set up logger factory
        when(loggerFactory.getLogger(any())).thenReturn(logger);

        // Create a real InMemoryPhotoTemplateRepository
        photoTemplateRepository = new InMemoryPhotoTemplateRepository(loggerFactory);

        // Create service with real repository
        photoTemplateService = new DefaultPhotoTemplateService(
                orderRepository,
                photoRepository,
                photoTemplateRepository,
                userRepository,
                loggerFactory
        );
    }

    @Test
    public void testGetAvailableTemplates_WithInMemoryRepository_AlwaysReturnsTemplates() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        System.out.println("[DEBUG_LOG] Testing with order ID: " + orderId.id());

        // Act
        List<PhotoTemplate> templates = photoTemplateService.getAvailableTemplates(orderId);

        // Assert
        assertNotNull(templates, "Templates list should not be null");
        assertFalse(templates.isEmpty(), "Templates list should not be empty");
        System.out.println("[DEBUG_LOG] Found " + templates.size() + " templates for order");

        // Log each template
        for (int i = 0; i < templates.size(); i++) {
            PhotoTemplate template = templates.get(i);
            System.out.println("[DEBUG_LOG] Template " + (i+1) + ": " + 
                              "Name=" + template.name() + ", " +
                              "Description=" + template.description());
        }
    }

    @Test
    public void testGetAvailableTemplates_WithInvalidTemplateName_StillReturnsTemplates() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        System.out.println("[DEBUG_LOG] Testing with order ID: " + orderId.id());

        // Try to associate an invalid template name with the order
        // This would normally fail silently in the InMemoryPhotoTemplateRepository
        boolean result = photoTemplateRepository.associateWithOrder(orderId, "INVALID_TEMPLATE_NAME", true);
        System.out.println("[DEBUG_LOG] Result of associating invalid template: " + result);
        
        // Act
        List<PhotoTemplate> templates = photoTemplateService.getAvailableTemplates(orderId);

        // Assert
        assertNotNull(templates, "Templates list should not be null");
        assertFalse(templates.isEmpty(), "Templates list should not be empty");
        System.out.println("[DEBUG_LOG] Found " + templates.size() + " templates for order");

        // Log each template
        for (int i = 0; i < templates.size(); i++) {
            PhotoTemplate template = templates.get(i);
            System.out.println("[DEBUG_LOG] Template " + (i+1) + ": " + 
                              "Name=" + template.name() + ", " +
                              "Description=" + template.description());
        }
    }
}