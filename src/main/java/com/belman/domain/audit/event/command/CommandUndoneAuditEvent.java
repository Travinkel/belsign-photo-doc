package com.belman.domain.audit.event.command;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.ui.flow.commands.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a command is undone.
 * <p>
 * This event captures information about the command that was undone,
 * including its description.
 */
public class CommandUndoneAuditEvent extends BaseAuditEvent {
    private final String commandDescription;

    /**
     * Creates a new CommandUndoneAuditEvent with the specified command.
     *
     * @param command the command that was undone
     */
    public CommandUndoneAuditEvent(Command<?> command) {
        super();
        this.commandDescription = command.getDescription();
    }

    /**
     * Creates a new CommandUndoneAuditEvent with the specified ID, timestamp, and command details.
     *
     * @param eventId            the unique identifier for this event
     * @param occurredOn         the timestamp when this event occurred
     * @param commandDescription the description of the command that was undone
     */
    public CommandUndoneAuditEvent(UUID eventId, Instant occurredOn, String commandDescription) {
        super(eventId, occurredOn);
        this.commandDescription = commandDescription;
    }

    /**
     * Gets the description of the command that was undone.
     *
     * @return the command description
     */
    public String getCommandDescription() {
        return commandDescription;
    }

    @Override
    public String getEventType() {
        return "COMMAND_UNDONE";
    }
}