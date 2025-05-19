package com.belman.dataaccess.mapper;

import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportStatus;

import java.util.List;

/**
 * Interface for mapping between ReportBusiness entities and database records.
 *
 * @param <D> the type of the database record
 */
public interface ReportMapper<D> extends EntityMapper<ReportBusiness, D> {

    /**
     * Maps a database record to a ReportId.
     *
     * @param record the database record
     * @return the ReportId
     */
    ReportId toReportId(D record);

    /**
     * Maps a database record to an OrderId.
     *
     * @param record the database record
     * @return the OrderId
     */
    OrderId toOrderId(D record);

    /**
     * Finds database records by order ID.
     *
     * @param orderId the order ID
     * @return a list of database records for the specified order
     */
    List<D> findByOrderId(OrderId orderId);

    /**
     * Finds database records by report status.
     *
     * @param status the report status
     * @return a list of database records with the specified status
     */
    List<D> findByStatus(ReportStatus status);
}