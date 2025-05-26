package com.belman.unit.presentation.worker;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.user.UserReference;
import com.belman.presentation.usecases.worker.WorkerFlowContext;
import com.belman.presentation.usecases.worker.WorkerFlowState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the WorkerFlowContext class.
 */
public class WorkerFlowContextTest {

    @Mock
    private OrderBusiness mockOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Reset the WorkerFlowContext to its initial state
        WorkerFlowContext.clear();

        // Set up the mock order
        when(mockOrder.getId()).thenReturn(new OrderId(UUID.randomUUID().toString()));
        when(mockOrder.getOrderNumber()).thenReturn(new OrderNumber("TEST-ORDER-123"));
    }

    /**
     * Test that setting the current order works correctly.
     */
    @Test
    void testSetCurrentOrder() {
        // Set the current order
        WorkerFlowContext.setCurrentOrder(mockOrder);

        // Verify that the order was set correctly
        assertEquals(mockOrder, WorkerFlowContext.getCurrentOrder());
        assertEquals(WorkerFlowState.ORDER_SELECTED, WorkerFlowContext.getCurrentFlowState());

        System.out.println("[DEBUG_LOG] First setCurrentOrder successful");

        // Set the same order again - this should not throw an exception with our fix
        WorkerFlowContext.setCurrentOrder(mockOrder);

        // Verify that the order is still set correctly
        assertEquals(mockOrder, WorkerFlowContext.getCurrentOrder());
        assertEquals(WorkerFlowState.ORDER_SELECTED, WorkerFlowContext.getCurrentFlowState());

        System.out.println("[DEBUG_LOG] Second setCurrentOrder successful - fix is working");
    }

    /**
     * Test that setting the current flow state to the same state works correctly.
     */
    @Test
    void testSetCurrentFlowStateToSameState() {
        // Set the initial state
        WorkerFlowContext.setCurrentFlowState(WorkerFlowState.INITIAL);
        assertEquals(WorkerFlowState.INITIAL, WorkerFlowContext.getCurrentFlowState());

        // Set the state to ORDER_SELECTED
        WorkerFlowContext.setCurrentFlowState(WorkerFlowState.ORDER_SELECTED);
        assertEquals(WorkerFlowState.ORDER_SELECTED, WorkerFlowContext.getCurrentFlowState());

        System.out.println("[DEBUG_LOG] First state transition successful");

        // Set the state to ORDER_SELECTED again - this should not throw an exception with our fix
        WorkerFlowContext.setCurrentFlowState(WorkerFlowState.ORDER_SELECTED);
        assertEquals(WorkerFlowState.ORDER_SELECTED, WorkerFlowContext.getCurrentFlowState());

        System.out.println("[DEBUG_LOG] Second state transition to same state successful - fix is working");
    }
}
