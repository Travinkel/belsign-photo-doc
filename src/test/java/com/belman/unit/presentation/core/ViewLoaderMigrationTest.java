package com.belman.unit.presentation.core;

import com.belman.presentation.core.ViewLoader;
import com.belman.presentation.usecases.admin.dashboard.AdminDashboardView;
import com.belman.presentation.usecases.qa.dashboard.QADashboardView;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ViewLoader class focusing on the migration from old to new package structure.
 * These tests verify that the ViewLoader can load views from both old and new locations.
 */
public class ViewLoaderMigrationTest {

    @Test
    @DisplayName("Should load admin dashboard view from new location")
    public void shouldLoadAdminDashboardViewFromNewLocation() {
        try {
            System.out.println("[DEBUG_LOG] Testing loading AdminDashboardView from new location");
            var components = ViewLoader.load(AdminDashboardView.class);
            assertNotNull(components, "Loaded components should not be null");
            assertNotNull(components.parent(), "Parent should not be null");
            assertNotNull(components.controller(), "Controller should not be null");
            assertNotNull(components.viewModel(), "ViewModel should not be null");
            System.out.println("[DEBUG_LOG] Successfully loaded AdminDashboardView from new location");
        } catch (Exception e) {
            fail("Failed to load AdminDashboardView: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should load QA dashboard view from new location")
    public void shouldLoadQADashboardViewFromNewLocation() {
        try {
            System.out.println("[DEBUG_LOG] Testing loading QADashboardView from new location");
            var components = ViewLoader.load(QADashboardView.class);
            assertNotNull(components, "Loaded components should not be null");
            assertNotNull(components.parent(), "Parent should not be null");
            assertNotNull(components.controller(), "Controller should not be null");
            assertNotNull(components.viewModel(), "ViewModel should not be null");
            System.out.println("[DEBUG_LOG] Successfully loaded QADashboardView from new location");
        } catch (Exception e) {
            fail("Failed to load QADashboardView: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should load worker photo cube view from new location")
    public void shouldLoadWorkerPhotoCubeViewFromNewLocation() {
        try {
            System.out.println("[DEBUG_LOG] Testing loading PhotoCubeView from new location");
            var components = ViewLoader.load(PhotoCubeView.class);
            assertNotNull(components, "Loaded components should not be null");
            assertNotNull(components.parent(), "Parent should not be null");
            assertNotNull(components.controller(), "Controller should not be null");
            assertNotNull(components.viewModel(), "ViewModel should not be null");
            System.out.println("[DEBUG_LOG] Successfully loaded PhotoCubeView from new location");
        } catch (Exception e) {
            fail("Failed to load PhotoCubeView: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should load login view from login package")
    public void shouldHandleLoginViewFromBothLocations() {
        try {
            System.out.println("[DEBUG_LOG] Testing loading LoginView from login package");

            // Load from login package
            var components = ViewLoader.load(com.belman.presentation.usecases.login.LoginView.class);
            assertNotNull(components, "Loaded components should not be null");
            assertNotNull(components.parent(), "Parent should not be null");
            assertNotNull(components.controller(), "Controller should not be null");
            assertNotNull(components.viewModel(), "ViewModel should not be null");
            System.out.println("[DEBUG_LOG] Successfully loaded LoginView from login package");

            // Check if old authentication.login package exists (should not exist anymore)
            try {
                Class<?> oldLoginViewClass = Class.forName("com.belman.presentation.usecases.authentication.login.LoginView");
                var oldLoginComponents = ViewLoader.load(oldLoginViewClass);
                assertNotNull(oldLoginComponents, "Loaded components should not be null");
                System.out.println("[DEBUG_LOG] Successfully loaded LoginView from authentication.login package (unexpected)");
            } catch (ClassNotFoundException e) {
                System.out.println("[DEBUG_LOG] LoginView class not found in authentication.login package, which is expected after migration");
            }

        } catch (Exception e) {
            fail("Failed to load LoginView: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should create fallback view when FXML not found")
    public void shouldCreateFallbackViewWhenFxmlNotFound() {
        try {
            System.out.println("[DEBUG_LOG] Testing fallback view creation");

            // Create a test class that doesn't have a corresponding FXML file
            class TestView extends com.belman.presentation.base.BaseView<com.belman.presentation.base.BaseViewModel<?>> {
            }

            var components = ViewLoader.load(TestView.class);
            assertNotNull(components, "Loaded components should not be null");
            assertNotNull(components.parent(), "Parent should not be null");
            assertNotNull(components.controller(), "Controller should not be null");
            assertNotNull(components.viewModel(), "ViewModel should not be null");

            // The parent should be a VBox with an error message
            assertTrue(components.parent() instanceof javafx.scene.layout.VBox, 
                    "Parent should be a VBox for fallback view");

            System.out.println("[DEBUG_LOG] Successfully created fallback view");
        } catch (Exception e) {
            fail("Failed to create fallback view: " + e.getMessage());
        }
    }
}
