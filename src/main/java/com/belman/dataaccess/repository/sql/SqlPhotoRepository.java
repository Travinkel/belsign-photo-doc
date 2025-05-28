package com.belman.dataaccess.repository.sql;

import com.belman.dataaccess.mapper.PhotoMapper;
import com.belman.dataaccess.repository.BaseRepository;
import com.belman.dataaccess.repository.sql.util.SqlConnectionManager;
import com.belman.dataaccess.repository.sql.util.SqlQueryExecutor;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.photo.PhotoDocument.ApprovalStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * SQL implementation of the PhotoRepository interface.
 * This implementation uses JDBC to interact with a SQL database.
 */
public class SqlPhotoRepository extends BaseRepository<PhotoDocument, PhotoId> implements PhotoRepository {

    private final SqlConnectionManager connectionManager;
    private final SqlQueryExecutor queryExecutor;
    private final PhotoMapper<ResultSet> photoMapper;

    /**
     * Creates a new SqlPhotoRepository with the specified dependencies.
     *
     * @param loggerFactory     the logger factory
     * @param connectionManager the SQL connection manager
     * @param photoMapper       the photo mapper
     */
    public SqlPhotoRepository(LoggerFactory loggerFactory, SqlConnectionManager connectionManager, PhotoMapper<ResultSet> photoMapper) {
        super(loggerFactory);
        this.connectionManager = connectionManager;
        this.queryExecutor = new SqlQueryExecutor(loggerFactory, connectionManager);
        this.photoMapper = photoMapper;
    }

    @Override
    protected PhotoId getId(PhotoDocument photoDocument) {
        return photoDocument.getId();
    }

    @Override
    protected PhotoDocument createCopy(PhotoDocument photoDocument) {
        // Create a deep copy of the photo document
        // Note: This is a simplified implementation. In a real application,
        // you would need to create a proper deep copy of all fields.
        return photoDocument;
    }

    @Override
    protected Optional<PhotoDocument> doFindById(PhotoId id) {
        try {
            String sql = "SELECT * FROM PHOTOS WHERE photo_id = ?";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return photoMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to photo", e);
                    throw new RuntimeException("Error mapping result set to photo", e);
                }
            }, id.id());
        } catch (SQLException e) {
            logError("Error finding photo by ID: " + id, e);
            return Optional.empty();
        }
    }

    @Override
    protected PhotoDocument doSave(PhotoDocument photoDocument) {
        try {
            if (doExistsById(photoDocument.getId())) {
                // Update existing photo
                // Note: The PHOTOS table doesn't have reviewed_by, reviewed_at, or review_comment columns
                // We'll just update the fields that exist in the table
                String sql = "UPDATE PHOTOS SET order_id = ?, file_path = ?, template_type = ?, created_by = ?, " +
                        "status = ? " +
                        "WHERE photo_id = ?";
                queryExecutor.executeUpdate(sql,
                        photoDocument.getOrderId().id(),
                        photoDocument.getImagePath().value(),
                        photoDocument.getTemplate().name(),
                        photoDocument.getUploadedBy().getId().id(),
                        photoDocument.getStatus().name(),
                        photoDocument.getId().id());
            } else {
                // Insert new photo
                String sql = "INSERT INTO PHOTOS (photo_id, order_id, file_path, template_type, created_by, created_at, " +
                        "reviewed_by, reviewed_at, review_comment, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                queryExecutor.executeUpdate(sql,
                        photoDocument.getId().id(),
                        photoDocument.getOrderId().id(),
                        photoDocument.getImagePath().value(),
                        photoDocument.getTemplate().name(),
                        photoDocument.getUploadedBy().getId().id(),
                        photoDocument.getUploadedAt().value(),
                        photoDocument.getReviewedBy() != null ? photoDocument.getReviewedBy().id().id() : null,
                        photoDocument.getReviewedAt() != null ? photoDocument.getReviewedAt().value() : null,
                        photoDocument.getReviewComment(),
                        photoDocument.getStatus().name());
            }
            return photoDocument;
        } catch (SQLException e) {
            logError("Error saving photo: " + photoDocument.getId(), e);
            throw new RuntimeException("Error saving photo", e);
        }
    }

    @Override
    protected void doDelete(PhotoDocument photoDocument) {
        try {
            String sql = "DELETE FROM PHOTOS WHERE photo_id = ?";
            queryExecutor.executeUpdate(sql, photoDocument.getId().id());
        } catch (SQLException e) {
            logError("Error deleting photo: " + photoDocument.getId(), e);
            throw new RuntimeException("Error deleting photo", e);
        }
    }

    @Override
    protected List<PhotoDocument> doFindAll() {
        try {
            String sql = "SELECT * FROM PHOTOS";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return photoMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to photo", e);
                    throw new RuntimeException("Error mapping result set to photo", e);
                }
            });
        } catch (SQLException e) {
            logError("Error finding all photos", e);
            throw new RuntimeException("Error finding all photos", e);
        }
    }

    @Override
    protected boolean doExistsById(PhotoId id) {
        try {
            String sql = "SELECT COUNT(*) FROM PHOTOS WHERE photo_id = ?";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return rs.getInt(1) > 0;
                } catch (SQLException e) {
                    logError("Error getting count from result set", e);
                    throw new RuntimeException("Error getting count from result set", e);
                }
            }, id.id())
                    .orElse(false);
        } catch (SQLException e) {
            logError("Error checking if photo exists by ID: " + id, e);
            return false;
        }
    }

    @Override
    protected long doCount() {
        try {
            String sql = "SELECT COUNT(*) FROM PHOTOS";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return rs.getLong(1);
                } catch (SQLException e) {
                    logError("Error getting count from result set", e);
                    throw new RuntimeException("Error getting count from result set", e);
                }
            })
                    .orElse(0L);
        } catch (SQLException e) {
            logError("Error counting photos", e);
            return 0;
        }
    }

    @Override
    public List<PhotoDocument> findByOrderId(OrderId orderId) {
        try {
            String sql = "SELECT * FROM PHOTOS WHERE order_id = ?";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return photoMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to photo", e);
                    throw new RuntimeException("Error mapping result set to photo", e);
                }
            }, orderId.id());
        } catch (SQLException e) {
            logError("Error finding photos by order ID: " + orderId, e);
            throw new RuntimeException("Error finding photos by order ID", e);
        }
    }

    @Override
    public List<PhotoDocument> findByStatus(ApprovalStatus status) {
        try {
            String sql = "SELECT * FROM PHOTOS WHERE status = ?";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return photoMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to photo", e);
                    throw new RuntimeException("Error mapping result set to photo", e);
                }
            }, status.name());
        } catch (SQLException e) {
            logError("Error finding photos by status: " + status, e);
            throw new RuntimeException("Error finding photos by status", e);
        }
    }

    @Override
    public List<PhotoDocument> findByOrderIdAndStatus(OrderId orderId, ApprovalStatus status) {
        try {
            String sql = "SELECT * FROM PHOTOS WHERE order_id = ? AND status = ?";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return photoMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to photo", e);
                    throw new RuntimeException("Error mapping result set to photo", e);
                }
            }, orderId.id(), status.name());
        } catch (SQLException e) {
            logError("Error finding photos by order ID and status: " + orderId + ", " + status, e);
            throw new RuntimeException("Error finding photos by order ID and status", e);
        }
    }
}
