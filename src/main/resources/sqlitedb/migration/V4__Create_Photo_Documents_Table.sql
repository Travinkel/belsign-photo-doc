-- Create Photo Documents Table
CREATE TABLE IF NOT EXISTS photo_documents
(
    id             TEXT PRIMARY KEY,
    order_id       TEXT  NOT NULL,
    angle          TEXT  NOT NULL,
    image_path     TEXT NOT NULL,
    status         TEXT  NOT NULL,
    uploaded_by    TEXT  NOT NULL,
    uploaded_at    TEXT    NOT NULL DEFAULT (datetime('now')),
    reviewed_by    TEXT,
    reviewed_at    TEXT,
    review_comment TEXT,
    created_at     TEXT    NOT NULL DEFAULT (datetime('now')),
    updated_at     TEXT    NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (order_id) REFERENCES orderAggregates (id),
    FOREIGN KEY (uploaded_by) REFERENCES users (id),
    FOREIGN KEY (reviewed_by) REFERENCES users (id)
);

-- Create Index on OrderBusiness ID
CREATE INDEX IF NOT EXISTS idx_photo_documents_order_id ON photo_documents (order_id);

-- Create Index on Status
CREATE INDEX IF NOT EXISTS idx_photo_documents_status ON photo_documents (status);

-- Create Index on Uploaded By
CREATE INDEX IF NOT EXISTS idx_photo_documents_uploaded_by ON photo_documents (uploaded_by);
