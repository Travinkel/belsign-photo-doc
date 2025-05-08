package com.belman.business.richbe.events;

import com.belman.business.richbe.shared.Command;

/**
 * Base class for command-related events.
 * <p>
 * This class provides common functionality for all command events,
 * such as storing the command that triggered the event.
 */
public abstract class CommandEvent extends AbstractDomainEvent {
    private final Command<?> command;

    /**
     * Creates a new CommandEvent with the specified command.
     *
     * @param command the command that triggered this event
     */
    protected CommandEvent(Command<?> command) {
        this.command = command;
    }

    /**
     * Gets the command that triggered this event.
     *
     * @return the command
     */
    public Command<?> getCommand() {
        return command;
    }

    /**
     * Gets the description of the command that triggered this event.
     *
     * @return the command description
     */
    public String getCommandDescription() {
        return command.getDescription();
    }
}