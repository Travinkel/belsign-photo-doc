package com.belman.belsign.application.scenes;

public enum ScreenPreset {
    IPAD_DEFAULT(800, 600, false),
    MODAL(400, 300, false),
    ADMIN_PANEL(1024, 768, true);

    public final int width;
    public final int height;
    public final boolean resizable;

    ScreenPreset(int width, int height, boolean resizable) {
        this.width = width;
        this.height = height;
        this.resizable = resizable;
    }
}
