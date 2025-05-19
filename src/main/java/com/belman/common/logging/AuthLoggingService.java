package com.belman.common.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for logging authentication and navigation events to a separate file.
 * This helps with debugging authentication and navigation issues.
 */
public class AuthLoggingService {
    private static final String LOG_FILE_NAME = "auth_navigation.log";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean initialized = false;
    private static File logFile;

    /**
     * Initializes the logging service.
     * This method should be called once during application startup.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Create log file in the user's home directory
            String userHome = System.getProperty("user.home");
            File logDir = new File(userHome, "belsign-logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            logFile = new File(logDir, LOG_FILE_NAME);
            
            // Clear the log file on startup
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, false))) {
                writer.println("=== Authentication and Navigation Log ===");
                writer.println("Started at: " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
                writer.println("=======================================");
            }

            // Schedule periodic flushing of the log queue
            scheduler.scheduleAtFixedRate(AuthLoggingService::flushLogQueue, 1, 1, TimeUnit.SECONDS);

            initialized = true;
            
            // Log initialization success
            logInfo("AuthLoggingService", "Logging service initialized. Log file: " + logFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to initialize AuthLoggingService: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Flushes the log queue to the log file.
     */
    private static void flushLogQueue() {
        if (logQueue.isEmpty()) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            String log;
            while ((log = logQueue.poll()) != null) {
                writer.println(log);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    /**
     * Logs an authentication event.
     *
     * @param source the source of the event
     * @param message the message to log
     */
    public static void logAuth(String source, String message) {
        log("AUTH", source, message);
    }

    /**
     * Logs a navigation event.
     *
     * @param source the source of the event
     * @param message the message to log
     */
    public static void logNavigation(String source, String message) {
        log("NAVIGATION", source, message);
    }

    /**
     * Logs a session event.
     *
     * @param source the source of the event
     * @param message the message to log
     */
    public static void logSession(String source, String message) {
        log("SESSION", source, message);
    }

    /**
     * Logs an error event.
     *
     * @param source the source of the event
     * @param message the message to log
     */
    public static void logError(String source, String message) {
        log("ERROR", source, message);
    }

    /**
     * Logs an info event.
     *
     * @param source the source of the event
     * @param message the message to log
     */
    public static void logInfo(String source, String message) {
        log("INFO", source, message);
    }

    /**
     * Logs a warning event.
     *
     * @param source the source of the event
     * @param message the message to log
     */
    public static void logWarning(String source, String message) {
        log("WARNING", source, message);
    }

    /**
     * Logs a debug event.
     *
     * @param source the source of the event
     * @param message the message to log
     */
    public static void logDebug(String source, String message) {
        log("DEBUG", source, message);
    }

    /**
     * Logs an event with the specified type.
     *
     * @param type the type of event
     * @param source the source of the event
     * @param message the message to log
     */
    private static void log(String type, String source, String message) {
        if (!initialized) {
            initialize();
        }

        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String logEntry = String.format("[%s] [%s] [%s] %s", timestamp, type, source, message);
        
        // Add to queue for async writing
        logQueue.add(logEntry);
        
        // Also print to console for immediate feedback
        System.out.println("[DEBUG_LOG] " + logEntry);
    }

    /**
     * Shuts down the logging service.
     * This method should be called once during application shutdown.
     */
    public static void shutdown() {
        flushLogQueue();
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}