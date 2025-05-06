package com.belman.domain.report;

import com.belman.domain.common.Timestamp;
import com.belman.domain.core.AggregateRoot;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.report.events.ReportGeneratedEvent;
import com.belman.domain.user.UserReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing a photo documentation report in the system.
 * <p>
 * Reports encapsulate photo documentation for specific orders, providing a formal
 * record of the quality inspection process. Reports can include multiple photos,
 * annotations, and comments, and they go through a defined lifecycle from generation
 * through delivery to customers.
 */
public class Report extends AggregateRoot<ReportId> {

    private final ReportId id;
    private final OrderId orderId;
    private final ReportType type;
    private ReportStatus status;
    private final UserReference createdBy;
    private final Timestamp createdAt;
    private UserReference approvedBy;
    private Timestamp approvedAt;
    private final List<PhotoId> photoReferences = new ArrayList<>();
    private String comments;
    private byte[] generatedContent;

    /**
     * Creates a new Report with the specified parameters.
     *
     * @param id        the unique identifier for this report
     * @param orderId   the ID of the order this report is for
     * @param type      the type of report
     * @param status    the initial status of the report
     * @param createdBy the user who created this report
     */
    public Report(ReportId id, OrderId orderId, ReportType type, ReportStatus status, UserReference createdBy) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Timestamp.now();

        // Register domain event
        registerDomainEvent(new ReportGeneratedEvent(id, orderId, type, createdBy));
    }

    /**
     * Adds a reference to a photo that is included in this report.
     *
     * @param photoId the ID of the photo to add
     */
    public void addPhotoReference(PhotoId photoId) {
        Objects.requireNonNull(photoId, "photoId must not be null");
        this.photoReferences.add(photoId);
    }

    /**
     * Sets comments for this report.
     *
     * @param comments the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * Stores the generated content (e.g., PDF binary) for this report.
     *
     * @param content the generated content
     */
    public void setGeneratedContent(byte[] content) {
        this.generatedContent = content != null ? content.clone() : null;
    }

    /**
     * Approves this report.
     *
     * @param approver the user approving the report
     * @throws IllegalStateException if the report is already approved
     */
    public void approve(UserReference approver) {
        Objects.requireNonNull(approver, "approver must not be null");

        if (status == ReportStatus.APPROVED) {
            throw new IllegalStateException("Report is already approved");
        }

        this.status = ReportStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = Timestamp.now();
    }

    /**
     * Delivers this report, marking it as delivered.
     *
     * @throws IllegalStateException if the report is not approved
     */
    public void deliver() {
        if (status != ReportStatus.APPROVED) {
            throw new IllegalStateException("Only approved reports can be delivered");
        }

        this.status = ReportStatus.DELIVERED;
    }

    @Override
    public ReportId getId() {
        return id;
    }

    /**
     * Returns the ID of the order this report is for.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the type of this report.
     *
     * @return the report type
     */
    public ReportType getType() {
        return type;
    }

    /**
     * Returns the current status of this report.
     *
     * @return the report status
     */
    public ReportStatus getStatus() {
        return status;
    }

    /**
     * Returns the user who created this report.
     *
     * @return the creating user
     */
    public UserReference getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the timestamp when this report was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the user who approved this report, or null if not approved.
     *
     * @return the approving user, or null if not approved
     */
    public UserReference getApprovedBy() {
        return approvedBy;
    }

    /**
     * Returns the timestamp when this report was approved, or null if not approved.
     *
     * @return the approval timestamp, or null if not approved
     */
    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    /**
     * Returns a list of photo IDs referenced by this report.
     *
     * @return an unmodifiable list of photo IDs
     */
    public List<PhotoId> getPhotoReferences() {
        return Collections.unmodifiableList(photoReferences);
    }

    /**
     * Returns the comments for this report.
     *
     * @return the comments, or null if none
     */
    public String getComments() {
        return comments;
    }

    /**
     * Returns a copy of the generated content for this report.
     *
     * @return a copy of the generated content, or null if none
     */
    public byte[] getGeneratedContent() {
        return generatedContent != null ? generatedContent.clone() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id.equals(report.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}