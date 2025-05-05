package com.belman.domain.shared;

/**
 * Event published when a command is successfully redone.
 * <p>
 * This event is published by the CommandManager after a command has been
 * successfully redone and moved from the redo stack back to the undo stack.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.CommandRedoneEvent} instead.
 */
@Deprecated
public class CommandRedoneEvent extends com.belman.domain.events.CommandRedoneEvent {

    /**
     * Creates a new CommandRedoneEvent with the specified command.
     *
     * @param command the command that was redone
     */
    public CommandRedoneEvent(Command<?> command) {
        super(command);
    }
}
