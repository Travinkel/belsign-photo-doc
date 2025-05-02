package com.belman.backbone.core.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

/**
 * Centralized error handling mechanism for the application.
 * This class provides methods for handling different types of errors,
 * logging errors, and displaying error messages to the user.
 */
public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    private static ErrorHandler instance;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private ErrorHandler() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Gets the singleton instance of the ErrorHandler.
     *
     * @return the ErrorHandler instance
     */
    public static synchronized ErrorHandler getInstance() {
        if (instance == null) {
            instance = new ErrorHandler();
        }
        return instance;
    }

    /**
     * Handles an exception by logging it and optionally displaying an error dialog.
     *
     * @param exception the exception to handle
     * @param message the error message to display
     * @param showDialog whether to show an error dialog to the user
     */
    public void handleException(Throwable exception, String message, boolean showDialog) {
        logger.error(message, exception);
        
        if (showDialog) {
            showErrorDialog(message, exception);
        }
    }

    /**
     * Handles an exception by logging it and displaying an error dialog.
     *
     * @param exception the exception to handle
     * @param message the error message to display
     */
    public void handleException(Throwable exception, String message) {
        handleException(exception, message, true);
    }

    /**
     * Handles an exception by logging it without displaying an error dialog.
     *
     * @param exception the exception to handle
     * @param message the error message to log
     */
    public void handleExceptionQuietly(Throwable exception, String message) {
        handleException(exception, message, false);
    }

    /**
     * Handles a runtime error by logging it and displaying an error dialog.
     *
     * @param message the error message to display
     */
    public void handleError(String message) {
        logger.error(message);
        showErrorDialog(message, null);
    }

    /**
     * Handles a runtime error by logging it without displaying an error dialog.
     *
     * @param message the error message to log
     */
    public void handleErrorQuietly(String message) {
        logger.error(message);
    }

    /**
     * Shows an error dialog with the specified message and exception details.
     *
     * @param message the error message to display
     * @param exception the exception to display details for, or null if no exception
     */
    private void showErrorDialog(String message, Throwable exception) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error has occurred");
            alert.setContentText(message);

            if (exception != null) {
                // Create expandable Exception section
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                String exceptionText = sw.toString();

                TextArea textArea = new TextArea(exceptionText);
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);

                GridPane expContent = new GridPane();
                expContent.setMaxWidth(Double.MAX_VALUE);
                expContent.add(textArea, 0, 0);

                alert.getDialogPane().setExpandableContent(expContent);
            }

            alert.showAndWait();
        });
    }

    /**
     * Executes an operation and handles any exceptions that occur.
     *
     * @param operation the operation to execute
     * @param errorMessage the error message to display if an exception occurs
     */
    public void executeWithErrorHandling(Runnable operation, String errorMessage) {
        try {
            operation.run();
        } catch (Exception e) {
            handleException(e, errorMessage);
        }
    }

    /**
     * Executes an operation that returns a result and handles any exceptions that occur.
     *
     * @param <T> the type of the result
     * @param operation the operation to execute
     * @param errorMessage the error message to display if an exception occurs
     * @param defaultValue the default value to return if an exception occurs
     * @return the result of the operation, or the default value if an exception occurs
     */
    public <T> T executeWithErrorHandling(
            java.util.function.Supplier<T> operation, 
            String errorMessage, 
            T defaultValue) {
        try {
            return operation.get();
        } catch (Exception e) {
            handleException(e, errorMessage);
            return defaultValue;
        }
    }

    /**
     * Executes an operation asynchronously and handles any exceptions that occur.
     *
     * @param <T> the type of the result
     * @param operation the operation to execute
     * @param onSuccess the callback to execute if the operation succeeds
     * @param errorMessage the error message to display if an exception occurs
     */
    public <T> void executeAsync(
            java.util.function.Supplier<T> operation, 
            Consumer<T> onSuccess, 
            String errorMessage) {
        new Thread(() -> {
            try {
                T result = operation.get();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Exception e) {
                handleException(e, errorMessage);
            }
        }).start();
    }
}