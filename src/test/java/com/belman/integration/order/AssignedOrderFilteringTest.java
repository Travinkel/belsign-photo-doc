package com.belman.integration.order;

import com.belman.application.usecase.order.DefaultOrderProgressService;
import com.belman.application.usecase.order.OrderProgressService;
import com.belman.application.usecase.photo.PhotoTemplateService;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for assigned order filtering with different case UUIDs.
 * This test verifies that the fix for the UUID case sensitivity issue works correctly.
 */
public class AssignedOrderFilteringTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PhotoTemplateService photoTemplateService;

    @Mock
    private LoggerFactory loggerFactory;

    private OrderProgressService orderProgressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up logger factory mock
        when(loggerFactory.getLogger(any())).thenReturn(mock(com.belman.domain.services.Logger.class));
        
        // Create the service under test
        orderProgressService = new DefaultOrderProgressService(orderRepository, photoTemplateService, loggerFactory);
    }

    /**
     * Test that assigned order filtering works correctly with different case UUIDs.
     * This test verifies that the fix for the UUID case sensitivity issue works correctly.
     */
    @Test
    void testAssignedOrderFilteringWithDifferentCase() {
        System.out.println("[DEBUG_LOG] Starting assigned order filtering with different case test");

        // Create a worker user with a mixed-case UUID
        String workerUuid = "A78B4D0AC15E8E00DD7B7B87DCB8E258";
        UserId workerId = new UserId(workerUuid);
        UserBusiness worker = new UserBusiness.Builder()
            .id(workerId)
            .username(new Username("testworker"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testworker@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create an order with the worker's ID in lowercase
        String orderUuid = "order-123";
        OrderId orderId = new OrderId(orderUuid);
        
        // Create a user reference with the worker's ID in lowercase
        UserReference assignedTo = new UserReference(
            new UserId(workerUuid.toLowerCase()), 
            new Username("testworker")
        );
        
        // Create an order assigned to the worker
        OrderBusiness assignedOrder = new OrderBusiness(
            orderId, 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        assignedOrder.setAssignedTo(assignedTo);
        
        // Create an unassigned order
        OrderBusiness unassignedOrder = new OrderBusiness(
            new OrderId("order-456"), 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        
        // Set up the repository mock to return the test orders
        when(orderRepository.findAll()).thenReturn(Arrays.asList(assignedOrder, unassignedOrder));

        // Call the method under test
        Optional<OrderBusiness> result = orderProgressService.getAssignedOrder(worker);

        // Verify the result
        assertTrue(result.isPresent(), "Should find an assigned order");
        assertEquals(orderId.id(), result.get().getId().id(), "Should return the correct order");
        
        // Verify that the repository was called
        verify(orderRepository).findAll();
        
        System.out.println("[DEBUG_LOG] Assigned order filtering with different case test passed");
    }
}