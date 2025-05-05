package com.belman.domain.shared;

/**
 * Base class for command-related events.
 * <p>
 * This class provides common functionality for all command events,
 * such as storing the command that triggered the event.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.CommandEvent} instead.
 */
@Deprecated
public abstract class CommandEvent extends com.belman.domain.events.CommandEvent {

    /**
     * Creates a new CommandEvent with the specified command.
     *
     * @param command the command that triggered this event
     */
    protected CommandEvent(Command<?> command) {
        super(command);
    }
}
