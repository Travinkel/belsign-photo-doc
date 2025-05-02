package com.belman.integration.infrastructure.persistence;

import com.belman.domain.aggregates.Order;
import com.belman.domain.aggregates.User;
import com.belman.domain.enums.OrderStatus;
import com.belman.domain.repositories.OrderRepository;
import com.belman.domain.specification.MinPhotosSpecification;
import com.belman.domain.specification.Specification;
import com.belman.domain.valueobjects.*;
import com.belman.infrastructure.persistence.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for InMemoryOrderRepository.
 * These tests verify that the in-memory implementation of OrderRepository
 * correctly implements all the methods defined in the interface.
 */
public class InMemoryOrderRepositoryTest {

    private OrderRepository orderRepository;
    private Order testOrder;
    private User testUser;
    private OrderId orderId;
    private OrderNumber orderNumber;

    @BeforeEach
    void setUp() {
        System.out.println("[DEBUG_LOG] Setting up InMemoryOrderRepositoryTest");

        // Create a new repository instance for each test
        orderRepository = new InMemoryOrderRepository();
        System.out.println("[DEBUG_LOG] Created InMemoryOrderRepository");

        // Create a test user
        UserId userId = new UserId(UUID.randomUUID());
        Username username = new Username("testuser");
        HashedPassword password = HashedPassword.fromPlainText("password");
        EmailAddress email = new EmailAddress("testuser@example.com");
        testUser = new User(userId, username, password, email);
        System.out.println("[DEBUG_LOG] Created test user: " + username.value());

        // Create a test order with a unique ID and order number
        orderId = new OrderId(UUID.randomUUID());
        // Create a valid order number using the factory method
        orderNumber = OrderNumber.of(1, 23, 123456, 12345678);
        Timestamp createdAt = new Timestamp(Instant.now());

        testOrder = new Order(orderId, orderNumber, testUser, createdAt);
        System.out.println("[DEBUG_LOG] Created test order with ID: " + orderId.id() + " and number: " + orderNumber.value());
    }

    @Test
    void save_newOrder_shouldPersistOrder() {
        System.out.println("[DEBUG_LOG] Running save_newOrder_shouldPersistOrder test");

        try {
            // When
            System.out.println("[DEBUG_LOG] Saving test order");
            orderRepository.save(testOrder);

            // Then
            System.out.println("[DEBUG_LOG] Finding order by ID");
            Order retrievedOrder = orderRepository.findById(orderId);

            assertNotNull(retrievedOrder, "Order should be found after saving");
            assertEquals(orderId.id(), retrievedOrder.getId().id(), "Order ID should match");
            assertEquals(orderNumber.value(), retrievedOrder.getOrderNumber().value(), "Order number should match");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findById_existingOrder_shouldReturnOrder() {
        System.out.println("[DEBUG_LOG] Running findById_existingOrder_shouldReturnOrder test");

        try {
            // Given
            System.out.println("[DEBUG_LOG] Saving test order");
            orderRepository.save(testOrder);

            // When
            System.out.println("[DEBUG_LOG] Finding order by ID");
            Order retrievedOrder = orderRepository.findById(orderId);

            // Then
            assertNotNull(retrievedOrder, "Order should be found");
            assertEquals(orderId.id(), retrievedOrder.getId().id(), "Order ID should match");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findById_nonExistingOrder_shouldReturnNull() {
        System.out.println("[DEBUG_LOG] Running findById_nonExistingOrder_shouldReturnNull test");

        try {
            // When
            System.out.println("[DEBUG_LOG] Finding non-existent order by ID");
            Order retrievedOrder = orderRepository.findById(new OrderId(UUID.randomUUID()));

            // Then
            assertNull(retrievedOrder, "Non-existent order should not be found");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findAll_withMultipleOrders_shouldReturnAllOrders() {
        System.out.println("[DEBUG_LOG] Running findAll_withMultipleOrders_shouldReturnAllOrders test");

        try {
            // Given
            System.out.println("[DEBUG_LOG] Saving test order");
            orderRepository.save(testOrder);

            // Create and save a second order
            OrderId secondOrderId = new OrderId(UUID.randomUUID());
            OrderNumber secondOrderNumber = OrderNumber.of(1, 23, 654321, 87654321);
            Timestamp createdAt = new Timestamp(Instant.now());
            Order secondOrder = new Order(secondOrderId, secondOrderNumber, testUser, createdAt);
            System.out.println("[DEBUG_LOG] Created second test order with ID: " + secondOrderId.id());
            orderRepository.save(secondOrder);

            // When
            System.out.println("[DEBUG_LOG] Finding all orders");
            List<Order> allOrders = orderRepository.findAll();

            // Then
            assertEquals(2, allOrders.size(), "Should find 2 orders");
            assertTrue(allOrders.stream().anyMatch(o -> o.getId().id().equals(orderId.id())), "First order should be in the list");
            assertTrue(allOrders.stream().anyMatch(o -> o.getId().id().equals(secondOrderId.id())), "Second order should be in the list");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findBySpecification_matchingOrders_shouldReturnMatchingOrders() {
        System.out.println("[DEBUG_LOG] Running findBySpecification_matchingOrders_shouldReturnMatchingOrders test");

        try {
            // Given
            System.out.println("[DEBUG_LOG] Saving test order");
            orderRepository.save(testOrder);

            // Create and save a second order with a different status
            OrderId secondOrderId = new OrderId(UUID.randomUUID());
            OrderNumber secondOrderNumber = OrderNumber.of(1, 23, 654321, 87654321);
            Timestamp createdAt = new Timestamp(Instant.now());
            Order secondOrder = new Order(secondOrderId, secondOrderNumber, testUser, createdAt);
            secondOrder.setStatus(OrderStatus.COMPLETED);
            System.out.println("[DEBUG_LOG] Created second test order with ID: " + secondOrderId.id() + " and status: " + secondOrder.getStatus());
            orderRepository.save(secondOrder);

            // Use MinPhotosSpecification with min=0 to get all orders
            // We'll filter for COMPLETED orders in the test
            Specification<Order> allOrdersSpec = new MinPhotosSpecification(0);

            // When
            System.out.println("[DEBUG_LOG] Finding orders by specification (all orders)");
            List<Order> allOrders = orderRepository.findBySpecification(allOrdersSpec);

            // Filter for COMPLETED orders
            System.out.println("[DEBUG_LOG] Filtering for COMPLETED orders");
            List<Order> completedOrders = allOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .toList();

            // Then
            assertEquals(1, completedOrders.size(), "Should find 1 completed order");
            assertEquals(secondOrderId.id(), completedOrders.get(0).getId().id(), "Completed order ID should match");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findByOrderNumber_existingOrder_shouldReturnOrder() {
        System.out.println("[DEBUG_LOG] Running findByOrderNumber_existingOrder_shouldReturnOrder test");

        try {
            // Given
            System.out.println("[DEBUG_LOG] Saving test order");
            orderRepository.save(testOrder);

            // When
            System.out.println("[DEBUG_LOG] Finding order by order number");
            Optional<Order> result = orderRepository.findByOrderNumber(orderNumber);

            // Then
            assertTrue(result.isPresent(), "Order should be found by order number");
            assertEquals(orderId.id(), result.get().getId().id(), "Order ID should match");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void findByOrderNumber_nonExistingOrder_shouldReturnEmpty() {
        System.out.println("[DEBUG_LOG] Running findByOrderNumber_nonExistingOrder_shouldReturnEmpty test");

        try {
            // When
            System.out.println("[DEBUG_LOG] Finding non-existent order by order number");
            Optional<Order> result = orderRepository.findByOrderNumber(OrderNumber.of(1, 23, 999999, 12345678));

            // Then
            assertTrue(result.isEmpty(), "Non-existent order should not be found by order number");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    void save_existingOrder_shouldUpdateOrder() {
        System.out.println("[DEBUG_LOG] Running save_existingOrder_shouldUpdateOrder test");

        try {
            // Given
            System.out.println("[DEBUG_LOG] Saving test order");
            orderRepository.save(testOrder);

            // When
            System.out.println("[DEBUG_LOG] Updating test order status");
            testOrder.setStatus(OrderStatus.IN_PROGRESS);
            orderRepository.save(testOrder);

            // Then
            System.out.println("[DEBUG_LOG] Finding updated order by ID");
            Order retrievedOrder = orderRepository.findById(orderId);

            assertNotNull(retrievedOrder, "Updated order should be found");
            assertEquals(OrderStatus.IN_PROGRESS, retrievedOrder.getStatus(), "Order status should be updated");

            System.out.println("[DEBUG_LOG] Test passed successfully");
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Error in test: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }
}
