-- Create the audit_events table
CREATE TABLE IF NOT EXISTS audit_events (
                              id          TEXT PRIMARY KEY,
                              event_type  TEXT NOT NULL,
                              entity_type TEXT NOT NULL,
                              entity_id   TEXT NOT NULL,
                              user_id     TEXT NOT NULL,
                              action      TEXT NOT NULL,
                              details     TEXT,
                              occurred_on TEXT NOT NULL,
                              created_at  TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE INDEX IF NOT EXISTS idx_audit_events_entity_type_entity_id ON audit_events (entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_events_user_id ON audit_events (user_id);
CREATE INDEX IF NOT EXISTS idx_audit_events_action ON audit_events (action);
CREATE INDEX IF NOT EXISTS idx_audit_events_occurred_on ON audit_events (occurred_on);

-- Create the audit_event_properties table
CREATE TABLE IF NOT EXISTS audit_event_properties (
                                        id             TEXT PRIMARY KEY,
                                        event_id       TEXT NOT NULL,
                                        property_name  TEXT NOT NULL,
                                        property_value TEXT,
                                        FOREIGN KEY (event_id) REFERENCES audit_events (id)
);

CREATE INDEX IF NOT EXISTS idx_audit_event_properties_event_id ON audit_event_properties (event_id);
