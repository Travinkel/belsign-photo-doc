package com.belman.repository.persistence;

import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportAggregate;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportDataAccess;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportStatus;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of the ReportDataAccess interface.
 * This class adapts the InMemoryReportRepository to the ReportDataAccess interface,
 * allowing the business layer to interact with the data layer through the ReportDataAccess interface.
 */
public class ReportDataAccessAdapter implements ReportDataAccess {
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
        return repository.findById(id)
                .map(this::convertToBusiness);
    }

    @Override
    public List<ReportBusiness> findAll() {
        return repository.findAll().stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }

    @Override
    public ReportBusiness save(ReportBusiness reportBusiness) {
        ReportAggregate aggregate = convertToAggregate(reportBusiness);
        repository.save(aggregate);
        return reportBusiness;
    }

    @Override
    public void delete(ReportBusiness reportBusiness) {
        repository.delete(reportBusiness.getId());
    }

    @Override
    public boolean deleteById(ReportId id) {
        return repository.delete(id);
    }

    @Override
    public boolean existsById(ReportId id) {
        return repository.findById(id).isPresent();
    }

    @Override
    public long count() {
        return repository.findAll().size();
    }

    @Override
    public List<ReportBusiness> findByOrderId(OrderId orderId) {
        return repository.findByOrderId(orderId).stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportBusiness> findByStatus(ReportStatus status) {
        return repository.findByStatus(status).stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }

    /**
     * Converts a ReportAggregate to a ReportBusiness.
     *
     * @param aggregate the aggregate to convert
     * @return the converted business object
     */
    private ReportBusiness convertToBusiness(ReportAggregate aggregate) {
        ReportBusiness.Builder builder = ReportBusiness.builder()
                .id(aggregate.getId())
                .orderId(aggregate.getOrderId())
                .approvedPhotos(aggregate.getApprovedPhotos())
                .generatedBy(convertUserAggregateToBusiness(aggregate.getGeneratedBy()))
                .generatedAt(aggregate.getGeneratedAt())
                .status(aggregate.getStatus())
                .format(aggregate.getFormat())
                .version(aggregate.getVersion())
                .comments(aggregate.getComments());

        if (aggregate.getRecipient() != null) {
            builder.recipient(convertCustomerAggregateToBusiness(aggregate.getRecipient()));
        }

        return builder.build();
    }

    /**
     * Converts a ReportBusiness to a ReportAggregate.
     *
     * @param business the business object to convert
     * @return the converted aggregate
     */
    private ReportAggregate convertToAggregate(ReportBusiness business) {
        ReportAggregate.Builder builder = ReportAggregate.builder()
                .id(business.getId())
                .orderId(business.getOrderId())
                .approvedPhotos(business.getApprovedPhotos())
                .generatedBy(convertUserBusinessToAggregate(business.getGeneratedBy()))
                .generatedAt(business.getGeneratedAt())
                .status(business.getStatus())
                .format(business.getFormat())
                .version(business.getVersion())
                .comments(business.getComments());

        if (business.getRecipient() != null) {
            builder.recipient(convertCustomerBusinessToAggregate(business.getRecipient()));
        }

        return builder.build();
    }

    /**
     * Converts a UserAggregate to a UserBusiness.
     * This is a placeholder implementation that would need to be replaced with actual conversion logic.
     *
     * @param userAggregate the user aggregate to convert
     * @return the converted user business object
     */
    private com.belman.domain.user.UserBusiness convertUserAggregateToBusiness(com.belman.domain.user.UserBusiness userAggregate) {
        // In a real implementation, this would convert a UserAggregate to a UserBusiness
        // For now, we'll use a placeholder implementation that creates a new UserBusiness with the same ID
        return com.belman.domain.user.UserBusiness.reconstitute(
                userAggregate.getId(),
                userAggregate.getUsername(),
                userAggregate.getPassword(),
                userAggregate.getName(),
                userAggregate.getEmail(),
                userAggregate.getPhoneNumber(),
                userAggregate.getApprovalState(),
                userAggregate.getRoles()
        );
    }

    /**
     * Converts a UserBusiness to a UserAggregate.
     * This is a placeholder implementation that would need to be replaced with actual conversion logic.
     *
     * @param userBusiness the user business object to convert
     * @return the converted user aggregate
     */
    private com.belman.domain.user.UserBusiness convertUserBusinessToAggregate(com.belman.domain.user.UserBusiness userBusiness) {
        // In a real implementation, this would convert a UserBusiness to a UserAggregate
        // For now, we'll use a placeholder implementation that creates a new UserAggregate with the same ID
        return com.belman.domain.user.UserBusiness.reconstitute(
                userBusiness.getId(),
                userBusiness.getUsername(),
                userBusiness.getPassword(),
                userBusiness.getName(),
                userBusiness.getEmail(),
                userBusiness.getPhoneNumber(),
                userBusiness.getApprovalState(),
                userBusiness.getRoles()
        );
    }

    /**
     * Converts a CustomerAggregate to a CustomerBusiness.
     * This is a placeholder implementation that would need to be replaced with actual conversion logic.
     *
     * @param customerAggregate the customer aggregate to convert
     * @return the converted customer business object
     */
    private com.belman.domain.customer.CustomerBusiness convertCustomerAggregateToBusiness(com.belman.domain.customer.CustomerAggregate customerAggregate) {
        // In a real implementation, this would convert a CustomerAggregate to a CustomerBusiness
        // For now, we'll use a placeholder implementation that creates a new CustomerBusiness with the same ID
        if (customerAggregate.isIndividual()) {
            return com.belman.domain.customer.CustomerBusiness.individual(
                    customerAggregate.getId(),
                    customerAggregate.getPersonName(),
                    customerAggregate.getEmail(),
                    customerAggregate.getPhoneNumber()
            );
        } else {
            return com.belman.domain.customer.CustomerBusiness.company(
                    customerAggregate.getId(),
                    customerAggregate.getCompany(),
                    customerAggregate.getEmail(),
                    customerAggregate.getPhoneNumber()
            );
        }
    }

    /**
     * Converts a CustomerBusiness to a CustomerAggregate.
     * This is a placeholder implementation that would need to be replaced with actual conversion logic.
     *
     * @param customerBusiness the customer business object to convert
     * @return the converted customer aggregate
     */
    private com.belman.domain.customer.CustomerAggregate convertCustomerBusinessToAggregate(com.belman.domain.customer.CustomerBusiness customerBusiness) {
        // In a real implementation, this would convert a CustomerBusiness to a CustomerAggregate
        // For now, we'll use a placeholder implementation that creates a new CustomerAggregate with the same ID
        if (customerBusiness.isIndividual()) {
            return com.belman.domain.customer.CustomerAggregate.individual(
                    customerBusiness.getId(),
                    customerBusiness.getPersonName(),
                    customerBusiness.getEmail(),
                    customerBusiness.getPhoneNumber()
            );
        } else {
            return com.belman.domain.customer.CustomerAggregate.company(
                    customerBusiness.getId(),
                    customerBusiness.getCompany(),
                    customerBusiness.getEmail(),
                    customerBusiness.getPhoneNumber()
            );
        }
    }
}