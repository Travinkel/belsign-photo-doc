package com.belman.unit.service.usecase.security;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.security.ExtendedAuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;
import com.belman.dataaccess.persistence.memory.InMemoryUserRepository;
import com.belman.application.usecase.security.DefaultExtendedAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the DefaultExtendedAuthenticationService class.
 * These tests verify that PIN code and QR code authentication work correctly.
 */
public class DefaultExtendedAuthenticationServiceTest {

    private ExtendedAuthenticationService authService;
    private InMemoryUserRepository userRepository;

}
