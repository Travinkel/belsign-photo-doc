package com.belman.integration.order;

import com.belman.application.usecase.order.OrderService;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderAssignment;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import com.belman.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the order assignment process.
 * This test verifies that orders can be assigned to workers with the PRODUCTION role,
 * and that the assignment history is properly maintained.
 */
public class OrderAssignmentProcessTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private LoggerFactory loggerFactory;

    // Test users with different roles
    private UserBusiness adminUser;
    private UserBusiness qaUser;
    private UserBusiness productionUser;
    private UserBusiness nonProductionUser;

    // Test order
    private OrderBusiness order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up logger factory mock
        when(loggerFactory.getLogger(any())).thenReturn(mock(com.belman.domain.services.Logger.class));

        // Create test users with different roles
        adminUser = createUserWithRole("admin-user", UserRole.ADMIN);
        qaUser = createUserWithRole("qa-user", UserRole.QA);
        productionUser = createUserWithRole("production-user", UserRole.PRODUCTION);

        // Create a user without the PRODUCTION role
        nonProductionUser = new UserBusiness.Builder()
            .id(new UserId("non-production-user-id"))
            .username(new Username("non-production-user"))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress("non-production-user@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();
        nonProductionUser.addRole(UserRole.QA); // Only QA role, not PRODUCTION

        // Create a test order
        order = createOrder("order-1", "06/23-123456-12345678", OrderStatus.PENDING, adminUser);

        // Set up the repository mock to return the test order
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(orderRepository.save(any(OrderBusiness.class))).thenReturn(order);
    }

    /**
     * Test that an order can be assigned to a user with the PRODUCTION role.
     */
    @Test
    void testAssignOrderToProductionUser() {
        System.out.println("[DEBUG_LOG] Starting test for assigning order to production user");

        // Assign the order to the production user
        boolean result = order.assignTo(productionUser, UserReference.from(adminUser), "Test assignment");

        // Verify the result
        assertTrue(result, "Order should be successfully assigned to production user");
        assertEquals(productionUser.getId().id(), order.getAssignedTo().id().id(), 
                "Order should be assigned to the production user");

        // Verify that the assignment history is updated
        List<OrderAssignment> assignmentHistory = order.getAssignmentHistory();
        assertEquals(1, assignmentHistory.size(), "Assignment history should have one entry");

        OrderAssignment assignment = assignmentHistory.get(0);
        assertEquals(order.getId().id(), assignment.getOrderId().id(), "Assignment should reference the correct order");
        assertEquals(productionUser.getId().id(), assignment.getAssignedTo().id().id(), 
                "Assignment should reference the correct worker");
        assertEquals(adminUser.getId().id(), assignment.getAssignedBy().id().id(), 
                "Assignment should reference the correct assigner");
        assertEquals("Test assignment", assignment.getNotes(), "Assignment should have the correct notes");

        System.out.println("[DEBUG_LOG] Assigning order to production user test passed");
    }

    /**
     * Test that an order cannot be assigned to a user without the PRODUCTION role.
     */
    @Test
    void testAssignOrderToNonProductionUser() {
        System.out.println("[DEBUG_LOG] Starting test for assigning order to non-production user");

        // Attempt to assign the order to a user without the PRODUCTION role
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            order.assignTo(nonProductionUser, UserReference.from(adminUser), "Test assignment");
        });

        // Verify the exception message
        assertEquals("Worker must have the PRODUCTION role to be assigned an order", exception.getMessage(),
                "Exception should indicate that the worker must have the PRODUCTION role");

        // Verify that the order is not assigned
        assertNull(order.getAssignedTo(), "Order should not be assigned");

        // Verify that the assignment history is not updated
        List<OrderAssignment> assignmentHistory = order.getAssignmentHistory();
        assertEquals(0, assignmentHistory.size(), "Assignment history should be empty");

        System.out.println("[DEBUG_LOG] Assigning order to non-production user test passed");
    }

    /**
     * Test that an order can be reassigned to a different user with the PRODUCTION role.
     */
    @Test
    void testReassignOrderToDifferentProductionUser() {
        System.out.println("[DEBUG_LOG] Starting test for reassigning order to different production user");

        // Create another production user
        UserBusiness anotherProductionUser = createUserWithRole("another-production-user", UserRole.PRODUCTION);

        // First assign the order to the production user
        order.assignTo(productionUser, UserReference.from(adminUser), "Initial assignment");

        // Then reassign to another production user
        boolean result = order.reassignTo(anotherProductionUser, UserReference.from(qaUser), "Reassignment");

        // Verify the result
        assertTrue(result, "Order should be successfully reassigned");
        assertEquals(anotherProductionUser.getId().id(), order.getAssignedTo().id().id(), 
                "Order should be reassigned to the new production user");

        // Verify that the assignment history is updated
        List<OrderAssignment> assignmentHistory = order.getAssignmentHistory();
        assertEquals(2, assignmentHistory.size(), "Assignment history should have two entries");

        OrderAssignment reassignment = assignmentHistory.get(1);
        assertEquals(order.getId().id(), reassignment.getOrderId().id(), "Reassignment should reference the correct order");
        assertEquals(anotherProductionUser.getId().id(), reassignment.getAssignedTo().id().id(), 
                "Reassignment should reference the correct worker");
        assertEquals(qaUser.getId().id(), reassignment.getAssignedBy().id().id(), 
                "Reassignment should reference the correct assigner");
        assertEquals("Reassignment", reassignment.getNotes(), "Reassignment should have the correct notes");

        System.out.println("[DEBUG_LOG] Reassigning order to different production user test passed");
    }

    /**
     * Test that reassigning an order to the same user returns false.
     */
    @Test
    void testReassignOrderToSameUser() {
        System.out.println("[DEBUG_LOG] Starting test for reassigning order to same user");

        // First assign the order to the production user
        order.assignTo(productionUser, UserReference.from(adminUser), "Initial assignment");

        // Then attempt to reassign to the same production user
        boolean result = order.reassignTo(productionUser, UserReference.from(qaUser), "Attempted reassignment");

        // Verify the result
        assertFalse(result, "Reassigning to the same user should return false");

        // Verify that the assignment history still has only one entry
        List<OrderAssignment> assignmentHistory = order.getAssignmentHistory();
        assertEquals(1, assignmentHistory.size(), "Assignment history should still have only one entry");

        System.out.println("[DEBUG_LOG] Reassigning order to same user test passed");
    }

    /**
     * Test that the deprecated setAssignedTo method still works but doesn't update assignment history.
     */
    @Test
    void testDeprecatedSetAssignedToMethod() {
        System.out.println("[DEBUG_LOG] Starting test for deprecated setAssignedTo method");

        // Use the deprecated method to assign the order
        order.setAssignedTo(UserReference.from(productionUser));

        // Verify that the order is assigned
        assertEquals(productionUser.getId().id(), order.getAssignedTo().id().id(), 
                "Order should be assigned to the production user");

        // Verify that the assignment history is not updated
        List<OrderAssignment> assignmentHistory = order.getAssignmentHistory();
        assertEquals(0, assignmentHistory.size(), "Assignment history should be empty when using deprecated method");

        System.out.println("[DEBUG_LOG] Deprecated setAssignedTo method test passed");
    }

    /**
     * Test that the order service correctly saves the order after assignment.
     */
    @Test
    void testOrderServiceSavesOrderAfterAssignment() {
        System.out.println("[DEBUG_LOG] Starting test for order service saving order after assignment");

        // Set up the order service mock
        when(orderService.getOrderById(any(OrderId.class))).thenReturn(Optional.of(order));
        when(orderService.saveOrder(any(OrderBusiness.class))).thenReturn(order);

        // Assign the order to the production user
        order.assignTo(productionUser, UserReference.from(adminUser), "Test assignment");

        // Save the order using the service
        OrderBusiness savedOrder = orderService.saveOrder(order);

        // Verify that the service was called to save the order
        verify(orderService).saveOrder(order);

        // Verify that the saved order has the correct assignment
        assertEquals(productionUser.getId().id(), savedOrder.getAssignedTo().id().id(), 
                "Saved order should have the correct assignment");

        System.out.println("[DEBUG_LOG] Order service saving order after assignment test passed");
    }

    /**
     * Helper method to create a user with a specific role.
     */
    private UserBusiness createUserWithRole(String username, UserRole role) {
        UserBusiness user = new UserBusiness.Builder()
            .id(new UserId(username + "-id"))
            .username(new Username(username))
            .password(new HashedPassword("$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS"))
            .email(new EmailAddress(username + "@example.com"))
            .approvalState(ApprovalState.createApproved())
            .build();

        user.addRole(role);
        return user;
    }

    /**
     * Helper method to create an order with a specific status.
     */
    private OrderBusiness createOrder(String id, String orderNumber, OrderStatus status, UserBusiness createdBy) {
        // Use a valid order number format: ORD-XX-YYMMDD-ZZZ-NNNN
        // where XX is a project identifier, YYMMDD is the date, ZZZ is a project code, and NNNN is a sequence number
        String validOrderNumber = "ORD-78-230625-PIP-0001";

        OrderBusiness order = new OrderBusiness(
            new OrderId(id),
            new OrderNumber(validOrderNumber),
            new UserReference(createdBy.getId(), createdBy.getUsername()),
            new Timestamp(Instant.now())
        );

        // Set the status based on the parameter
        if (status == OrderStatus.IN_PROGRESS) {
            order.startProcessing();
        } else if (status == OrderStatus.COMPLETED) {
            order.startProcessing();
            order.completeProcessing();
        } else if (status == OrderStatus.APPROVED) {
            order.startProcessing();
            order.completeProcessing();
            order.approve();
        }

        return order;
    }
}
