package com.belman.presentation.usecases.archive.photo.workflow;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoTemplate;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.services.PhotoService;
import com.belman.application.usecase.order.OrderService;
import com.belman.application.usecase.photo.CameraService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.flow.commands.FetchNextPhotoTemplateCommand;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.archive.authentication.login.LoginView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * ViewModel for the photo workflow process.
 * This class manages the state and logic for guiding users through
 * taking all required photos for an order.
 */
public class PhotoWorkflowViewModel extends BaseViewModel<PhotoWorkflowViewModel> {

    private final SessionContext sessionContext = ServiceLocator.getService(SessionContext.class);
    private final AuthenticationService authenticationService = ServiceLocator.getService(AuthenticationService.class);
    private final CameraService cameraService = ServiceLocator.getService(CameraService.class);

    @Inject
    private OrderService orderService;

    @Inject
    private PhotoService photoService;

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("Loading...");
    private final StringProperty templateDescription = new SimpleStringProperty("");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final BooleanProperty cameraAvailable = new SimpleBooleanProperty(false);
    private final ObjectProperty<OrderBusiness> currentOrder = new SimpleObjectProperty<>();
    private final ObjectProperty<PhotoTemplate> currentTemplate = new SimpleObjectProperty<>();
    private final ListProperty<PhotoDocument> takenPhotos = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final IntegerProperty photosCompleted = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPhotosRequired = new SimpleIntegerProperty(0);

    @Override
    public void onShow() {
        // Check if camera is available
        cameraAvailable.set(cameraService.isCameraAvailable());

        if (!cameraAvailable.get()) {
            errorMessage.set("Camera is not available on this device.");
            return;
        }

        // Load the current order for the user
        loadCurrentOrder();
    }

    /**
     * Loads the current order for the logged-in user.
     */
    private void loadCurrentOrder() {
        loading.set(true);
        statusMessage.set("Loading current order...");

        // Get the current user
        SessionContext.getCurrentUser().ifPresentOrElse(
            user -> {
                // Get all orders and find the first one for the current user
                List<OrderBusiness> orders = orderService.getAllOrders();

                // Filter orders for the current user
                List<OrderBusiness> userOrders = orders.stream()
                    .filter(o -> o.getCreatedBy() != null && 
                           o.getCreatedBy().id().equals(user.getId()))
                    .toList();

                if (userOrders.isEmpty()) {
                    errorMessage.set("No active order found for the current user.");
                    loading.set(false);
                    return;
                }

                // Set the current order to the first order for the user
                OrderBusiness order = userOrders.get(0);
                currentOrder.set(order);

                // Load the photos for this order
                loadPhotosForOrder(order.getId());
            },
            () -> {
                errorMessage.set("No user is logged in.");
                loading.set(false);
            }
        );
    }

    /**
     * Loads the photos for the specified order.
     *
     * @param orderId the ID of the order
     */
    private void loadPhotosForOrder(OrderId orderId) {
        statusMessage.set("Loading photos...");

        // Get all photos for the order
        List<PhotoDocument> photos = photoService.getPhotosForOrder(orderId);
        takenPhotos.setAll(photos);

        // Update the photos completed count
        photosCompleted.set(photos.size());

        // Define the required templates for this type of order
        // In a real application, this would be determined by the order type or product
        totalPhotosRequired.set(4); // TOP, SIDE, FRONT, BACK views

        // Load the next template
        loadNextTemplate();
    }

    /**
     * Loads the next photo template that needs to be captured.
     */
    private void loadNextTemplate() {
        statusMessage.set("Loading next template...");

        // Use the FetchNextPhotoTemplateCommand to get the next template
        FetchNextPhotoTemplateCommand command = new FetchNextPhotoTemplateCommand(currentOrder.get().getId());

        // In a real implementation, we would use a CommandFactory or ServiceLocator to get the command
        // with its dependencies already injected. For now, we'll use the command directly.

        // Execute the command
        CompletableFuture<Optional<PhotoTemplate>> future = command.execute();
        future.thenAccept(templateOpt -> {
            if (templateOpt.isPresent()) {
                // Set the current template
                currentTemplate.set(templateOpt.get());
                templateDescription.set(templateOpt.get().description());
                statusMessage.set("Ready to take photo: " + templateOpt.get().name());
            } else {
                // All templates have been captured
                currentTemplate.set(null);
                templateDescription.set("");
                statusMessage.set("All required photos have been taken!");
            }
            loading.set(false);
        }).exceptionally(ex -> {
            errorMessage.set("Error loading next template: " + ex.getMessage());
            loading.set(false);
            return null;
        });
    }

    /**
     * Takes a photo using the device camera.
     */
    public void takePhoto() {
        if (currentTemplate.get() == null) {
            errorMessage.set("No template selected.");
            return;
        }

        if (!cameraService.isCameraAvailable()) {
            errorMessage.set("Camera is not available.");
            return;
        }

        loading.set(true);
        statusMessage.set("Taking photo...");

        // Take the photo
        Optional<File> photoFile = cameraService.takePhoto();

        photoFile.ifPresentOrElse(
            file -> {
                // Upload the photo
                try {
                    PhotoDocument photo = photoService.uploadPhoto(
                        file,
                        currentOrder.get().getId(),
                        currentTemplate.get(),
                        sessionContext.getUser().orElseThrow(() -> new IllegalStateException("User not logged in"))
                    );

                    // Add the photo to the list
                    takenPhotos.add(photo);

                    // Update the photos completed count
                    photosCompleted.set(takenPhotos.size());

                    // Load the next template
                    loadNextTemplate();

                    // Show success message
                    statusMessage.set("Photo taken successfully! " + photosCompleted.get() + " of " + totalPhotosRequired.get() + " photos taken.");
                } catch (Exception e) {
                    errorMessage.set("Error uploading photo: " + e.getMessage());
                    loading.set(false);
                }
            },
            () -> {
                // User cancelled
                statusMessage.set("Photo capture cancelled.");
                loading.set(false);
            }
        );
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        try {
            // Log out the user
            authenticationService.logout();

            // Clear the session context using the static method
            SessionContext.clear();

            // Navigate to the login view
            Router.navigateTo(LoginView.class);
        } catch (Exception e) {
            errorMessage.set("Error logging out: " + e.getMessage());
        }
    }

    // Getters for properties

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public StringProperty templateDescriptionProperty() {
        return templateDescription;
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public BooleanProperty cameraAvailableProperty() {
        return cameraAvailable;
    }

    public ObjectProperty<OrderBusiness> currentOrderProperty() {
        return currentOrder;
    }

    public ObjectProperty<PhotoTemplate> currentTemplateProperty() {
        return currentTemplate;
    }

    public ListProperty<PhotoDocument> takenPhotosProperty() {
        return takenPhotos;
    }

    public IntegerProperty photosCompletedProperty() {
        return photosCompleted;
    }

    public IntegerProperty totalPhotosRequiredProperty() {
        return totalPhotosRequired;
    }
}
