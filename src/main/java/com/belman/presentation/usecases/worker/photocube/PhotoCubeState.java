package com.belman.presentation.usecases.worker.photocube;

/**
 * Represents the possible states of the PhotoCube workflow.
 * This enum is used to explicitly track the current state of the photo capture process,
 * making state transitions more visible and easier to validate.
 */
public enum PhotoCubeState {
    /**
     * Initial state when the view is loading data (orders, templates, etc.)
     */
    LOADING,

    /**
     * State when the user is selecting a template to capture
     */
    SELECTING_TEMPLATE,

    /**
     * State when the camera preview is active but no photo has been captured yet
     */
    CAMERA_PREVIEW,

    /**
     * State when a photo is being captured (between pressing the capture button and saving the photo)
     */
    CAPTURING_PHOTO,

    /**
     * State when the user is reviewing a captured photo before accepting or retaking it
     */
    REVIEWING_PHOTO,

    /**
     * State when all required photos have been captured and the user can proceed to summary
     */
    COMPLETED,

    /**
     * Error state when something goes wrong during the process
     */
    ERROR;

    /**
     * Checks if a transition from the current state to the target state is valid.
     *
     * @param targetState the state to transition to
     * @return true if the transition is valid, false otherwise
     */
    public boolean canTransitionTo(PhotoCubeState targetState) {
        switch (this) {
            case LOADING:
                // From LOADING, can only go to SELECTING_TEMPLATE or ERROR
                return targetState == SELECTING_TEMPLATE || targetState == ERROR;
                
            case SELECTING_TEMPLATE:
                // From SELECTING_TEMPLATE, can go to CAMERA_PREVIEW, COMPLETED, or ERROR
                return targetState == CAMERA_PREVIEW || targetState == COMPLETED || targetState == ERROR;
                
            case CAMERA_PREVIEW:
                // From CAMERA_PREVIEW, can go to CAPTURING_PHOTO, SELECTING_TEMPLATE, or ERROR
                return targetState == CAPTURING_PHOTO || targetState == SELECTING_TEMPLATE || targetState == ERROR;
                
            case CAPTURING_PHOTO:
                // From CAPTURING_PHOTO, can go to REVIEWING_PHOTO or ERROR
                return targetState == REVIEWING_PHOTO || targetState == ERROR;
                
            case REVIEWING_PHOTO:
                // From REVIEWING_PHOTO, can go to SELECTING_TEMPLATE, CAMERA_PREVIEW, COMPLETED, or ERROR
                return targetState == SELECTING_TEMPLATE || targetState == CAMERA_PREVIEW || 
                       targetState == COMPLETED || targetState == ERROR;
                
            case COMPLETED:
                // From COMPLETED, can go back to SELECTING_TEMPLATE or ERROR
                return targetState == SELECTING_TEMPLATE || targetState == ERROR;
                
            case ERROR:
                // From ERROR, can go to any state except CAPTURING_PHOTO
                return targetState != CAPTURING_PHOTO;
                
            default:
                return false;
        }
    }

    /**
     * Returns a user-friendly description of the current state.
     *
     * @return a string describing the current state
     */
    public String getDescription() {
        switch (this) {
            case LOADING:
                return "Loading data...";
                
            case SELECTING_TEMPLATE:
                return "Select a template to capture";
                
            case CAMERA_PREVIEW:
                return "Position the camera and tap 'Capture' when ready";
                
            case CAPTURING_PHOTO:
                return "Capturing photo...";
                
            case REVIEWING_PHOTO:
                return "Review the captured photo";
                
            case COMPLETED:
                return "All required photos have been captured";
                
            case ERROR:
                return "An error occurred";
                
            default:
                return "Unknown state";
        }
    }
}