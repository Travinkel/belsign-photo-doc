package com.belman.domain.shared;

/**
 * Event published when a command is successfully redone.
 * <p>
 * This event is published by the CommandManager after a command has been
 * successfully redone and moved from the redo stack back to the undo stack.
 */
public class CommandRedoneEvent extends CommandEvent {

    /**
     * Creates a new CommandRedoneEvent with the specified command.
     *
     * @param command the command that was redone
     */
    public CommandRedoneEvent(Command<?> command) {
        super(command);
    }

    @Override
    public String toString() {
        return "CommandRedoneEvent{command=" + getCommandDescription() + "}";
    }
}