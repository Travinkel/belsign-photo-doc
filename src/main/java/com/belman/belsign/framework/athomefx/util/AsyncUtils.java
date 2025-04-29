package com.belman.belsign.framework.athomefx.util;

import com.belman.belsign.framework.athomefx.logging.Logger;
import javafx.application.Platform;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Utility class for handling asynchronous operations.
 * Provides a more fluent API for handling asynchronous operations and errors.
 */
public class AsyncUtils {
    private static final Logger logger = Logger.getLogger(AsyncUtils.class);
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Result of an asynchronous operation.
     * 
     * @param <T> the type of the result
     */
    public static class AsyncResult<T> {
        private final CompletableFuture<T> future;

        private AsyncResult(CompletableFuture<T> future) {
            this.future = future;
        }

        /**
         * Registers a callback to be called when the operation completes successfully.
         * The callback is executed on the JavaFX Application Thread.
         * 
         * @param callback the callback to call
         * @return this AsyncResult for method chaining
         */
        public AsyncResult<T> onSuccess(Consumer<T> callback) {
            future.thenAcceptAsync(result -> {
                try {
                    Platform.runLater(() -> callback.accept(result));
                } catch (Exception e) {
                    logger.error("Error in onSuccess callback", e);
                }
            }, executor);
            return this;
        }

        /**
         * Registers a callback to be called when the operation fails.
         * The callback is executed on the JavaFX Application Thread.
         * 
         * @param callback the callback to call
         * @return this AsyncResult for method chaining
         */
        public AsyncResult<T> onError(Consumer<Throwable> callback) {
            future.exceptionally(ex -> {
                try {
                    Platform.runLater(() -> callback.accept(ex));
                } catch (Exception e) {
                    logger.error("Error in onError callback", e);
                }
                return null;
            });
            return this;
        }

        /**
         * Registers a callback to be called when the operation completes, whether successfully or not.
         * The callback is executed on the JavaFX Application Thread.
         * 
         * @param callback the callback to call
         * @return this AsyncResult for method chaining
         */
        public AsyncResult<T> onComplete(Runnable callback) {
            future.whenCompleteAsync((result, ex) -> {
                try {
                    Platform.runLater(callback);
                } catch (Exception e) {
                    logger.error("Error in onComplete callback", e);
                }
            }, executor);
            return this;
        }

        /**
         * Gets the CompletableFuture for this AsyncResult.
         * 
         * @return the CompletableFuture
         */
        public CompletableFuture<T> getFuture() {
            return future;
        }
    }

    /**
     * Executes an asynchronous operation.
     * 
     * @param supplier the operation to execute
     * @param <T> the type of the result
     * @return an AsyncResult for handling the result
     */
    public static <T> AsyncResult<T> execute(Supplier<T> supplier) {
        logger.debug("Executing async operation");
        CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, executor);
        return new AsyncResult<>(future);
    }

    /**
     * Executes an asynchronous operation that returns void.
     * 
     * @param runnable the operation to execute
     * @return an AsyncResult for handling the result
     */
    public static AsyncResult<Void> execute(Runnable runnable) {
        logger.debug("Executing async operation (void)");
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, executor);
        return new AsyncResult<>(future);
    }

    /**
     * Executes an asynchronous operation with a timeout.
     * 
     * @param supplier the operation to execute
     * @param timeoutMs the timeout in milliseconds
     * @param <T> the type of the result
     * @return an AsyncResult for handling the result
     */
    public static <T> AsyncResult<T> executeWithTimeout(Supplier<T> supplier, long timeoutMs) {
        logger.debug("Executing async operation with timeout: {} ms", timeoutMs);
        CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, executor)
                .orTimeout(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        return new AsyncResult<>(future);
    }

    /**
     * Executes an asynchronous operation that returns void with a timeout.
     * 
     * @param runnable the operation to execute
     * @param timeoutMs the timeout in milliseconds
     * @return an AsyncResult for handling the result
     */
    public static AsyncResult<Void> executeWithTimeout(Runnable runnable, long timeoutMs) {
        logger.debug("Executing async operation (void) with timeout: {} ms", timeoutMs);
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, executor)
                .orTimeout(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
        return new AsyncResult<>(future);
    }

    /**
     * Executes an operation on the JavaFX Application Thread.
     * 
     * @param runnable the operation to execute
     */
    public static void runOnFxThread(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    }

    /**
     * Executes an operation on the JavaFX Application Thread and waits for it to complete.
     * 
     * @param runnable the operation to execute
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public static void runOnFxThreadAndWait(Runnable runnable) throws InterruptedException {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    runnable.run();
                } finally {
                    latch.countDown();
                }
            });
            latch.await();
        }
    }

    /**
     * Shuts down the executor service.
     * This should be called when the application is shutting down.
     */
    public static void shutdown() {
        logger.info("Shutting down AsyncUtils executor service");
        executor.shutdown();
    }

    // Add missing import for CountDownLatch
    private static class CountDownLatch {
        private final java.util.concurrent.CountDownLatch latch;

        public CountDownLatch(int count) {
            this.latch = new java.util.concurrent.CountDownLatch(count);
        }

        public void countDown() {
            latch.countDown();
        }

        public void await() throws InterruptedException {
            latch.await();
        }
    }
}
