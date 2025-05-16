package com.belman.ui.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.order.photo.PhotoTemplate;
import com.belman.domain.services.PhotoService;
import com.belman.domain.user.UserBusiness;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Command for capturing a photo and attaching it to an order.
 * <p>
 * This command takes a photo file, an order ID, and a photo template,
 * and uses the PhotoService to upload the photo and attach it to the order.
 */
public class CapturePhotoCommand implements Command<PhotoDocument> {
    
    @Inject
    private PhotoService photoService;
    
    @Inject
    private SessionContext sessionContext;
    
    private final File photoFile;
    private final OrderId orderId;
    private final PhotoTemplate template;
    private PhotoId createdPhotoId;
    
    /**
     * Creates a new CapturePhotoCommand with the specified photo file, order ID, and template.
     *
     * @param photoFile the photo file to upload
     * @param orderId   the ID of the order to attach the photo to
     * @param template  the template that describes the photo
     */
    public CapturePhotoCommand(File photoFile, OrderId orderId, PhotoTemplate template) {
        this.photoFile = photoFile;
        this.orderId = orderId;
        this.template = template;
    }
    
    @Override
    public CompletableFuture<PhotoDocument> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (photoFile == null) {
                throw new IllegalArgumentException("Photo file cannot be null");
            }
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
            }
            if (template == null) {
                throw new IllegalArgumentException("Photo template cannot be null");
            }
            
            // Get the current user
            Optional<UserBusiness> userOpt = sessionContext.getUser();
            if (userOpt.isEmpty()) {
                throw new IllegalStateException("No user is logged in");
            }
            
            // Upload the photo
            PhotoDocument photo = photoService.uploadPhoto(photoFile, orderId, template, userOpt.get());
            
            // Store the created photo ID for undo
            createdPhotoId = photo.getPhotoId();
            
            return photo;
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo: no photo was created"));
        }
        
        return CompletableFuture.runAsync(() -> {
            // Delete the photo
            boolean deleted = photoService.deletePhoto(createdPhotoId);
            if (!deleted) {
                throw new RuntimeException("Failed to delete photo: " + createdPhotoId.id());
            }
        });
    }
    
    @Override
    public boolean canUndo() {
        return createdPhotoId != null;
    }
    
    @Override
    public String getDescription() {
        return "Capture photo for order: " + orderId.id() + " with template: " + template.name();
    }
}