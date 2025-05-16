package com.belman.ui.flow.commands;

// Using the BaseService from the application.core package
// This class is already in the application.core package, so no import needed

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.services.LoggerFactory;
import com.belman.service.base.BaseService;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CompletableFuture;

/**
 * Manages command execution, history tracking, and undo/redo functionality.
 * <p>
 * The CommandManager is a singleton service that executes commands, tracks command history,
 * and provides undo/redo functionality. It also publishes events when commands are executed,
 * undone, or redone.
 */
public class CommandManager extends BaseService {
    private static CommandManager instance;

    // Command history stacks
    private final Deque<Command<?>> undoStack = new ArrayDeque<>();
    private final Deque<Command<?>> redoStack = new ArrayDeque<>();


    // Maximum history size
    private int maxHistorySize = 100;

    /**
     * Private constructor to enforce singleton pattern.
     *
     * @param loggerFactory the factory to create loggers
     */
    private CommandManager(LoggerFactory loggerFactory) {
        super(loggerFactory);
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return null;
    }

    /**
     * Gets the singleton instance of the CommandManager.
     *
     * @return the CommandManager instance
     */
    public static synchronized CommandManager getInstance() {
        if (instance == null) {
            LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);
            instance = new CommandManager(loggerFactory);
        }
        return instance;
    }

    /**
     * Executes a command and adds it to the undo stack if it supports undo.
     *
     * @param command the command to execute
     * @param <T>     the type of result returned by the command
     * @return a CompletableFuture that will complete with the result of the command execution
     */
    public <T> CompletableFuture<T> execute(Command<T> command) {
        if (!command.canExecute()) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("Command cannot be executed: " + command.getDescription()));
        }

        logInfo("Executing command: {}", command.getDescription());

        return command.execute()
                .thenApply(result -> {
                    // If the command supports undo, add it to the undo stack
                    if (command.canUndo()) {
                        addToUndoStack(command);
                        // Clear the redo stack when a new command is executed
                        redoStack.clear();
                    }
                    return result;
                })
                .exceptionally(ex -> {
                    logError("Command execution failed: {}", command.getDescription(), ex);
                    throw new RuntimeException("Command execution failed: " + command.getDescription(), ex);
                });
    }

    /**
     * Adds a command to the undo stack, respecting the maximum history size.
     *
     * @param command the command to add
     */
    private void addToUndoStack(Command<?> command) {
        undoStack.push(command);

        // Trim the history if needed
        if (undoStack.size() > maxHistorySize) {
            undoStack.removeLast();
        }
    }

    /**
     * Undoes the most recently executed command.
     *
     * @return a CompletableFuture that will complete when the undo operation is complete
     * @throws IllegalStateException if there are no commands to undo
     */
    public CompletableFuture<Void> undo() {
        if (undoStack.isEmpty()) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("No commands to undo"));
        }

        Command<?> command = undoStack.pop();
        logInfo("Undoing command: {}", command.getDescription());

        return command.undo()
                .thenAccept(v -> {
                    // Add the command to the redo stack
                    redoStack.push(command);
                })
                .exceptionally(ex -> {
                    logError("Command undo failed: {}", command.getDescription(), ex);
                    // Put the command back on the undo stack
                    undoStack.push(command);
                    throw new RuntimeException("Command undo failed: " + command.getDescription(), ex);
                });
    }

    /**
     * Redoes the most recently undone command.
     *
     * @return a CompletableFuture that will complete with the result of the command execution
     * @throws IllegalStateException if there are no commands to redo
     */
    public CompletableFuture<?> redo() {
        if (redoStack.isEmpty()) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("No commands to redo"));
        }

        Command<?> command = redoStack.pop();
        logInfo("Redoing command: {}", command.getDescription());

        return command.execute()
                .thenApply(result -> {
                    // Add the command back to the undo stack
                    addToUndoStack(command);
                    return result;
                })
                .exceptionally(ex -> {
                    logError("Command redo failed: {}", command.getDescription(), ex);
                    // Put the command back on the redo stack
                    redoStack.push(command);
                    throw new RuntimeException("Command redo failed: " + command.getDescription(), ex);
                });
    }

    /**
     * Checks if there are commands that can be undone.
     *
     * @return true if there are commands that can be undone, false otherwise
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Checks if there are commands that can be redone.
     *
     * @return true if there are commands that can be redone, false otherwise
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Clears the command history.
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
        logInfo("Command history cleared");
    }

    /**
     * Gets the maximum number of commands to keep in the history.
     *
     * @return the maximum history size
     */
    public int getMaxHistorySize() {
        return maxHistorySize;
    }

    /**
     * Sets the maximum number of commands to keep in the history.
     *
     * @param maxHistorySize the maximum history size
     * @throws IllegalArgumentException if maxHistorySize is less than 1
     */
    public void setMaxHistorySize(int maxHistorySize) {
        if (maxHistorySize < 1) {
            throw new IllegalArgumentException("Max history size must be at least 1");
        }
        this.maxHistorySize = maxHistorySize;

        // Trim the history if needed
        while (undoStack.size() > maxHistorySize) {
            undoStack.removeLast();
        }
    }

    /**
     * Gets the number of commands in the undo stack.
     *
     * @return the undo stack size
     */
    public int getUndoStackSize() {
        return undoStack.size();
    }

    /**
     * Gets the number of commands in the redo stack.
     *
     * @return the redo stack size
     */
    public int getRedoStackSize() {
        return redoStack.size();
    }
}
