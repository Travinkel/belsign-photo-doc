package com.belman.presentation.providers;

import com.belman.domain.photo.PhotoTemplate;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Provider for user-friendly labels for photo templates.
 * This class maps technical template names to user-friendly display labels.
 */
public class PhotoTemplateLabelProvider {

    private static final Map<PhotoTemplate, String> DISPLAY_LABELS = createDisplayLabels();
    private static final Map<PhotoTemplate, String> TOOLTIPS = createTooltips();

    /**
     * Creates and initializes the display labels map.
     * 
     * @return an unmodifiable map of photo templates to display labels
     */
    private static Map<PhotoTemplate, String> createDisplayLabels() {
        Map<PhotoTemplate, String> labels = new HashMap<>();

        // Initialize display labels for standard views
        labels.put(PhotoTemplate.TOP_VIEW_OF_JOINT, "Top View");
        labels.put(PhotoTemplate.SIDE_VIEW_OF_WELD, "Side View of Weld");
        labels.put(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY, "Front View");
        labels.put(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY, "Back View");
        labels.put(PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY, "Left Side View");
        labels.put(PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY, "Right Side View");
        labels.put(PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY, "Bottom View");

        // Initialize display labels for detailed views
        labels.put(PhotoTemplate.CLOSE_UP_OF_WELD, "Close-up of Weld");
        labels.put(PhotoTemplate.ANGLED_VIEW_OF_JOINT, "Angled View of Joint");
        labels.put(PhotoTemplate.OVERVIEW_OF_ASSEMBLY, "Overview");

        // Initialize display labels for custom requirements
        labels.put(PhotoTemplate.CUSTOM, "Custom View");

        return Collections.unmodifiableMap(labels);
    }

    /**
     * Creates and initializes the tooltips map.
     * 
     * @return an unmodifiable map of photo templates to tooltips
     */
    private static Map<PhotoTemplate, String> createTooltips() {
        Map<PhotoTemplate, String> tooltips = new HashMap<>();

        // Initialize tooltips for standard views
        tooltips.put(PhotoTemplate.TOP_VIEW_OF_JOINT, "Take a clear photo directly from above the joint");
        tooltips.put(PhotoTemplate.SIDE_VIEW_OF_WELD, "Take a clear photo showing the side profile of the weld");
        tooltips.put(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY, "Take a photo showing the front of the assembly");
        tooltips.put(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY, "Take a photo showing the back of the assembly");
        tooltips.put(PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY, "Take a photo showing the left side of the assembly");
        tooltips.put(PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY, "Take a photo showing the right side of the assembly");
        tooltips.put(PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY, "Take a photo from underneath the assembly");

        // Initialize tooltips for detailed views
        tooltips.put(PhotoTemplate.CLOSE_UP_OF_WELD, "Take a detailed close-up photo of the weld");
        tooltips.put(PhotoTemplate.ANGLED_VIEW_OF_JOINT, "Take a photo of the joint from an angled perspective");
        tooltips.put(PhotoTemplate.OVERVIEW_OF_ASSEMBLY, "Take a photo showing the entire assembly");

        // Initialize tooltips for custom requirements
        tooltips.put(PhotoTemplate.CUSTOM, "Follow the specific instructions provided for this photo");

        return Collections.unmodifiableMap(tooltips);
    }

    /**
     * Gets the user-friendly display label for a photo template.
     *
     * @param template the photo template
     * @return the user-friendly display label, or the template name if no label is defined
     */
    public static String getDisplayLabel(PhotoTemplate template) {
        if (template == null) {
            return "Unknown";
        }
        return DISPLAY_LABELS.getOrDefault(template, template.name());
    }

    /**
     * Gets the tooltip text for a photo template.
     *
     * @param template the photo template
     * @return the tooltip text, or the template description if no tooltip is defined
     */
    public static String getTooltip(PhotoTemplate template) {
        if (template == null) {
            return "";
        }
        return TOOLTIPS.getOrDefault(template, template.description());
    }
}
