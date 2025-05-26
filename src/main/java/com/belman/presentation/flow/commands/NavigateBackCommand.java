package com.belman.presentation.flow.commands;

import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel;

import java.util.concurrent.CompletableFuture;

/**
 * Command for navigating back from the PhotoCubeView.
 * <p>
 * This command delegates to the PhotoCubeViewModel to navigate back to the previous view.
 * It does not support undo operations as navigation is typically one-way.
 */
public class NavigateBackCommand implements Command<Void> {
    
    private final PhotoCubeViewModel viewModel;
    
    /**
     * Creates a new NavigateBackCommand with the specified view model.
     *
     * @param viewModel the PhotoCubeViewModel to use for navigation
     */
    public NavigateBackCommand(PhotoCubeViewModel viewModel) {
        this.viewModel = viewModel;
    }
    
    @Override
    public CompletableFuture<Void> execute() {
        return CompletableFuture.runAsync(() -> {
            if (viewModel == null) {
                throw new IllegalArgumentException("ViewModel cannot be null");
            }
            
            viewModel.goBack();
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("Cannot undo navigation back"));
    }
    
    @Override
    public boolean canUndo() {
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Navigate back to previous view";
    }
}