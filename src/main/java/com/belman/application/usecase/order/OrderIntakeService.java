package com.belman.application.usecase.order;

import com.belman.application.base.BaseService;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.dataaccess.provider.OrderProvider;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for ingesting orders from external sources.
 * This service periodically checks for new orders from the configured OrderProvider
 * and processes them using the OrderService.
 */
public class OrderIntakeService extends BaseService {

    private final OrderProvider orderProvider;
    private final OrderService orderService;
    private final UserBusiness defaultUser;
    private final ScheduledExecutorService scheduler;
    private boolean isRunning = false;

    /**
     * Creates a new OrderIntakeService with the specified dependencies.
     *
     * @param loggerFactory the logger factory
     * @param orderProvider the order provider
     * @param orderService  the order service
     * @param defaultUser   the default user to use for creating orders
     */
    public OrderIntakeService(LoggerFactory loggerFactory, OrderProvider orderProvider, OrderService orderService, UserBusiness defaultUser) {
        super(loggerFactory);
        this.orderProvider = orderProvider;
        this.orderService = orderService;
        this.defaultUser = defaultUser;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Starts the order intake process.
     * This method schedules a periodic task to check for new orders.
     *
     * @param initialDelay the initial delay before the first check
     * @param period       the period between checks
     * @param unit         the time unit for the initial delay and period
     */
    public void start(long initialDelay, long period, TimeUnit unit) {
        if (isRunning) {
            logInfo("Order intake service is already running");
            return;
        }

        logInfo("Starting order intake service with provider: " + orderProvider.getName());
        scheduler.scheduleAtFixedRate(this::processNewOrders, initialDelay, period, unit);
        isRunning = true;
    }

    /**
     * Stops the order intake process.
     */
    public void stop() {
        if (!isRunning) {
            logInfo("Order intake service is not running");
            return;
        }

        logInfo("Stopping order intake service");
        scheduler.shutdown();
        isRunning = false;
    }

    /**
     * Processes new orders from the order provider.
     * This method is called periodically by the scheduler.
     */
    private void processNewOrders() {
        try {
            logInfo("Checking for new orders from provider: " + orderProvider.getName());

            if (!orderProvider.hasNewOrders()) {
                logInfo("No new orders found");
                return;
            }

            List<OrderBusiness> newOrders = orderProvider.fetchNewOrders();
            logInfo("Found " + newOrders.size() + " new orders");

            for (OrderBusiness order : newOrders) {
                try {
                    // Extract the order number from the order
                    if (order.getOrderNumber() == null) {
                        logError("Order has no order number, skipping", (Throwable) null);
                        continue;
                    }

                    // Create a new order using the order service
                    OrderBusiness newOrder = orderService.createOrder(order.getOrderNumber(), defaultUser);
                    logInfo("Created order: " + newOrder.getOrderNumber());
                } catch (Exception e) {
                    logError("Error creating order: " + (order.getOrderNumber() != null ? order.getOrderNumber() : "unknown"), e);
                }
            }
        } catch (Exception e) {
            logError("Error processing new orders", e);
        }
    }

    /**
     * Checks if the order intake service is running.
     *
     * @return true if the service is running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Gets the order provider used by this service.
     *
     * @return the order provider
     */
    public OrderProvider getOrderProvider() {
        return orderProvider;
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return ServiceLocator.getService(LoggerFactory.class);
    }
}
