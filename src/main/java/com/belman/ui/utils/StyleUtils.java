package com.belman.ui.utils;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for working with CSS styles.
 */
public final class StyleUtils {
    /**
     * Private constructor to prevent instantiation.
     */
    private StyleUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Adds CSS style classes to a node.
     *
     * @param node         the node
     * @param styleClasses the style classes to add
     */
    public static void addStyleClasses(Node node, String... styleClasses) {
        if (node == null || styleClasses == null) {
            return;
        }
        node.getStyleClass().addAll(Arrays.asList(styleClasses));
    }

    /**
     * Removes CSS style classes from a node.
     *
     * @param node         the node
     * @param styleClasses the style classes to remove
     */
    public static void removeStyleClasses(Node node, String... styleClasses) {
        if (node == null || styleClasses == null) {
            return;
        }
        List<String> styleClassList = Arrays.asList(styleClasses);
        node.getStyleClass().removeAll(styleClassList);
    }

    /**
     * Toggles a CSS style class on a node.
     *
     * @param node       the node
     * @param styleClass the style class to toggle
     * @return true if the style class was added, false if it was removed
     */
    public static boolean toggleStyleClass(Node node, String styleClass) {
        if (node == null || styleClass == null || styleClass.isEmpty()) {
            return false;
        }

        if (node.getStyleClass().contains(styleClass)) {
            node.getStyleClass().remove(styleClass);
            return false;
        } else {
            node.getStyleClass().add(styleClass);
            return true;
        }
    }

    /**
     * Checks if a node has a CSS style class.
     *
     * @param node       the node
     * @param styleClass the style class to check
     * @return true if the node has the style class, false otherwise
     */
    public static boolean hasStyleClass(Node node, String styleClass) {
        if (node == null || styleClass == null || styleClass.isEmpty()) {
            return false;
        }
        return node.getStyleClass().contains(styleClass);
    }

    /**
     * Adds a CSS stylesheet to a scene.
     *
     * @param scene      the scene
     * @param stylesheet the stylesheet URL
     */
    public static void addStylesheet(Scene scene, String stylesheet) {
        if (scene == null || stylesheet == null || stylesheet.isEmpty()) {
            return;
        }
        scene.getStylesheets().add(stylesheet);
    }

    /**
     * Removes a CSS stylesheet from a scene.
     *
     * @param scene      the scene
     * @param stylesheet the stylesheet URL
     */
    public static void removeStylesheet(Scene scene, String stylesheet) {
        if (scene == null || stylesheet == null || stylesheet.isEmpty()) {
            return;
        }
        scene.getStylesheets().remove(stylesheet);
    }

    /**
     * Applies CSS styles to all nodes in a parent node.
     *
     * @param parent the parent node
     * @param style  the CSS style
     */
    public static void applyStyleToChildren(Parent parent, String style) {
        if (parent == null || style == null) {
            return;
        }
        for (Node child : parent.getChildrenUnmodifiable()) {
            applyStyle(child, style);
        }
    }

    /**
     * Applies a CSS style to a node.
     *
     * @param node  the node
     * @param style the CSS style
     */
    public static void applyStyle(Node node, String style) {
        if (node == null || style == null) {
            return;
        }
        node.setStyle(style);
    }
}