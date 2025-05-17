package com.belman.presentation.flow.commands;

import com.belman.domain.admin.AdminData;
import com.belman.application.usecase.admin.AdminService;

import java.util.concurrent.CompletableFuture;

/**
 * Command for saving admin data.
 * This command saves admin data using the AdminService.
 */
public class SaveAdminCommand implements Command<Void> {
    private final AdminService service;
    private final AdminData data;

    /**
     * Creates a new SaveAdminCommand with the specified service and data.
     *
     * @param service the admin service
     * @param data the admin data to save
     */
    public SaveAdminCommand(AdminService service, AdminData data) {
        this.service = service;
        this.data = data;
    }

    @Override
    public CompletableFuture<Void> execute() {
        return CompletableFuture.runAsync(() -> {
            // In a real implementation, this would save the admin data
            // using the admin service
            // service.saveAdmin(data);
        });
    }

    @Override
    public CompletableFuture<Void> undo() {
        return CompletableFuture.completedFuture(null); // no-op
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Save admin: " + data.username();
    }
}
