package com.belman.domain.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A composite command that executes multiple commands in sequence.
 * This class implements the Composite pattern for commands.
 */
public class CompositeCommand implements Command {

    private final String description;
    private final List<Command> commands;

    /**
     * Creates a new composite command with the specified description.
     *
     * @param description a description of the composite command
     */
    public CompositeCommand(String description) {
        this.description = Objects.requireNonNull(description, "Description must not be null");
        this.commands = new ArrayList<>();
    }

    /**
     * Adds a command to this composite command.
     *
     * @param command the command to add
     * @return this composite command
     */
    public CompositeCommand addCommand(Command command) {
        Objects.requireNonNull(command, "Command must not be null");
        commands.add(command);
        return this;
    }

    /**
     * Gets the commands in this composite command.
     *
     * @return an unmodifiable list of commands
     */
    public List<Command> getCommands() {
        return List.copyOf(commands);
    }

    /**
     * Executes all commands in sequence.
     */
    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    /**
     * Undoes all commands in reverse order.
     */
    @Override
    public void undo() {
        if (!isUndoable()) {
            throw new UnsupportedOperationException("Cannot undo composite command with non-undoable commands");
        }

        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }

    /**
     * Redoes all commands in original order.
     */
    @Override
    public void redo() {
        if (!isRedoable()) {
            throw new UnsupportedOperationException("Cannot redo composite command with non-redoable commands");
        }

        for (Command command : commands) {
            command.redo();
        }
    }

    /**
     * Gets the description of this composite command.
     *
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Checks if all commands in this composite command can be undone.
     *
     * @return true if all commands can be undone, false otherwise
     */
    @Override
    public boolean isUndoable() {
        return commands.stream().allMatch(Command::isUndoable);
    }

    /**
     * Checks if all commands in this composite command can be redone.
     *
     * @return true if all commands can be redone, false otherwise
     */
    @Override
    public boolean isRedoable() {
        return commands.stream().allMatch(Command::isRedoable);
    }
}