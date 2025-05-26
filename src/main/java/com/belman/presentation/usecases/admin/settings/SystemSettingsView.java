package com.belman.presentation.usecases.admin.settings;

import com.belman.presentation.base.BaseView;

/**
 * View class for the system settings screen.
 * This view allows administrators to configure system-wide settings.
 */
public class SystemSettingsView extends BaseView<SystemSettingsViewModel> {
    /**
     * Creates a new SystemSettingsView.
     */
    public SystemSettingsView() {
        super(SystemSettingsViewModel.class);
    }

    @Override
    protected String getFxmlPath() {
        return "/com/belman/presentation/usecases/admin/settings/SystemSettingsView.fxml";
    }
}