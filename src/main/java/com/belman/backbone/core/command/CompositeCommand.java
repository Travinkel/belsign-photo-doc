package com.belman.backbone.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A command that executes multiple commands as a unit.
 * <p>
 * This command supports transaction-like behavior with rollback on failure.
 * If any command in the sequence fails, all previously executed commands
 * will be undone in reverse order.
 *
 * @param <T> the type of result returned by this command
 */
public class CompositeCommand<T> implements Command<T> {
    private final List<Command<?>> commands;
    private final String description;
    private final boolean rollbackOnFailure;
    private final int resultCommandIndex;
    private final List<Command<?>> executedCommands = new ArrayList<>();

    /**
     * Creates a new CompositeCommand with the specified commands.
     *
     * @param description       a description of the composite command
     * @param rollbackOnFailure whether to rollback on failure
     * @param resultCommandIndex the index of the command whose result should be returned
     * @param commands          the commands to execute
     */
    @SafeVarargs
    public CompositeCommand(String description, boolean rollbackOnFailure, int resultCommandIndex, Command<?>... commands) {
        if (commands == null || commands.length == 0) {
            throw new IllegalArgumentException("Commands cannot be null or empty");
        }
        if (resultCommandIndex < 0 || resultCommandIndex >= commands.length) {
            throw new IllegalArgumentException("Result command index out of bounds: " + resultCommandIndex);
        }

        this.commands = Arrays.asList(commands);
        this.description = description != null ? description : "Composite command";
        this.rollbackOnFailure = rollbackOnFailure;
        this.resultCommandIndex = resultCommandIndex;
    }

    /**
     * Creates a new CompositeCommand with the specified commands, returning the result of the last command.
     *
     * @param description       a description of the composite command
     * @param rollbackOnFailure whether to rollback on failure
     * @param commands          the commands to execute
     */
    @SafeVarargs
    public CompositeCommand(String description, boolean rollbackOnFailure, Command<?>... commands) {
        this(description, rollbackOnFailure, commands.length - 1, commands);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<T> execute() {
        // Clear the list of executed commands
        executedCommands.clear();

        // Execute each command in sequence
        CompletableFuture<Object> future = CompletableFuture.completedFuture(null);

        for (int i = 0; i < commands.size(); i++) {
            final Command<?> command = commands.get(i);
            final int commandIndex = i;

            future = future.thenCompose(ignored -> {
                if (!command.canExecute()) {
                    if (rollbackOnFailure) {
                        // Rollback all executed commands in reverse order
                        return rollback().thenCompose(v -> 
                            CompletableFuture.<Object>failedFuture(
                                new IllegalStateException("Command cannot be executed: " + command.getDescription())));
                    } else {
                        return CompletableFuture.<Object>failedFuture(
                            new IllegalStateException("Command cannot be executed: " + command.getDescription()));
                    }
                }

                return command.execute().thenApply(result -> {
                    // Add the command to the list of executed commands
                    executedCommands.add(command);

                    // If this is the result command, store its result
                    if (commandIndex == resultCommandIndex) {
                        return result;
                    }

                    return null;
                });
            }).exceptionally(ex -> {
                if (rollbackOnFailure) {
                    // Rollback all executed commands in reverse order
                    rollback().join();
                }
                throw new RuntimeException("Command execution failed: " + command.getDescription(), ex);
            });
        }

        return (CompletableFuture<T>) future.thenApply(result -> result);
    }

    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo composite command: not all commands support undo"));
        }

        return rollback();
    }

    /**
     * Rolls back all executed commands in reverse order.
     *
     * @return a CompletableFuture that will complete when all commands have been undone
     */
    private CompletableFuture<Void> rollback() {
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        // Undo all executed commands in reverse order
        List<Command<?>> reversedCommands = new ArrayList<>(executedCommands);
        Collections.reverse(reversedCommands);

        for (Command<?> command : reversedCommands) {
            future = future.thenCompose(ignored -> {
                if (command.canUndo()) {
                    return command.undo();
                } else {
                    return CompletableFuture.completedFuture(null);
                }
            });
        }

        return future;
    }

    @Override
    public boolean canExecute() {
        // The composite command can be executed if all its commands can be executed
        for (Command<?> command : commands) {
            if (!command.canExecute()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canUndo() {
        // The composite command can be undone if all executed commands can be undone
        for (Command<?> command : executedCommands) {
            if (!command.canUndo()) {
                return false;
            }
        }
        return !executedCommands.isEmpty();
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Gets the list of commands in this composite command.
     *
     * @return the list of commands
     */
    public List<Command<?>> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Gets the list of commands that have been executed.
     *
     * @return the list of executed commands
     */
    public List<Command<?>> getExecutedCommands() {
        return Collections.unmodifiableList(executedCommands);
    }
}
