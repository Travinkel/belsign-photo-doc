package com.belman.presentation.base.feature;

import com.belman.presentation.base.BaseView;
import com.belman.presentation.base.BaseViewModel;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

/**
 * Base class for all photo documentation views in the application.
 * Provides common functionality specific to photo documentation views.
 *
 * @param <T> The type of ViewModel associated with this view
 */
public abstract class PhotoDocumentationBaseView<T extends BaseViewModel<?>> extends BaseView<T> {

    /**
     * Constructor for the photo documentation base view.
     */
    public PhotoDocumentationBaseView() {
        super();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        // Common AppBar setup for photo documentation views
        appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()));
        
        // Set default title - can be overridden by subclasses
        appBar.setTitleText(getDefaultTitle());
        
        // Allow subclasses to add additional items
        customizeAppBar(appBar);
    }

    /**
     * Gets the default title for this view.
     * Can be overridden by subclasses to provide a specific title.
     *
     * @return the default title
     */
    protected String getDefaultTitle() {
        return "Photo Documentation";
    }

    /**
     * Allows subclasses to customize the AppBar beyond the common setup.
     * Default implementation does nothing.
     *
     * @param appBar the AppBar to customize
     */
    protected void customizeAppBar(AppBar appBar) {
        // Default implementation does nothing
    }

    /**
     * Navigates back to the previous view.
     * This method should be overridden by subclasses to provide specific navigation behavior.
     */
    protected void navigateBack() {
        // Default implementation uses the standard back navigation
        // Subclasses should override this method if they need specific navigation behavior
        com.belman.presentation.core.ViewStackManager.getInstance().navigateBack();
    }
    
    /**
     * Shows a confirmation dialog before navigating away if there are unsaved changes.
     * This method should be called by subclasses before navigating away from the view.
     * 
     * @param hasUnsavedChanges whether there are unsaved changes
     * @param navigateAction the action to perform if the user confirms navigation
     */
    protected void confirmNavigationIfNeeded(boolean hasUnsavedChanges, Runnable navigateAction) {
        if (hasUnsavedChanges) {
            // Show confirmation dialog
            try {
                com.belman.presentation.components.UIComponentUtils.showConfirmation(
                    this, 
                    "Unsaved Changes", 
                    "You have unsaved changes. Are you sure you want to leave this page?",
                    "Leave", 
                    "Stay", 
                    confirmed -> {
                        if (confirmed) {
                            navigateAction.run();
                        }
                    }
                );
            } catch (Exception e) {
                System.err.println("Error showing confirmation dialog: " + e.getMessage());
                // Fall back to direct navigation if dialog fails
                navigateAction.run();
            }
        } else {
            // No unsaved changes, navigate directly
            navigateAction.run();
        }
    }
}