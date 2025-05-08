package com.belman.business.domain.events;

import com.belman.business.domain.shared.Command;

/**
 * Event published when a command is successfully undone.
 * <p>
 * This event is published by the CommandManager after a command has been
 * successfully undone and moved from the undo stack to the redo stack.
 */
public class CommandUndoneEvent extends CommandEvent {

    /**
     * Creates a new CommandUndoneEvent with the specified command.
     *
     * @param command the command that was undone
     */
    public CommandUndoneEvent(Command<?> command) {
        super(command);
    }

    @Override
    public String toString() {
        return "CommandUndoneEvent{command=" + getCommandDescription() + "}";
    }
}