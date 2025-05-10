package com.belman.domain.report;

import com.belman.domain.core.DataAccessInterface;
import com.belman.domain.order.OrderId;

import java.util.List;

/**
 * Data access interface for Report business object.
 */
public interface ReportDataAccess extends DataAccessInterface<ReportBusiness, ReportId> {

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