package com.belman.repository.persistence.memory;

import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.report.ReportStatus;
import com.belman.repository.logging.EmojiLoggerFactory;
import com.belman.service.base.BaseService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the ReportRepository interface.
 * This implementation stores reports in memory and is suitable for development and testing.
 * In a production environment, this would be replaced with a database-backed implementation.
 */
public class InMemoryReportRepository extends BaseService implements ReportRepository {
    private final Map<ReportId, ReportBusiness> reportsById = new HashMap<>();
    private final Map<OrderId, List<ReportId>> reportIdsByOrderId = new HashMap<>();

    /**
     * Creates a new InMemoryReportRepository.
     */
    public InMemoryReportRepository() {
        super(EmojiLoggerFactory.getInstance());
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
        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }

        ReportId reportId = report.getId();
        OrderId orderId = report.getOrderId();

        // Store the report by ID
        reportsById.put(reportId, report);

        // Update the mapping from order ID to report IDs
        List<ReportId> reportIds = reportIdsByOrderId.computeIfAbsent(orderId, k -> new ArrayList<>());
        if (!reportIds.contains(reportId)) {
            reportIds.add(reportId);
        }

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
        ReportBusiness report = reportsById.get(id);
        if (report != null) {
            // Remove the report from the ID map
            reportsById.remove(id);

            // Remove the report ID from the order ID mapping
            OrderId orderId = report.getOrderId();
            List<ReportId> reportIds = reportIdsByOrderId.get(orderId);
            if (reportIds != null) {
                reportIds.remove(id);
                if (reportIds.isEmpty()) {
                    reportIdsByOrderId.remove(orderId);
                }
            }

            return true;
        }
        return false;
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
        List<ReportId> reportIds = reportIdsByOrderId.getOrDefault(orderId, new ArrayList<>());
        return reportIds.stream()
                .map(reportsById::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportBusiness> findByStatus(ReportStatus status) {
        return reportsById.values().stream()
                .filter(report -> report.getStatus() == status)
                .collect(Collectors.toList());
    }
}
