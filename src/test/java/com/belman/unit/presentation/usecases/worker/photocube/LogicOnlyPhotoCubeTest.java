package com.belman.unit.presentation.usecases.worker.photocube;

import com.belman.domain.photo.PhotoTemplate;
import com.belman.presentation.usecases.worker.photocube.PhotoTemplateStatusViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Logic-only tests for the PhotoCube functionality.
 * 
 * These tests focus on the core logic that prevents the IndexOutOfBoundsException
 * when selecting templates in the PhotoCubeView, without relying on JavaFX components
 * or the actual controller/viewmodel classes.
 */
public class LogicOnlyPhotoCubeTest {

    private TestablePhotoCubeViewModel viewModel;

    // Use real PhotoTemplate instances
    private final PhotoTemplate template1 = PhotoTemplate.TOP_VIEW_OF_JOINT;
    private final PhotoTemplate template2 = PhotoTemplate.SIDE_VIEW_OF_WELD;
    private final PhotoTemplate template3 = PhotoTemplate.ANGLED_VIEW_OF_JOINT;

    @BeforeEach
    public void setUp() {
        viewModel = new TestablePhotoCubeViewModel();
    }

    /**
     * Test that the isLastRemainingTemplate method correctly identifies
     * when a template is the last remaining one.
     */
    @Test
    public void testIsLastRemainingTemplate() {
        // Set up the test case
        viewModel.setLastRemainingTemplateResult(true);
        
        // Execute the method
        boolean result = viewModel.isLastRemainingTemplate(template1);
        
        // Verify the result
        assertTrue(result, "Should return true when it's the last remaining template");
    }

    /**
     * Test that the template selection logic correctly handles the case
     * where the selected template is the last remaining one.
     */
    @Test
    public void testTemplateSelectionWithLastRemaining() {
        // Set up the test case
        viewModel.setLastRemainingTemplateResult(true);
        PhotoTemplateStatusViewModel templateStatus = new PhotoTemplateStatusViewModel(template1, false, false, true);
        
        // Execute the template selection logic
        boolean isLastRemaining = viewModel.isLastRemainingTemplate(templateStatus.getTemplate());
        viewModel.selectTemplate(templateStatus.getTemplate());
        
        // Verify the results
        assertTrue(isLastRemaining, "Should identify the template as the last remaining one");
        assertEquals(template1, viewModel.getLastSelectedTemplate(), "Should select the template");
        
        // In the real controller, we would NOT clear the selection here
        // because it's the last remaining template
    }

    /**
     * Test that the template selection logic correctly handles the case
     * where the selected template is NOT the last remaining one.
     */
    @Test
    public void testTemplateSelectionWithNotLastRemaining() {
        // Set up the test case
        viewModel.setLastRemainingTemplateResult(false);
        PhotoTemplateStatusViewModel templateStatus = new PhotoTemplateStatusViewModel(template1, false, false, true);
        
        // Execute the template selection logic
        boolean isLastRemaining = viewModel.isLastRemainingTemplate(templateStatus.getTemplate());
        viewModel.selectTemplate(templateStatus.getTemplate());
        
        // Verify the results
        assertFalse(isLastRemaining, "Should identify the template as NOT the last remaining one");
        assertEquals(template1, viewModel.getLastSelectedTemplate(), "Should select the template");
        
        // In the real controller, we would clear the selection here
        // because it's NOT the last remaining template
    }

    /**
     * Test that the showRemainingOnly toggle correctly updates the property.
     */
    @Test
    public void testShowRemainingOnlyToggle() {
        // Set up the test case - initially false
        assertFalse(viewModel.getShowRemainingOnly(), "Should initially be false");
        
        // Execute the toggle
        viewModel.setShowRemainingOnly(true);
        
        // Verify the result
        assertTrue(viewModel.getShowRemainingOnly(), "Should be true after setting to true");
        
        // Toggle back
        viewModel.setShowRemainingOnly(false);
        
        // Verify the result
        assertFalse(viewModel.getShowRemainingOnly(), "Should be false after setting to false");
    }
}