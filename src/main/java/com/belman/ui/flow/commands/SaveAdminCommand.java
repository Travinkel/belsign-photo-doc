package com.belman.ui.flow.commands;

import com.belman.service.usecase.admin.AdminService;
import com.belman.ui.usecases.admin.AdminViewModel;

public class SaveAdminCommand implements Command<Void> {
    private final AdminService service;
    private final AdminData data;
}
