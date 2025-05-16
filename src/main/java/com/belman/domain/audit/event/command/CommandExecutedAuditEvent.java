package com.belman.domain.audit.event.command;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.ui.flow.commands.Command;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a command is executed.
 * <p>
 * This event captures information about the command that was executed,
 * including its description and whether it can be undone.
 */
public class CommandExecutedAuditEvent extends BaseAuditEvent {
    private final String commandDescription;
    private final boolean canUndo;

    /**
     * Creates a new CommandExecutedAuditEvent with the specified command.
     *
     * @param command the command that was executed
     */
    public CommandExecutedAuditEvent(Command<?> command) {
        super();
        this.commandDescription = command.getDescription();
        this.canUndo = command.canUndo();
    }

    /**
     * Creates a new CommandExecutedAuditEvent with the specified ID, timestamp, and command details.
     *
     * @param eventId            the unique identifier for this event
     * @param occurredOn         the timestamp when this event occurred
     * @param commandDescription the description of the command that was executed
     * @param canUndo            whether the command can be undone
     */
    public CommandExecutedAuditEvent(UUID eventId, Instant occurredOn, String commandDescription, boolean canUndo) {
        super(eventId, occurredOn);
        this.commandDescription = commandDescription;
        this.canUndo = canUndo;
    }

    /**
     * Gets the description of the command that was executed.
     *
     * @return the command description
     */
    public String getCommandDescription() {
        return commandDescription;
    }

    /**
     * Checks if the command can be undone.
     *
     * @return true if the command can be undone, false otherwise
     */
    public boolean canUndo() {
        return canUndo;
    }

    @Override
    public String getEventType() {
        return "COMMAND_EXECUTED";
    }
}