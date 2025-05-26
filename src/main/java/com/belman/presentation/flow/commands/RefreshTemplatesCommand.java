package com.belman.presentation.flow.commands;

import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel;

import java.util.concurrent.CompletableFuture;

/**
 * Command for refreshing templates in the PhotoCubeView.
 * <p>
 * This command delegates to the PhotoCubeViewModel to reload templates.
 * It does not support undo operations as refreshing data is typically not undoable.
 */
public class RefreshTemplatesCommand implements Command<Void> {
    
    private final PhotoCubeViewModel viewModel;
    
    /**
     * Creates a new RefreshTemplatesCommand with the specified view model.
     *
     * @param viewModel the PhotoCubeViewModel to use for refreshing templates
     */
    public RefreshTemplatesCommand(PhotoCubeViewModel viewModel) {
        this.viewModel = viewModel;
    }
    
    @Override
    public CompletableFuture<Void> execute() {
        return CompletableFuture.runAsync(() -> {
            if (viewModel == null) {
                throw new IllegalArgumentException("ViewModel cannot be null");
            }
            
            viewModel.onShow(); // This method reloads the current order and its photos
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("Cannot undo refreshing templates"));
    }
    
    @Override
    public boolean canUndo() {
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Refresh templates";
    }
}