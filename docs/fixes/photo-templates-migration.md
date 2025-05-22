# Photo Templates Migration

## Issue
The orders in the system didn't have any photo templates associated with them yet, which was causing the `AssignedOrderView` to show an empty state message: "No Photo Templates Available".

## Solution
A database migration file was created to add photo templates to all existing orders. The migration file:

1. Creates a `PHOTO_TEMPLATES` table to store the predefined templates
2. Creates an `ORDER_PHOTO_TEMPLATES` table to associate orders with photo templates
3. Inserts the predefined templates from the `PhotoTemplate` class into the `PHOTO_TEMPLATES` table
4. Associates all existing orders with all templates using a `CROSS JOIN`
5. Creates indexes for better performance

## Implementation Details

### Database Schema
The migration adds two new tables to the database:

#### PHOTO_TEMPLATES
- `template_id`: Unique identifier for the template
- `name`: Name of the template (e.g., "TOP_VIEW_OF_JOINT")
- `description`: Description of the template for workers
- `created_at`: Timestamp when the template was created

#### ORDER_PHOTO_TEMPLATES
- `order_id`: Reference to the order
- `template_id`: Reference to the photo template
- `required`: Boolean flag indicating if the template is required for the order
- `created_at`: Timestamp when the association was created

### Migration File
The migration file `V15__Add_Photo_Templates_Table.sql` follows the Flyway naming convention and contains the SQL statements to create the tables, insert the predefined templates, and associate them with existing orders.

## Future Improvements
Currently, the `DefaultWorkerService.getAvailableTemplates()` method returns a hardcoded list of predefined templates. In the future, this method should be updated to query the database for templates associated with the order using the new tables created by this migration.

```java
@Override
public List<PhotoTemplate> getAvailableTemplates(OrderId orderId) {
    // TODO: Query the database for templates associated with the order
    // SELECT pt.name, pt.description
    // FROM PHOTO_TEMPLATES pt
    // JOIN ORDER_PHOTO_TEMPLATES opt ON pt.template_id = opt.template_id
    // WHERE opt.order_id = ?

    // For now, we'll continue to return all predefined templates
    List<PhotoTemplate> templates = Arrays.asList(
            PhotoTemplate.TOP_VIEW_OF_JOINT,
            PhotoTemplate.SIDE_VIEW_OF_WELD
            // ... other templates
    );

    return templates;
}
```

This would allow for order-specific templates and more flexibility in the future.
