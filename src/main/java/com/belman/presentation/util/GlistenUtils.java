package com.belman.presentation.util;

import com.belman.common.logging.EmojiLogger;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility class for working with Gluon's UI components.
 * Provides methods for accessing and manipulating UI elements in a way that
 * doesn't depend on deprecated MobileApplication class.
 */
public class GlistenUtils {
    private static final EmojiLogger logger = EmojiLogger.getLogger(GlistenUtils.class);
    private static final Map<View, AppBar> appBarCache = createAppBarCache();

    /**
     * Creates a weak hash map for caching AppBar instances.
     * Using a weak hash map ensures that View instances can be garbage collected
     * when they are no longer referenced elsewhere in the application.
     * 
     * @return a new weak hash map for caching AppBar instances
     */
    private static Map<View, AppBar> createAppBarCache() {
        return new WeakHashMap<>();
    }

    /**
     * Gets the AppBar for a View.
     * This method provides a replacement for the deprecated MobileApplication.getInstance().getAppBar() method.
     * It creates a new AppBar instance if one doesn't exist for the view.
     *
     * @param view the view to get the AppBar for
     * @return the AppBar for the view, or null if the view is null
     */
    public static AppBar getAppBar(View view) {
        if (view == null) {
            logger.warn("Cannot get AppBar: view is null");
            return null;
        }

        // Check if we already have an AppBar for this view
        if (appBarCache.containsKey(view)) {
            return appBarCache.get(view);
        }

        try {
            // Create a new AppBar for this view
            AppBar appBar = new AppBar();
            appBar.getActionItems().clear();

            // Cache the AppBar for this view
            appBarCache.put(view, appBar);

            return appBar;
        } catch (Exception e) {
            logger.error("Failed to create AppBar for view: {}", view.getClass().getSimpleName(), e);
            return null;
        }
    }
}
