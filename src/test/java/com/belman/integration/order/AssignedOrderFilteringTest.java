package com.belman.integration.order;

import com.belman.application.usecase.order.DefaultOrderProgressService;
import com.belman.application.usecase.order.OrderProgressService;
import com.belman.application.usecase.photo.PhotoTemplateService;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for assigned order filtering with different case UUIDs and various scenarios.
 * This test verifies that the order assignment and completion functionality works correctly.
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

    /**
     * Test that assigned order filtering works correctly with uppercase UUID in the order
     * and mixed case in the worker.
     */
    @Test
    void testAssignedOrderFilteringWithUppercaseOrderUuid() {
        System.out.println("[DEBUG_LOG] Starting assigned order filtering with uppercase order UUID test");

        // Create a worker user with a mixed-case UUID
        String workerUuid = "a78b4d0ac15e8e00dd7b7b87dcb8e258";
        UserId workerId = new UserId(workerUuid);
        UserBusiness worker = new UserBusiness.Builder()
            .id(workerId)
            .username(new Username("testworker"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testworker@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create an order with the worker's ID in UPPERCASE
        String orderUuid = "order-123";
        OrderId orderId = new OrderId(orderUuid);

        // Create a user reference with the worker's ID in UPPERCASE
        UserReference assignedTo = new UserReference(
            new UserId(workerUuid.toUpperCase()), 
            new Username("testworker")
        );

        // Create an order assigned to the worker
        OrderBusiness assignedOrder = new OrderBusiness(
            orderId, 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        assignedOrder.setAssignedTo(assignedTo);

        // Set up the repository mock to return the test order
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(assignedOrder));

        // Call the method under test
        Optional<OrderBusiness> result = orderProgressService.getAssignedOrder(worker);

        // Verify the result
        assertTrue(result.isPresent(), "Should find an assigned order");
        assertEquals(orderId.id(), result.get().getId().id(), "Should return the correct order");

        // Verify that the repository was called
        verify(orderRepository).findAll();

        System.out.println("[DEBUG_LOG] Assigned order filtering with uppercase order UUID test passed");
    }

    /**
     * Test that when multiple orders are assigned to the same worker, the first one is returned.
     */
    @Test
    void testMultipleOrdersAssignedToSameWorker() {
        System.out.println("[DEBUG_LOG] Starting multiple orders assigned to same worker test");

        // Create a worker user
        String workerUuid = "worker-123";
        UserId workerId = new UserId(workerUuid);
        UserBusiness worker = new UserBusiness.Builder()
            .id(workerId)
            .username(new Username("testworker"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testworker@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create a user reference for the worker
        UserReference assignedTo = new UserReference(workerId, new Username("testworker"));

        // Create multiple orders assigned to the worker
        OrderBusiness firstOrder = new OrderBusiness(
            new OrderId("order-123"), 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        firstOrder.setAssignedTo(assignedTo);

        OrderBusiness secondOrder = new OrderBusiness(
            new OrderId("order-456"), 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        secondOrder.setAssignedTo(assignedTo);

        // Set up the repository mock to return the test orders
        when(orderRepository.findAll()).thenReturn(Arrays.asList(firstOrder, secondOrder));

        // Call the method under test
        Optional<OrderBusiness> result = orderProgressService.getAssignedOrder(worker);

        // Verify the result
        assertTrue(result.isPresent(), "Should find an assigned order");
        assertEquals("order-123", result.get().getId().id(), "Should return the first order");

        // Verify that the repository was called
        verify(orderRepository).findAll();

        System.out.println("[DEBUG_LOG] Multiple orders assigned to same worker test passed");
    }

    /**
     * Test that when no orders are assigned to the worker, an empty Optional is returned.
     */
    @Test
    void testNoOrdersAssignedToWorker() {
        System.out.println("[DEBUG_LOG] Starting no orders assigned to worker test");

        // Create a worker user
        String workerUuid = "worker-123";
        UserId workerId = new UserId(workerUuid);
        UserBusiness worker = new UserBusiness.Builder()
            .id(workerId)
            .username(new Username("testworker"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testworker@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create orders not assigned to the worker
        OrderBusiness firstOrder = new OrderBusiness(
            new OrderId("order-123"), 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );

        OrderBusiness secondOrder = new OrderBusiness(
            new OrderId("order-456"), 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        secondOrder.setAssignedTo(new UserReference(new UserId("other-worker"), new Username("otherWorker")));

        // Set up the repository mock to return the test orders
        when(orderRepository.findAll()).thenReturn(Arrays.asList(firstOrder, secondOrder));

        // Call the method under test
        Optional<OrderBusiness> result = orderProgressService.getAssignedOrder(worker);

        // Verify the result
        assertFalse(result.isPresent(), "Should not find an assigned order");

        // Verify that the repository was called
        verify(orderRepository).findAll();

        System.out.println("[DEBUG_LOG] No orders assigned to worker test passed");
    }

    /**
     * Test that orders with null assignedTo field are handled gracefully.
     */
    @Test
    void testOrderWithNullAssignedTo() {
        System.out.println("[DEBUG_LOG] Starting order with null assignedTo test");

        // Create a worker user
        String workerUuid = "worker-123";
        UserId workerId = new UserId(workerUuid);
        UserBusiness worker = new UserBusiness.Builder()
            .id(workerId)
            .username(new Username("testworker"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testworker@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create an order with null assignedTo
        OrderBusiness orderWithNullAssignedTo = new OrderBusiness(
            new OrderId("order-123"), 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );
        // Note: Not setting assignedTo, so it remains null

        // Set up the repository mock to return the test order
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(orderWithNullAssignedTo));

        // Call the method under test
        Optional<OrderBusiness> result = orderProgressService.getAssignedOrder(worker);

        // Verify the result
        assertFalse(result.isPresent(), "Should not find an assigned order");

        // Verify that the repository was called
        verify(orderRepository).findAll();

        System.out.println("[DEBUG_LOG] Order with null assignedTo test passed");
    }

    /**
     * Test successful completion of an order.
     */
    @Test
    void testSuccessfulOrderCompletion() {
        System.out.println("[DEBUG_LOG] Starting successful order completion test");

        // Create a user who will complete the order
        String userUuid = "user-123";
        UserId userId = new UserId(userUuid);
        UserBusiness user = new UserBusiness.Builder()
            .id(userId)
            .username(new Username("testuser"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testuser@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create an order ID
        OrderId orderId = new OrderId("order-123");

        // Create an order
        OrderBusiness order = new OrderBusiness(
            orderId, 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );

        // Set the order status to IN_PROGRESS (required for completeProcessing to work)
        order.startProcessing();

        // Set up the repository mock to return the test order
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Set up the photo template service mock to indicate all required photos are present
        when(photoTemplateService.hasAllRequiredPhotos(orderId)).thenReturn(true);

        // Call the method under test
        boolean result = orderProgressService.completeOrder(orderId, user);

        // Verify the result
        assertTrue(result, "Should successfully complete the order");

        // Verify that the repository was called to find and save the order
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);

        // Verify that the order status was changed to COMPLETED
        assertEquals(OrderStatus.COMPLETED, order.getStatus(), "Order status should be COMPLETED");

        System.out.println("[DEBUG_LOG] Successful order completion test passed");
    }

    /**
     * Test completion of an order that doesn't have all required photos.
     */
    @Test
    void testOrderCompletionWithMissingPhotos() {
        System.out.println("[DEBUG_LOG] Starting order completion with missing photos test");

        // Create a user who will complete the order
        String userUuid = "user-123";
        UserId userId = new UserId(userUuid);
        UserBusiness user = new UserBusiness.Builder()
            .id(userId)
            .username(new Username("testuser"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testuser@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create an order ID
        OrderId orderId = new OrderId("order-123");

        // Create an order
        OrderBusiness order = new OrderBusiness(
            orderId, 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );

        // Set up the repository mock to return the test order
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Set up the photo template service mock to indicate not all required photos are present
        when(photoTemplateService.hasAllRequiredPhotos(orderId)).thenReturn(false);

        // Call the method under test
        boolean result = orderProgressService.completeOrder(orderId, user);

        // Verify the result
        assertFalse(result, "Should not complete the order due to missing photos");

        // Verify that the repository was called to find the order but not to save it
        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any(OrderBusiness.class));

        // Verify that the order status was not changed
        assertNotEquals(OrderStatus.COMPLETED, order.getStatus(), "Order status should not be COMPLETED");

        System.out.println("[DEBUG_LOG] Order completion with missing photos test passed");
    }

    /**
     * Test completion of a non-existent order.
     */
    @Test
    void testCompletionOfNonExistentOrder() {
        System.out.println("[DEBUG_LOG] Starting completion of non-existent order test");

        // Create a user who will complete the order
        String userUuid = "user-123";
        UserId userId = new UserId(userUuid);
        UserBusiness user = new UserBusiness.Builder()
            .id(userId)
            .username(new Username("testuser"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testuser@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create an order ID for a non-existent order
        OrderId orderId = new OrderId("non-existent-order");

        // Set up the repository mock to return empty (order not found)
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Call the method under test
        boolean result = orderProgressService.completeOrder(orderId, user);

        // Verify the result
        assertFalse(result, "Should not complete a non-existent order");

        // Verify that the repository was called to find the order but not to save it
        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any(OrderBusiness.class));

        System.out.println("[DEBUG_LOG] Completion of non-existent order test passed");
    }

    /**
     * Test exception handling during order completion.
     */
    @Test
    void testExceptionHandlingDuringOrderCompletion() {
        System.out.println("[DEBUG_LOG] Starting exception handling during order completion test");

        // Create a user who will complete the order
        String userUuid = "user-123";
        UserId userId = new UserId(userUuid);
        UserBusiness user = new UserBusiness.Builder()
            .id(userId)
            .username(new Username("testuser"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("testuser@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        // Create an order ID
        OrderId orderId = new OrderId("order-123");

        // Create a real order (can't mock OrderBusiness)
        OrderBusiness order = new OrderBusiness(
            orderId, 
            new UserReference(new UserId("admin-123"), new Username("admin")),
            new Timestamp(Instant.now())
        );

        // Set the order status to IN_PROGRESS (required for completeProcessing to work)
        order.startProcessing();

        // Set up the repository mock to return the test order
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Set up the photo template service mock to indicate all required photos are present
        when(photoTemplateService.hasAllRequiredPhotos(orderId)).thenReturn(true);

        // Set up the repository mock to throw an exception when save is called
        doThrow(new RuntimeException("Test exception")).when(orderRepository).save(any(OrderBusiness.class));

        // Call the method under test
        boolean result = orderProgressService.completeOrder(orderId, user);

        // Verify the result
        assertFalse(result, "Should handle exceptions gracefully and return false");

        // Verify that the repository was called to find the order and attempt to save it
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(OrderBusiness.class));

        System.out.println("[DEBUG_LOG] Exception handling during order completion test passed");
    }
}
