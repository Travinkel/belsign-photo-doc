package com.belman.ui.util;

import com.belman.common.logging.EmojiLogger;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Utility class for working with Gluon's UI components.
 * Provides methods for accessing and manipulating UI elements in a way that
 * doesn't depend on deprecated MobileApplication class.
 */
public class GlistenUtils {
    private static final EmojiLogger logger = EmojiLogger.getLogger(GlistenUtils.class);

    /**
     * Gets the AppBar for a View.
     * This method provides a replacement for the deprecated MobileApplication.getInstance().getAppBar() method.
     *
     * @param view the view to get the AppBar for
     * @return the AppBar for the view, or null if the view doesn't have an AppBar
     */
    public static AppBar getAppBar(View view) {
        if (view == null) {
            logger.warn("Cannot get AppBar: view is null");
            return null;
        }

        try {
            // In Gluon, the View class has an AppBar that can be accessed directly
            return view.getAppBar();
        } catch (Exception e) {
            logger.error("Failed to get AppBar for view: {}", view.getClass().getSimpleName(), e);
            return null;
        }
    }
}