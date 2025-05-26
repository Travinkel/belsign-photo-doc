package com.belman.presentation.usecases.worker;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Static context for sharing data between views in the production worker flow.
 * This class provides a simple way to store and retrieve data that needs to be
 * shared between different views in the flow.
 * 
 * This class includes enhanced error handling, thread safety, and validation.
 * It provides comprehensive error tracking, recovery mechanisms, and proper logging.
 */
public class WorkerFlowContext {
    private static final Logger LOGGER = Logger.getLogger(WorkerFlowContext.class.getName());

    private static OrderBusiness currentOrder;
    private static PhotoTemplate selectedTemplate;
    private static final List<PhotoDocument> takenPhotos = new CopyOnWriteArrayList<>();
    private static final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private static final List<WorkerFlowError> errors = new CopyOnWriteArrayList<>();

    // Enhanced error handling
    private static final Map<ErrorSeverity, List<BiConsumer<WorkerFlowError, WorkerFlowContext>>> errorHandlersBySeverity = new HashMap<>();
    private static Consumer<WorkerFlowError> legacyErrorHandler; // For backward compatibility
    private static boolean recoveryMode = false;

    // State validation
    private static final Map<String, List<String>> validStateTransitions = new HashMap<>();
    private static String currentState;

    // Enhanced state management using WorkerFlowState enum
    private static WorkerFlowState currentFlowState = WorkerFlowState.INITIAL;
    private static final List<Consumer<WorkerFlowState>> stateChangeListeners = new CopyOnWriteArrayList<>();

    // Data validation
    private static final Map<String, List<String>> requiredAttributes = new HashMap<>();
    private static boolean validationEnabled = false;

    /**
     * Represents an error that occurred during the worker flow.
     */
    public static class WorkerFlowError {
        private final String errorId;
        private final String message;
        private final Exception cause;
        private final ErrorSeverity severity;
        private final LocalDateTime timestamp;
        private final Map<String, Object> context;

        /**
         * Creates a new worker flow error.
         * 
         * @param message the error message
         * @param cause the cause of the error, or null if not applicable
         * @param severity the severity of the error
         */
        public WorkerFlowError(String message, Exception cause, ErrorSeverity severity) {
            this(message, cause, severity, new HashMap<>());
        }

        /**
         * Creates a new worker flow error with context information.
         * 
         * @param message the error message
         * @param cause the cause of the error, or null if not applicable
         * @param severity the severity of the error
         * @param context additional context information about the error
         */
        public WorkerFlowError(String message, Exception cause, ErrorSeverity severity, Map<String, Object> context) {
            this.errorId = UUID.randomUUID().toString();
            this.message = message;
            this.cause = cause;
            this.severity = severity;
            this.timestamp = LocalDateTime.now();
            this.context = new HashMap<>(context);
        }

        /**
         * Gets the unique ID of this error.
         * 
         * @return the error ID
         */
        public String getErrorId() {
            return errorId;
        }

        /**
         * Gets the error message.
         * 
         * @return the error message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the cause of the error.
         * 
         * @return the cause of the error, or null if not applicable
         */
        public Exception getCause() {
            return cause;
        }

        /**
         * Gets the severity of the error.
         * 
         * @return the severity of the error
         */
        public ErrorSeverity getSeverity() {
            return severity;
        }

        /**
         * Gets the timestamp when this error occurred.
         * 
         * @return the timestamp
         */
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        /**
         * Gets the context information for this error.
         * 
         * @return an unmodifiable map of context information
         */
        public Map<String, Object> getContext() {
            return Collections.unmodifiableMap(context);
        }

        /**
         * Adds context information to this error.
         * 
         * @param key the context key
         * @param value the context value
         */
        public void addContext(String key, Object value) {
            context.put(key, value);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(severity).append(" [").append(errorId).append("] at ").append(timestamp);
            sb.append(": ").append(message);

            if (cause != null) {
                sb.append(" (").append(cause.getMessage()).append(")");
            }

            if (!context.isEmpty()) {
                sb.append(" - Context: ").append(context);
            }

            return sb.toString();
        }
    }

    /**
     * Represents the severity of a worker flow error.
     */
    public enum ErrorSeverity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    /**
     * Sets the error handler that will be called when an error is reported.
     * This method is maintained for backward compatibility.
     * 
     * @param handler the error handler
     * @see #addErrorHandler(ErrorSeverity, BiConsumer)
     */
    public static void setErrorHandler(Consumer<WorkerFlowError> handler) {
        LOGGER.log(Level.INFO, "Setting legacy error handler: {0}", 
                  (handler != null ? "provided" : "null"));
        legacyErrorHandler = handler;
    }

    /**
     * Adds an error handler for a specific severity level.
     * 
     * @param severity the severity level to handle
     * @param handler the error handler
     */
    public static void addErrorHandler(ErrorSeverity severity, BiConsumer<WorkerFlowError, WorkerFlowContext> handler) {
        Objects.requireNonNull(severity, "Severity must not be null");
        Objects.requireNonNull(handler, "Handler must not be null");

        LOGGER.log(Level.INFO, "Adding error handler for severity: {0}", severity);

        errorHandlersBySeverity.computeIfAbsent(severity, k -> new ArrayList<>()).add(handler);
    }

    /**
     * Removes all error handlers for a specific severity level.
     * 
     * @param severity the severity level
     */
    public static void clearErrorHandlers(ErrorSeverity severity) {
        Objects.requireNonNull(severity, "Severity must not be null");

        LOGGER.log(Level.INFO, "Clearing error handlers for severity: {0}", severity);

        errorHandlersBySeverity.remove(severity);
    }

    /**
     * Removes all error handlers.
     */
    public static void clearAllErrorHandlers() {
        LOGGER.log(Level.INFO, "Clearing all error handlers");

        errorHandlersBySeverity.clear();
        legacyErrorHandler = null;
    }

    /**
     * Reports an error that occurred during the worker flow.
     * 
     * @param message the error message
     * @param cause the cause of the error, or null if not applicable
     * @param severity the severity of the error
     * @return the created error object
     */
    public static WorkerFlowError reportError(String message, Exception cause, ErrorSeverity severity) {
        return reportError(message, cause, severity, new HashMap<>());
    }

    /**
     * Reports an error that occurred during the worker flow with additional context information.
     * 
     * @param message the error message
     * @param cause the cause of the error, or null if not applicable
     * @param severity the severity of the error
     * @param context additional context information about the error
     * @return the created error object
     */
    public static WorkerFlowError reportError(String message, Exception cause, ErrorSeverity severity, Map<String, Object> context) {
        WorkerFlowError error = new WorkerFlowError(message, cause, severity, context);
        errors.add(error);

        // Add current state information to the error context
        if (currentOrder != null) {
            error.addContext("currentOrderId", currentOrder.getId().id());
            if (currentOrder.getOrderNumber() != null) {
                error.addContext("currentOrderNumber", currentOrder.getOrderNumber().value());
            }
        }

        if (selectedTemplate != null) {
            error.addContext("selectedTemplate", selectedTemplate.name());
        }

        error.addContext("takenPhotosCount", takenPhotos.size());
        error.addContext("recoveryMode", recoveryMode);

        LOGGER.log(mapSeverityToLogLevel(severity), "Reported error: {0}", error);

        // Call severity-specific handlers
        List<BiConsumer<WorkerFlowError, WorkerFlowContext>> handlers = errorHandlersBySeverity.get(severity);
        if (handlers != null) {
            for (BiConsumer<WorkerFlowError, WorkerFlowContext> handler : handlers) {
                try {
                    handler.accept(error, WorkerFlowContext.class.cast(null)); // Pass null as context for now
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error in error handler: " + e.getMessage(), e);
                }
            }
        }

        // Call legacy handler for backward compatibility
        if (legacyErrorHandler != null) {
            try {
                legacyErrorHandler.accept(error);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error in legacy error handler: " + e.getMessage(), e);
            }
        }

        // Automatically enter recovery mode for critical errors
        if (severity == ErrorSeverity.CRITICAL && !recoveryMode) {
            enterRecoveryMode();
        }

        return error;
    }

    /**
     * Maps an error severity to a log level.
     * 
     * @param severity the error severity
     * @return the corresponding log level
     */
    private static Level mapSeverityToLogLevel(ErrorSeverity severity) {
        switch (severity) {
            case INFO:
                return Level.INFO;
            case WARNING:
                return Level.WARNING;
            case ERROR:
                return Level.SEVERE;
            case CRITICAL:
                return Level.SEVERE;
            default:
                return Level.INFO;
        }
    }

    /**
     * Enters recovery mode, which enables automatic recovery mechanisms for certain operations.
     */
    public static void enterRecoveryMode() {
        LOGGER.log(Level.WARNING, "Entering recovery mode");
        recoveryMode = true;
    }

    /**
     * Exits recovery mode.
     */
    public static void exitRecoveryMode() {
        LOGGER.log(Level.INFO, "Exiting recovery mode");
        recoveryMode = false;
    }

    /**
     * Checks if the context is in recovery mode.
     * 
     * @return true if in recovery mode, false otherwise
     */
    public static boolean isInRecoveryMode() {
        return recoveryMode;
    }

    /**
     * Gets all errors that have been reported.
     * 
     * @return an unmodifiable list of errors
     */
    public static List<WorkerFlowError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Clears all reported errors.
     */
    public static void clearErrors() {
        LOGGER.log(Level.INFO, "Clearing {0} errors", errors.size());
        errors.clear();
    }

    /**
     * Sets the current order.
     *
     * @param order the order to set
     * @throws IllegalArgumentException if the order is null
     */
    public static void setCurrentOrder(OrderBusiness order) {
        try {
            Objects.requireNonNull(order, "Order must not be null");

            // Validate state transition if state validation is enabled
            if (currentState != null) {
                validateStateTransition("ORDER_SELECTED");
                setState("ORDER_SELECTED");
            }

            // Update the flow state
            setCurrentFlowState(WorkerFlowState.ORDER_SELECTED);

            LOGGER.log(Level.INFO, "Setting current order: ID={0}, Number={1}", 
                      new Object[]{order.getId().id(), 
                                  (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null")});
            currentOrder = order;
        } catch (Exception e) {
            reportError("Failed to set current order", e, ErrorSeverity.ERROR);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Gets the current order.
     *
     * @return the current order
     * @throws IllegalStateException if no order is set
     */
    public static OrderBusiness getCurrentOrder() {
        try {
            if (currentOrder == null) {
                throw new IllegalStateException("No current order is set");
            }

            LOGGER.log(Level.FINE, "Getting current order: ID={0}, Number={1}", 
                      new Object[]{currentOrder.getId().id(), 
                                  (currentOrder.getOrderNumber() != null ? currentOrder.getOrderNumber().value() : "null")});
            return currentOrder;
        } catch (Exception e) {
            reportError("Failed to get current order", e, ErrorSeverity.WARNING);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Sets the selected template.
     *
     * @param template the template to set
     * @throws IllegalArgumentException if the template is null
     */
    public static void setSelectedTemplate(PhotoTemplate template) {
        try {
            Objects.requireNonNull(template, "Template must not be null");

            // Validate state transition if state validation is enabled
            if (currentState != null) {
                validateStateTransition("TEMPLATE_SELECTED");
                setState("TEMPLATE_SELECTED");
            }

            // Update the flow state
            setCurrentFlowState(WorkerFlowState.TEMPLATE_SELECTED);

            LOGGER.log(Level.INFO, "Setting selected template: {0}", template.name());
            selectedTemplate = template;
        } catch (Exception e) {
            reportError("Failed to set selected template", e, ErrorSeverity.ERROR);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Gets the selected template.
     *
     * @return the selected template
     * @throws IllegalStateException if no template is selected
     */
    public static PhotoTemplate getSelectedTemplate() {
        try {
            if (selectedTemplate == null) {
                throw new IllegalStateException("No template is selected");
            }

            LOGGER.log(Level.FINE, "Getting selected template: {0}", selectedTemplate.name());
            return selectedTemplate;
        } catch (Exception e) {
            reportError("Failed to get selected template", e, ErrorSeverity.WARNING);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Adds a photo to the list of taken photos.
     *
     * @param photo the photo to add
     * @throws IllegalArgumentException if the photo is null
     */
    public static void addTakenPhoto(PhotoDocument photo) {
        try {
            Objects.requireNonNull(photo, "Photo must not be null");
            Objects.requireNonNull(photo.getPhotoId(), "Photo ID must not be null");
            Objects.requireNonNull(photo.getTemplate(), "Photo template must not be null");

            // Validate state transition if state validation is enabled
            if (currentState != null) {
                validateStateTransition("PHOTO_CAPTURE");
                setState("PHOTO_CAPTURE");
            }

            // Update the flow state
            setCurrentFlowState(WorkerFlowState.PHOTO_CAPTURE);

            LOGGER.log(Level.INFO, "Adding taken photo: ID={0}, Template={1}", 
                      new Object[]{photo.getPhotoId().id(), photo.getTemplate().name()});
            takenPhotos.add(photo);
            LOGGER.log(Level.INFO, "Total taken photos now: {0}", takenPhotos.size());
        } catch (Exception e) {
            reportError("Failed to add taken photo", e, ErrorSeverity.ERROR);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Gets the list of taken photos.
     *
     * @return an unmodifiable list of taken photos
     */
    public static List<PhotoDocument> getTakenPhotos() {
        try {
            // Validate state transition if state validation is enabled
            if (currentState != null) {
                validateStateTransition("PHOTO_REVIEW");
                setState("PHOTO_REVIEW");
            }

            // Update the flow state
            setCurrentFlowState(WorkerFlowState.PHOTO_REVIEW);

            LOGGER.log(Level.FINE, "Getting taken photos list, count: {0}", takenPhotos.size());
            return Collections.unmodifiableList(takenPhotos);
        } catch (Exception e) {
            reportError("Failed to get taken photos", e, ErrorSeverity.WARNING);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Sets an attribute.
     *
     * @param key the key
     * @param value the value
     * @throws IllegalArgumentException if the key is null or empty
     */
    public static void setAttribute(String key, Object value) {
        try {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("Attribute key must not be null or empty");
            }

            LOGGER.log(Level.FINE, "Setting attribute: key={0}, value={1}", 
                      new Object[]{key, (value != null ? value.toString() : "null")});
            attributes.put(key, value);
        } catch (Exception e) {
            reportError("Failed to set attribute: " + key, e, ErrorSeverity.ERROR);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Gets an attribute.
     *
     * @param key the key
     * @return the value, or null if not found
     * @throws IllegalArgumentException if the key is null or empty
     */
    public static Object getAttribute(String key) {
        try {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("Attribute key must not be null or empty");
            }

            Object value = attributes.get(key);
            LOGGER.log(Level.FINE, "Getting attribute: key={0}, value={1}", 
                      new Object[]{key, (value != null ? value.toString() : "null")});
            return value;
        } catch (Exception e) {
            reportError("Failed to get attribute: " + key, e, ErrorSeverity.WARNING);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Gets an attribute with type safety.
     *
     * @param <T> the expected type of the attribute
     * @param key the key
     * @param type the class of the expected type
     * @return the value cast to the expected type, or null if not found
     * @throws IllegalArgumentException if the key is null or empty
     * @throws ClassCastException if the attribute is not of the expected type
     */
    public static <T> T getAttribute(String key, Class<T> type) {
        try {
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("Attribute key must not be null or empty");
            }

            Objects.requireNonNull(type, "Type must not be null");

            Object value = attributes.get(key);
            if (value == null) {
                LOGGER.log(Level.FINE, "Getting typed attribute: key={0}, type={1}, value=null", 
                          new Object[]{key, type.getSimpleName()});
                return null;
            }

            if (!type.isInstance(value)) {
                throw new ClassCastException("Attribute '" + key + "' is not of type " + type.getSimpleName() + 
                                           " but of type " + value.getClass().getSimpleName());
            }

            T typedValue = type.cast(value);
            LOGGER.log(Level.FINE, "Getting typed attribute: key={0}, type={1}, value={2}", 
                      new Object[]{key, type.getSimpleName(), typedValue});
            return typedValue;
        } catch (Exception e) {
            reportError("Failed to get typed attribute: " + key, e, ErrorSeverity.WARNING);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Clears all data in the context.
     */
    public static void clear() {
        try {
            LOGGER.log(Level.INFO, "Clearing all context data");

            if (currentOrder != null) {
                LOGGER.log(Level.INFO, "Clearing current order: ID={0}, Number={1}", 
                          new Object[]{currentOrder.getId().id(), 
                                      (currentOrder.getOrderNumber() != null ? currentOrder.getOrderNumber().value() : "null")});
            }

            if (selectedTemplate != null) {
                LOGGER.log(Level.INFO, "Clearing selected template: {0}", selectedTemplate.name());
            }

            LOGGER.log(Level.INFO, "Clearing {0} taken photos", takenPhotos.size());
            LOGGER.log(Level.INFO, "Clearing {0} attributes", attributes.size());

            currentOrder = null;
            selectedTemplate = null;
            takenPhotos.clear();
            attributes.clear();
            // Note: We don't clear errors here, as they might be useful for debugging

            // Reset state if state validation is enabled
            if (currentState != null) {
                currentState = "INITIAL";
                LOGGER.log(Level.INFO, "Reset state to: {0}", currentState);
            }

            // Reset flow state
            WorkerFlowState oldState = currentFlowState;
            currentFlowState = WorkerFlowState.INITIAL;
            LOGGER.log(Level.INFO, "Reset flow state from {0} to {1}", new Object[]{oldState, currentFlowState});

            // Exit recovery mode if active
            if (recoveryMode) {
                exitRecoveryMode();
            }
        } catch (Exception e) {
            reportError("Failed to clear context", e, ErrorSeverity.ERROR);
            throw e; // Re-throw to maintain backward compatibility
        }
    }

    /**
     * Initializes the state validation system with valid state transitions.
     * This should be called during application startup.
     */
    public static void initializeStateValidation() {
        // Define valid state transitions
        validStateTransitions.clear();

        // Initial state can transition to order selection
        validStateTransitions.put("INITIAL", List.of("ORDER_SELECTED"));

        // Order selection can transition to template selection
        validStateTransitions.put("ORDER_SELECTED", List.of("TEMPLATE_SELECTED"));

        // Template selection can transition to photo capture or back to order selection
        validStateTransitions.put("TEMPLATE_SELECTED", List.of("PHOTO_CAPTURE", "ORDER_SELECTED"));

        // Photo capture can transition to photo review or back to template selection
        validStateTransitions.put("PHOTO_CAPTURE", List.of("PHOTO_REVIEW", "TEMPLATE_SELECTED"));

        // Photo review can transition to photo capture, template selection, or completion
        validStateTransitions.put("PHOTO_REVIEW", List.of("PHOTO_CAPTURE", "TEMPLATE_SELECTED", "COMPLETED"));

        // Completed state can transition back to initial state
        validStateTransitions.put("COMPLETED", List.of("INITIAL"));

        // Set initial state
        currentState = "INITIAL";

        LOGGER.log(Level.INFO, "State validation initialized with {0} states", validStateTransitions.size());

        // Also initialize data validation
        initializeDataValidation();
    }

    /**
     * Sets the current state of the worker flow.
     * 
     * @param state the new state
     * @throws IllegalStateException if the state transition is not valid or required data is missing
     */
    public static void setState(String state) {
        Objects.requireNonNull(state, "State must not be null");

        if (currentState == null) {
            // State validation not initialized
            currentState = state;
            LOGGER.log(Level.INFO, "Set initial state to: {0}", state);
            return;
        }

        // Validate state transition
        validateStateTransition(state);

        // Validate required data for the new state
        try {
            validateRequiredDataForState(state);
        } catch (IllegalStateException e) {
            // Report error but don't throw to maintain backward compatibility
            reportError("Cannot transition to state '" + state + "' due to missing required data", 
                       e, ErrorSeverity.WARNING);

            // Log the validation failure
            LOGGER.log(Level.WARNING, "State transition validation failed: {0}", e.getMessage());

            // If in recovery mode, allow the transition despite missing data
            if (!recoveryMode) {
                throw e;
            } else {
                LOGGER.log(Level.WARNING, "Allowing state transition despite missing data (recovery mode)");
            }
        }

        String oldState = currentState;
        currentState = state;

        LOGGER.log(Level.INFO, "State transition: {0} -> {1}", new Object[]{oldState, state});
    }

    /**
     * Gets the current state of the worker flow.
     * 
     * @return the current state, or null if state validation is not initialized
     */
    public static String getState() {
        return currentState;
    }

    /**
     * Validates that a transition to the specified state is valid from the current state.
     * 
     * @param newState the new state to transition to
     * @throws IllegalStateException if the state transition is not valid
     */
    public static void validateStateTransition(String newState) {
        if (currentState == null || newState == null) {
            return; // State validation not initialized or new state is null
        }

        List<String> validNextStates = validStateTransitions.get(currentState);
        if (validNextStates == null) {
            throw new IllegalStateException("Current state '" + currentState + "' has no defined transitions");
        }

        if (!validNextStates.contains(newState)) {
            throw new IllegalStateException("Invalid state transition from '" + currentState + "' to '" + newState + 
                                           "'. Valid transitions are: " + validNextStates);
        }
    }

    /**
     * Checks if state validation is enabled.
     * 
     * @return true if state validation is enabled, false otherwise
     */
    public static boolean isStateValidationEnabled() {
        return currentState != null;
    }

    /**
     * Initializes the data validation system with required attributes for each state.
     * This should be called during application startup.
     */
    public static void initializeDataValidation() {
        // Define required attributes for each state
        requiredAttributes.clear();

        // Initial state has no required attributes
        requiredAttributes.put("INITIAL", Collections.emptyList());

        // Order selected state requires the current order
        requiredAttributes.put("ORDER_SELECTED", List.of("currentOrder"));

        // Template selected state requires the current order and selected template
        requiredAttributes.put("TEMPLATE_SELECTED", List.of("currentOrder", "selectedTemplate"));

        // Photo capture state requires the current order, selected template
        requiredAttributes.put("PHOTO_CAPTURE", List.of("currentOrder", "selectedTemplate"));

        // Photo review state requires the current order, selected template, and at least one photo
        requiredAttributes.put("PHOTO_REVIEW", List.of("currentOrder", "selectedTemplate", "takenPhotos"));

        // Completed state requires the current order and at least one photo
        requiredAttributes.put("COMPLETED", List.of("currentOrder", "takenPhotos"));

        // Enable validation
        validationEnabled = true;

        LOGGER.log(Level.INFO, "Data validation initialized with {0} states", requiredAttributes.size());
    }

    /**
     * Enables or disables data validation.
     * 
     * @param enabled whether validation should be enabled
     */
    public static void setValidationEnabled(boolean enabled) {
        validationEnabled = enabled;
        LOGGER.log(Level.INFO, "Data validation {0}", enabled ? "enabled" : "disabled");
    }

    /**
     * Checks if data validation is enabled.
     * 
     * @return true if data validation is enabled, false otherwise
     */
    public static boolean isValidationEnabled() {
        return validationEnabled;
    }

    /**
     * Validates that all required attributes for the current state are present.
     * 
     * @throws IllegalStateException if a required attribute is missing
     */
    public static void validateRequiredData() {
        if (!validationEnabled || currentState == null) {
            return; // Validation not enabled or state not initialized
        }

        List<String> required = requiredAttributes.get(currentState);
        if (required == null) {
            LOGGER.log(Level.WARNING, "No required attributes defined for state: {0}", currentState);
            return;
        }

        List<String> missing = new ArrayList<>();

        for (String attribute : required) {
            switch (attribute) {
                case "currentOrder":
                    if (currentOrder == null) {
                        missing.add("currentOrder");
                    }
                    break;
                case "selectedTemplate":
                    if (selectedTemplate == null) {
                        missing.add("selectedTemplate");
                    }
                    break;
                case "takenPhotos":
                    if (takenPhotos.isEmpty()) {
                        missing.add("takenPhotos");
                    }
                    break;
                default:
                    // Check for custom attributes
                    if (attribute.startsWith("attribute:")) {
                        String attributeName = attribute.substring("attribute:".length());
                        if (!attributes.containsKey(attributeName)) {
                            missing.add(attributeName);
                        }
                    }
                    break;
            }
        }

        if (!missing.isEmpty()) {
            String errorMessage = "Missing required data for state '" + currentState + "': " + missing;
            LOGGER.log(Level.WARNING, errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Validates that all required attributes for the specified state are present.
     * 
     * @param state the state to validate
     * @throws IllegalStateException if a required attribute is missing
     */
    public static void validateRequiredDataForState(String state) {
        if (!validationEnabled) {
            return; // Validation not enabled
        }

        List<String> required = requiredAttributes.get(state);
        if (required == null) {
            LOGGER.log(Level.WARNING, "No required attributes defined for state: {0}", state);
            return;
        }

        List<String> missing = new ArrayList<>();

        for (String attribute : required) {
            switch (attribute) {
                case "currentOrder":
                    if (currentOrder == null) {
                        missing.add("currentOrder");
                    }
                    break;
                case "selectedTemplate":
                    if (selectedTemplate == null) {
                        missing.add("selectedTemplate");
                    }
                    break;
                case "takenPhotos":
                    if (takenPhotos.isEmpty()) {
                        missing.add("takenPhotos");
                    }
                    break;
                default:
                    // Check for custom attributes
                    if (attribute.startsWith("attribute:")) {
                        String attributeName = attribute.substring("attribute:".length());
                        if (!attributes.containsKey(attributeName)) {
                            missing.add(attributeName);
                        }
                    }
                    break;
            }
        }

        if (!missing.isEmpty()) {
            String errorMessage = "Missing required data for state '" + state + "': " + missing;
            LOGGER.log(Level.WARNING, errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    /**
     * Gets the current flow state.
     * 
     * @return the current flow state
     */
    public static WorkerFlowState getCurrentFlowState() {
        return currentFlowState;
    }

    /**
     * Sets the current flow state.
     * This method validates the state transition and notifies all registered listeners.
     * 
     * @param newState the new flow state
     * @throws IllegalStateException if the state transition is not valid or required data is missing
     */
    public static void setCurrentFlowState(WorkerFlowState newState) {
        Objects.requireNonNull(newState, "New state must not be null");

        // Skip validation if transitioning to the same state
        if (currentFlowState == newState) {
            LOGGER.log(Level.FINE, "Staying in current state: {0}", newState);
            return;
        }

        // Check if the transition is valid
        if (!currentFlowState.canTransitionTo(newState)) {
            String errorMessage = "Invalid state transition from '" + currentFlowState + "' to '" + newState + "'";
            LOGGER.log(Level.WARNING, errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        // Map the enum state to string state for data validation
        String stringState = mapFlowStateToStringState(newState);

        // Validate required data for the new state
        if (validationEnabled && stringState != null) {
            try {
                validateRequiredDataForState(stringState);
            } catch (IllegalStateException e) {
                // Report error but don't throw to maintain backward compatibility
                reportError("Cannot transition to state '" + newState + "' due to missing required data", 
                           e, ErrorSeverity.WARNING);

                // Log the validation failure
                LOGGER.log(Level.WARNING, "State transition validation failed: {0}", e.getMessage());

                // If in recovery mode, allow the transition despite missing data
                if (!recoveryMode) {
                    throw e;
                } else {
                    LOGGER.log(Level.WARNING, "Allowing state transition despite missing data (recovery mode)");
                }
            }
        }

        WorkerFlowState oldState = currentFlowState;
        currentFlowState = newState;

        LOGGER.log(Level.INFO, "Flow state transition: {0} -> {1}", new Object[]{oldState, newState});

        // Notify all listeners
        for (Consumer<WorkerFlowState> listener : stateChangeListeners) {
            try {
                listener.accept(newState);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error in state change listener: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Maps a WorkerFlowState enum value to its corresponding string state.
     * 
     * @param flowState the flow state enum value
     * @return the corresponding string state, or null if no mapping exists
     */
    private static String mapFlowStateToStringState(WorkerFlowState flowState) {
        switch (flowState) {
            case INITIAL:
                return "INITIAL";
            case ORDER_SELECTED:
                return "ORDER_SELECTED";
            case TEMPLATE_SELECTED:
                return "TEMPLATE_SELECTED";
            case PHOTO_CAPTURE:
                return "PHOTO_CAPTURE";
            case PHOTO_REVIEW:
                return "PHOTO_REVIEW";
            case COMPLETING_ORDER:
                return "COMPLETED"; // Map to the same validation requirements
            case COMPLETED:
                return "COMPLETED";
            case ERROR:
                return null; // No validation for error state
            default:
                return null;
        }
    }

    /**
     * Adds a listener that will be notified when the flow state changes.
     * 
     * @param listener the listener to add
     */
    public static void addStateChangeListener(Consumer<WorkerFlowState> listener) {
        Objects.requireNonNull(listener, "Listener must not be null");
        stateChangeListeners.add(listener);
    }

    /**
     * Removes a state change listener.
     * 
     * @param listener the listener to remove
     * @return true if the listener was removed, false if it wasn't registered
     */
    public static boolean removeStateChangeListener(Consumer<WorkerFlowState> listener) {
        return stateChangeListeners.remove(listener);
    }

    /**
     * Removes all state change listeners.
     */
    public static void clearStateChangeListeners() {
        stateChangeListeners.clear();
    }
}
