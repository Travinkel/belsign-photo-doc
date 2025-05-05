package com.belman.domain.shared;

/**
 * Event published when a command is successfully executed.
 * <p>
 * This event is published by the CommandManager after a command has been
 * successfully executed and added to the undo stack.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.CommandExecutedEvent} instead.
 */
@Deprecated
public class CommandExecutedEvent extends com.belman.domain.events.CommandExecutedEvent {

    /**
     * Creates a new CommandExecutedEvent with the specified command.
     *
     * @param command the command that was executed
     */
    public CommandExecutedEvent(Command<?> command) {
        super(command);
    }
}
