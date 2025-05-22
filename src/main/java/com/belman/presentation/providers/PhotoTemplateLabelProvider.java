package com.belman.presentation.providers;

import com.belman.domain.photo.PhotoTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Provider for user-friendly labels for photo templates.
 * This class maps technical template names to user-friendly display labels.
 */
public class PhotoTemplateLabelProvider {

    private static final Map<PhotoTemplate, String> DISPLAY_LABELS = new HashMap<>();
    private static final Map<PhotoTemplate, String> TOOLTIPS = new HashMap<>();

    static {
        // Initialize display labels for standard views
        DISPLAY_LABELS.put(PhotoTemplate.TOP_VIEW_OF_JOINT, "Top View");
        DISPLAY_LABELS.put(PhotoTemplate.SIDE_VIEW_OF_WELD, "Side View of Weld");
        DISPLAY_LABELS.put(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY, "Front View");
        DISPLAY_LABELS.put(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY, "Back View");
        DISPLAY_LABELS.put(PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY, "Left Side View");
        DISPLAY_LABELS.put(PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY, "Right Side View");
        DISPLAY_LABELS.put(PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY, "Bottom View");
        
        // Initialize display labels for detailed views
        DISPLAY_LABELS.put(PhotoTemplate.CLOSE_UP_OF_WELD, "Close-up of Weld");
        DISPLAY_LABELS.put(PhotoTemplate.ANGLED_VIEW_OF_JOINT, "Angled View of Joint");
        DISPLAY_LABELS.put(PhotoTemplate.OVERVIEW_OF_ASSEMBLY, "Overview");
        
        // Initialize display labels for custom requirements
        DISPLAY_LABELS.put(PhotoTemplate.CUSTOM, "Custom View");
        
        // Initialize tooltips for standard views
        TOOLTIPS.put(PhotoTemplate.TOP_VIEW_OF_JOINT, "Take a clear photo directly from above the joint");
        TOOLTIPS.put(PhotoTemplate.SIDE_VIEW_OF_WELD, "Take a clear photo showing the side profile of the weld");
        TOOLTIPS.put(PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY, "Take a photo showing the front of the assembly");
        TOOLTIPS.put(PhotoTemplate.BACK_VIEW_OF_ASSEMBLY, "Take a photo showing the back of the assembly");
        TOOLTIPS.put(PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY, "Take a photo showing the left side of the assembly");
        TOOLTIPS.put(PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY, "Take a photo showing the right side of the assembly");
        TOOLTIPS.put(PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY, "Take a photo from underneath the assembly");
        
        // Initialize tooltips for detailed views
        TOOLTIPS.put(PhotoTemplate.CLOSE_UP_OF_WELD, "Take a detailed close-up photo of the weld");
        TOOLTIPS.put(PhotoTemplate.ANGLED_VIEW_OF_JOINT, "Take a photo of the joint from an angled perspective");
        TOOLTIPS.put(PhotoTemplate.OVERVIEW_OF_ASSEMBLY, "Take a photo showing the entire assembly");
        
        // Initialize tooltips for custom requirements
        TOOLTIPS.put(PhotoTemplate.CUSTOM, "Follow the specific instructions provided for this photo");
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