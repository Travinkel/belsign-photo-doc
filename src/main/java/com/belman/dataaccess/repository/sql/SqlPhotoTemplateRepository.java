package com.belman.dataaccess.repository.sql;

import com.belman.dataaccess.repository.BaseRepository;
import com.belman.dataaccess.repository.sql.util.SqlConnectionManager;
import com.belman.dataaccess.repository.sql.util.SqlQueryExecutor;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.photo.PhotoTemplateRepository;
import com.belman.domain.services.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * SQL implementation of the PhotoTemplateRepository interface.
 * This implementation uses JDBC to interact with a SQL database.
 */
public class SqlPhotoTemplateRepository extends BaseRepository<PhotoTemplate, String> implements PhotoTemplateRepository {

    private final SqlConnectionManager connectionManager;
    private final SqlQueryExecutor queryExecutor;

    /**
     * Creates a new SqlPhotoTemplateRepository with the specified dependencies.
     *
     * @param loggerFactory     the logger factory
     * @param connectionManager the SQL connection manager
     */
    public SqlPhotoTemplateRepository(LoggerFactory loggerFactory, SqlConnectionManager connectionManager) {
        super(loggerFactory);
        this.connectionManager = connectionManager;
        this.queryExecutor = new SqlQueryExecutor(loggerFactory, connectionManager);
    }

    @Override
    protected String getId(PhotoTemplate photoTemplate) {
        return photoTemplate.name();
    }

    @Override
    protected PhotoTemplate createCopy(PhotoTemplate photoTemplate) {
        // PhotoTemplate is immutable, so we can return the original
        return photoTemplate;
    }

    @Override
    protected Optional<PhotoTemplate> doFindById(String id) {
        try {
            String sql = "SELECT name, description FROM PHOTO_TEMPLATES WHERE template_id = ?";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return mapResultSetToPhotoTemplate(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to photo template", e);
                    throw new RuntimeException("Error mapping result set to photo template", e);
                }
            }, id);
        } catch (SQLException e) {
            logError("Error finding photo template by ID: " + id, e);
            return Optional.empty();
        }
    }

    @Override
    protected PhotoTemplate doSave(PhotoTemplate photoTemplate) {
        try {
            // Check if the template already exists
            String checkSql = "SELECT COUNT(*) FROM PHOTO_TEMPLATES WHERE name = ?";
            boolean exists = queryExecutor.executeQueryForObject(checkSql, rs -> {
                try {
                    return rs.getInt(1) > 0;
                } catch (SQLException e) {
                    logError("Error getting count from result set", e);
                    throw new RuntimeException("Error getting count from result set", e);
                }
            }, photoTemplate.name()).orElse(false);

            if (exists) {
                // Update existing template
                String sql = "UPDATE PHOTO_TEMPLATES SET description = ? WHERE name = ?";
                queryExecutor.executeUpdate(sql, photoTemplate.description(), photoTemplate.name());
            } else {
                // Insert new template
                String sql = "INSERT INTO PHOTO_TEMPLATES (template_id, name, description) VALUES (?, ?, ?)";
                String templateId = UUID.randomUUID().toString();
                queryExecutor.executeUpdate(sql, templateId, photoTemplate.name(), photoTemplate.description());
            }
            return photoTemplate;
        } catch (SQLException e) {
            logError("Error saving photo template: " + photoTemplate.name(), e);
            throw new RuntimeException("Error saving photo template", e);
        }
    }

    @Override
    protected void doDelete(PhotoTemplate photoTemplate) {
        try {
            // First, delete any associations with orders
            String deleteMappingsSql = "DELETE FROM ORDER_PHOTO_TEMPLATES WHERE template_id IN (SELECT template_id FROM PHOTO_TEMPLATES WHERE name = ?)";
            queryExecutor.executeUpdate(deleteMappingsSql, photoTemplate.name());

            // Then delete the template
            String sql = "DELETE FROM PHOTO_TEMPLATES WHERE name = ?";
            queryExecutor.executeUpdate(sql, photoTemplate.name());
        } catch (SQLException e) {
            logError("Error deleting photo template: " + photoTemplate.name(), e);
            throw new RuntimeException("Error deleting photo template", e);
        }
    }

    @Override
    protected List<PhotoTemplate> doFindAll() {
        try {
            String sql = "SELECT name, description FROM PHOTO_TEMPLATES";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return mapResultSetToPhotoTemplate(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to photo template", e);
                    throw new RuntimeException("Error mapping result set to photo template", e);
                }
            });
        } catch (SQLException e) {
            logError("Error finding all photo templates", e);
            throw new RuntimeException("Error finding all photo templates", e);
        }
    }

    @Override
    protected boolean doExistsById(String id) {
        try {
            String sql = "SELECT COUNT(*) FROM PHOTO_TEMPLATES WHERE template_id = ?";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return rs.getInt(1) > 0;
                } catch (SQLException e) {
                    logError("Error getting count from result set", e);
                    throw new RuntimeException("Error getting count from result set", e);
                }
            }, id).orElse(false);
        } catch (SQLException e) {
            logError("Error checking if photo template exists by ID: " + id, e);
            return false;
        }
    }

    @Override
    protected long doCount() {
        try {
            String sql = "SELECT COUNT(*) FROM PHOTO_TEMPLATES";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return rs.getLong(1);
                } catch (SQLException e) {
                    logError("Error getting count from result set", e);
                    throw new RuntimeException("Error getting count from result set", e);
                }
            }).orElse(0L);
        } catch (SQLException e) {
            logError("Error counting photo templates", e);
            return 0;
        }
    }

    @Override
    public List<PhotoTemplate> findByOrderId(OrderId orderId) {
        try {
            String sql = "SELECT pt.name, pt.description " +
                         "FROM PHOTO_TEMPLATES pt " +
                         "JOIN ORDER_PHOTO_TEMPLATES opt ON pt.template_id = opt.template_id " +
                         "WHERE opt.order_id = ?";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return mapResultSetToPhotoTemplate(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to photo template", e);
                    throw new RuntimeException("Error mapping result set to photo template", e);
                }
            }, orderId.id());
        } catch (SQLException e) {
            logError("Error finding photo templates by order ID: " + orderId.id(), e);
            throw new RuntimeException("Error finding photo templates by order ID", e);
        }
    }

    @Override
    public boolean associateWithOrder(OrderId orderId, String templateId, boolean required) {
        try {
            // Check if the association already exists
            String checkSql = "SELECT COUNT(*) FROM ORDER_PHOTO_TEMPLATES WHERE order_id = ? AND template_id = ?";
            boolean exists = queryExecutor.executeQueryForObject(checkSql, rs -> {
                try {
                    return rs.getInt(1) > 0;
                } catch (SQLException e) {
                    logError("Error getting count from result set", e);
                    throw new RuntimeException("Error getting count from result set", e);
                }
            }, orderId.id(), templateId).orElse(false);

            if (exists) {
                // Update existing association
                String sql = "UPDATE ORDER_PHOTO_TEMPLATES SET required = ? WHERE order_id = ? AND template_id = ?";
                queryExecutor.executeUpdate(sql, required ? 1 : 0, orderId.id(), templateId);
            } else {
                // Insert new association
                String sql = "INSERT INTO ORDER_PHOTO_TEMPLATES (order_id, template_id, required) VALUES (?, ?, ?)";
                queryExecutor.executeUpdate(sql, orderId.id(), templateId, required ? 1 : 0);
            }
            return true;
        } catch (SQLException e) {
            logError("Error associating photo template with order: " + templateId + " -> " + orderId.id(), e);
            return false;
        }
    }

    @Override
    public boolean removeFromOrder(OrderId orderId, String templateId) {
        try {
            String sql = "DELETE FROM ORDER_PHOTO_TEMPLATES WHERE order_id = ? AND template_id = ?";
            int rowsAffected = queryExecutor.executeUpdate(sql, orderId.id(), templateId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Error removing photo template from order: " + templateId + " -> " + orderId.id(), e);
            return false;
        }
    }

    @Override
    public boolean isRequiredForOrder(OrderId orderId, String templateId) {
        try {
            String sql = "SELECT required FROM ORDER_PHOTO_TEMPLATES WHERE order_id = ? AND template_id = ?";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return rs.getBoolean(1);
                } catch (SQLException e) {
                    logError("Error getting boolean from result set", e);
                    throw new RuntimeException("Error getting boolean from result set", e);
                }
            }, orderId.id(), templateId).orElse(false);
        } catch (SQLException e) {
            logError("Error checking if photo template is required for order: " + templateId + " -> " + orderId.id(), e);
            return false;
        }
    }

    /**
     * Maps a ResultSet to a PhotoTemplate.
     *
     * @param rs the ResultSet to map
     * @return the mapped PhotoTemplate
     * @throws SQLException if an error occurs while accessing the ResultSet
     */
    private PhotoTemplate mapResultSetToPhotoTemplate(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String description = rs.getString("description");
        return PhotoTemplate.of(name, description);
    }
}
