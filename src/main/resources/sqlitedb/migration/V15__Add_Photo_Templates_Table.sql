-- Create a table for photo templates
CREATE TABLE IF NOT EXISTS photo_templates (
    template_id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create a table to associate orders with photo templates
CREATE TABLE IF NOT EXISTS order_photo_templates (
    order_id TEXT NOT NULL,
    template_id TEXT NOT NULL,
    required BOOLEAN DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id, template_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (template_id) REFERENCES photo_templates(template_id)
);

-- Insert predefined photo templates
INSERT INTO photo_templates (template_id, name, description)
VALUES
    (hex(randomblob(16)), 'TOP_VIEW_OF_JOINT', 'Take a photo from above the joint.'),
    (hex(randomblob(16)), 'SIDE_VIEW_OF_WELD', 'Take a photo from the side of the weld.'),
    (hex(randomblob(16)), 'FRONT_VIEW_OF_ASSEMBLY', 'Take a photo from the front of the assembly.'),
    (hex(randomblob(16)), 'BACK_VIEW_OF_ASSEMBLY', 'Take a photo from the back of the assembly.'),
    (hex(randomblob(16)), 'LEFT_VIEW_OF_ASSEMBLY', 'Take a photo from the left side of the assembly.'),
    (hex(randomblob(16)), 'RIGHT_VIEW_OF_ASSEMBLY', 'Take a photo from the right side of the assembly.'),
    (hex(randomblob(16)), 'BOTTOM_VIEW_OF_ASSEMBLY', 'Take a photo from below the assembly.'),
    (hex(randomblob(16)), 'CLOSE_UP_OF_WELD', 'Take a close-up photo of the weld.'),
    (hex(randomblob(16)), 'ANGLED_VIEW_OF_JOINT', 'Take a photo of the joint from an angled perspective.'),
    (hex(randomblob(16)), 'OVERVIEW_OF_ASSEMBLY', 'Take an overview photo of the entire assembly.'),
    (hex(randomblob(16)), 'CUSTOM', 'Follow specific instructions provided for this photo.');

-- Associate all existing orders with all templates
-- This ensures that all orders have templates associated with them
INSERT INTO order_photo_templates (order_id, template_id)
SELECT o.order_id, pt.template_id
FROM orders o
CROSS JOIN photo_templates pt;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_order_photo_templates_order_id ON order_photo_templates(order_id);
CREATE INDEX IF NOT EXISTS idx_order_photo_templates_template_id ON order_photo_templates(template_id);
