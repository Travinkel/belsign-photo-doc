# Belsign Development Mode Guide

This document provides information on how to use the different storage types in the Belsign application.

## Storage Types

The Belsign application supports three storage types:

1. **Memory Mode** (`BELSIGN_STORAGE_TYPE=memory`): Uses in-memory repositories for testing and development.
2. **SQLite Mode** (`BELSIGN_STORAGE_TYPE=sqlite`): Uses SQLite database for development.
3. **SQL Server Mode** (`BELSIGN_STORAGE_TYPE=sqlserver`): Uses SQL Server database for production.

## How to Switch Between Storage Types

You can switch between storage types by setting the `BELSIGN_STORAGE_TYPE` environment variable to one of the following values:

- `memory`: Uses in-memory repositories
- `sqlite`: Uses SQLite database
- `sqlserver`: Uses SQL Server database

### Setting the Environment Variable

#### Windows

```cmd
set BELSIGN_STORAGE_TYPE=memory
```

#### macOS/Linux

```bash
export BELSIGN_STORAGE_TYPE=memory
```

### IntelliJ IDEA

You can also set the environment variable in IntelliJ IDEA:

1. Open the Run/Debug Configurations dialog
2. Select your run configuration
3. Go to the "Environment" tab
4. Add a new environment variable with name `BELSIGN_STORAGE_TYPE` and value `memory`, `sqlite`, or `sqlserver`

## Expected Data Per Mode

### Memory Mode

In memory mode, the application uses in-memory repositories with the following test data:

- **Users**: Admin, Production, QA
- **Orders**: 1-2 sample orders assigned to production user
- **Photo Templates**: All templates are available

### SQLite Mode

In SQLite mode, the application uses a SQLite database with the following data:

- **Users**: Same as memory mode
- **Orders**: Same as memory mode, plus any orders created during development
- **Photo Templates**: All templates are available

### SQL Server Mode

In SQL Server mode, the application uses a SQL Server database with production data:

- **Users**: Production users
- **Orders**: Production orders
- **Photo Templates**: All templates are available

## Known Differences

- **Memory Mode**: Data is lost when the application is restarted
- **SQLite Mode**: Data persists between application restarts, but is stored locally
- **SQL Server Mode**: Data is shared across all instances of the application

## Fallback Behavior

If the application fails to connect to the specified database, it will fall back to a simpler storage type in the following order:

1. SQL Server -> SQLite -> Memory
2. SQLite -> Memory

This ensures that the application can start even if the preferred database is not available.

## Development Tips

- Use memory mode for quick testing and development
- Use SQLite mode for more persistent development
- Use SQL Server mode for production-like testing

## Troubleshooting

If you encounter issues with a specific storage type, try the following:

1. Check the logs for any error messages
2. Verify that the database connection settings are correct
3. Try a different storage type to isolate the issue