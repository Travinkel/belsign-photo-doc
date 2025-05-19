package com.belman.dataaccess.mapper;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;

import java.util.List;

/**
 * Interface for mapping between UserBusiness entities and database records.
 *
 * @param <D> the type of the database record
 */
public interface UserMapper<D> extends EntityMapper<UserBusiness, D> {

    /**
     * Maps a database record to a UserId.
     *
     * @param record the database record
     * @return the UserId
     */
    UserId toUserId(D record);

    /**
     * Maps a database record to a Username.
     *
     * @param record the database record
     * @return the Username
     */
    Username toUsername(D record);

    /**
     * Maps a database record to an EmailAddress.
     *
     * @param record the database record
     * @return the EmailAddress
     */
    EmailAddress toEmailAddress(D record);

    /**
     * Finds a database record by username.
     *
     * @param username the username
     * @return the database record, or null if not found
     */
    D findByUsername(Username username);

    /**
     * Finds a database record by email.
     *
     * @param email the email
     * @return the database record, or null if not found
     */
    D findByEmail(EmailAddress email);

    /**
     * Finds database records by role.
     *
     * @param role the role
     * @return a list of database records with the specified role
     */
    List<D> findByRole(UserRole role);

    /**
     * Finds a database record by NFC ID.
     *
     * @param nfcId the NFC ID
     * @return the database record, or null if not found
     */
    D findByNfcId(String nfcId);
}