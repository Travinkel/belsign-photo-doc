package com.belman.unit.presentation.usecases.worker.photocube;

import com.belman.domain.photo.PhotoTemplate;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.lifecycle.ViewModelLifecycle;

/**
 * A test-specific implementation of the PhotoCubeViewModel
 * that extends BaseViewModel to be compatible with BaseController.
 * 
 * This class provides the minimal functionality needed for testing
 * the PhotoCubeViewController's logic.
 */
public class TestablePhotoCubeViewModel extends BaseViewModel<TestablePhotoCubeViewModel> {

    private boolean showRemainingOnly = false;
    private PhotoTemplate lastSelectedTemplate = null;
    private boolean lastRemainingTemplateResult = false;

    /**
     * Sets whether to show only remaining templates.
     * 
     * @param showRemainingOnly whether to show only remaining templates
     */
    public void setShowRemainingOnly(boolean showRemainingOnly) {
        this.showRemainingOnly = showRemainingOnly;
    }

    /**
     * Gets whether to show only remaining templates.
     * 
     * @return whether to show only remaining templates
     */
    public boolean getShowRemainingOnly() {
        return showRemainingOnly;
    }

    /**
     * Selects a photo template for capture.
     * 
     * @param template the template to select
     */
    public void selectTemplate(PhotoTemplate template) {
        this.lastSelectedTemplate = template;
    }

    /**
     * Gets the last selected template.
     * 
     * @return the last selected template
     */
    public PhotoTemplate getLastSelectedTemplate() {
        return lastSelectedTemplate;
    }

    /**
     * Sets the result to return from isLastRemainingTemplate.
     * 
     * @param result the result to return
     */
    public void setLastRemainingTemplateResult(boolean result) {
        this.lastRemainingTemplateResult = result;
    }

    /**
     * Checks if the given template is the last remaining template in the filtered list.
     * 
     * @param template the template to check
     * @return the result set by setLastRemainingTemplateResult
     */
    public boolean isLastRemainingTemplate(PhotoTemplate template) {
        return lastRemainingTemplateResult;
    }
}
