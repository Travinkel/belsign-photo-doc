package com.belman.presentation.flow.commands;

import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewModel;

import java.util.concurrent.CompletableFuture;

/**
 * Command for starting the camera preview in the PhotoCubeView.
 * <p>
 * This command delegates to the PhotoCubeViewModel to start the camera preview.
 * It does not support undo operations as stopping the camera preview would be a separate action.
 */
public class StartCameraPreviewCommand implements Command<Void> {
    
    private final PhotoCubeViewModel viewModel;
    
    /**
     * Creates a new StartCameraPreviewCommand with the specified view model.
     *
     * @param viewModel the PhotoCubeViewModel to use for starting the camera preview
     */
    public StartCameraPreviewCommand(PhotoCubeViewModel viewModel) {
        this.viewModel = viewModel;
    }
    
    @Override
    public CompletableFuture<Void> execute() {
        return CompletableFuture.runAsync(() -> {
            if (viewModel == null) {
                throw new IllegalArgumentException("ViewModel cannot be null");
            }
            
            viewModel.startCameraPreview();
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        return CompletableFuture.failedFuture(
                new UnsupportedOperationException("Cannot undo starting camera preview"));
    }
    
    @Override
    public boolean canUndo() {
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Start camera preview";
    }
}