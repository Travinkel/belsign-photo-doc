package com.belman.domain.report;

import com.belman.domain.common.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.user.UserReference;

import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregate root representing a report in the BelSign system.
 * <p>
 * Reports are generated from order data, particularly from approved photos,
 * and can be delivered to customers in various formats. Reports capture
 * the state of an order's photo documentation at a specific point in time.
 */
public class ReportAggregate {
    private final ReportId id;
    private final OrderId orderId;
    private String title;
    private ReportFormat format;
    private ReportStatus status;
    private URL fileUrl;
    private final Timestamp createdAt;
    private final UserReference createdBy;
    private Timestamp completedAt;
    private String errorMessage;
    private final Set<String> includedPhotoIds = new HashSet<>();

    /**
     * Creates a new Report with the basic information needed for generation.
     *
     * @param id        the unique identifier for this report
     * @param orderId   the ID of the order this report is for
     * @param title     the title of the report
     * @param format    the format of the report
     * @param createdBy reference to the user who created this report
     * @param createdAt the timestamp when this report was created
     */
    public ReportAggregate(ReportId id, OrderId orderId, String title, ReportFormat format,
                           UserReference createdBy, Timestamp createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.format = Objects.requireNonNull(format, "format must not be null");
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.status = ReportStatus.PENDING;
    }

    /**
     * Returns the unique identifier for this report.
     */
    public ReportId getId() {
        return id;
    }

    /**
     * Returns the ID of the order this report is for.
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the title of this report.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets or updates the title of this report.
     *
     * @param title the new title
     * @throws NullPointerException  if title is null
     * @throws IllegalStateException if the report is not in PENDING status
     */
    public void setTitle(String title) {
        if (status != ReportStatus.PENDING) {
            throw new IllegalStateException("Cannot update title for a report that is not in PENDING status");
        }
        this.title = Objects.requireNonNull(title, "title must not be null");
    }

    /**
     * Returns the format of this report.
     */
    public ReportFormat getFormat() {
        return format;
    }

    /**
     * Sets or updates the format of this report.
     *
     * @param format the new format
     * @throws NullPointerException  if format is null
     * @throws IllegalStateException if the report is not in PENDING status
     */
    public void setFormat(ReportFormat format) {
        if (status != ReportStatus.PENDING) {
            throw new IllegalStateException("Cannot update format for a report that is not in PENDING status");
        }
        this.format = Objects.requireNonNull(format, "format must not be null");
    }

    /**
     * Returns the current status of this report.
     */
    public ReportStatus getStatus() {
        return status;
    }

    /**
     * Returns the URL where the report file can be accessed, if available.
     */
    public URL getFileUrl() {
        return fileUrl;
    }

    /**
     * Returns the timestamp when this report was created.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns reference to the user who created this report.
     */
    public UserReference getCreatedBy() {
        return createdBy;
    }

    /**
     * Returns the timestamp when this report was completed, if available.
     */
    public Timestamp getCompletedAt() {
        return completedAt;
    }

    /**
     * Returns the error message if the report generation failed, or null if no error.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Adds a photo to be included in this report.
     *
     * @param photoId the ID of the photo to include
     * @throws IllegalStateException if the report is not in PENDING status
     */
    public void includePhoto(String photoId) {
        if (status != ReportStatus.PENDING) {
            throw new IllegalStateException("Cannot modify included photos for a report that is not in PENDING status");
        }
        includedPhotoIds.add(photoId);
    }

    /**
     * Returns the IDs of all photos included in this report.
     */
    public Set<String> getIncludedPhotoIds() {
        return Set.copyOf(includedPhotoIds);
    }

    /**
     * Marks this report as generating.
     *
     * @throws IllegalStateException if the report is not in PENDING status
     */
    public void startGeneration() {
        if (status != ReportStatus.PENDING) {
            throw new IllegalStateException("Cannot start generation for a report that is not in PENDING status");
        }
        status = ReportStatus.GENERATING;
    }

    /**
     * Marks this report as completed with the generated file URL.
     *
     * @param fileUrl     the URL where the report file can be accessed
     * @param completedAt the timestamp when the report was completed
     * @throws NullPointerException  if fileUrl or completedAt is null
     * @throws IllegalStateException if the report is not in GENERATING status
     */
    public void complete(URL fileUrl, Timestamp completedAt) {
        if (status != ReportStatus.GENERATING) {
            throw new IllegalStateException("Cannot complete a report that is not in GENERATING status");
        }
        this.fileUrl = Objects.requireNonNull(fileUrl, "fileUrl must not be null");
        this.completedAt = Objects.requireNonNull(completedAt, "completedAt must not be null");
        this.status = ReportStatus.COMPLETED;
    }

    /**
     * Marks this report as failed with an error message.
     *
     * @param errorMessage the error message describing why the report generation failed
     * @throws NullPointerException  if errorMessage is null
     * @throws IllegalStateException if the report is not in GENERATING status
     */
    public void fail(String errorMessage) {
        if (status != ReportStatus.GENERATING) {
            throw new IllegalStateException("Cannot mark as failed a report that is not in GENERATING status");
        }
        this.errorMessage = Objects.requireNonNull(errorMessage, "errorMessage must not be null");
        this.status = ReportStatus.FAILED;
    }

    /**
     * Archives this report.
     *
     * @throws IllegalStateException if the report is not in COMPLETED status
     */
    public void archive() {
        if (status != ReportStatus.COMPLETED) {
            throw new IllegalStateException("Cannot archive a report that is not in COMPLETED status");
        }
        status = ReportStatus.ARCHIVED;
    }
}