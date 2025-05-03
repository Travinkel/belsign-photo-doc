package com.belman.backbone.core.command;

/**
 * Event published when a command is successfully executed.
 * <p>
 * This event is published by the CommandManager after a command has been
 * successfully executed and added to the undo stack.
 */
public class CommandExecutedEvent extends CommandEvent {

    /**
     * Creates a new CommandExecutedEvent with the specified command.
     *
     * @param command the command that was executed
     */
    public CommandExecutedEvent(Command<?> command) {
        super(command);
    }

    @Override
    public String toString() {
        return "CommandExecutedEvent{command=" + getCommandDescription() + "}";
    }
}