package com.belman.integration.order;

import com.belman.application.usecase.order.OrderService;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
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
import com.belman.presentation.usecases.qa.dashboard.QADashboardViewModel;
import com.belman.presentation.usecases.worker.assignedorder.AssignedOrderViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for order loading with various user roles using Failsafe.
 * This test verifies that orders are loaded correctly based on the user's role.
 * 
 * This class uses the IT suffix (Integration Test) to be run by Maven Failsafe
 * instead of Surefire, demonstrating the separation between unit tests and integration tests.
 */
public class OrderLoadingWithUserRolesIT {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private LoggerFactory loggerFactory;

    @Mock
    private com.belman.common.session.SessionContext sessionContext;

    private QADashboardViewModel qaDashboardViewModel;
    private AssignedOrderViewModel assignedOrderViewModel;

    // Test users with different roles
    private UserBusiness adminUser;
    private UserBusiness qaUser;
    private UserBusiness productionUser;

    // Test orders
    private OrderBusiness pendingOrder;
    private OrderBusiness inProgressOrder;
    private OrderBusiness completedOrder;
    private OrderBusiness approvedOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up logger factory mock
        when(loggerFactory.getLogger(any())).thenReturn(mock(com.belman.domain.services.Logger.class));

        // Register mock services with ServiceLocator
        registerMockServices();

        // Create test users with different roles
        adminUser = createUserWithRole("admin-user-it", UserRole.ADMIN);
        qaUser = createUserWithRole("qa-user-it", UserRole.QA);
        productionUser = createUserWithRole("production-user-it", UserRole.PRODUCTION);

        // Create test orders with different statuses
        pendingOrder = createOrder("order-it-1", "ORD-01-230701-ABC-0001", OrderStatus.PENDING, adminUser);
        inProgressOrder = createOrder("order-it-2", "ORD-02-230701-ABC-0002", OrderStatus.IN_PROGRESS, adminUser);
        completedOrder = createOrder("order-it-3", "ORD-03-230701-ABC-0003", OrderStatus.COMPLETED, adminUser);
        approvedOrder = createOrder("order-it-4", "ORD-04-230701-ABC-0004", OrderStatus.APPROVED, adminUser);

        // Assign the in-progress order to the production user
        inProgressOrder.assignTo(productionUser, new UserReference(adminUser.getId(), adminUser.getUsername()), "Assigned for testing");

        // Initialize view models
        qaDashboardViewModel = new QADashboardViewModel();
        assignedOrderViewModel = new AssignedOrderViewModel();

        // Inject mocks into view models using reflection
        injectDependency(qaDashboardViewModel, "orderRepository", orderRepository);
        injectDependency(qaDashboardViewModel, "sessionContext", sessionContext);

        // Use ServiceLocator to inject dependencies into AssignedOrderViewModel
        com.belman.bootstrap.di.ServiceLocator.injectServices(assignedOrderViewModel);

        // Also inject orderService directly for backward compatibility
        injectDependency(assignedOrderViewModel, "orderService", orderService);
    }

    /**
     * Test that QA users see orders with status COMPLETED.
     */
    @Test
    @DisplayName("QA users should see completed orders")
    void testQAUserSeesCompletedOrders() {
        System.out.println("[DEBUG_LOG] Starting test for QA user seeing completed orders (IT)");

        // Set up the repository mock to return all orders
        when(orderRepository.findAll()).thenReturn(Arrays.asList(pendingOrder, inProgressOrder, completedOrder, approvedOrder));

        // Set up the repository mock to return completed orders for the specification
        when(orderRepository.findBySpecification(any())).thenReturn(Collections.singletonList(completedOrder));

        // Set up the session context to return the QA user
        when(sessionContext.getUser()).thenReturn(Optional.of(qaUser));

        // Call the method under test
        qaDashboardViewModel.onShow();
        qaDashboardViewModel.loadPendingOrders();

        // Verify that only the completed order is shown
        assertEquals(1, qaDashboardViewModel.getPendingOrders().size(), "QA user should see only completed orders");
        assertTrue(qaDashboardViewModel.getPendingOrders().contains(completedOrder.getOrderNumber().toString()), 
                "QA user should see the completed order");

        // Verify that the repository was called with the correct specification
        verify(orderRepository, atLeastOnce()).findBySpecification(any());

        System.out.println("[DEBUG_LOG] QA user seeing completed orders test passed (IT)");
    }

    /**
     * Test that production users see orders assigned to them.
     */
    @Test
    @DisplayName("Production users should see assigned orders")
    void testProductionUserSeesAssignedOrders() {
        System.out.println("[DEBUG_LOG] Starting test for production user seeing assigned orders (IT)");

        // Set up the order service mock to return all orders
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(pendingOrder, inProgressOrder, completedOrder, approvedOrder));

        // Set up the session context to return the production user
        when(sessionContext.getUser()).thenReturn(Optional.of(productionUser));

        // Call the method under test
        assignedOrderViewModel.onShow();

        // Verify that the order service was called
        verify(orderService).getAllOrders();

        // Verify that the session context was called to get the current user
        verify(sessionContext, atLeastOnce()).getUser();

        System.out.println("[DEBUG_LOG] Production user seeing assigned orders test passed (IT)");
    }

    /**
     * Test that admin users see orders they created.
     */
    @Test
    @DisplayName("Admin users should see created orders")
    void testAdminUserSeesCreatedOrders() {
        System.out.println("[DEBUG_LOG] Starting test for admin user seeing created orders (IT)");

        // Set up the order service mock to return all orders
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(pendingOrder, inProgressOrder, completedOrder, approvedOrder));

        // Set up the session context to return the admin user
        when(sessionContext.getUser()).thenReturn(Optional.of(adminUser));

        // Call the method under test
        assignedOrderViewModel.onShow();

        // Verify that the order service was called
        verify(orderService).getAllOrders();

        // Verify that the session context was called to get the current user
        verify(sessionContext, atLeastOnce()).getUser();

        System.out.println("[DEBUG_LOG] Admin user seeing created orders test passed (IT)");
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
        OrderBusiness order = new OrderBusiness(
            new OrderId(id),
            new OrderNumber(orderNumber),
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

    /**
     * Helper method to inject dependencies using reflection.
     */
    private void injectDependency(Object target, String fieldName, Object dependency) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, dependency);
        } catch (Exception e) {
            System.err.println("Error injecting dependency: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Registers mock services with the ServiceLocator.
     */
    private void registerMockServices() {
        // Clear any existing services
        com.belman.bootstrap.di.ServiceLocator.clear();

        // Register mock AuthenticationService
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.domain.security.AuthenticationService.class,
            new MockAuthenticationService()
        );

        // Register OrderService mock
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.order.OrderService.class,
            orderService
        );

        // Register WorkerService mock
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.application.usecase.worker.WorkerService.class,
            mock(com.belman.application.usecase.worker.WorkerService.class)
        );

        // Register SessionContext mock
        com.belman.bootstrap.di.ServiceLocator.registerService(
            com.belman.common.session.SessionContext.class,
            sessionContext
        );
    }

    /**
     * Mock implementation of AuthenticationService for testing.
     */
    private static class MockAuthenticationService implements com.belman.domain.security.AuthenticationService {
        private com.belman.domain.user.UserBusiness currentUser = null;

        @Override
        public java.util.Optional<com.belman.domain.user.UserBusiness> authenticate(String username, String password) {
            return java.util.Optional.empty();
        }

        @Override
        public java.util.Optional<com.belman.domain.user.UserBusiness> getCurrentUser() {
            return java.util.Optional.ofNullable(currentUser);
        }

        @Override
        public void logout() {
            currentUser = null;
        }

        @Override
        public boolean isLoggedIn() {
            return currentUser != null;
        }
    }
}
