package com.belman.domain.services;


import com.belman.domain.aggregates.Order;
import com.belman.domain.entities.Report;
import com.belman.domain.aggregates.User;

/**
 * Service for building a QC report based on an order's approved photos.
 */
public interface ReportBuilderService {
    /**
     * Builds and returns a Report for the specified order using its approved photos.
     * @param order the order to generate a report for
     * @param generatedBy the user generating the report
     * @return the constructed Report
     */
    Report buildReport(Order order, User generatedBy);
}
