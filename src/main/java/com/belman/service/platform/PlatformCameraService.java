package com.belman.service.platform;

import com.belman.domain.services.CameraService;

/**
 * Service layer interface for accessing the device camera and photo gallery.
 * This interface extends the domain layer CameraService interface and serves as
 * a bridge between the domain and repository layers.
 */
public interface PlatformCameraService extends CameraService {
    // This interface inherits all methods from the domain layer CameraService interface
    // No additional methods are needed at this time
}