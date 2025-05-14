package com.belman.ui.core;

import java.util.Stack;

/**
 * Manager for UI commands.
 * This is part of the Command pattern for UI actions.
 */
public class CommandManager {
    private final Stack<UICommand> undoStack = new Stack<>();
    
    /**
     * Executes a command and adds it to the undo stack.
     *
     * @param command the command to execute
     */
    public void executeCommand(UICommand command) {
        command.execute();
        undoStack.push(command);
    }
    
    /**
     * Undoes the last executed command.
     */
    public void undo() {
        if (!undoStack.isEmpty()) {
            undoStack.pop().undo();
        }
    }
}