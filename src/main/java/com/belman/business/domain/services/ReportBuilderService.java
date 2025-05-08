package com.belman.business.domain.services;


import com.belman.business.domain.order.OrderAggregate;
import com.belman.business.domain.report.ReportAggregate;
import com.belman.business.domain.user.UserAggregate;

/**
 * Service for building a QC report based on an order's approved photos.
 */
public interface ReportBuilderService {
    /**
     * Builds and returns a ReportAggregate for the specified orderAggregate using its approved photos.
     * @param orderAggregate the orderAggregate to generate a report for
     * @param generatedBy the user generating the report
     * @return the constructed ReportAggregate
     */
    ReportAggregate buildReport(OrderAggregate orderAggregate, UserAggregate generatedBy);
}
