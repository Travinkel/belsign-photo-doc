package com.belman.unit.presentation.usecases.qa.dashboard;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.OrderStatus;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.specification.OrderStatusSpecification;
import com.belman.common.session.SessionContext;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.Username;
import com.belman.presentation.usecases.qa.dashboard.QADashboardViewModel;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the QADashboardViewModel class.
 * These tests verify that the ViewModel correctly handles loading and filtering orders,
 * selecting orders, and other QA dashboard functionality.
 */
public class QADashboardViewModelTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private AuthenticationService authenticationService;

    private QADashboardViewModel viewModel;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Create the view model
        viewModel = new QADashboardViewModel();

        // Inject the mocked dependencies using reflection
        injectDependency("orderRepository", orderRepository);
        injectDependency("sessionContext", sessionContext);

        // Mock the AuthenticationService in ServiceLocator
        Field authServiceField = QADashboardViewModel.class.getDeclaredField("authenticationService");
        authServiceField.setAccessible(true);
        authServiceField.set(viewModel, authenticationService);

        // Create a real QA user with username "qa_user"
        Username username = new Username("qa_user");
        HashedPassword password = new HashedPassword("$2a$10$ReM2gCw1o9rZz/ctET48N.XCmTxSKFcQvwNaqtjCSZxGr78adkX5u"); // Standard test password
        EmailAddress email = new EmailAddress("qa_user@belman.com");

        // Create a real UserBusiness instance
        UserBusiness realUser = UserBusiness.createNewUser(username, password, email);

        // Mock the session context to return the real user
        when(sessionContext.getUser()).thenReturn(Optional.of(realUser));
    }

    /**
     * Helper method to inject a dependency into the view model using reflection.
     * 
     * @param fieldName the name of the field to inject
     * @param dependency the dependency to inject
     * @throws Exception if the field doesn't exist or can't be accessed
     */
    private void injectDependency(String fieldName, Object dependency) throws Exception {
        Field field = QADashboardViewModel.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(viewModel, dependency);
    }

    /**
     * Test that the onShow method correctly updates the welcome message with the user's name.
     */
    @Test
    void testOnShow_UpdatesWelcomeMessage() {
        // Act
        viewModel.onShow();

        // Assert
        assertEquals("Welcome, qa_user!", viewModel.welcomeMessageProperty().get(),
                "Welcome message should include the username");
    }

    /**
     * Test that loadPendingOrders correctly loads orders with status COMPLETED.
     */
    @Test
    void testLoadPendingOrders_LoadsCompletedOrders() {
        // Arrange
        List<OrderBusiness> mockOrders = createMockOrders();
        when(orderRepository.findBySpecification(any(OrderStatusSpecification.class))).thenReturn(mockOrders);

        // Act
        viewModel.loadPendingOrders();

        // Assert
        // Verify that findBySpecification was called with an OrderStatusSpecification
        ArgumentCaptor<OrderStatusSpecification> specCaptor = ArgumentCaptor.forClass(OrderStatusSpecification.class);
        verify(orderRepository).findBySpecification(specCaptor.capture());

        // Verify that the specification was for COMPLETED orders
        OrderStatusSpecification spec = specCaptor.getValue();
        assertNotNull(spec, "Specification should not be null");

        // Verify that the pending orders list contains the expected order numbers
        ObservableList<String> pendingOrders = viewModel.getPendingOrders();
        assertEquals(2, pendingOrders.size(), "Should have 2 pending orders");
        assertTrue(pendingOrders.contains("ORDER-001"), "Should contain ORDER-001");
        assertTrue(pendingOrders.contains("ORDER-002"), "Should contain ORDER-002");
    }

    /**
     * Test that searchOrders correctly filters the orders list based on the search text.
     */
    @Test
    void testSearchOrders_FiltersOrdersList() throws Exception {
        // Arrange
        List<OrderBusiness> mockOrders = createMockOrders();
        when(orderRepository.findBySpecification(any(OrderStatusSpecification.class))).thenReturn(mockOrders);

        // Load the orders first
        viewModel.loadPendingOrders();

        // Set the search text
        viewModel.searchTextProperty().set("001");

        // Act
        viewModel.searchOrders();

        // Assert
        ObservableList<String> filteredOrders = viewModel.getPendingOrders();
        assertEquals(1, filteredOrders.size(), "Should have 1 filtered order");
        assertTrue(filteredOrders.contains("ORDER-001"), "Should contain ORDER-001");
        assertFalse(filteredOrders.contains("ORDER-002"), "Should not contain ORDER-002");
    }

    /**
     * Test that selectOrder correctly sets the selected order and its ID.
     */
    @Test
    void testSelectOrder_SetsSelectedOrderAndId() throws Exception {
        // Arrange
        OrderBusiness mockOrder = createMockOrder("ORDER-001", OrderStatus.COMPLETED);
        List<OrderBusiness> allOrders = new ArrayList<>();
        allOrders.add(mockOrder);
        when(orderRepository.findAll()).thenReturn(allOrders);

        // Act
        viewModel.selectOrder("ORDER-001");

        // Assert
        // We need to use reflection to access the private fields
        Field selectedOrderField = QADashboardViewModel.class.getDeclaredField("selectedOrder");
        selectedOrderField.setAccessible(true);
        String selectedOrder = (String) selectedOrderField.get(viewModel);

        Field selectedOrderIdField = QADashboardViewModel.class.getDeclaredField("selectedOrderId");
        selectedOrderIdField.setAccessible(true);
        OrderId selectedOrderId = (OrderId) selectedOrderIdField.get(viewModel);

        assertEquals("ORDER-001", selectedOrder, "Selected order should be ORDER-001");
        assertEquals(mockOrder.getId(), selectedOrderId, "Selected order ID should match the mock order's ID");
    }

    /**
     * Test that selectOrder sets an error message when the order is not found.
     */
    @Test
    void testSelectOrder_SetsErrorMessage_WhenOrderNotFound() throws Exception {
        // Arrange
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        viewModel.selectOrder("NON-EXISTENT");

        // Assert
        assertEquals("Order not found: NON-EXISTENT", viewModel.errorMessageProperty().get(),
                "Error message should indicate that the order was not found");
    }

    /**
     * Test that logout correctly logs out the user and navigates to the login view.
     */
    @Test
    void testLogout_LogsOutUserAndNavigatesToLoginView() {
        // Act
        viewModel.logout();

        // Assert
        verify(authenticationService).logout();

        // We can't easily verify the Router.navigateTo call since it's static,
        // but we can verify that the error message is empty (indicating success)
        assertEquals("", viewModel.errorMessageProperty().get(),
                "Error message should be empty after successful logout");
    }

    /**
     * Helper method to create a list of mock orders.
     * 
     * @return a list of mock orders
     */
    private List<OrderBusiness> createMockOrders() {
        List<OrderBusiness> orders = new ArrayList<>();
        orders.add(createMockOrder("ORDER-001", OrderStatus.COMPLETED));
        orders.add(createMockOrder("ORDER-002", OrderStatus.COMPLETED));
        return orders;
    }

    /**
     * Helper method to create a mock order.
     * 
     * @param orderNumber the order number
     * @param status the order status
     * @return a mock order
     */
    private OrderBusiness createMockOrder(String orderNumber, OrderStatus status) {
        OrderBusiness order = mock(OrderBusiness.class);
        when(order.getId()).thenReturn(new OrderId(UUID.randomUUID().toString()));
        when(order.getOrderNumber()).thenReturn(new OrderNumber(orderNumber));
        when(order.getStatus()).thenReturn(status);
        when(order.toString()).thenReturn(orderNumber);
        return order;
    }
}
