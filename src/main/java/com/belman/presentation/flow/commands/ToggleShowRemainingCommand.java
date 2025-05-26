package com.belman.presentation.flow.commands;

import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel;

import java.util.concurrent.CompletableFuture;

/**
 * Command for toggling the "show remaining only" filter in the PhotoCubeView.
 * <p>
 * This command delegates to the PhotoCubeViewModel to toggle the filter.
 * It supports undo operations by toggling the filter back to its previous state.
 */
public class ToggleShowRemainingCommand implements Command<Void> {
    
    private final PhotoCubeViewModel viewModel;
    private final boolean showRemainingOnly;
    private boolean previousState;
    
    /**
     * Creates a new ToggleShowRemainingCommand with the specified view model and state.
     *
     * @param viewModel the PhotoCubeViewModel to use for toggling the filter
     * @param showRemainingOnly the new state for the filter
     */
    public ToggleShowRemainingCommand(PhotoCubeViewModel viewModel, boolean showRemainingOnly) {
        this.viewModel = viewModel;
        this.showRemainingOnly = showRemainingOnly;
    }
    
    @Override
    public CompletableFuture<Void> execute() {
        return CompletableFuture.runAsync(() -> {
            if (viewModel == null) {
                throw new IllegalArgumentException("ViewModel cannot be null");
            }
            
            // Store the previous state for undo
            previousState = viewModel.showRemainingOnlyProperty().get();
            
            // Set the new state
            viewModel.setShowRemainingOnly(showRemainingOnly);
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        return CompletableFuture.runAsync(() -> {
            if (viewModel == null) {
                throw new IllegalArgumentException("ViewModel cannot be null");
            }
            
            // Restore the previous state
            viewModel.setShowRemainingOnly(previousState);
        });
    }
    
    @Override
    public boolean canUndo() {
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Toggle show remaining only filter to " + showRemainingOnly;
    }
}