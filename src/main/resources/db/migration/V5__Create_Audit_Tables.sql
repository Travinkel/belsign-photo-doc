-- Create the audit_events table
CREATE TABLE audit_events
(
    id          UNIQUEIDENTIFIER PRIMARY KEY,
    event_type  VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id   VARCHAR(255) NOT NULL,
    user_id     VARCHAR(255) NOT NULL,
    action      VARCHAR(255) NOT NULL,
    details     NVARCHAR(MAX),
    occurred_on DATETIME2    NOT NULL,
    created_at  DATETIME2    NOT NULL DEFAULT GETDATE()
);

-- Create indexes for faster querying
CREATE INDEX idx_audit_events_entity_type_entity_id ON audit_events (entity_type, entity_id);
CREATE INDEX idx_audit_events_user_id ON audit_events (user_id);
CREATE INDEX idx_audit_events_action ON audit_events (action);
CREATE INDEX idx_audit_events_occurred_on ON audit_events (occurred_on);

-- Create the audit_event_properties table for storing additional properties
CREATE TABLE audit_event_properties
(
    id             UNIQUEIDENTIFIER PRIMARY KEY,
    event_id       UNIQUEIDENTIFIER NOT NULL,
    property_name  VARCHAR(255)     NOT NULL,
    property_value NVARCHAR(MAX),
    CONSTRAINT fk_audit_event_properties_event_id FOREIGN KEY (event_id) REFERENCES audit_events (id) ON DELETE CASCADE
);

-- Create index for faster querying
CREATE INDEX idx_audit_event_properties_event_id ON audit_event_properties (event_id);