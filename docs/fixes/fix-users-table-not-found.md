# Fix for "no such table: USERS" Error

## Issue Description

When attempting to authenticate users, the application was encountering the following error:

```
org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (no such table: USERS)
```

This error occurred because the application was defaulting to memory mode instead of SQLite mode, which meant that the database migrations weren't being executed and the USERS table wasn't being created.

## Root Cause Analysis

1. The `StorageTypeConfig` class was configured to use "memory" as the default storage type.
2. When the application started, it would use in-memory repositories instead of the SQLite database.
3. The database migrations, including `V9__Implement_New_Schema.sql` which creates the USERS table, weren't being executed.
4. When the application tried to authenticate a user, it would try to query the USERS table, which didn't exist.

## Solution

The solution was to change the default storage type from "memory" to "sqlite" in the `StorageTypeConfig` class. This ensures that the application uses the SQLite database by default, which causes the database migrations to be executed and the USERS table to be created.

### Changes Made

1. Modified `StorageTypeConfig.java` to use "sqlite" as the default storage type:
   ```java
   // Changed default from "memory" to "sqlite" to ensure database migrations run and tables are created
   // This fixes the "no such table: USERS" error when trying to authenticate
   private static final String DEFAULT_STORAGE_TYPE = "sqlite";
   ```

## Verification

To verify that the solution works:

1. Start the application without setting the `BELSIGN_STORAGE_TYPE` environment variable or system property.
2. Attempt to log in with a valid username and password.
3. The application should now be able to authenticate users without encountering the "no such table: USERS" error.

## Additional Notes

- If you want to use memory mode for testing, you can still do so by setting the `BELSIGN_STORAGE_TYPE` environment variable or system property to "memory".
- The SQLite database file is created at `src/main/resources/sqlitedb/mydb.db` if it doesn't exist.
- The database migrations are executed from the `src/main/resources/sqlitedb/migration` directory.