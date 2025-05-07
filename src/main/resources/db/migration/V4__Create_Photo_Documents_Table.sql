USE BelSign;

-- Create Photo Documents Table
CREATE TABLE photo_documents (
    id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    angle VARCHAR(50) NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    uploaded_by VARCHAR(36) NOT NULL,
    uploaded_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    reviewed_by VARCHAR(36),
    reviewed_at DATETIME2,
    review_comment TEXT,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    FOREIGN KEY (order_id) REFERENCES orderAggregates(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id),
    FOREIGN KEY (reviewed_by) REFERENCES users(id)
);

-- Create Index on OrderAggregate ID
CREATE INDEX idx_photo_documents_order_id ON photo_documents(order_id);

-- Create Index on Status
CREATE INDEX idx_photo_documents_status ON photo_documents(status);

-- Create Index on Uploaded By
CREATE INDEX idx_photo_documents_uploaded_by ON photo_documents(uploaded_by);