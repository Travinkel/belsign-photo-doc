package com.belman.domain.commands;

/**
 * Represents a command in the domain, following the Command pattern.
 * Commands encapsulate actions that can be executed, undone, and redone.
 */
public interface Command {

    /**
     * Executes the command, performing the action.
     */
    void execute();

    /**
     * Undoes the command, reverting the action.
     * If a command cannot be undone, this method should throw an UnsupportedOperationException.
     */
    void undo();

    /**
     * Redoes the command, repeating the action after it has been undone.
     * If a command cannot be redone, this method should throw an UnsupportedOperationException.
     */
    void redo();

    /**
     * Gets a description of the command.
     *
     * @return a string describing the command
     */
    String getDescription();

    /**
     * Checks if the command can be undone.
     *
     * @return true if the command can be undone, false otherwise
     */
    boolean isUndoable();

    /**
     * Checks if the command can be redone.
     *
     * @return true if the command can be redone, false otherwise
     */
    boolean isRedoable();
}