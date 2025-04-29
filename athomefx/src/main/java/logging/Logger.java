package logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging facade for the framework.
 * This class provides a simple interface for logging that can be used throughout the framework.
 * 
 * Note: For production use, it's recommended to add SLF4J as a dependency and replace this
 * implementation with one that delegates to SLF4J.
 * 
 * <dependency>
 *     <groupId>org.slf4j</groupId>
 *     <artifactId>slf4j-api</artifactId>
 *     <version>2.0.7</version>
 * </dependency>
 * <dependency>
 *     <groupId>ch.qos.logback</groupId>
 *     <artifactId>logback-classic</artifactId>
 *     <version>1.4.11</version>
 * </dependency>
 */
public class Logger {
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Log levels.
     */
    public enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
    
    private final String name;
    private static Level minimumLevel = Level.INFO;
    
    /**
     * Creates a new Logger for the specified class.
     * 
     * @param clazz the class to log for
     */
    private Logger(Class<?> clazz) {
        this.name = clazz.getName();
    }
    
    /**
     * Gets a Logger for the specified class.
     * 
     * @param clazz the class to log for
     * @return a Logger for the specified class
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }
    
    /**
     * Sets the minimum log level.
     * Messages with a level below this will not be logged.
     * 
     * @param level the minimum log level
     */
    public static void setMinimumLevel(Level level) {
        minimumLevel = level;
    }
    
    /**
     * Logs a message at the TRACE level.
     * 
     * @param message the message to log
     */
    public void trace(String message) {
        log(Level.TRACE, message);
    }
    
    /**
     * Logs a message with parameters at the TRACE level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void trace(String message, Object... args) {
        log(Level.TRACE, formatMessage(message, args));
    }
    
    /**
     * Logs a message at the DEBUG level.
     * 
     * @param message the message to log
     */
    public void debug(String message) {
        log(Level.DEBUG, message);
    }
    
    /**
     * Logs a message with parameters at the DEBUG level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void debug(String message, Object... args) {
        log(Level.DEBUG, formatMessage(message, args));
    }
    
    /**
     * Logs a message at the INFO level.
     * 
     * @param message the message to log
     */
    public void info(String message) {
        log(Level.INFO, message);
    }
    
    /**
     * Logs a message with parameters at the INFO level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void info(String message, Object... args) {
        log(Level.INFO, formatMessage(message, args));
    }
    
    /**
     * Logs a message at the WARN level.
     * 
     * @param message the message to log
     */
    public void warn(String message) {
        log(Level.WARN, message);
    }
    
    /**
     * Logs a message with parameters at the WARN level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void warn(String message, Object... args) {
        log(Level.WARN, formatMessage(message, args));
    }
    
    /**
     * Logs a message at the ERROR level.
     * 
     * @param message the message to log
     */
    public void error(String message) {
        log(Level.ERROR, message);
    }
    
    /**
     * Logs a message with parameters at the ERROR level.
     * 
     * @param message the message to log
     * @param args the parameters to the message
     */
    public void error(String message, Object... args) {
        log(Level.ERROR, formatMessage(message, args));
    }
    
    /**
     * Logs a message with an exception at the ERROR level.
     * 
     * @param message the message to log
     * @param throwable the exception to log
     */
    public void error(String message, Throwable throwable) {
        log(Level.ERROR, message + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }
    
    /**
     * Logs a message at the specified level.
     * 
     * @param level the log level
     * @param message the message to log
     */
    private void log(Level level, String message) {
        if (level.ordinal() < minimumLevel.ordinal()) {
            return;
        }
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        System.out.println(timestamp + " [" + level + "] " + name + " - " + message);
    }
    
    /**
     * Formats a message with parameters.
     * 
     * @param message the message to format
     * @param args the parameters to the message
     * @return the formatted message
     */
    private String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        
        StringBuilder sb = new StringBuilder();
        int argIndex = 0;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '{' && i + 1 < message.length() && message.charAt(i + 1) == '}') {
                if (argIndex < args.length) {
                    sb.append(args[argIndex++]);
                } else {
                    sb.append("{}");
                }
                i++; // Skip the closing brace
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}