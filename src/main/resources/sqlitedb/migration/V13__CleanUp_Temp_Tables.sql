-- Drop temporary tables if they still exist
DROP TABLE IF EXISTS users_temp;
DROP TABLE IF EXISTS orders_temp;
DROP TABLE IF EXISTS photos_temp;
DROP TABLE IF EXISTS reports_temp;
DROP TABLE IF EXISTS report_photos_temp;
DROP TABLE IF EXISTS photo_annotations_temp;
DROP TABLE IF EXISTS user_roles_temp;
DROP TABLE IF EXISTS customers_temp;

-- Optional: vacuum to clean up space after drop (SQLite only)
-- VACUUM;

-- Log cleanup for audit/debug visibility
-- You may also add an INSERT into an audit table if needed
