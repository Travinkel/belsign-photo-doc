package com.belman.domain.services;


import com.belman.domain.order.OrderBusiness;
import com.belman.domain.report.ReportAggregate;
import com.belman.domain.user.UserBusiness;

/**
 * Service for building a QC report based on an order's approved photos.
 */
public interface ReportBuilderService {
    /**
     * Builds and returns a ReportAggregate for the specified orderBusiness using its approved photos.
     *
     * @param orderBusiness the orderBusiness to generate a report for
     * @param generatedBy   the user generating the report
     * @return the constructed ReportAggregate
     */
    ReportAggregate buildReport(OrderBusiness orderBusiness, UserBusiness generatedBy);
}
