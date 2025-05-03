package com.belman.application.commands.ui;

import com.belman.domain.shared.Command;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.core.TransitionPresets;
import com.belman.presentation.core.ViewTransition;
import com.gluonhq.charm.glisten.mvc.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

/**
 * Command for navigating to a different view.
 * <p>
 * This command uses the Router to navigate to a specified view and supports
 * undo functionality by navigating back to the previous view. It also supports
 * transitions between views.
 */
public class NavigateCommand implements Command<Void> {
    private final Class<? extends View> targetViewClass;
    private final Map<String, Object> parameters;
    private final ViewTransition transition;
    private Stack<Class<? extends View>> previousNavigationHistory;

    /**
     * Creates a new NavigateCommand with the specified target view.
     *
     * @param targetViewClass the class of the view to navigate to
     */
    public NavigateCommand(Class<? extends View> targetViewClass) {
        this(targetViewClass, new HashMap<>());
    }

    /**
     * Creates a new NavigateCommand with the specified target view and parameters.
     *
     * @param targetViewClass the class of the view to navigate to
     * @param parameters      the parameters to pass to the view
     */
    public NavigateCommand(Class<? extends View> targetViewClass, Map<String, Object> parameters) {
        this(targetViewClass, parameters, TransitionPresets.forward());
    }

    /**
     * Creates a new NavigateCommand with the specified target view and transition.
     *
     * @param targetViewClass the class of the view to navigate to
     * @param transition      the transition to use
     */
    public NavigateCommand(Class<? extends View> targetViewClass, ViewTransition transition) {
        this(targetViewClass, new HashMap<>(), transition);
    }

    /**
     * Creates a new NavigateCommand with the specified target view, parameters, and transition.
     *
     * @param targetViewClass the class of the view to navigate to
     * @param parameters      the parameters to pass to the view
     * @param transition      the transition to use
     */
    public NavigateCommand(Class<? extends View> targetViewClass, Map<String, Object> parameters, ViewTransition transition) {
        if (targetViewClass == null) {
            throw new IllegalArgumentException("Target view class cannot be null");
        }
        this.targetViewClass = targetViewClass;
        this.parameters = parameters != null ? parameters : new HashMap<>();
        this.transition = transition != null ? transition : TransitionPresets.forward();
    }

    @Override
    public CompletableFuture<Void> execute() {
        // Store the current navigation history for undo
        previousNavigationHistory = Router.getNavigationHistory();

        // Navigate to the target view with the specified transition
        Router.navigateTo(targetViewClass, parameters, transition);

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo navigation: no previous history"));
        }

        // Clear the current navigation history
        Router.clearNavigationHistory();

        // Restore the previous navigation history (except the last item which is the current view)
        Stack<Class<? extends View>> history = new Stack<>();
        history.addAll(previousNavigationHistory);

        if (!history.isEmpty()) {
            // Navigate to the previous view (the last item in the history) with a backward transition
            Class<? extends View> previousView = history.pop();
            Router.navigateTo(previousView, TransitionPresets.backward());

            // Restore the rest of the history
            for (Class<? extends View> view : history) {
                Router.getNavigationHistory().push(view);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public boolean canUndo() {
        return previousNavigationHistory != null && !previousNavigationHistory.isEmpty();
    }

    @Override
    public String getDescription() {
        return "Navigate to " + targetViewClass.getSimpleName();
    }
}
