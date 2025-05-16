package com.belman.ui.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoTemplate;
import com.belman.domain.services.PhotoService;
import com.belman.service.usecase.order.OrderService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Command for fetching the next required photo template for the current order.
 * <p>
 * This command determines which photo templates are still needed for an order
 * and returns the next template that should be captured.
 */
public class FetchNextPhotoTemplateCommand implements Command<Optional<PhotoTemplate>> {
    
    @Inject
    private OrderService orderService;
    
    @Inject
    private PhotoService photoService;
    
    @Inject
    private SessionContext sessionContext;
    
    private final OrderId orderId;
    
    /**
     * Creates a new FetchNextPhotoTemplateCommand for the specified order.
     *
     * @param orderId the ID of the order to fetch the next photo template for
     */
    public FetchNextPhotoTemplateCommand(OrderId orderId) {
        this.orderId = orderId;
    }
    
    @Override
    public CompletableFuture<Optional<PhotoTemplate>> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
            }
            
            // Get the order
            Optional<OrderBusiness> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isEmpty()) {
                return Optional.empty();
            }
            
            // Get all photos for the order
            List<PhotoDocument> existingPhotos = photoService.getPhotosForOrder(orderId);
            
            // Get all templates that have already been captured
            List<PhotoTemplate> capturedTemplates = existingPhotos.stream()
                    .map(PhotoDocument::getTemplate)
                    .collect(Collectors.toList());
            
            // Define the required templates for this type of order
            // In a real application, this would be determined by the order type or product
            List<PhotoTemplate> requiredTemplates = Arrays.asList(
                    PhotoTemplate.TOP_VIEW_OF_JOINT,
                    PhotoTemplate.SIDE_VIEW_OF_WELD,
                    PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                    PhotoTemplate.BACK_VIEW_OF_ASSEMBLY
            );
            
            // Find the first required template that hasn't been captured yet
            return requiredTemplates.stream()
                    .filter(template -> !capturedTemplates.contains(template))
                    .findFirst();
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        // This command doesn't modify any state, so there's nothing to undo
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public boolean canUndo() {
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Fetch next photo template for order: " + orderId.id();
    }
}