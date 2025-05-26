package com.belman.presentation.usecases.worker;

/**
 * Represents the possible states of the Worker Flow.
 * This enum is used to explicitly track the current state of the worker flow process,
 * making state transitions more visible and easier to validate.
 */
public enum WorkerFlowState {
    /**
     * Initial state when no order is selected
     */
    INITIAL,

    /**
     * State when an order has been selected but no template has been selected
     */
    ORDER_SELECTED,

    /**
     * State when a template has been selected but no photos have been taken
     */
    TEMPLATE_SELECTED,

    /**
     * State when photos are being captured
     */
    PHOTO_CAPTURE,

    /**
     * State when photos are being reviewed
     */
    PHOTO_REVIEW,

    /**
     * State when all required photos have been taken and the order is being completed
     */
    COMPLETING_ORDER,

    /**
     * State when the order has been completed
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
    public boolean canTransitionTo(WorkerFlowState targetState) {
        switch (this) {
            case INITIAL:
                // From INITIAL, can only go to ORDER_SELECTED or ERROR
                return targetState == ORDER_SELECTED || targetState == ERROR;
                
            case ORDER_SELECTED:
                // From ORDER_SELECTED, can go to TEMPLATE_SELECTED, INITIAL, or ERROR
                return targetState == TEMPLATE_SELECTED || targetState == INITIAL || targetState == ERROR;
                
            case TEMPLATE_SELECTED:
                // From TEMPLATE_SELECTED, can go to PHOTO_CAPTURE, ORDER_SELECTED, or ERROR
                return targetState == PHOTO_CAPTURE || targetState == ORDER_SELECTED || targetState == ERROR;
                
            case PHOTO_CAPTURE:
                // From PHOTO_CAPTURE, can go to PHOTO_REVIEW, TEMPLATE_SELECTED, or ERROR
                return targetState == PHOTO_REVIEW || targetState == TEMPLATE_SELECTED || targetState == ERROR;
                
            case PHOTO_REVIEW:
                // From PHOTO_REVIEW, can go to COMPLETING_ORDER, PHOTO_CAPTURE, TEMPLATE_SELECTED, or ERROR
                return targetState == COMPLETING_ORDER || targetState == PHOTO_CAPTURE || 
                       targetState == TEMPLATE_SELECTED || targetState == ERROR;
                
            case COMPLETING_ORDER:
                // From COMPLETING_ORDER, can go to COMPLETED or ERROR
                return targetState == COMPLETED || targetState == ERROR;
                
            case COMPLETED:
                // From COMPLETED, can go back to INITIAL or ERROR
                return targetState == INITIAL || targetState == ERROR;
                
            case ERROR:
                // From ERROR, can go to any state
                return true;
                
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
            case INITIAL:
                return "Ready to select an order";
                
            case ORDER_SELECTED:
                return "Order selected, ready to select a template";
                
            case TEMPLATE_SELECTED:
                return "Template selected, ready to capture photos";
                
            case PHOTO_CAPTURE:
                return "Capturing photos";
                
            case PHOTO_REVIEW:
                return "Reviewing photos";
                
            case COMPLETING_ORDER:
                return "Completing order";
                
            case COMPLETED:
                return "Order completed";
                
            case ERROR:
                return "An error occurred";
                
            default:
                return "Unknown state";
        }
    }
}