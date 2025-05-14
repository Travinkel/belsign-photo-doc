package com.belman.ui.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Utility class for working with layouts.
 */
public final class LayoutUtils {
    /**
     * Private constructor to prevent instantiation.
     */
    private LayoutUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a spacer region that grows horizontally.
     *
     * @return the spacer region
     */
    public static Region createHSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Creates a spacer region that grows vertically.
     *
     * @return the spacer region
     */
    public static Region createVSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Creates a horizontal box with the specified spacing and alignment.
     *
     * @param spacing   the spacing between nodes
     * @param alignment the alignment of nodes
     * @param nodes     the nodes to add to the box
     * @return the horizontal box
     */
    public static HBox createHBox(double spacing, Pos alignment, Node... nodes) {
        HBox hbox = new HBox(spacing);
        hbox.setAlignment(alignment);
        if (nodes != null && nodes.length > 0) {
            hbox.getChildren().addAll(nodes);
        }
        return hbox;
    }

    /**
     * Creates a vertical box with the specified spacing and alignment.
     *
     * @param spacing   the spacing between nodes
     * @param alignment the alignment of nodes
     * @param nodes     the nodes to add to the box
     * @return the vertical box
     */
    public static VBox createVBox(double spacing, Pos alignment, Node... nodes) {
        VBox vbox = new VBox(spacing);
        vbox.setAlignment(alignment);
        if (nodes != null && nodes.length > 0) {
            vbox.getChildren().addAll(nodes);
        }
        return vbox;
    }

    /**
     * Sets the margin for a node.
     *
     * @param node   the node
     * @param margin the margin
     */
    public static void setMargin(Node node, double margin) {
        setMargin(node, margin, margin, margin, margin);
    }

    /**
     * Sets the margin for a node.
     *
     * @param node   the node
     * @param top    the top margin
     * @param right  the right margin
     * @param bottom the bottom margin
     * @param left   the left margin
     */
    public static void setMargin(Node node, double top, double right, double bottom, double left) {
        if (node == null) {
            return;
        }
        Insets insets = new Insets(top, right, bottom, left);
        // Use the appropriate layout class's setMargin method
        // This will work regardless of the parent layout
        javafx.scene.layout.HBox.setMargin(node, insets);
    }

    /**
     * Sets the horizontal grow priority for a node.
     *
     * @param node     the node
     * @param priority the priority
     */
    public static void setHGrow(Node node, Priority priority) {
        if (node == null) {
            return;
        }
        HBox.setHgrow(node, priority);
    }

    /**
     * Sets the vertical grow priority for a node.
     *
     * @param node     the node
     * @param priority the priority
     */
    public static void setVGrow(Node node, Priority priority) {
        if (node == null) {
            return;
        }
        VBox.setVgrow(node, priority);
    }

    /**
     * Sets the alignment for a node if it's a horizontal box.
     *
     * @param node      the node
     * @param alignment the alignment
     */
    public static void setHAlignment(Node node, Pos alignment) {
        if (node == null || !(node instanceof HBox)) {
            return;
        }
        ((HBox) node).setAlignment(alignment);
    }

    /**
     * Sets the alignment for a node if it's a vertical box.
     *
     * @param node      the node
     * @param alignment the alignment
     */
    public static void setVAlignment(Node node, Pos alignment) {
        if (node == null || !(node instanceof VBox)) {
            return;
        }
        ((VBox) node).setAlignment(alignment);
    }
}
