package unit.athomefx.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.AsyncUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AsyncUtilsTest {

    @BeforeEach
    void setUp() {
        // No setup needed
    }

    @AfterEach
    void tearDown() {
        // No teardown needed
    }

    @Test
    void testExecuteWithResult() throws Exception {
        // Create a latch to wait for the operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Create an atomic reference to store the result
        AtomicReference<String> result = new AtomicReference<>();

        // Execute an operation that returns a result
        AsyncUtils.execute(() -> "test result")
            .onSuccess(r -> {
                result.set(r);
                latch.countDown();
            });

        // Wait for the operation to complete
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        // Verify that the result was set
        assertEquals("test result", result.get());
    }

    @Test
    void testExecuteWithVoid() throws Exception {
        // Create a latch to wait for the operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Create an atomic boolean to track if the operation completed
        AtomicBoolean completed = new AtomicBoolean(false);

        // Execute an operation that returns void
        AsyncUtils.execute(() -> {
            // Do something
            completed.set(true);
        }).onComplete(() -> {
            latch.countDown();
        });

        // Wait for the operation to complete
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        // Verify that the operation completed
        assertTrue(completed.get());
    }

    @Test
    void testExecuteWithError() throws Exception {
        // Create a latch to wait for the operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Create an atomic reference to store the error
        AtomicReference<Throwable> error = new AtomicReference<>();

        // Execute an operation that throws an exception
        AsyncUtils.execute(() -> {
            throw new RuntimeException("test error");
        }).onError(e -> {
            error.set(e);
            latch.countDown();
        });

        // Wait for the operation to complete
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        // Verify that the error was set
        assertNotNull(error.get());
        assertEquals("test error", error.get().getMessage());
    }

    @Test
    void testExecuteWithTimeout() throws Exception {
        // Create a latch to wait for the operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Create an atomic reference to store the result
        AtomicReference<String> result = new AtomicReference<>();

        // Execute an operation with a timeout
        AsyncUtils.executeWithTimeout(() -> {
            try {
                // Simulate a long-running operation
                Thread.sleep(100);
                return "test result";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Operation interrupted", e);
            }
        }, 1000)
            .onSuccess(r -> {
                result.set(r);
                latch.countDown();
            });

        // Wait for the operation to complete
        assertTrue(latch.await(2, TimeUnit.SECONDS));

        // Verify that the result was set
        assertEquals("test result", result.get());
    }

    @Test
    void testExecuteWithTimeoutExceeded() throws Exception {
        // Create a latch to wait for the operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Create an atomic reference to store the error
        AtomicReference<Throwable> error = new AtomicReference<>();

        // Execute an operation with a timeout that will be exceeded
        AsyncUtils.executeWithTimeout(() -> {
            try {
                // Simulate a long-running operation
                Thread.sleep(500);
                return "test result";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Operation interrupted", e);
            }
        }, 100)
            .onError(e -> {
                error.set(e);
                latch.countDown();
            });

        // Wait for the operation to complete
        assertTrue(latch.await(2, TimeUnit.SECONDS));

        // Verify that the error was set and is a timeout exception
        assertNotNull(error.get());
        assertTrue(error.get() instanceof java.util.concurrent.TimeoutException);
    }

    @Test
    void testRunOnFxThread() {
        // This test is difficult to test properly without a JavaFX application thread
        // We'll just verify that the method doesn't throw an exception
        assertDoesNotThrow(() -> {
            AsyncUtils.runOnFxThread(() -> {
                // Do nothing
            });
        });
    }

    @Test
    void testGetFuture() throws Exception {
        // Create a CompletableFuture to compare with
        CompletableFuture<String> expected = CompletableFuture.completedFuture("test result");

        // Execute an operation and get the future
        CompletableFuture<String> actual = AsyncUtils.execute(() -> "test result").getFuture();

        // Verify that the future completes with the expected result
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void testOnComplete() throws Exception {
        // Create a latch to wait for the operation to complete
        CountDownLatch latch = new CountDownLatch(1);

        // Create atomic booleans to track if the success and complete callbacks were called
        AtomicBoolean successCalled = new AtomicBoolean(false);
        AtomicBoolean completeCalled = new AtomicBoolean(false);

        // Execute an operation
        AsyncUtils.execute(() -> "test result")
            .onSuccess(r -> {
                successCalled.set(true);
            })
            .onComplete(() -> {
                completeCalled.set(true);
                latch.countDown();
            });

        // Wait for the operation to complete
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        // Verify that both callbacks were called
        assertTrue(successCalled.get());
        assertTrue(completeCalled.get());
    }

    @Test
    void testMultipleCallbacks() throws Exception {
        // Create a latch to wait for all callbacks to complete
        CountDownLatch latch = new CountDownLatch(2);

        // Create an atomic integer to count the number of callbacks called
        AtomicInteger callbackCount = new AtomicInteger(0);

        // Execute an operation with multiple success callbacks
        AsyncUtils.execute(() -> "test result")
            .onSuccess(r -> {
                callbackCount.incrementAndGet();
                latch.countDown();
            })
            .onSuccess(r -> {
                callbackCount.incrementAndGet();
                latch.countDown();
            });

        // Wait for all callbacks to complete
        assertTrue(latch.await(1, TimeUnit.SECONDS));

        // Verify that both callbacks were called
        assertEquals(2, callbackCount.get());
    }
}
