package com.belman.business.domain.shared;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for the Command pattern implementation.
 * <p>
 * The Command pattern encapsulates a request as an object, allowing for parameterization
 * of clients with different requests, queuing of requests, and logging of operations.
 * <p>
 * Commands can be executed and, if supported, undone. They can also return results
 * asynchronously using CompletableFuture.
 *
 * @param <T> the type of result returned by this command
 */
public interface Command<T> {

    /**
     * Executes the command and returns the result asynchronously.
     *
     * @return a CompletableFuture that will complete with the result of the command execution
     */
    CompletableFuture<T> execute();

    /**
     * Undoes the command, if supported.
     *
     * @return a CompletableFuture that will complete when the undo operation is complete
     * @throws UnsupportedOperationException if the command does not support undo
     */
    CompletableFuture<Void> undo();

    /**
     * Checks if this command can be executed in the current state.
     *
     * @return true if the command can be executed, false otherwise
     */
    default boolean canExecute() {
        return true;
    }

    /**
     * Checks if this command can be undone after execution.
     *
     * @return true if the command can be undone, false otherwise
     */
    default boolean canUndo() {
        return false;
    }

    /**
     * Gets a description of this command, useful for logging and UI display.
     *
     * @return a description of the command
     */
    default String getDescription() {
        return getClass().getSimpleName();
    }
}