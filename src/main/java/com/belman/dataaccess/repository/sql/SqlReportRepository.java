package com.belman.dataaccess.repository.sql;

import com.belman.dataaccess.mapper.ReportMapper;
import com.belman.dataaccess.repository.BaseRepository;
import com.belman.dataaccess.repository.sql.util.SqlConnectionManager;
import com.belman.dataaccess.repository.sql.util.SqlQueryExecutor;
import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.report.ReportStatus;
import com.belman.domain.services.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * SQL implementation of the ReportRepository interface.
 * This implementation uses JDBC to interact with a SQL database.
 */
public class SqlReportRepository implements ReportRepository {

    private final LoggerFactory loggerFactory;
    private final SqlConnectionManager connectionManager;
    private final SqlQueryExecutor queryExecutor;
    private final ReportMapper<ResultSet> reportMapper;

    /**
     * Creates a new SqlReportRepository with the specified dependencies.
     *
     * @param loggerFactory     the logger factory
     * @param connectionManager the SQL connection manager
     * @param reportMapper      the report mapper
     */
    public SqlReportRepository(LoggerFactory loggerFactory, SqlConnectionManager connectionManager, ReportMapper<ResultSet> reportMapper) {
        this.loggerFactory = loggerFactory;
        this.connectionManager = connectionManager;
        this.queryExecutor = new SqlQueryExecutor(loggerFactory, connectionManager);
        this.reportMapper = reportMapper;
    }

    @Override
    public Optional<ReportBusiness> findById(ReportId id) {
        try {
            String sql = "SELECT * FROM reports WHERE id = ?";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return reportMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to report", e);
                    throw new RuntimeException("Error mapping result set to report", e);
                }
            }, id.getValue());
        } catch (SQLException e) {
            logError("Error finding report by ID: " + id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<ReportBusiness> findAll() {
        try {
            String sql = "SELECT * FROM reports";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return reportMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to report", e);
                    throw new RuntimeException("Error mapping result set to report", e);
                }
            });
        } catch (SQLException e) {
            logError("Error finding all reports", e);
            throw new RuntimeException("Error finding all reports", e);
        }
    }

    @Override
    public ReportBusiness save(ReportBusiness report) {
        try {
            if (existsById(report.getId())) {
                // Update existing report
                String sql = "UPDATE reports SET order_id = ?, generated_by = ?, generated_at = ?, " +
                        "recipient = ?, format = ?, status = ?, comments = ?, version = ? " +
                        "WHERE id = ?";
                queryExecutor.executeUpdate(sql,
                        report.getOrderId().id(),
                        report.getGeneratedBy().getId().id(),
                        report.getGeneratedAt().value(),
                        report.getRecipient() != null ? report.getRecipient().getId().id() : null,
                        report.getFormat().name(),
                        report.getStatus().name(),
                        report.getComments(),
                        report.getVersion(),
                        report.getId().getValue());
            } else {
                // Insert new report
                String sql = "INSERT INTO reports (id, order_id, generated_by, generated_at, " +
                        "recipient, format, status, comments, version) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                queryExecutor.executeUpdate(sql,
                        report.getId().getValue(),
                        report.getOrderId().id(),
                        report.getGeneratedBy().getId().id(),
                        report.getGeneratedAt().value(),
                        report.getRecipient() != null ? report.getRecipient().getId().id() : null,
                        report.getFormat().name(),
                        report.getStatus().name(),
                        report.getComments(),
                        report.getVersion());

                // Insert report photos
                if (!report.getApprovedPhotos().isEmpty()) {
                    String photoSql = "INSERT INTO report_photos (report_id, photo_id) VALUES (?, ?)";
                    report.getApprovedPhotos().forEach(photo -> {
                        try {
                            queryExecutor.executeUpdate(photoSql, report.getId().getValue(), photo.getId().id());
                        } catch (SQLException e) {
                            logError("Error inserting report photo: " + photo.getId(), e);
                        }
                    });
                }
            }
            return report;
        } catch (SQLException e) {
            logError("Error saving report: " + report.getId(), e);
            throw new RuntimeException("Error saving report", e);
        }
    }

    @Override
    public void delete(ReportBusiness report) {
        deleteById(report.getId());
    }

    @Override
    public boolean deleteById(ReportId id) {
        try {
            // Delete report photos first
            String photoSql = "DELETE FROM report_photos WHERE report_id = ?";
            queryExecutor.executeUpdate(photoSql, id.getValue());

            // Delete the report
            String sql = "DELETE FROM reports WHERE id = ?";
            int rowsAffected = queryExecutor.executeUpdate(sql, id.getValue());
            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Error deleting report: " + id, e);
            throw new RuntimeException("Error deleting report", e);
        }
    }

    @Override
    public boolean existsById(ReportId id) {
        try {
            String sql = "SELECT COUNT(*) FROM reports WHERE id = ?";
            return queryExecutor.executeQueryForObject(sql, rs -> {
                try {
                    return rs.getInt(1) > 0;
                } catch (SQLException e) {
                    logError("Error getting count from result set", e);
                    throw new RuntimeException("Error getting count from result set", e);
                }
            }, id.getValue())
                    .orElse(false);
        } catch (SQLException e) {
            logError("Error checking if report exists by ID: " + id, e);
            return false;
        }
    }

    @Override
    public long count() {
        try {
            String sql = "SELECT COUNT(*) FROM reports";
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
            logError("Error counting reports", e);
            return 0;
        }
    }

    @Override
    public List<ReportBusiness> findByOrderId(OrderId orderId) {
        try {
            String sql = "SELECT * FROM reports WHERE order_id = ?";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return reportMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to report", e);
                    throw new RuntimeException("Error mapping result set to report", e);
                }
            }, orderId.id());
        } catch (SQLException e) {
            logError("Error finding reports by order ID: " + orderId, e);
            throw new RuntimeException("Error finding reports by order ID", e);
        }
    }

    @Override
    public List<ReportBusiness> findByStatus(ReportStatus status) {
        try {
            String sql = "SELECT * FROM reports WHERE status = ?";
            return queryExecutor.executeQuery(sql, rs -> {
                try {
                    return reportMapper.fromResultSet(rs);
                } catch (SQLException e) {
                    logError("Error mapping result set to report", e);
                    throw new RuntimeException("Error mapping result set to report", e);
                }
            }, status.name());
        } catch (SQLException e) {
            logError("Error finding reports by status: " + status, e);
            throw new RuntimeException("Error finding reports by status", e);
        }
    }

    /**
     * Logs an error message.
     *
     * @param message the error message
     * @param e       the exception
     */
    private void logError(String message, Exception e) {
        if (loggerFactory != null) {
            loggerFactory.getLogger(this.getClass()).error(message, e);
        } else {
            System.err.println(message + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}