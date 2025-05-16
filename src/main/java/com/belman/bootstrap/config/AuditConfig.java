package com.belman.bootstrap.config;


import com.belman.domain.audit.AuditFacade;
import com.belman.domain.audit.AuditRepository;
import com.belman.domain.audit.DefaultAuditFacade;
import com.belman.domain.core.BusinessObject;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.repository.persistence.memory.InMemoryAuditRepository;

/**
 * Configuration class for setting up the audit system.
 * <p>
 * This class is responsible for initializing the AuditFacade and setting it on the
 * BusinessObject class. It should be called during application startup.
 */
public class AuditConfig {

    private final LoggerFactory loggerFactory;

    /**
     * Creates a new AuditConfig with the specified logger factory.
     *
     * @param loggerFactory the logger factory to use for creating loggers
     */
    public AuditConfig(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    /**
     * Initializes the audit system with an in-memory repository.
     * This is suitable for development and testing environments.
     */
    public void initializeWithInMemoryRepository() {
        Logger logger = loggerFactory.getLogger(DefaultAuditFacade.class);
        AuditRepository auditRepository = new InMemoryAuditRepository();
        AuditFacade auditFacade = new DefaultAuditFacade(auditRepository, logger);

        // Set the AuditFacade on the BusinessObject class
        BusinessObject.setAuditFacade(auditFacade);

        logger.info("Audit system initialized with in-memory repository");
    }

    /**
     * Initializes the audit system with the specified repository.
     * This allows for more flexibility in how audit events are stored.
     *
     * @param auditRepository the repository to use for storing audit events
     */
    public void initialize(AuditRepository auditRepository) {
        Logger logger = loggerFactory.getLogger(DefaultAuditFacade.class);
        AuditFacade auditFacade = new DefaultAuditFacade(auditRepository, logger);

        // Set the AuditFacade on the BusinessObject class
        BusinessObject.setAuditFacade(auditFacade);

        logger.info("Audit system initialized with custom repository: {}",
                auditRepository.getClass().getSimpleName());
    }
}