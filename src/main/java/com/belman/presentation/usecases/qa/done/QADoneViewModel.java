package com.belman.presentation.usecases.qa.done;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.qa.dashboard.QADashboardView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for the QA done view.
 * Provides data and operations for displaying a completion message after approving or rejecting an order.
 */
public class QADoneViewModel extends BaseViewModel<QADoneViewModel> {
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty completionMessage = new SimpleStringProperty("QA process completed");
    private final BooleanProperty approved = new SimpleBooleanProperty(false);

    @Inject
    private SessionContext sessionContext;

    /**
     * Default constructor for use by the ViewLoader.
     */
    public QADoneViewModel() {
        // Default constructor
    }

    @Override
    public void onShow() {
        // Get parameters from the navigation context
        String orderNumberStr = Router.getParameter("orderNumber");
        if (orderNumberStr != null) {
            orderNumber.set(orderNumberStr);
        }

        Boolean approvedValue = Router.getParameter("approved");
        if (approvedValue != null) {
            approved.set(approvedValue);

            // Update completion message based on approval status
            if (approved.get()) {
                completionMessage.set("Order " + orderNumber.get() + " has been approved");
            } else {
                completionMessage.set("Order " + orderNumber.get() + " has been rejected");
            }
        }
    }

    /**
     * Navigates back to the QA dashboard.
     */
    public void navigateToDashboard() {
        Router.navigateTo(QADashboardView.class);
    }

    /**
     * Gets the order number property.
     *
     * @return the order number property
     */
    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    /**
     * Gets the completion message property.
     *
     * @return the completion message property
     */
    public StringProperty completionMessageProperty() {
        return completionMessage;
    }

    /**
     * Gets the approved property.
     *
     * @return the approved property
     */
    public BooleanProperty approvedProperty() {
        return approved;
    }
}