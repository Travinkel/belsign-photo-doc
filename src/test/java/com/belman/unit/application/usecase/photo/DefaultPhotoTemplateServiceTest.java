package com.belman.unit.application.usecase.photo;

import com.belman.application.usecase.photo.DefaultPhotoTemplateService;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.photo.PhotoTemplateRepository;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the DefaultPhotoTemplateService class.
 */
public class DefaultPhotoTemplateServiceTest {

    private DefaultPhotoTemplateService photoTemplateService;
    private OrderRepository orderRepository;
    private PhotoRepository photoRepository;
    private PhotoTemplateRepository photoTemplateRepository;
    private UserRepository userRepository;
    private LoggerFactory loggerFactory;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        // Create mocks
        orderRepository = Mockito.mock(OrderRepository.class);
        photoRepository = Mockito.mock(PhotoRepository.class);
        photoTemplateRepository = Mockito.mock(PhotoTemplateRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        loggerFactory = Mockito.mock(LoggerFactory.class);
        logger = Mockito.mock(Logger.class);

        // Set up logger factory
        when(loggerFactory.getLogger(any())).thenReturn(logger);

        // Create service
        photoTemplateService = new DefaultPhotoTemplateService(
                orderRepository,
                photoRepository,
                photoTemplateRepository,
                userRepository,
                loggerFactory
        );
    }

    @Test
    public void testGetAvailableTemplates_NoTemplatesFound_CreatesAndAssociatesDefaultTemplates() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());

        // Create a real QA user with username "qa_user"
        Username qaUsername = new Username("qa_user");
        HashedPassword password = new HashedPassword("$2a$10$ReM2gCw1o9rZz/ctET48N.XCmTxSKFcQvwNaqtjCSZxGr78adkX5u"); // Standard test password
        EmailAddress email = new EmailAddress("qa_user@belman.com");

        // Create a real UserBusiness instance
        UserBusiness qaUser = UserBusiness.createNewUser(qaUsername, password, email);

        // Mock the userRepository to return the real user when findByUsername is called with "qa_user"
        when(userRepository.findByUsername(eq(new Username("qa_user")))).thenReturn(Optional.of(qaUser));

        // Mock empty templates list
        when(photoTemplateRepository.findByOrderId(orderId)).thenReturn(Collections.emptyList());

        // Mock successful template association
        when(photoTemplateRepository.associateWithOrder(eq(orderId), any(), eq(true))).thenReturn(true);

        // Act
        System.out.println("[DEBUG_LOG] Calling getAvailableTemplates for order ID: " + orderId.id());
        List<PhotoTemplate> templates = photoTemplateService.getAvailableTemplates(orderId);

        // Assert
        assertNotNull(templates, "Templates list should not be null");
        assertFalse(templates.isEmpty(), "Templates list should not be empty");
        assertEquals(10, templates.size(), "Templates list should contain 10 default templates");

        // Verify that findByOrderId was called
        verify(photoTemplateRepository).findByOrderId(orderId);

        // Verify that findByUsername was called with "qa_user"
        verify(userRepository).findByUsername(new Username("qa_user"));

        // Verify that associateWithOrder was called for each template
        verify(photoTemplateRepository, times(10)).associateWithOrder(eq(orderId), any(), eq(true));

        System.out.println("[DEBUG_LOG] Test passed: Default templates were created and associated with the order");
    }

    @Test
    public void testGetAvailableTemplates_TemplatesAlreadyAssociated_ReturnsExistingTemplates() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());

        // Create a list of templates
        List<PhotoTemplate> existingTemplates = List.of(
            PhotoTemplate.TOP_VIEW_OF_JOINT,
            PhotoTemplate.SIDE_VIEW_OF_WELD
        );

        // Mock existing templates
        when(photoTemplateRepository.findByOrderId(orderId)).thenReturn(existingTemplates);

        // Act
        System.out.println("[DEBUG_LOG] Calling getAvailableTemplates for order ID: " + orderId.id());
        List<PhotoTemplate> templates = photoTemplateService.getAvailableTemplates(orderId);

        // Assert
        assertNotNull(templates, "Templates list should not be null");
        assertFalse(templates.isEmpty(), "Templates list should not be empty");
        assertEquals(2, templates.size(), "Templates list should contain 2 existing templates");
        assertEquals(existingTemplates, templates, "Templates list should match existing templates");

        // Verify that findByOrderId was called
        verify(photoTemplateRepository).findByOrderId(orderId);

        // Verify that findByUsername was not called
        verify(userRepository, never()).findByUsername(any());

        // Verify that associateWithOrder was not called
        verify(photoTemplateRepository, never()).associateWithOrder(any(), any(), anyBoolean());

        System.out.println("[DEBUG_LOG] Test passed: Existing templates were returned without creating new ones");
    }

    @Test
    public void testGetAvailableTemplates_QAUserNotFound_ReturnsDefaultTemplatesWithoutAssociation() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());

        // Mock empty templates list
        when(photoTemplateRepository.findByOrderId(orderId)).thenReturn(Collections.emptyList());

        // Mock QA user not found
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        // Act
        System.out.println("[DEBUG_LOG] Calling getAvailableTemplates for order ID: " + orderId.id());
        List<PhotoTemplate> templates = photoTemplateService.getAvailableTemplates(orderId);

        // Assert
        assertNotNull(templates, "Templates list should not be null");
        assertFalse(templates.isEmpty(), "Templates list should not be empty");
        assertEquals(10, templates.size(), "Templates list should contain 10 default templates");

        // Verify that findByOrderId was called
        verify(photoTemplateRepository).findByOrderId(orderId);

        // Verify that findByUsername was called with "qa_user"
        verify(userRepository).findByUsername(new Username("qa_user"));

        // Verify that associateWithOrder was not called
        verify(photoTemplateRepository, never()).associateWithOrder(any(), any(), anyBoolean());

        System.out.println("[DEBUG_LOG] Test passed: Default templates were returned without association when QA user not found");
    }
}
