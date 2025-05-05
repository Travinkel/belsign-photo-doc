package com.belman.domain.shared;

/**
 * Event published when a command is successfully undone.
 * <p>
 * This event is published by the CommandManager after a command has been
 * successfully undone and moved from the undo stack to the redo stack.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.CommandUndoneEvent} instead.
 */
@Deprecated
public class CommandUndoneEvent extends com.belman.domain.events.CommandUndoneEvent {

    /**
     * Creates a new CommandUndoneEvent with the specified command.
     *
     * @param command the command that was undone
     */
    public CommandUndoneEvent(Command<?> command) {
        super(command);
    }
}
