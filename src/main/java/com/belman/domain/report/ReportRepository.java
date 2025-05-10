package com.belman.domain.report;

import com.belman.domain.order.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing reports.
 * This interface follows the Repository pattern from Domain-Driven Design.
 */
public interface ReportRepository {

    /**
     * Finds a report by ID.
     *
     * @param id the report ID to search for
     * @return an Optional containing the report if found, or empty if not found
     */
    Optional<ReportAggregate> findById(ReportId id);

    /**
     * Finds all reports for a specific order.
     *
     * @param orderId the order ID to search for
     * @return a list of reports for the specified order
     */
    List<ReportAggregate> findByOrderId(OrderId orderId);

    /**
     * Finds all reports with a specific status.
     *
     * @param status the report status to search for
     * @return a list of reports with the specified status
     */
    List<ReportAggregate> findByStatus(ReportStatus status);

    /**
     * Saves a report.
     * If the report already exists, it will be updated.
     * If the report does not exist, it will be created.
     *
     * @param report the report to save
     */
    void save(ReportAggregate report);

    /**
     * Deletes a report by ID.
     *
     * @param id the ID of the report to delete
     * @return true if the report was deleted, false if the report was not found
     */
    boolean delete(ReportId id);

    /**
     * Gets all reports.
     *
     * @return a list of all reports
     */
    List<ReportAggregate> findAll();
}