package com.belman.repository.persistence.adapter;

import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.report.ReportStatus;
import com.belman.repository.persistence.memory.InMemoryReportRepository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of the ReportRepository interface.
 * This class adapts the InMemoryReportRepository to the ReportRepository interface,
 * allowing the business layer to interact with the data layer through the ReportRepository interface.
 */
public class ReportDataAccessAdapter implements ReportRepository {
    private final InMemoryReportRepository repository;

    /**
     * Creates a new ReportDataAccessAdapter with the specified repository.
     *
     * @param repository the repository to adapt
     */
    public ReportDataAccessAdapter(InMemoryReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ReportBusiness> findById(ReportId id) {
        return repository.findById(id);
    }

    @Override
    public List<ReportBusiness> findAll() {
        return repository.findAll();
    }

    @Override
    public ReportBusiness save(ReportBusiness reportBusiness) {
        return repository.save(reportBusiness);
    }

    @Override
    public void delete(ReportBusiness reportBusiness) {
        repository.delete(reportBusiness);
    }

    @Override
    public boolean deleteById(ReportId id) {
        return repository.deleteById(id);
    }

    @Override
    public boolean existsById(ReportId id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<ReportBusiness> findByOrderId(OrderId orderId) {
        return repository.findByOrderId(orderId);
    }

    @Override
    public List<ReportBusiness> findByStatus(ReportStatus status) {
        return repository.findByStatus(status);
    }
}