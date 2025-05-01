package com.belman.integration.gluon;

import dev.stefan.athomefx.gluon.mock.MockGluonView;

/**
 * Mock login view for integration testing.
 */
public class LoginView extends MockGluonView<TestViewModel> {
    
    /**
     * Gets the view model.
     * 
     * @return the view model
     */
    @Override
    public TestViewModel getViewModel() {
        return (TestViewModel) super.getViewModel();
    }
}