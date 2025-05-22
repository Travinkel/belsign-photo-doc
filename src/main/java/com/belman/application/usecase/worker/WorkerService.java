package com.belman.application.usecase.worker;

import com.belman.application.usecase.order.OrderProgressService;
import com.belman.application.usecase.photo.PhotoCaptureService;
import com.belman.application.usecase.photo.PhotoTemplateService;

/**
 * Service for production worker operations.
 * Provides methods for managing orders, capturing photos, and completing orders.
 * This interface extends the more specialized services to maintain backward compatibility.
 */
public interface WorkerService extends PhotoCaptureService, PhotoTemplateService, OrderProgressService {
    // No additional methods needed as all methods are inherited from the extended interfaces
}
