package com.belman.ui.base;

import com.belman.common.logging.EmojiLogger;
import com.belman.ui.utils.DialogUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Base class for views that can handle errors.
 * This is part of the Error Boundary pattern for error handling.
 *
 * @param <T> the type of view model
 */
public abstract class ErrorBoundaryView<T extends BaseViewModel<T>> extends BaseView<T> {
    private static final EmojiLogger logger = EmojiLogger.getLogger(ErrorBoundaryView.class);

    /**
     * Executes a function and handles any errors that occur.
     *
     * @param function the function to execute
     */
    protected void tryExecute(Runnable function) {
        try {
            function.run();
        } catch (Throwable error) {
            handleError(error);
        }
    }

    /**
     * Handles an error.
     *
     * @param error the error to handle
     */
    protected void handleError(Throwable error) {
        logger.error("Error in view: {}", error.getMessage(), error);

        // Show an error dialog
        DialogUtils.showError(
                "Error",
                "An error occurred",
                error.getMessage()
        );

        // Render a fallback UI
        Node fallback = renderFallback(error);
        if (fallback != null) {
            // Replace the current content with the fallback
            getChildren().clear();
            getChildren().add(fallback);
        }
    }

    /**
     * Renders a fallback UI when an error occurs.
     * Subclasses can override this method to provide a custom fallback UI.
     *
     * @param error the error that occurred
     * @return the fallback UI
     */
    protected Node renderFallback(Throwable error) {
        VBox fallback = new VBox(10);
        fallback.getStyleClass().add("error-fallback");

        Label errorLabel = new Label("An error occurred");
        errorLabel.getStyleClass().add("error-title");

        Label messageLabel = new Label(error.getMessage());
        messageLabel.getStyleClass().add("error-message");
        messageLabel.setWrapText(true);

        fallback.getChildren().addAll(errorLabel, messageLabel);

        return fallback;
    }

    /**
     * Executes a function that returns a value and handles any errors that occur.
     *
     * @param <R>      the type of the return value
     * @param function the function to execute
     * @param fallback the fallback value to return if an error occurs
     * @return the result of the function, or the fallback value if an error occurs
     */
    protected <R> R tryExecute(ThrowingSupplier<R> function, R fallback) {
        try {
            return function.get();
        } catch (Throwable error) {
            handleError(error);
            return fallback;
        }
    }

    /**
     * Functional interface for a supplier that can throw an exception.
     *
     * @param <R> the type of the return value
     */
    @FunctionalInterface
    public interface ThrowingSupplier<R> {
        /**
         * Gets a result.
         *
         * @return a result
         * @throws Throwable if an error occurs
         */
        R get() throws Throwable;
    }
}