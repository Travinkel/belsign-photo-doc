package com.belman.integration.gluon;

import com.belman.backbone.core.base.BaseService;

/**
 * Test service for integration testing.
 */
public class TestService extends BaseService {
    
    /**
     * Gets test data.
     * 
     * @return test data string
     */
    public String getData() {
        return "Test service data";
    }
    
    /**
     * Authenticates a user.
     * 
     * @param username the username
     * @param password the password
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate(String username, String password) {
        // Simple authentication logic for testing
        return "admin".equals(username) && "password".equals(password);
    }
}