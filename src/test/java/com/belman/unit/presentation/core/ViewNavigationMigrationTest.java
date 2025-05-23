package com.belman.unit.presentation.core;

import com.belman.presentation.core.ViewStackManager;
import com.belman.presentation.usecases.admin.dashboard.AdminDashboardView;
import com.belman.presentation.usecases.qa.dashboard.QADashboardView;
import com.belman.presentation.usecases.worker.dashboard.WorkerDashboardView;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for navigation between views during and after the migration.
 * These tests verify that navigation works correctly with the new package structure.
 */
public class ViewNavigationMigrationTest {

    private ViewStackManager viewStackManager;

    @BeforeEach
    public void setUp() {
        // Get the singleton instance of ViewStackManager
        viewStackManager = ViewStackManager.getInstance();
        
        // Clear the view stack to start with a clean state
        viewStackManager.clearStack();
        
        System.out.println("[DEBUG_LOG] Set up ViewStackManager for testing");
    }

    @Test
    @DisplayName("Should navigate between admin views")
    public void shouldNavigateBetweenAdminViews() {
        try {
            System.out.println("[DEBUG_LOG] Testing navigation between admin views");
            
            // Register and show the admin dashboard view
            viewStackManager.registerView(AdminDashboardView.class);
            viewStackManager.show(AdminDashboardView.class);
            
            // Verify the current view is AdminDashboardView
            assertEquals(AdminDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be AdminDashboardView");
            
            // Navigate to another admin view (if available) or use a different view for testing
            viewStackManager.registerView(QADashboardView.class);
            viewStackManager.show(QADashboardView.class);
            
            // Verify the current view is the second view
            assertEquals(QADashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be QADashboardView");
            
            // Navigate back
            viewStackManager.navigateBack();
            
            // Verify we're back to the admin dashboard view
            assertEquals(AdminDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be AdminDashboardView after navigating back");
            
            System.out.println("[DEBUG_LOG] Successfully navigated between admin views");
        } catch (Exception e) {
            fail("Failed to navigate between admin views: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should navigate between worker views")
    public void shouldNavigateBetweenWorkerViews() {
        try {
            System.out.println("[DEBUG_LOG] Testing navigation between worker views");
            
            // Register and show the worker dashboard view
            viewStackManager.registerView(WorkerDashboardView.class);
            viewStackManager.show(WorkerDashboardView.class);
            
            // Verify the current view is WorkerDashboardView
            assertEquals(WorkerDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be WorkerDashboardView");
            
            // Navigate to the photo cube view
            viewStackManager.registerView(PhotoCubeView.class);
            viewStackManager.show(PhotoCubeView.class);
            
            // Verify the current view is PhotoCubeView
            assertEquals(PhotoCubeView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be PhotoCubeView");
            
            // Navigate back
            viewStackManager.navigateBack();
            
            // Verify we're back to the worker dashboard view
            assertEquals(WorkerDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be WorkerDashboardView after navigating back");
            
            System.out.println("[DEBUG_LOG] Successfully navigated between worker views");
        } catch (Exception e) {
            fail("Failed to navigate between worker views: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should navigate across different user roles")
    public void shouldNavigateAcrossDifferentUserRoles() {
        try {
            System.out.println("[DEBUG_LOG] Testing navigation across different user roles");
            
            // Register and show the admin dashboard view
            viewStackManager.registerView(AdminDashboardView.class);
            viewStackManager.show(AdminDashboardView.class);
            
            // Verify the current view is AdminDashboardView
            assertEquals(AdminDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be AdminDashboardView");
            
            // Navigate to the QA dashboard view
            viewStackManager.registerView(QADashboardView.class);
            viewStackManager.show(QADashboardView.class);
            
            // Verify the current view is QADashboardView
            assertEquals(QADashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be QADashboardView");
            
            // Navigate to the worker dashboard view
            viewStackManager.registerView(WorkerDashboardView.class);
            viewStackManager.show(WorkerDashboardView.class);
            
            // Verify the current view is WorkerDashboardView
            assertEquals(WorkerDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be WorkerDashboardView");
            
            // Navigate back twice
            viewStackManager.navigateBack(); // Back to QADashboardView
            viewStackManager.navigateBack(); // Back to AdminDashboardView
            
            // Verify we're back to the admin dashboard view
            assertEquals(AdminDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be AdminDashboardView after navigating back twice");
            
            System.out.println("[DEBUG_LOG] Successfully navigated across different user roles");
        } catch (Exception e) {
            fail("Failed to navigate across different user roles: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should handle navigation with unregistered views")
    public void shouldHandleNavigationWithUnregisteredViews() {
        try {
            System.out.println("[DEBUG_LOG] Testing navigation with unregistered views");
            
            // Try to show an unregistered view
            assertThrows(IllegalArgumentException.class, () -> {
                viewStackManager.show(AdminDashboardView.class);
            }, "Should throw IllegalArgumentException when showing unregistered view");
            
            // Register and show a view
            viewStackManager.registerView(AdminDashboardView.class);
            viewStackManager.show(AdminDashboardView.class);
            
            // Verify the current view is AdminDashboardView
            assertEquals(AdminDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should be AdminDashboardView");
            
            // Try to navigate back when there's only one view in the stack
            viewStackManager.navigateBack();
            
            // Verify we're still on the admin dashboard view
            assertEquals(AdminDashboardView.class, viewStackManager.getCurrentView().getClass(),
                    "Current view should still be AdminDashboardView after attempting to navigate back");
            
            System.out.println("[DEBUG_LOG] Successfully handled navigation with unregistered views");
        } catch (Exception e) {
            fail("Failed to handle navigation with unregistered views: " + e.getMessage());
        }
    }
}