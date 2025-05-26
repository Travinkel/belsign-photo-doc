package com.belman.domain.order;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.common.base.BusinessObject;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.photo.PhotoTemplateRepository;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.UserRole;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Primary business object representing a customer order in the BelSign system.
 * <p>
 * The OrderBusiness object is a central entity in the business model that represents a customer's
 * order for which photo documentation needs to be collected and managed. It serves as the
 * primary container for photo documents and maintains the lifecycle of an order from creation
 * through completion, approval, and delivery.
 * <p>
 * An OrderBusiness contains:
 * - Basic identification (ID and order number)
 * - Customer information
 * - Product details
 * - Delivery information
 * - Status tracking
 * - Collection of associated photo documents
 * - Metadata about creation
 * <p>
 * The OrderBusiness object enforces business rules related to:
 * - Order status transitions
 * - Photo document management
 * - Quality control approval processes
 */
public class OrderBusiness extends BusinessObject<OrderId> {
    private final OrderId id;
    private final UserReference createdBy;
    private final Timestamp createdAt;
    private final List<PhotoId> photoIds = new ArrayList<>();
    private final List<OrderAssignment> assignmentHistory = new ArrayList<>();
    private OrderNumber orderNumber;
    private CustomerId customerId;
    private ProductDescription productDescription;
    private DeliveryInformation deliveryInformation;
    private OrderStatus status;
    private UserReference assignedTo;
    private OrderPriority priority = OrderPriority.NORMAL;
    private LocalDate dueDate;
    private String comments;
    private Timestamp completedAt;

    /**
     * Creates a new OrderBusiness with the specified ID, creator, and creation time.
     *
     * @param id        the unique identifier for this order
     * @param createdBy the user who created this order
     * @param createdAt the time when this order was created
     */
    public OrderBusiness(OrderId id, UserReference createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Creates a new OrderBusiness with the specified ID, order number, creator, and creation time.
     *
     * @param id          the unique identifier for this order
     * @param orderNumber the business order number
     * @param createdBy   the user who created this order
     * @param createdAt   the time when this order was created
     */
    public OrderBusiness(OrderId id, OrderNumber orderNumber, UserReference createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Creates a new OrderBusiness with all details.
     *
     * @param id                  the unique identifier for this order
     * @param orderNumber         the business order number
     * @param customerId          the ID of the customer who placed the order
     * @param productDescription  the product description
     * @param deliveryInformation the delivery information
     * @param createdBy           the user who created this order
     * @param createdAt           the time when this order was created
     */
    public OrderBusiness(OrderId id, OrderNumber orderNumber, CustomerId customerId,
                         ProductDescription productDescription, DeliveryInformation deliveryInformation,
                         UserReference createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.productDescription = Objects.requireNonNull(productDescription, "productDescription must not be null");
        this.deliveryInformation = Objects.requireNonNull(deliveryInformation, "deliveryInformation must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = OrderStatus.PENDING;
    }

    /**
     * Returns the business order number for this order.
     */
    public OrderNumber getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets or updates the business order number for this order.
     *
     * @param orderNumber the business order number to set
     * @throws NullPointerException if orderNumber is null
     */
    public void setOrderNumber(OrderNumber orderNumber) {
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the ID of the customer who placed this order.
     */
    public CustomerId getCustomerId() {
        return customerId;
    }

    /**
     * Sets or updates the customer who placed this order.
     *
     * @param customerId the ID of the customer
     * @throws NullPointerException if customerId is null
     */
    public void setCustomerId(CustomerId customerId) {
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the description of the product being ordered.
     */
    public ProductDescription getProductDescription() {
        return productDescription;
    }

    /**
     * Sets or updates the description of the product being ordered.
     *
     * @param productDescription the product description to set
     * @throws NullPointerException if productDescription is null
     */
    public void setProductDescription(ProductDescription productDescription) {
        this.productDescription = Objects.requireNonNull(productDescription, "productDescription must not be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the delivery information for this order.
     */
    public DeliveryInformation getDeliveryInformation() {
        return deliveryInformation;
    }

    /**
     * Sets or updates the delivery information for this order.
     *
     * @param deliveryInformation the delivery information to set
     * @throws NullPointerException if deliveryInformation is null
     */
    public void setDeliveryInformation(DeliveryInformation deliveryInformation) {
        this.deliveryInformation = Objects.requireNonNull(deliveryInformation, "deliveryInformation must not be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the current status of this order.
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Updates the status of this order.
     *
     * @param status the new status to set
     * @throws NullPointerException if status is null
     */
    public void setStatus(OrderStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the user who created this order.
     */
    public UserReference getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the timestamp when this order was created.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns an unmodifiable view of all photo IDs associated with this order.
     */
    public List<PhotoId> getPhotoIds() {
        return Collections.unmodifiableList(photoIds);
    }

    /**
     * Associates a photo with this order by adding its ID to the order's photo collection.
     *
     * @param photoId the ID of the photo to add to this order
     * @throws NullPointerException if photoId is null
     */
    public void addPhotoId(PhotoId photoId) {
        Objects.requireNonNull(photoId, "photoId must not be null");
        this.photoIds.add(photoId);
        updateLastModifiedAt();
    }

    /**
     * Checks if this order is ready for quality assurance review.
     * An order is ready for QA review when it has at least one photo
     * and its status is COMPLETED, indicating that production work is finished.
     */
    public boolean isReadyForQaReview() {
        return status == OrderStatus.COMPLETED && !photoIds.isEmpty();
    }

    /**
     * Starts processing this order by changing its status to IN_PROGRESS.
     *
     * @throws IllegalStateException if the order is not in PENDING status
     */
    public void startProcessing() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot start processing an order with status: " + status);
        }
        status = OrderStatus.IN_PROGRESS;
        updateLastModifiedAt();
    }

    /**
     * Completes processing of this order by changing its status to COMPLETED.
     * Also sets the completedAt timestamp to the current time.
     *
     * @throws IllegalStateException if the order is not in IN_PROGRESS status
     */
    public void completeProcessing() {
        if (status != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete an order with status: " + status);
        }
        status = OrderStatus.COMPLETED;
        this.completedAt = new Timestamp(Instant.now());
        updateLastModifiedAt();
    }


    /**
     * Approves this order by changing its status to APPROVED.
     *
     * @throws IllegalStateException if the order is not in COMPLETED status
     */
    public void approve() {
        if (status != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot approve an order with status: " + status);
        }
        status = OrderStatus.APPROVED;
        updateLastModifiedAt();
    }

    /**
     * Rejects this order by changing its status to REJECTED.
     *
     * @throws IllegalStateException if the order is not in COMPLETED status
     */
    public void reject() {
        if (status != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot reject an order with status: " + status);
        }
        status = OrderStatus.REJECTED;
        updateLastModifiedAt();
    }

    /**
     * Marks this order as delivered by changing its status to DELIVERED.
     *
     * @throws IllegalStateException if the order is not in APPROVED status
     */
    public void deliver() {
        if (status != OrderStatus.APPROVED) {
            throw new IllegalStateException("Cannot deliver an order with status: " + status);
        }
        status = OrderStatus.DELIVERED;
        updateLastModifiedAt();
    }

    /**
     * Cancels this order by changing its status to CANCELLED.
     * An order can be cancelled at any stage before delivery.
     *
     * @throws IllegalStateException if the order is already DELIVERED
     */
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel an order that has already been delivered");
        }

        OrderStatus previousStatus = status;
        status = OrderStatus.CANCELLED;
        updateLastModifiedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBusiness that = (OrderBusiness) o;
        return id.equals(that.id);
    }

    /**
     * Returns the unique identifier for this order.
     */
    @Override
    public OrderId getId() {
        return id;
    }

    /**
     * Returns the user who this order is assigned to.
     *
     * @return the user reference of the assigned worker, or null if not assigned
     */
    public UserReference getAssignedTo() {
        return assignedTo;
    }

    /**
     * Sets or updates the user who this order is assigned to.
     * This method is deprecated in favor of the assignTo method which provides validation
     * and tracks assignment history.
     *
     * @param assignedTo the user to assign this order to
     * @deprecated Use {@link #assignTo(UserBusiness, UserReference, String)} instead
     */
    @Deprecated
    public void setAssignedTo(UserReference assignedTo) {
        this.assignedTo = assignedTo;
        updateLastModifiedAt();
    }

    /**
     * Returns an unmodifiable view of the assignment history for this order.
     *
     * @return an unmodifiable list of order assignments
     */
    public List<OrderAssignment> getAssignmentHistory() {
        return Collections.unmodifiableList(assignmentHistory);
    }

    /**
     * Assigns this order to a worker.
     * This method validates that the worker has the PRODUCTION role before assigning.
     *
     * @param worker    the worker to assign this order to
     * @param assignedBy the user who is making the assignment
     * @param notes     optional notes about the assignment (can be null)
     * @return true if the assignment was successful, false otherwise
     * @throws IllegalArgumentException if the worker does not have the PRODUCTION role
     * @throws NullPointerException if worker or assignedBy is null
     */
    public boolean assignTo(UserBusiness worker, UserReference assignedBy, String notes) {
        Objects.requireNonNull(worker, "worker must not be null");
        Objects.requireNonNull(assignedBy, "assignedBy must not be null");

        // Validate that the worker has the PRODUCTION role
        if (!worker.getRoles().contains(UserRole.PRODUCTION)) {
            throw new IllegalArgumentException("Worker must have the PRODUCTION role to be assigned an order");
        }

        // Create a reference to the worker
        UserReference workerRef = UserReference.from(worker);

        // Create a timestamp for the assignment
        Timestamp assignedAt = new Timestamp(Instant.now());

        // Create an assignment record
        OrderAssignment assignment = new OrderAssignment(id, workerRef, assignedBy, assignedAt, notes);

        // Add the assignment to the history
        assignmentHistory.add(assignment);

        // Update the assignedTo field
        this.assignedTo = workerRef;

        // Update the last modified timestamp
        updateLastModifiedAt();

        return true;
    }

    /**
     * Reassigns this order to a different worker.
     * This method validates that the new worker has the PRODUCTION role before reassigning.
     *
     * @param newWorker the new worker to assign this order to
     * @param assignedBy the user who is making the reassignment
     * @param notes     optional notes about the reassignment (can be null)
     * @return true if the reassignment was successful, false otherwise
     * @throws IllegalArgumentException if the new worker does not have the PRODUCTION role
     * @throws NullPointerException if newWorker or assignedBy is null
     */
    public boolean reassignTo(UserBusiness newWorker, UserReference assignedBy, String notes) {
        Objects.requireNonNull(newWorker, "newWorker must not be null");
        Objects.requireNonNull(assignedBy, "assignedBy must not be null");

        // Validate that the new worker has the PRODUCTION role
        if (!newWorker.getRoles().contains(UserRole.PRODUCTION)) {
            throw new IllegalArgumentException("Worker must have the PRODUCTION role to be assigned an order");
        }

        // Create a reference to the new worker
        UserReference newWorkerRef = UserReference.from(newWorker);

        // Check if this is actually a reassignment (different worker)
        if (this.assignedTo != null && this.assignedTo.id().equals(newWorkerRef.id())) {
            return false; // Not a reassignment, same worker
        }

        // Create a timestamp for the reassignment
        Timestamp assignedAt = new Timestamp(Instant.now());

        // Create an assignment record
        OrderAssignment assignment = new OrderAssignment(id, newWorkerRef, assignedBy, assignedAt, notes);

        // Add the assignment to the history
        assignmentHistory.add(assignment);

        // Update the assignedTo field
        this.assignedTo = newWorkerRef;

        // Update the last modified timestamp
        updateLastModifiedAt();

        return true;
    }

    /**
     * Checks if this order is currently assigned to a worker.
     *
     * @return true if the order is assigned, false otherwise
     */
    public boolean isAssigned() {
        return assignedTo != null;
    }

    /**
     * Unassigns this order from its current worker.
     *
     * @param unassignedBy the user who is unassigning the order
     * @param reason       the reason for unassigning the order
     * @return true if the order was unassigned, false if it wasn't assigned
     * @throws NullPointerException if unassignedBy is null
     */
    public boolean unassign(UserReference unassignedBy, String reason) {
        Objects.requireNonNull(unassignedBy, "unassignedBy must not be null");

        if (assignedTo == null) {
            return false; // Not assigned, nothing to do
        }

        // Create a timestamp for the unassignment
        Timestamp unassignedAt = new Timestamp(Instant.now());

        // Create an assignment record with null assignedTo to represent unassignment
        OrderAssignment unassignment = new OrderAssignment(
                id, 
                null, // null assignedTo represents unassignment
                unassignedBy, 
                unassignedAt, 
                "Unassigned: " + (reason != null ? reason : "No reason provided"));

        // Add the unassignment to the history
        assignmentHistory.add(unassignment);

        // Update the assignedTo field
        this.assignedTo = null;

        // Update the last modified timestamp
        updateLastModifiedAt();

        return true;
    }

    /**
     * Returns the priority of this order.
     */
    public OrderPriority getPriority() {
        return priority;
    }

    /**
     * Sets or updates the priority of this order.
     *
     * @param priority the priority to set
     * @throws NullPointerException if priority is null
     */
    public void setPriority(OrderPriority priority) {
        this.priority = Objects.requireNonNull(priority, "priority must not be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the due date of this order.
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Sets or updates the due date of this order.
     *
     * @param dueDate the due date to set
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        updateLastModifiedAt();
    }

    /**
     * Returns the comments for this order.
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets or updates the comments for this order.
     *
     * @param comments the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
        updateLastModifiedAt();
    }

    /**
     * Returns the timestamp when this order was completed.
     */
    public Timestamp getCompletedAt() {
        return completedAt;
    }

    /**
     * Checks if this order has all required photos taken.
     * An order has all required photos when photos for all required templates have been captured.
     *
     * @param capturedPhotos the list of photos captured for this order
     * @return true if all required photos have been taken, false otherwise
     */
    public boolean hasRequiredPhotosTaken(List<PhotoDocument> capturedPhotos) {
        List<PhotoTemplate> missingTemplates = getMissingRequiredTemplates(capturedPhotos);
        return missingTemplates.isEmpty();
    }

    /**
     * Gets the list of required photo templates that are missing photos for this order.
     *
     * @param capturedPhotos the list of photos captured for this order
     * @return a list of required templates that are missing photos
     */
    public List<PhotoTemplate> getMissingRequiredTemplates(List<PhotoDocument> capturedPhotos) {
        // Get the templates of the captured photos
        List<PhotoTemplate> capturedTemplates = capturedPhotos.stream()
                .map(PhotoDocument::getTemplate)
                .collect(Collectors.toList());

        // Define the required templates
        // Note: In a real implementation, this should be retrieved from the PhotoTemplateRepository
        // based on the templates associated with this order. For now, we include all standard templates
        // to ensure we don't miss any required photos.
        List<PhotoTemplate> requiredTemplates = Arrays.asList(
                PhotoTemplate.TOP_VIEW_OF_JOINT,
                PhotoTemplate.SIDE_VIEW_OF_WELD,
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY,
                PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY,
                PhotoTemplate.CLOSE_UP_OF_WELD,
                PhotoTemplate.ANGLED_VIEW_OF_JOINT,
                PhotoTemplate.OVERVIEW_OF_ASSEMBLY
        );

        // Find the missing required templates
        return getMissingRequiredTemplates(capturedPhotos, requiredTemplates);
    }

    /**
     * Gets the list of required photo templates that are missing photos for this order,
     * using the provided list of required templates.
     *
     * @param capturedPhotos the list of photos captured for this order
     * @param requiredTemplates the list of templates that are required for this order
     * @return a list of required templates that are missing photos
     */
    public List<PhotoTemplate> getMissingRequiredTemplates(List<PhotoDocument> capturedPhotos, List<PhotoTemplate> requiredTemplates) {
        // Get the templates of the captured photos
        List<PhotoTemplate> capturedTemplates = capturedPhotos.stream()
                .map(PhotoDocument::getTemplate)
                .collect(Collectors.toList());

        // Find the missing required templates
        return requiredTemplates.stream()
                .filter(template -> !capturedTemplates.contains(template))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new order with the specified order number and creator.
     *
     * @param orderNumber the order number
     * @param createdBy the user who created the order
     * @return a new OrderBusiness instance
     */
    public static OrderBusiness createNew(OrderNumber orderNumber, UserBusiness createdBy) {
        Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        Objects.requireNonNull(createdBy, "createdBy must not be null");

        OrderId orderId = OrderId.newId();
        UserReference userRef = new UserReference(createdBy.getId(), createdBy.getUsername());
        Timestamp timestamp = new Timestamp(Instant.now());

        return new OrderBusiness(orderId, orderNumber, userRef, timestamp);
    }

    /**
     * Creates a new order with the specified order number, creator, and assigned templates.
     *
     * @param orderNumber the order number
     * @param createdBy the user who created the order
     * @param templates the templates to assign to the order
     * @param templateRepository the repository to use for associating templates with the order
     * @return a new OrderBusiness instance with the specified templates assigned
     */
    public static OrderBusiness createWithAssignedTemplates(
            OrderNumber orderNumber,
            UserBusiness createdBy,
            List<PhotoTemplate> templates,
            PhotoTemplateRepository templateRepository) {

        Objects.requireNonNull(orderNumber, "orderNumber must not be null");
        Objects.requireNonNull(createdBy, "createdBy must not be null");
        Objects.requireNonNull(templates, "templates must not be null");
        Objects.requireNonNull(templateRepository, "templateRepository must not be null");

        // Create the order
        OrderBusiness order = createNew(orderNumber, createdBy);

        // Associate templates with the order
        for (PhotoTemplate template : templates) {
            templateRepository.associateWithOrder(order.getId(), template.name(), true);
        }

        return order;
    }
}
