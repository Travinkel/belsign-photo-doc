package com.belman.ui.core;

/**
 * Interface for UI commands.
 * This is part of the Command pattern for UI actions.
 */
public interface UICommand {
    /**
     * Executes the command.
     */
    void execute();

    /**
     * Undoes the command.
     */
    void undo();
}