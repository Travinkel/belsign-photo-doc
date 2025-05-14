package com.belman.domain.report;

import com.belman.domain.order.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Data access interface for Report business object.
 */
public interface ReportDataAccess {
    /**
     * Finds a report by its ID.
     *
     * @param id the report ID to search for
     * @return an Optional containing the report if found, or empty if not found
     */
    Optional<ReportBusiness> findById(ReportId id);

    /**
     * Finds all reports.
     *
     * @return a list of all reports
     */
    List<ReportBusiness> findAll();

    /**
     * Saves a report (creates or updates).
     *
     * @param report the report to save
     * @return the saved report
     */
    ReportBusiness save(ReportBusiness report);

    /**
     * Deletes a report.
     *
     * @param report the report to delete
     */
    void delete(ReportBusiness report);

    /**
     * Deletes a report by its ID.
     *
     * @param id the ID of the report to delete
     * @return true if the report was deleted, false if the report was not found
     */
    boolean deleteById(ReportId id);

    /**
     * Checks if a report with the given ID exists.
     *
     * @param id the ID to check
     * @return true if a report with the given ID exists, false otherwise
     */
    boolean existsById(ReportId id);

    /**
     * Counts the number of reports.
     *
     * @return the number of reports
     */
    long count();

    /**
     * Finds all reports for a specific order.
     *
     * @param orderId the order ID to search for
     * @return a list of reports for the specified order
     */
    List<ReportBusiness> findByOrderId(OrderId orderId);

    /**
     * Finds all reports with a specific status.
     *
     * @param status the report status to search for
     * @return a list of reports with the specified status
     */
    List<ReportBusiness> findByStatus(ReportStatus status);
}
