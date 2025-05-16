package com.belman.domain.audit.event.command;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.ui.flow.commands.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a command is redone.
 * <p>
 * This event captures information about the command that was redone,
 * including its description.
 */
public class CommandRedoneAuditEvent extends BaseAuditEvent {
    private final String commandDescription;

    /**
     * Creates a new CommandRedoneAuditEvent with the specified command.
     *
     * @param command the command that was redone
     */
    public CommandRedoneAuditEvent(Command<?> command) {
        super();
        this.commandDescription = command.getDescription();
    }

    /**
     * Creates a new CommandRedoneAuditEvent with the specified ID, timestamp, and command details.
     *
     * @param eventId            the unique identifier for this event
     * @param occurredOn         the timestamp when this event occurred
     * @param commandDescription the description of the command that was redone
     */
    public CommandRedoneAuditEvent(UUID eventId, Instant occurredOn, String commandDescription) {
        super(eventId, occurredOn);
        this.commandDescription = commandDescription;
    }

    /**
     * Gets the description of the command that was redone.
     *
     * @return the command description
     */
    public String getCommandDescription() {
        return commandDescription;
    }

    @Override
    public String getEventType() {
        return "COMMAND_REDONE";
    }
}