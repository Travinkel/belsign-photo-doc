package com.belman.repository.persistence.adapter;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.user.*;
import com.belman.repository.persistence.memory.InMemoryUserRepository;

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
        UserBusiness businessObject = convertToBusiness(user);
        repository.save(businessObject);

        // Update email mapping if needed
        repository.addEmailMapping(user.getEmail(), user.getId());
    }

    @Override
    public boolean delete(UserId id) {
        return repository.delete(id);
    }

    /**
     * Creates a copy of a UserBusiness object.
     *
     * @param business the business object to copy
     * @return the copied business object
     */
    private UserBusiness convertToBusiness(UserBusiness business) {
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
