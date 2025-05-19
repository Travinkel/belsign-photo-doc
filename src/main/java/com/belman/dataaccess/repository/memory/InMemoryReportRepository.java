package com.belman.dataaccess.repository.memory;

import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.report.ReportStatus;
import com.belman.domain.services.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the ReportRepository interface.
 * This implementation stores reports in memory and is suitable for development and testing.
 * In a production environment, this would be replaced with a database-backed implementation.
 */
public class InMemoryReportRepository implements ReportRepository {

    private final Map<ReportId, ReportBusiness> reportsById = new ConcurrentHashMap<>();
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new InMemoryReportRepository with the specified logger factory.
     *
     * @param loggerFactory the logger factory
     */
    public InMemoryReportRepository(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Optional<ReportBusiness> findById(ReportId id) {
        return Optional.ofNullable(reportsById.get(id));
    }

    @Override
    public List<ReportBusiness> findAll() {
        return new ArrayList<>(reportsById.values());
    }

    @Override
    public ReportBusiness save(ReportBusiness report) {
        reportsById.put(report.getId(), createCopy(report));
        return report;
    }

    @Override
    public void delete(ReportBusiness report) {
        if (report != null) {
            deleteById(report.getId());
        }
    }

    @Override
    public boolean deleteById(ReportId id) {
        return reportsById.remove(id) != null;
    }

    @Override
    public boolean existsById(ReportId id) {
        return reportsById.containsKey(id);
    }

    @Override
    public long count() {
        return reportsById.size();
    }

    @Override
    public List<ReportBusiness> findByOrderId(OrderId orderId) {
        return reportsById.values().stream()
                .filter(report -> report.getOrderId().equals(orderId))
                .map(this::createCopy)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportBusiness> findByStatus(ReportStatus status) {
        return reportsById.values().stream()
                .filter(report -> report.getStatus() == status)
                .map(this::createCopy)
                .collect(Collectors.toList());
    }

    /**
     * Creates a deep copy of a report to prevent external modification.
     * This is important for maintaining the integrity of the aggregate root pattern.
     *
     * @param report the report to copy
     * @return a deep copy of the report
     */
    private ReportBusiness createCopy(ReportBusiness report) {
        // In a real implementation, this would create a proper deep copy
        // For simplicity, we're returning the original object
        // In a production environment, you would need to implement proper deep copying
        return report;
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