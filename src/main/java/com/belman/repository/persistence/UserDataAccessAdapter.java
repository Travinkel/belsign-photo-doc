package com.belman.repository.persistence;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserDataAccess;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of the UserDataAccess interface.
 * This class adapts the InMemoryUserRepository to the UserDataAccess interface,
 * allowing the business layer to interact with the data layer through the UserDataAccess interface.
 */
public class UserDataAccessAdapter implements UserDataAccess {
    private final InMemoryUserRepository repository;

    /**
     * Creates a new UserDataAccessAdapter with the specified repository.
     *
     * @param repository the repository to adapt
     */
    public UserDataAccessAdapter(InMemoryUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<UserBusiness> findByUsername(Username username) {
        return repository.findByUsername(username)
                .map(this::convertToBusiness);
    }

    @Override
    public Optional<UserBusiness> findByEmail(EmailAddress email) {
        return repository.findByEmail(email)
                .map(this::convertToBusiness);
    }

    @Override
    public Optional<UserBusiness> findById(UserId id) {
        return repository.findById(id)
                .map(this::convertToBusiness);
    }

    @Override
    public List<UserBusiness> findAll() {
        return repository.findAll().stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserBusiness> findByRole(UserRole role) {
        return repository.findByRole(role).stream()
                .map(this::convertToBusiness)
                .collect(Collectors.toList());
    }

    @Override
    public void save(UserBusiness user) {
        UserBusiness aggregate = convertToAggregate(user);
        repository.save(aggregate);
        
        // Update email mapping if needed
        repository.addEmailMapping(user.getEmail(), user.getId());
    }

    @Override
    public boolean delete(UserId id) {
        return repository.delete(id);
    }

    /**
     * Converts a UserBusiness to a UserBusiness.
     *
     * @param aggregate the aggregate to convert
     * @return the converted business object
     */
    private UserBusiness convertToBusiness(UserBusiness aggregate) {
        return UserBusiness.reconstitute(
                aggregate.getId(),
                aggregate.getUsername(),
                aggregate.getPassword(),
                aggregate.getName(),
                aggregate.getEmail(),
                aggregate.getPhoneNumber(),
                aggregate.getApprovalState(),
                aggregate.getRoles()
        );
    }

    /**
     * Converts a UserBusiness to a UserBusiness.
     *
     * @param business the business object to convert
     * @return the converted aggregate
     */
    private UserBusiness convertToAggregate(UserBusiness business) {
        return UserBusiness.reconstitute(
                business.getId(),
                business.getUsername(),
                business.getPassword(),
                business.getName(),
                business.getEmail(),
                business.getPhoneNumber(),
                business.getApprovalState(),
                business.getRoles()
        );
    }
}