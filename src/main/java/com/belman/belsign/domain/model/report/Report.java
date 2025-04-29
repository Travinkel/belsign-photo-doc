package com.belman.belsign.domain.model.report;

import com.belman.belsign.domain.model.customer.Customer;
import com.belman.belsign.domain.model.order.OrderId;
import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.user.User;
import com.belman.belsign.domain.model.shared.Timestamp;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a QC report for an order.
 * Contains approved photos and metadata about its creation.
 * 
 * Use the {@code builder()} method to create instances of this class.
 */
public class Report {
    private final ReportId id;
    private final OrderId orderId;
    private final List<PhotoDocument> approvedPhotos;
    private final User generatedBy;
    private final Timestamp generatedAt;
    private Customer recipient;
    private ReportFormat format;
    private ReportStatus status;
    private String comments;
    private int version;

    /**
     * Creates a new Report using the provided builder.
     * 
     * @param builder the builder containing the report's properties
     */
    private Report(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "id must not be null");
        this.orderId = Objects.requireNonNull(builder.orderId, "orderId must not be null");
        this.approvedPhotos = Objects.requireNonNull(builder.approvedPhotos, "approvedPhotos must not be null");
        this.generatedBy = Objects.requireNonNull(builder.generatedBy, "generatedBy must not be null");
        this.generatedAt = Objects.requireNonNull(builder.generatedAt, "generatedAt must not be null");
        this.recipient = builder.recipient;
        this.format = builder.format != null ? builder.format : ReportFormat.PDF;
        this.status = builder.status != null ? builder.status : ReportStatus.DRAFT;
        this.comments = builder.comments;
        this.version = builder.version > 0 ? builder.version : 1;
    }

    /**
     * Creates a new builder for constructing Report instances.
     * 
     * @return a new Report builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new Report with the specified details.
     * This constructor is maintained for backward compatibility with existing tests.
     * 
     * @param orderId the ID of the order this report is for
     * @param approvedPhotos the approved photos to include in the report
     * @param generatedBy the user who generated this report
     * @param generatedAt the time when this report was generated
     * @deprecated Use {@link Builder} instead
     */
    @Deprecated
    public Report(OrderId orderId, List<PhotoDocument> approvedPhotos, User generatedBy, Timestamp generatedAt) {
        this(builder()
            .id(ReportId.newId())
            .orderId(orderId)
            .approvedPhotos(approvedPhotos)
            .generatedBy(generatedBy)
            .generatedAt(generatedAt));
    }

    /**
     * Creates a new Report with the specified details.
     * 
     * @param id the unique identifier for this report
     * @param orderId the ID of the order this report is for
     * @param approvedPhotos the approved photos to include in the report
     * @param generatedBy the user who generated this report
     * @param generatedAt the time when this report was generated
     * @deprecated Use {@link Builder} instead
     */
    @Deprecated
    public Report(ReportId id, OrderId orderId, List<PhotoDocument> approvedPhotos, User generatedBy, Timestamp generatedAt) {
        this(builder()
            .id(id)
            .orderId(orderId)
            .approvedPhotos(approvedPhotos)
            .generatedBy(generatedBy)
            .generatedAt(generatedAt));
    }

    /**
     * Creates a new Report with the specified details, including recipient and format.
     * 
     * @param id the unique identifier for this report
     * @param orderId the ID of the order this report is for
     * @param approvedPhotos the approved photos to include in the report
     * @param generatedBy the user who generated this report
     * @param generatedAt the time when this report was generated
     * @param recipient the customer who will receive this report
     * @param format the format of the report
     * @deprecated Use {@link Builder} instead
     */
    @Deprecated
    public Report(ReportId id, OrderId orderId, List<PhotoDocument> approvedPhotos, User generatedBy, 
                 Timestamp generatedAt, Customer recipient, ReportFormat format) {
        this(builder()
            .id(id)
            .orderId(orderId)
            .approvedPhotos(approvedPhotos)
            .generatedBy(generatedBy)
            .generatedAt(generatedAt)
            .recipient(recipient)
            .format(format));
    }

    public ReportId getId() {
        return id;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public List<PhotoDocument> getApprovedPhotos() {
        return Collections.unmodifiableList(approvedPhotos);
    }

    public User getGeneratedBy() {
        return generatedBy;
    }

    public Timestamp getGeneratedAt() {
        return generatedAt;
    }

    public Customer getRecipient() {
        return recipient;
    }

    public void setRecipient(Customer recipient) {
        this.recipient = Objects.requireNonNull(recipient, "recipient must not be null");
    }

    public ReportFormat getFormat() {
        return format;
    }

    public void setFormat(ReportFormat format) {
        this.format = Objects.requireNonNull(format, "format must not be null");
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getVersion() {
        return version;
    }

    /**
     * Increments the version number of this report.
     * Should be called when making significant changes to the report.
     */
    public void incrementVersion() {
        this.version++;
    }

    /**
     * Finalizes this report, changing its status to FINAL.
     * Once finalized, a report cannot be modified.
     * 
     * @throws IllegalStateException if the report has no recipient
     */
    public void finalizeReport() {
        if (recipient == null) {
            throw new IllegalStateException("Cannot finalize a report without a recipient");
        }
        this.status = ReportStatus.FINAL;
    }

    /**
     * Marks this report as sent to the customer.
     * 
     * @throws IllegalStateException if the report is not in FINAL status
     */
    public void markAsSent() {
        if (status != ReportStatus.FINAL) {
            throw new IllegalStateException("Cannot mark a report as sent if it is not finalized");
        }
        this.status = ReportStatus.SENT;
    }

    /**
     * Archives this report.
     */
    public void archive() {
        this.status = ReportStatus.ARCHIVED;
    }

    /**
     * @return true if this report is in draft status and can be modified
     */
    public boolean isDraft() {
        return status == ReportStatus.DRAFT;
    }

    /**
     * @return true if this report is finalized and cannot be modified
     */
    public boolean isFinal() {
        return status == ReportStatus.FINAL;
    }

    /**
     * @return true if this report has been sent to the customer
     */
    public boolean isSent() {
        return status == ReportStatus.SENT;
    }

    /**
     * @return true if this report has been archived
     */
    public boolean isArchived() {
        return status == ReportStatus.ARCHIVED;
    }
    /**
     * Builder for creating Report instances.
     * This class follows the Builder pattern to simplify the creation of complex Report objects.
     */
    public static class Builder {
        private ReportId id;
        private OrderId orderId;
        private List<PhotoDocument> approvedPhotos;
        private User generatedBy;
        private Timestamp generatedAt;
        private Customer recipient;
        private ReportFormat format;
        private ReportStatus status;
        private String comments;
        private int version;

        /**
         * Creates a new Builder instance.
         */
        private Builder() {
            // Default constructor
        }

        /**
         * Sets the report ID.
         * 
         * @param id the report ID
         * @return this builder for method chaining
         */
        public Builder id(ReportId id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the order ID.
         * 
         * @param orderId the order ID
         * @return this builder for method chaining
         */
        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        /**
         * Sets the approved photos.
         * 
         * @param approvedPhotos the approved photos
         * @return this builder for method chaining
         */
        public Builder approvedPhotos(List<PhotoDocument> approvedPhotos) {
            this.approvedPhotos = approvedPhotos;
            return this;
        }

        /**
         * Sets the user who generated the report.
         * 
         * @param generatedBy the user who generated the report
         * @return this builder for method chaining
         */
        public Builder generatedBy(User generatedBy) {
            this.generatedBy = generatedBy;
            return this;
        }

        /**
         * Sets the time when the report was generated.
         * 
         * @param generatedAt the time when the report was generated
         * @return this builder for method chaining
         */
        public Builder generatedAt(Timestamp generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        /**
         * Sets the recipient of the report.
         * 
         * @param recipient the recipient
         * @return this builder for method chaining
         */
        public Builder recipient(Customer recipient) {
            this.recipient = recipient;
            return this;
        }

        /**
         * Sets the format of the report.
         * 
         * @param format the format
         * @return this builder for method chaining
         */
        public Builder format(ReportFormat format) {
            this.format = format;
            return this;
        }

        /**
         * Sets the status of the report.
         * 
         * @param status the status
         * @return this builder for method chaining
         */
        public Builder status(ReportStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Sets the comments for the report.
         * 
         * @param comments the comments
         * @return this builder for method chaining
         */
        public Builder comments(String comments) {
            this.comments = comments;
            return this;
        }

        /**
         * Sets the version of the report.
         * 
         * @param version the version
         * @return this builder for method chaining
         */
        public Builder version(int version) {
            this.version = version;
            return this;
        }

        /**
         * Builds a new Report instance with the properties set in this builder.
         * 
         * @return a new Report instance
         * @throws NullPointerException if any required property is null
         */
        public Report build() {
            if (id == null) {
                id = ReportId.newId();
            }
            return new Report(this);
        }
    }
}
